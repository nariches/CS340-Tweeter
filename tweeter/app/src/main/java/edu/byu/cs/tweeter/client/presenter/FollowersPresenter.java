package edu.byu.cs.tweeter.client.presenter;

import java.util.List;

import edu.byu.cs.tweeter.client.model.service.FollowService;
import edu.byu.cs.tweeter.client.model.service.UserService;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;

public class FollowersPresenter extends PagedPresenter<User> implements FollowService.GetFollowersObserver {

    public interface FollowersView extends PagedPresenter.PagedView<User> {}

    private static final int PAGE_SIZE = 10;

    private FollowersView followersView;

    public FollowersPresenter(FollowersView view, AuthToken authToken, User targetUser) {
        super(view, authToken, targetUser);
        this.followersView = view;
    }

    @Override
    public void getFollowersSucceeded(List<User> users, boolean hasMorePages) {
        if (users.size() == 0) {
            getItemsSucceeded(hasMorePages, null);
        }
        else {
            getItemsSucceeded(hasMorePages, users.get(users.size() - 1));
        }

        followersView.addItems(users);
    }


    @Override
    public void getItems(AuthToken authToken, User targetUser, int pageSize, User lastItem) {
        new FollowService().getFollowers(authToken, targetUser, PAGE_SIZE, lastItem, this);
    }

    @Override
    public String getDescription() {
        return "Followers";
    }

}
