package edu.byu.cs.tweeter.client.view.presenter;

import static org.mockito.ArgumentMatchers.any;

import java.util.Arrays;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import edu.byu.cs.tweeter.client.cache.Cache;
import edu.byu.cs.tweeter.client.model.service.StatusService;
import edu.byu.cs.tweeter.client.presenter.StatusPresenter;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;

import edu.byu.cs.tweeter.client.model.service.StatusService;
import edu.byu.cs.tweeter.client.presenter.StoryPresenter;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.request.StoryRequest;
import edu.byu.cs.tweeter.model.net.response.StoryResponse;


public class StatusServiceTest {

    private StatusService.GetStoryObserver mockGetStoryObserver;
    private StatusPresenter.StatusView mockStoryView;
    private StatusService statusService;
    private StoryPresenter mockStoryPresenter;
    private AuthToken mockToken;
    private User currentUser;
    private StoryResponse expectedResponse;
    private FakeObserver fakeObserverSpy;
    private CountDownLatch countdownLatch;

    private void resetCountdownLatch() {
        countdownLatch = new CountDownLatch(1);
    }

    private void awaitCountdownLatch() throws InterruptedException {
        countdownLatch.await();
        resetCountdownLatch();
    }

    private class FakeObserver implements StatusService.GetStoryObserver {

        @Override
        public void getFailed(String message) {

        }

        @Override
        public void getThrewException(Exception ex) {

        }

        @Override
        public void getStorySucceeded(List<Status> statuses, boolean hasMorePages) {
            countdownLatch.countDown();
        }
    }

    @Before
    public void setup() {

        //mockStoryView = Mockito.mock(StatusPresenter.StatusView.class);
        statusService = new StatusService();
        mockToken = new AuthToken();
        FakeObserver mySpy = new FakeObserver();
        fakeObserverSpy = Mockito.spy(mySpy);

        resetCountdownLatch();

        currentUser = new User("FirstName", "LastName", null);
    }

    @Test
    public void testGetStory_Succeeded() throws InterruptedException {

        statusService.getStory(mockToken, currentUser, 3, null, fakeObserverSpy);

        awaitCountdownLatch();

        Mockito.verify(fakeObserverSpy).getStorySucceeded(Mockito.anyList(), Mockito.anyBoolean());
    }
}
