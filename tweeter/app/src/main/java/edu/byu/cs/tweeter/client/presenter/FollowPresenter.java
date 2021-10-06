package edu.byu.cs.tweeter.client.presenter;

import java.util.List;

import edu.byu.cs.client.R;
import edu.byu.cs.tweeter.client.cache.Cache;
import edu.byu.cs.tweeter.client.model.service.FollowService;
import edu.byu.cs.tweeter.client.model.service.UserService;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;

public class FollowPresenter extends Presenter implements FollowService.FollowObserver, FollowService.UnfollowObserver, FollowService.FollowingCountObserver, FollowService.FollowerCountObserver {

    public interface FollowView extends Presenter.View {
        void enableFollowButton(boolean value);
        void updateFollowButton(String text, int textColor, int backgroundColor);
        void updateFollowersCount(int count);
        void updateFollowingCount(int count);
    }

    private FollowView followView;
    private User targetUser;
    private AuthToken authToken;
    private User selectedUser;
    private FollowService.FollowObserver observer;

    public FollowPresenter(FollowView view, AuthToken authToken, User selectedUser) {
        super(view);
        this.followView = view;
        this.authToken = authToken;
        this.selectedUser = selectedUser;
    }

    public void updateFollow(String buttonText) {
        followView.enableFollowButton(false);
        if (buttonText.equals("Following")) {
            followView.displayInfoMessage("Removing " + selectedUser.getName() + "...");
            new FollowService().unfollow(authToken, selectedUser, this);
        }
        else {
            followView.displayInfoMessage("Adding " + selectedUser.getName() + "...");
            new FollowService().follow(authToken, selectedUser, this);
        }
        updateFollowerAndFollowingCounts(authToken, selectedUser);
    }

    public void updateFollowerAndFollowingCounts(AuthToken authToken, User selectedUser) {
        new FollowService().updateFollowingCount(authToken, selectedUser, this);
        new FollowService().updateFollowersCount(authToken, selectedUser, this);
    }

    @Override
    public void followSucceeded() {
        followView.enableFollowButton(true);
        followView.updateFollowButton("Following", R.color.white, R.color.colorAccent);
    }

    @Override
    public void unfollowSucceeded() {
        followView.enableFollowButton(true);
        followView.updateFollowButton("follow", R.color.white, R.color.colorAccent);
    }

    @Override
    public void followerCountSucceeded(int count) {
        followView.updateFollowersCount(count);
    }

    @Override
    public void followingCountSucceeded(int count) {
        followView.updateFollowingCount(count);
    }

    @Override
    public String getDescription() {
        return "Follow";
    }

}
