package edu.byu.cs.tweeter.client.presenter;

import android.view.View;

import edu.byu.cs.tweeter.client.model.service.Service;

public abstract class Presenter implements Service.ServiceObserver {

    public interface View {
        void displayInfoMessage(String message);
        void displayErrorMessage(String message);
    }

    private View view;

    Presenter(View view) {
        this.view = view;
    }

    @Override
    public void getFailed(String message) {
        this.view.displayErrorMessage(getDescription() + " failed: " + message);
    }

    @Override
    public void getThrewException(Exception ex) {
        this.view.displayErrorMessage(getDescription() + " threw exception: " + ex.getMessage());
    }

    public abstract String getDescription();


}
