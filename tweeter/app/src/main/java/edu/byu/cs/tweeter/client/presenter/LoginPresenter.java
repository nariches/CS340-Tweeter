package edu.byu.cs.tweeter.client.presenter;

import edu.byu.cs.tweeter.client.model.service.UserService;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;

public class LoginPresenter extends Presenter implements UserService.AuthenticationObserver {

    public interface LoginView extends Presenter.View {
        void navigateToUser(User user);
        void clearErrorMessage();
        void clearInfoMessage();
    }

    private LoginView loginView;

    public LoginPresenter(LoginView view) {
        super(view);
        this.loginView = view;
    }

    public void login(String alias, String password) {

        loginView.clearInfoMessage();
        loginView.clearErrorMessage();
        String message = validateLogin(alias, password);
        if (message == null) {
            loginView.displayInfoMessage("Logging in...");
            new UserService().login(alias, password, this);
        }
        else {
            loginView.displayErrorMessage("Login failed: " + message);
        }
    }

    private String validateLogin(String alias, String password) {
        if (alias.charAt(0) != '@') {
            return "Alias must begin with @.";
        }
        if (alias.length() < 2) {
            return "Alias must contain 1 or more characters after the @.";
        }
        if (password.length() == 0) {
            return "Password cannot be empty.";
        }
        return null;
    }

    @Override
    public void authenticationSucceeded(AuthToken authToken, User user) {
        loginView.navigateToUser(user);
        loginView.clearErrorMessage();
        loginView.displayInfoMessage("Hello, " + user.getName());
    }

    @Override
    public String getDescription() {
        return "Login";
    }

}
