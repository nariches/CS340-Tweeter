package edu.byu.cs.tweeter.client.presenter;

import java.util.List;

import edu.byu.cs.tweeter.client.cache.Cache;
import edu.byu.cs.tweeter.client.model.service.FollowService;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;

public class IsFollowerPresenter extends Presenter implements FollowService.IsFollowerObserver {

    public interface IsFollowerView extends Presenter.View {
        void enableFollowButton(boolean value);
        void updateFollowButton(String text, int textColor, int backgroundColor);
        void setFollowAvailable();
        void setFollowUnavailable();
    }

    private IsFollowerView isFollowerView;
    private User selectedUser;
    private AuthToken authToken;
    private FollowService.FollowObserver observer;

    public IsFollowerPresenter(IsFollowerView view, AuthToken authToken, User selectedUser) {
        super(view);
        this.isFollowerView = view;
        this.authToken = authToken;
        this.selectedUser = selectedUser;
    }

    public void isFollower(User targetUser) {
        if (selectedUser.compareTo(Cache.getInstance().getCurrUser()) == 0) {
            isFollowerView.setFollowUnavailable();
        }
        else {
            isFollowerView.setFollowAvailable();
            new FollowService().isFollower(authToken, targetUser, selectedUser, this);
        }
    }

    @Override
    public void isFollowerSucceeded(String text, int textColor, int backgroundColor) {
        isFollowerView.updateFollowButton(text, textColor, backgroundColor);
    }

    @Override
    public String getDescription() {
        return "IsFollower";
    }
}
