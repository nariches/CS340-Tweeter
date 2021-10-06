package edu.byu.cs.tweeter.client.presenter;

import android.view.View;

import java.util.List;

import edu.byu.cs.tweeter.client.model.service.FollowService;
import edu.byu.cs.tweeter.client.model.service.Service;
import edu.byu.cs.tweeter.client.model.service.UserService;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;

public abstract class PagedPresenter<T> extends Presenter implements UserService.GetUserObserver, Service.PagedObserver<T> {

    private static final int PAGE_SIZE = 10;
    private User targetUser;
    private AuthToken authToken;
    private T lastItem;
    private boolean hasMorePages = true;
    private boolean isLoading = false;
    private boolean isGettingUser;
    private PagedView pagedView;

    public interface PagedView<T> extends Presenter.View {
        void navigateToUser(User user);
        void setLoading(boolean value);
        void addItems(List<T> items);
    }

    public PagedPresenter(PagedView view, AuthToken authToken, User user) {
        super(view);
        this.pagedView = view;
        this.authToken = authToken;
        this.targetUser = user;
    }

    @Override
    public void getUserSucceeded(User user) {
        pagedView.navigateToUser(user);
    }

    public void loadMoreItems() {
        if (!isLoading && hasMorePages) {
            isLoading = true;
            pagedView.setLoading(true);
            getItems(authToken, targetUser, PAGE_SIZE, lastItem);
        }
    }

    @Override
    public void getItemsSucceeded(boolean hasMorePages, T lastItem) {
        pagedView.setLoading(false);
        this.hasMorePages = hasMorePages;
        this.isLoading = false;
    }

    public void getUser(String alias) {
        pagedView.displayInfoMessage("Getting user's profile...");
        new UserService().getUser(authToken, alias, this);
    }

    public abstract void getItems(AuthToken authToken, User targetUser, int pageSize, T lastItem);
}
