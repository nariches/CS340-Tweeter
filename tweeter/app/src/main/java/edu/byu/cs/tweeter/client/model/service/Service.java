package edu.byu.cs.tweeter.client.model.service;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import androidx.annotation.NonNull;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import edu.byu.cs.tweeter.client.backgroundTask.BackgroundTask;
import edu.byu.cs.tweeter.client.backgroundTask.GetFollowingTask;
import edu.byu.cs.tweeter.client.backgroundTask.LoginTask;
import edu.byu.cs.tweeter.model.domain.User;

public abstract class Service<T extends Runnable> {

    public interface ServiceObserver {
        void getFailed(String message);
        void getThrewException(Exception ex);
    }

    public interface PagedObserver<T> extends ServiceObserver {
        void getItemsSucceeded(boolean hasMorePages, T lastItem);
    }

    public void executeTask(T task) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(task);
    }

    public void executeCountTask(T task) {
        ExecutorService executor = Executors.newFixedThreadPool(2);
        executor.execute(task);
    }



    protected abstract class ServiceHandler extends Handler {

        private ServiceObserver observer;

        public ServiceHandler(Looper looper, ServiceObserver observer) {
            super(looper);
            this.observer = observer;
        }

        public ServiceHandler(ServiceObserver observer) {
            this.observer = observer;
        }

        @Override
        public void handleMessage(@NonNull Message msg) {

            boolean success = msg.getData().getBoolean(BackgroundTask.SUCCESS_KEY);
            if (success) {
                handleSucceeded(msg);
            } else if (msg.getData().containsKey(BackgroundTask.MESSAGE_KEY)) {
                String message = msg.getData().getString(BackgroundTask.MESSAGE_KEY);
                observer.getFailed(message);
            } else if (msg.getData().containsKey(BackgroundTask.EXCEPTION_KEY)) {
                Exception ex = (Exception) msg.getData().getSerializable(BackgroundTask.EXCEPTION_KEY);
                observer.getThrewException(ex);
            }
        }
        public abstract void handleSucceeded(Message msg);
    }
}
