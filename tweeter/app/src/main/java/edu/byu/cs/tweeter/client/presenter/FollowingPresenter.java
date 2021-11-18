package edu.byu.cs.tweeter.client.presenter;

import java.util.List;

import edu.byu.cs.tweeter.client.model.service.FollowService;
import edu.byu.cs.tweeter.client.model.service.UserService;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;

public class FollowingPresenter extends PagedPresenter<User> implements FollowService.GetFollowingObserver {

    public interface FollowingView extends PagedPresenter.PagedView<User> {}

    private static final int PAGE_SIZE = 10;

    private FollowingView followingView;

    public FollowingPresenter(FollowingView view, AuthToken authToken, User targetUser) {
        super(view, authToken, targetUser);
        this.followingView = view;
    }

    @Override
        public void getFollowingSucceeded(List<User> users, boolean hasMorePages) {
        if (users.size() == 0) {
            getItemsSucceeded(hasMorePages, null);
        }
        else {
            getItemsSucceeded(hasMorePages, users.get(users.size() - 1));
        }

        followingView.addItems(users);
        }

    @Override
    public void getItems(AuthToken authToken, User targetUser, int pageSize, User lastItem) {
        new FollowService().getFollowing(authToken, targetUser, PAGE_SIZE, lastItem, this);
    }

    @Override
    public String getDescription() {
        return "Following";
    }

}
