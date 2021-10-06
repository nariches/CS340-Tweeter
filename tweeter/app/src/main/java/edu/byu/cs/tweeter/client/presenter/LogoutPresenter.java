package edu.byu.cs.tweeter.client.presenter;

import edu.byu.cs.tweeter.client.model.service.Service;
import edu.byu.cs.tweeter.client.model.service.UserService;

public class LogoutPresenter extends Presenter implements UserService.LogoutObserver {

    public interface LogoutView extends Presenter.View {
        void logoutUser();
    }

    private LogoutView logoutView;

    public LogoutPresenter(LogoutView view) {
        super(view);
        this.logoutView = view;
    }

    public void logout() {
        new UserService().logout(this);
    }

    @Override
    public void logoutSucceeded() {
        logoutView.logoutUser();
    }

    @Override
    public String getDescription() {
        return "Logout";
    }

}
