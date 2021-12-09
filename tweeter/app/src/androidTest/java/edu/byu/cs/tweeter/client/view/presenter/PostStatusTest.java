package edu.byu.cs.tweeter.client.view.presenter;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.util.Arrays;

import edu.byu.cs.tweeter.client.cache.Cache;
import edu.byu.cs.tweeter.client.model.service.StatusService;
import edu.byu.cs.tweeter.client.presenter.StatusPresenter;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;

public class PostStatusTest {
    private StatusPresenter.StatusView mockStatusView;
    private StatusService mockStatusService;
    //private Cache mockCache;

    private StatusPresenter statusPresenterSpy;

    private AuthToken mockAuthToken;
    private Status mockStatus;
    private StatusService.PostStatusObserver mockPostStatusObserver;
    private User user;

    @Before
    public void setup() {
        mockStatusView = Mockito.mock(StatusPresenter.StatusView.class);
        mockStatusService = Mockito.mock(StatusService.class);
        //mockCache = Mockito.mock(Cache.class); //not needed?

        user = new User();

        mockAuthToken = new AuthToken("test", "test");
        mockStatus = new Status("test", user, "datetime", Arrays.asList(""), Arrays.asList(""));
        //mockPostStatusObserver = new StatusService.PostStatusObserver();
        statusPresenterSpy = Mockito.spy(new StatusPresenter(mockStatusView));
        Mockito.doReturn(mockStatusService).when(statusPresenterSpy).getStatusService();
        //Mockito.doReturn(mockAuthToken).when(statusPresenterSpy).getAuthToken();
    }

    @Test
    public void testPostStatus_postStatusSucceeds() {

        // Setup the test case
        Answer<Void> postStatusSucceededAnswer = new Answer<Void>() {
            @Override
            public Void answer(InvocationOnMock invocation) throws Throwable {
                StatusService.PostStatusObserver observer = invocation.getArgument(2);
                observer.postStatusSucceeded();
                return null;
            }
        };

        Mockito.doAnswer(postStatusSucceededAnswer).when(mockStatusService).
                postStatus(Mockito.any(), Mockito.any(), Mockito.any());

        // Run the test case

        statusPresenterSpy.postStatus(any(), any());

        Mockito.verify(mockStatusView).displayInfoMessage("Posting Status...");
        Mockito.verify(mockStatusView).displayStatus();
    }

    @Test
    public void testPostStatus_postStatusFails() {
// Setup the test case
        Answer<Void> postStatusFailedAnswer = new Answer<Void>() {
            @Override
            public Void answer(InvocationOnMock invocation) throws Throwable {
                StatusService.PostStatusObserver observer = invocation.getArgument(2);
                observer.getFailed("Post status failed message");
                return null;
            }
        };

        Mockito.doAnswer(postStatusFailedAnswer).when(mockStatusService).
                postStatus(any(), any(), any());

        // Run the test case

        statusPresenterSpy.postStatus(any(), any());

        Mockito.verify(statusPresenterSpy).postStatus(any(), any());
        Mockito.verify(mockStatusView).displayInfoMessage("Posting Status...");
        Mockito.verify(mockStatusView).displayErrorMessage("Status failed: Post status failed message");
    }

    @Test
    public void testPostStatus_postStatusThrowsException() {
// Setup the test case
        Answer<Void> postStatusThrewExceptionAnswer = new Answer<Void>() {
            @Override
            public Void answer(InvocationOnMock invocation) throws Throwable {
                StatusService.PostStatusObserver observer = invocation.getArgument(2);
                observer.getFailed("Post status threw exception message");
                return null;
            }
        };

        Mockito.doAnswer(postStatusThrewExceptionAnswer).when(mockStatusService).
                postStatus(any(), any(), any());

        // Run the test case

        statusPresenterSpy.postStatus(any(), any());

        Mockito.verify(statusPresenterSpy).postStatus(any(), any());
        Mockito.verify(mockStatusView).displayInfoMessage("Posting Status...");
        Mockito.verify(mockStatusView).displayErrorMessage("Status failed: Post status threw exception message");
    }
}
