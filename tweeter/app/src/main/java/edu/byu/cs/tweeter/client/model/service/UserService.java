package edu.byu.cs.tweeter.client.model.service;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Base64;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import java.io.ByteArrayOutputStream;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import edu.byu.cs.tweeter.client.backgroundTask.GetUserTask;
import edu.byu.cs.tweeter.client.backgroundTask.LoginTask;
import edu.byu.cs.tweeter.client.backgroundTask.LogoutTask;
import edu.byu.cs.tweeter.client.backgroundTask.RegisterTask;
import edu.byu.cs.tweeter.client.cache.Cache;
import edu.byu.cs.tweeter.client.view.main.MainActivity;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;

public class UserService extends Service {

    public AuthenticationObserver authenticationObserver;

    public interface AuthenticationObserver extends ServiceObserver {
        void authenticationSucceeded(AuthToken authToken, User user);

    }

    public interface LogoutObserver extends ServiceObserver {
        void logoutSucceeded();
//        void logoutFailed(String message);
//        void logoutThrewException(Exception ex);
    }

    public interface GetUserObserver extends ServiceObserver {
        void getUserSucceeded(User user);
    }

    public void login(String alias, String password, AuthenticationObserver observer) {
        LoginTask loginTask = new LoginTask(alias,
                password, new LoginHandler(Looper.getMainLooper(), observer));
        executeTask(loginTask);
    }

    public void logout(LogoutObserver observer) {
        LogoutTask logoutTask = new LogoutTask(Cache.getInstance().getCurrUserAuthToken(), new LogoutHandler(observer));
        executeTask(logoutTask);
    }

    public void getUser(AuthToken authToken, String alias, GetUserObserver observer) {
        GetUserTask getUserTask = new GetUserTask(authToken, alias, new GetUserHandler(observer));
        executeTask(getUserTask);
    }

    public void register(String firstName, String lastName, String alias, String password,
                         ImageView imageToUpload, AuthenticationObserver observer) {
        //Register the user
        Bitmap image = ((BitmapDrawable) imageToUpload.getDrawable()).getBitmap();
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, bos);
        byte[] imageBytes = bos.toByteArray();
        String imageBytesBase64 = Base64.encodeToString(imageBytes, Base64.NO_WRAP);

        RegisterTask registerTask = new RegisterTask(firstName, lastName,
                alias, password, imageBytesBase64, new RegisterHandler(observer));
        executeTask(registerTask);
    }


    private class GetUserHandler extends ServiceHandler {

        private GetUserObserver observer;
        private User user;

        public GetUserHandler(GetUserObserver observer) {
            super(observer);
            this.observer = observer;
        }

        @Override
        public void handleSucceeded(Message msg) {
            this.user = (User) msg.getData().getSerializable(GetUserTask.USER_KEY);
            observer.getUserSucceeded(user);
        }
    }

    /**
     * Message handler (i.e., observer) for LoginTask
     */
    private class LoginHandler extends ServiceHandler {

        private AuthenticationObserver observer;
        private User loggedInUser;
        private AuthToken authToken;

        private LoginHandler(Looper looper, AuthenticationObserver observer) {
            super(looper, observer);
            this.observer = observer;
        }

        private LoginHandler(AuthenticationObserver observer) {
            super(observer);
            this.observer = observer;
        }

        @Override
        public void handleSucceeded(Message msg) {
            this.loggedInUser = (User) msg.getData().getSerializable(LoginTask.USER_KEY);
            this.authToken = (AuthToken) msg.getData().getSerializable(LoginTask.AUTH_TOKEN_KEY);
            // Cache user session information
            Cache.getInstance().setCurrUser(loggedInUser);
            Cache.getInstance().setCurrUserAuthToken(authToken);
            observer.authenticationSucceeded(authToken, loggedInUser);
        }


    }

    private class LogoutHandler extends ServiceHandler {

        private LogoutObserver observer;

        public LogoutHandler(LogoutObserver observer) {
            super(observer);
            this.observer = observer;
        }

        @Override
        public void handleSucceeded(Message msg) {
            observer.logoutSucceeded();
        }
    }


    private class RegisterHandler extends ServiceHandler {

        private AuthenticationObserver observer;
        private User registeredUser;
        private AuthToken authToken;

        private RegisterHandler(AuthenticationObserver observer) {
            super(observer);
            this.observer = observer;
        }


        @Override
        public void handleSucceeded(Message msg) {
            this.registeredUser = (User) msg.getData().getSerializable(RegisterTask.USER_KEY);
            this.authToken = (AuthToken) msg.getData().getSerializable(RegisterTask.AUTH_TOKEN_KEY);
            Cache.getInstance().setCurrUser(registeredUser);
            Cache.getInstance().setCurrUserAuthToken(authToken);
            observer.authenticationSucceeded(authToken, registeredUser);
        }
    }

}

