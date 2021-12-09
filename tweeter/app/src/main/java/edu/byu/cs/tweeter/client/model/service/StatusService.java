package edu.byu.cs.tweeter.client.model.service;

import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.widget.Toast;

import androidx.annotation.NonNull;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import edu.byu.cs.tweeter.client.backgroundTask.GetFeedTask;
import edu.byu.cs.tweeter.client.backgroundTask.GetStoryTask;
import edu.byu.cs.tweeter.client.backgroundTask.GetUserTask;
import edu.byu.cs.tweeter.client.backgroundTask.PostStatusTask;
import edu.byu.cs.tweeter.client.cache.Cache;
import edu.byu.cs.tweeter.client.view.main.MainActivity;
import edu.byu.cs.tweeter.client.view.main.story.StoryFragment;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;

public class StatusService extends Service {

    public interface GetFeedObserver extends ServiceObserver {
        void getFeedSucceeded(List<Status> statuses, boolean hasMorePages);
    }

    public interface GetStoryObserver extends ServiceObserver {
        void getStorySucceeded(List<Status> statuses, boolean hasMorePages);
    }

    public interface PostStatusObserver extends ServiceObserver {
        void postStatusSucceeded();
    }

    public void getFeed(AuthToken authToken, User targetUser, int limit, Status lastStatus,
                        GetFeedObserver observer) {
        GetFeedTask getFeedTask = new GetFeedTask(authToken,
                targetUser, limit, lastStatus, new GetFeedHandler(observer));
        executeTask(getFeedTask);
    }

    public void getStory(AuthToken authToken, User targetUser, int limit, Status lastStatus,
                         GetStoryObserver observer) {
        GetStoryTask getStoryTask = new GetStoryTask(authToken,
                targetUser, limit, lastStatus, new GetStoryHandler(Looper.getMainLooper(), observer));
        executeTask(getStoryTask);
    }

    public void postStatus(AuthToken authToken, Status newStatus, PostStatusObserver observer) {
        PostStatusTask statusTask = new PostStatusTask(authToken,
                newStatus, new PostStatusHandler(Looper.getMainLooper(), observer));
        executeTask(statusTask);
    }

    /**
     * Message handler (i.e., observer) for GetFeedTask.
     */
    private class GetFeedHandler extends ServiceHandler {

        private GetFeedObserver observer;
        private List<Status> statuses;
        private boolean hasMorePages;

        public GetFeedHandler(GetFeedObserver observer) {
            super(observer);
            this.observer = observer;
        }

        @Override
        public void handleSucceeded(Message msg) {
            this.statuses = (List<Status>) msg.getData().getSerializable(GetFeedTask.ITEMS_KEY);
            this.hasMorePages = msg.getData().getBoolean(GetFeedTask.MORE_PAGES_KEY);
            observer.getFeedSucceeded(statuses, hasMorePages);
        }
    }



    /**
     * Message handler (i.e., observer) for GetStoryTask.
     */
    private class GetStoryHandler extends ServiceHandler {

        private GetStoryObserver observer;
        private List<Status> statuses;
        private boolean hasMorePages;

        public GetStoryHandler(Looper looper, GetStoryObserver obvserver) {
            super(looper, obvserver);
            this.observer = obvserver;
        }

        public GetStoryHandler(GetStoryObserver observer) {
            super(observer);
            this.observer = observer;
        }

        @Override
        public void handleSucceeded(Message msg) {
            this.statuses = (List<Status>) msg.getData().getSerializable(GetStoryTask.ITEMS_KEY);
            this.hasMorePages = msg.getData().getBoolean(GetStoryTask.MORE_PAGES_KEY);
            observer.getStorySucceeded(statuses, hasMorePages);
        }
    }

    // PostStatusHandler

    private class PostStatusHandler extends ServiceHandler {

        private PostStatusObserver observer;

        public PostStatusHandler(Looper looper, PostStatusObserver observer) {
            super(looper, observer);
            this.observer = observer;
        }

        public PostStatusHandler(PostStatusObserver observer) {
            super(observer);
            this.observer = observer;
        }
        @Override
        public void handleSucceeded(Message msg) {
            observer.postStatusSucceeded();
        }
    }
}
