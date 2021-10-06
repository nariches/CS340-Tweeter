package edu.byu.cs.tweeter.client.presenter;

import edu.byu.cs.tweeter.client.model.service.StatusService;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.Status;

public class StatusPresenter extends Presenter implements StatusService.PostStatusObserver {

    public interface StatusView extends Presenter.View {
        void displayStatus(); //???
    }

    private StatusView statusView;

    public StatusPresenter(StatusView view) {
        super(view);
        this.statusView = view;
    }

    public void postStatus(AuthToken authToken, Status status) {
        new StatusService().postStatus(authToken, status, this);
    }

    @Override
    public void postStatusSucceeded() {
        statusView.displayStatus();
    }

    @Override
    public String getDescription() {
        return "Status";
    }
}
