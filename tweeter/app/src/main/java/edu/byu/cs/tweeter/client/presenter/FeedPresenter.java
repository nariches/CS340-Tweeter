package edu.byu.cs.tweeter.client.presenter;

import java.util.List;

import edu.byu.cs.tweeter.client.model.service.StatusService;
import edu.byu.cs.tweeter.client.model.service.UserService;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;

public class FeedPresenter extends PagedPresenter<Status> implements StatusService.GetFeedObserver {

    public interface FeedView extends PagedPresenter.PagedView<Status> {}

    private static final int PAGE_SIZE = 10;

    private FeedView feedView;

    public FeedPresenter(FeedView view, AuthToken authToken, User targetUser) {
        super(view, authToken, targetUser);
        this.feedView = view;
    }

    @Override
    public void getFeedSucceeded(List<Status> statuses, boolean hasMorePages) {
        if (statuses.size() == 0) {
            getItemsSucceeded(hasMorePages, null);
        }
        else {
            getItemsSucceeded(hasMorePages, statuses.get(statuses.size() - 1));
        }

        feedView.addItems(statuses);
    }

    @Override
    public void getItems(AuthToken authToken, User targetUser, int pageSize, Status lastItem) {
        new StatusService().getFeed(authToken, targetUser, pageSize, lastItem, this);
    }

    @Override
    public String getDescription() {
        return "Get feed";
    }
}
