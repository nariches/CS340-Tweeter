package edu.byu.cs.tweeter.client.presenter;

import android.graphics.Bitmap;
import android.widget.ImageView;

import edu.byu.cs.tweeter.client.model.service.UserService;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;

public class RegisterPresenter extends Presenter implements UserService.AuthenticationObserver {

    public interface RegisterView extends Presenter.View {
        void navigateToUser(User user);
        void clearErrorMessage();
        void clearInfoMessage();
    }

    private RegisterView registerView;

    public RegisterPresenter(RegisterView view) {
        super(view);
        this.registerView = view;
    }

    public void register(String firstName, String lastName, String alias, String password,
                         ImageView imageToUpload) {
        registerView.clearErrorMessage();
        registerView.clearInfoMessage();
        String message = validateRegister(firstName, lastName, alias, password, imageToUpload);
        if (message == null) {
            registerView.displayInfoMessage("Registering...");
            new UserService().register(firstName, lastName, alias, password,
                    imageToUpload, this);
        }
        else {
            registerView.displayErrorMessage("Register failed: " + message);
        }
    }

    public String validateRegister(String firstName, String lastName, String alias, String password,
                                 ImageView imageToUpload) {
        if (firstName.length() == 0) {
            return "First Name cannot be empty.";
        }
        if (lastName.length() == 0) {
            return "Last Name cannot be empty.";
        }
        if (alias.length() == 0) {
            return "Alias cannot be empty.";
        }
        if (alias.charAt(0) != '@') {
            return "Alias must begin with @.";
        }
        if (alias.length() < 2) {
            throw new IllegalArgumentException("Alias must contain 1 or more characters after the @.");
        }
        if (password.length() == 0) {
            throw new IllegalArgumentException("Password cannot be empty.");
        }

        if (imageToUpload.getDrawable() == null) {
            throw new IllegalArgumentException("Profile image must be uploaded.");
        }
        return null;
    }
    @Override
    public void authenticationSucceeded(AuthToken authToken, User user) {
        registerView.navigateToUser(user);
        registerView.clearErrorMessage();
        registerView.displayInfoMessage("Hello, " + user.getName());
    }

    @Override
    public String getDescription() {
        return "Register";
    }

}
