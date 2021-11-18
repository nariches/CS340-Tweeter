package edu.byu.cs.tweeter.client.model.service;

import android.os.Handler;
import android.os.Message;
import android.widget.Toast;

import androidx.annotation.NonNull;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import edu.byu.cs.client.R;
import edu.byu.cs.tweeter.client.backgroundTask.FollowTask;
import edu.byu.cs.tweeter.client.backgroundTask.GetFollowersCountTask;
import edu.byu.cs.tweeter.client.backgroundTask.GetFollowersTask;
import edu.byu.cs.tweeter.client.backgroundTask.GetFollowingCountTask;
import edu.byu.cs.tweeter.client.backgroundTask.GetFollowingTask;
import edu.byu.cs.tweeter.client.backgroundTask.IsFollowerTask;
import edu.byu.cs.tweeter.client.backgroundTask.UnfollowTask;
import edu.byu.cs.tweeter.client.cache.Cache;
import edu.byu.cs.tweeter.client.view.main.MainActivity;
import edu.byu.cs.tweeter.client.view.main.followers.FollowersFragment;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;

public class FollowService extends Service {

    public interface GetFollowingObserver extends ServiceObserver {
        void getFollowingSucceeded(List<User> users, boolean hasMorePages);
    }

    public interface GetFollowersObserver extends ServiceObserver {
        void getFollowersSucceeded(List<User> users, boolean hasMorePages);
    }

    public interface FollowObserver extends ServiceObserver {
        void followSucceeded();
    }

    public interface UnfollowObserver extends ServiceObserver {
        void unfollowSucceeded();
    }

    public interface FollowingCountObserver extends ServiceObserver {
        void followingCountSucceeded(int count);
    }

    public interface FollowerCountObserver extends ServiceObserver {
        void followerCountSucceeded(int count);
    }

    public interface IsFollowerObserver extends ServiceObserver {
        void isFollowerSucceeded(String text, int textColor, int backgroundColor);
    }

    public void getFollowing(AuthToken authToken, User targetUser, int limit, User lastFollowee,
                             GetFollowingObserver observer) {

        GetFollowingTask getFollowingTask = new GetFollowingTask(authToken, targetUser, limit,
                lastFollowee, new GetFollowingHandler(observer));
        executeTask(getFollowingTask);

    }

    public void getFollowers(AuthToken authToken, User targetUser, int limit, User lastFollower,
                             GetFollowersObserver observer) {
        GetFollowersTask getFollowersTask = new GetFollowersTask(authToken,
                targetUser, limit, lastFollower, new GetFollowersHandler(observer));
        executeTask(getFollowersTask);
    }

    public void follow(AuthToken authToken, User currUser, User selectedUser, FollowObserver observer) {
        FollowTask followTask = new FollowTask(authToken, currUser,
                selectedUser, new FollowHandler(observer));
        executeTask(followTask);
    }

    public void unfollow(AuthToken authToken, User currUser, User selectedUser, UnfollowObserver observer) {
        UnfollowTask unfollowTask = new UnfollowTask(authToken, currUser,
                selectedUser, new UnfollowHandler(observer));
        executeTask(unfollowTask);
    }

    public void isFollower(AuthToken authToken, User targetUser, User selectedUser, IsFollowerObserver observer) {
        IsFollowerTask isFollowerTask = new IsFollowerTask(authToken,
                targetUser, selectedUser, new IsFollowerHandler(observer));
        executeTask(isFollowerTask);
    }

    public void updateFollowingCount(AuthToken authToken, User selectedUser, FollowingCountObserver observer) {
        GetFollowingCountTask followingCountTask = new GetFollowingCountTask(authToken,
                selectedUser, new GetFollowingCountHandler(observer));
        executeCountTask(followingCountTask);
    }

    public void updateFollowersCount(AuthToken authToken, User selectedUser, FollowerCountObserver observer) {
        GetFollowersCountTask followersCountTask = new GetFollowersCountTask(authToken,
                selectedUser, new GetFollowersCountHandler(observer));
        executeCountTask(followersCountTask);
    }

    private class GetFollowingHandler extends ServiceHandler {

        private GetFollowingObserver observer;
        private List<User> followees;
        private boolean hasMorePages;

        public GetFollowingHandler(GetFollowingObserver observer) {
            super(observer);
            this.observer = observer;
        }

        @Override
        public void handleSucceeded(Message msg) {
            this.followees = (List<User>) msg.getData().getSerializable(GetFollowingTask.ITEMS_KEY); //Changed FOLLOWEES_KEY to ITEMS_KEY
            this.hasMorePages = msg.getData().getBoolean(GetFollowingTask.MORE_PAGES_KEY);
            observer.getFollowingSucceeded(followees, hasMorePages);
        }
    }

    /**
     * Message handler (i.e., observer) for GetFollowersTask.
     */
    private class GetFollowersHandler extends ServiceHandler {

        private GetFollowersObserver observer;
        private List<User> followers;
        private boolean hasMorePages;

        public GetFollowersHandler(GetFollowersObserver observer) {
            super(observer);
            this.observer = observer;
        }


        @Override
        public void handleSucceeded(Message msg) {
            this.followers = (List<User>) msg.getData().getSerializable(GetFollowersTask.ITEMS_KEY);
            this.hasMorePages = msg.getData().getBoolean(GetFollowersTask.MORE_PAGES_KEY);
            observer.getFollowersSucceeded(followers, hasMorePages);
        }
    }

    // FollowHandler

    private class FollowHandler extends ServiceHandler {

        private FollowObserver observer;

        public FollowHandler(FollowObserver observer) {
            super(observer);
            this.observer = observer;
        }

        @Override
        public void handleSucceeded(Message msg) {
            observer.followSucceeded();
        }

    }

    // UnfollowHandler

    private class UnfollowHandler extends ServiceHandler {

        private UnfollowObserver observer;

        public UnfollowHandler(UnfollowObserver observer) {
            super(observer);
            this.observer = observer;
        }

        @Override
        public void handleSucceeded(Message msg) {
            observer.unfollowSucceeded();
        }
    }



    private class IsFollowerHandler extends ServiceHandler {

        private IsFollowerObserver observer;
        private boolean isFollower;

        public IsFollowerHandler(IsFollowerObserver observer) {
            super(observer);
            this.observer = observer;
        }

        @Override
        public void handleSucceeded(Message msg) {
            this.isFollower = msg.getData().getBoolean(IsFollowerTask.IS_FOLLOWER_KEY);
            if (isFollower) {
                observer.isFollowerSucceeded("Following", R.color.white, R.color.lightGray);
            }
            else {
                observer.isFollowerSucceeded("Follow", R.color.colorAccent, R.color.white);
            }
        }

    }


    // GetFollowingCountHandler

    private class GetFollowingCountHandler extends ServiceHandler {

        private FollowingCountObserver observer;
        private int count;

        public GetFollowingCountHandler(FollowingCountObserver observer) {
            super(observer);
            this.observer = observer;
        }

        @Override
        public void handleSucceeded(Message msg) {
            this.count = msg.getData().getInt(GetFollowingCountTask.COUNT_KEY);
            observer.followingCountSucceeded(count);
        }
    }

    // GetFollowersCountHandler

    private class GetFollowersCountHandler extends ServiceHandler {

        private FollowerCountObserver observer;
        private int count;

        public GetFollowersCountHandler(FollowerCountObserver observer) {
            super(observer);
            this.observer = observer;
        }

        @Override
        public void handleSucceeded(Message msg) {
            this.count = msg.getData().getInt(GetFollowersCountTask.COUNT_KEY);
            observer.followerCountSucceeded(count);
        }

    }


}
