package edu.byu.cs.tweeter.client.view.presenter;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import edu.byu.cs.tweeter.client.cache.Cache;
import edu.byu.cs.tweeter.client.model.service.StatusService;
import edu.byu.cs.tweeter.client.model.service.UserService;
import edu.byu.cs.tweeter.client.presenter.LoginPresenter;
import edu.byu.cs.tweeter.client.presenter.StatusPresenter;
import edu.byu.cs.tweeter.client.presenter.StoryPresenter;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.request.LoginRequest;
import edu.byu.cs.tweeter.model.net.request.PostStatusRequest;
import edu.byu.cs.tweeter.model.net.response.LoginResponse;
import edu.byu.cs.tweeter.model.net.response.PostStatusResponse;

public class PostStoryTest {

    private PostStatusRequest request;
    private PostStatusResponse expectedResponse;
    private LoginPresenter loginPresenterSpy;
    private LoginPresenter.LoginView mockLoginView;
    private StatusPresenter statusPresenterSpy;
    private StoryPresenter storyPresenterSpy;
    private StatusPresenter.StatusView mockStatusView;
    private StoryPresenter.StoryView mockStoryView;
    private User currentUser;
    private Status testStatus;
    private StatusService mockStatusService;
    private StoryPresenter mockStoryPresenter;

    public int findUrlEndIndex(String word) {
        if (word.contains(".com")) {
            int index = word.indexOf(".com");
            index += 4;
            return index;
        } else if (word.contains(".org")) {
            int index = word.indexOf(".org");
            index += 4;
            return index;
        } else if (word.contains(".edu")) {
            int index = word.indexOf(".edu");
            index += 4;
            return index;
        } else if (word.contains(".net")) {
            int index = word.indexOf(".net");
            index += 4;
            return index;
        } else if (word.contains(".mil")) {
            int index = word.indexOf(".mil");
            index += 4;
            return index;
        } else {
            return word.length();
        }
    }

    @Before
    public void setup() throws ParseException {
        currentUser = new User("person2", "person2", "@person2", null);

        String postMessage = "Hi from PostStoryTest";
        List<String> containedMentions = new ArrayList<>();

        for (String word : postMessage.split("\\s")) {
            if (word.startsWith("@")) {
                word = word.replaceAll("[^a-zA-Z0-9]", "");
                word = "@".concat(word);

                containedMentions.add(word);
            }
        }

        List<String> containedUrls = new ArrayList<>();
        for (String word : postMessage.split("\\s")) {
            if (word.startsWith("http://") || word.startsWith("https://")) {

                int index = findUrlEndIndex(word);

                word = word.substring(0, index);

                containedUrls.add(word);
            }
        }

        SimpleDateFormat userFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        SimpleDateFormat statusFormat = new SimpleDateFormat("MMM d yyyy h:mm aaa");
        String dateTime = statusFormat.format(userFormat.parse(LocalDate.now().toString() + " " + LocalTime.now().toString().substring(0, 8)));
        testStatus = new Status(postMessage, currentUser, dateTime, containedUrls, containedMentions);
        mockLoginView = Mockito.mock(LoginPresenter.LoginView.class);
        loginPresenterSpy = Mockito.spy(new LoginPresenter(mockLoginView));
        mockStatusView = Mockito.mock(StatusPresenter.StatusView.class);
        mockStoryView = Mockito.mock(StoryPresenter.StoryView.class);
        statusPresenterSpy = Mockito.spy(new StatusPresenter(mockStatusView));
        //mockStatusService = Mockito.mock(StatusService.class);
        mockStoryPresenter = Mockito.mock(StoryPresenter.class);
        //Mockito.doReturn(mockStatusService).when(statusPresenterSpy).getStatusService();
    }

    @Test
    public void testPostStory_succeeded() throws Exception {

        Answer<Void> postStatusSucceededAnswer = new Answer<Void>() {
            @Override
            public Void answer(InvocationOnMock invocation) throws Throwable {
                StatusService.PostStatusObserver observer = invocation.getArgument(2);
                observer.postStatusSucceeded();
                return null;
            }
        };

        Answer<Void> getStorySucceededAnswer = new Answer<Void>() {
            @Override
            public Void answer(InvocationOnMock invocation) throws Throwable {
                List<Status> story = invocation.getArgument(0);
                for (Status status: story) {
                    if (status.equals(testStatus)) {
                        return null;
                    }
                }
                Assert.fail();
                return null;
            }
        };

//        Mockito.doAnswer(postStatusSucceededAnswer).when(mockStatusService).
//                postStatus(Mockito.any(), Mockito.any(), Mockito.any());

        Mockito.doAnswer(postStatusSucceededAnswer).when(statusPresenterSpy).postStatus(Mockito.any(), Mockito.any());

        Mockito.doAnswer(getStorySucceededAnswer).when(mockStoryPresenter).getStorySucceeded(Mockito.any(), Mockito.anyBoolean());

        loginPresenterSpy.login(currentUser.getAlias(), "a");
        Thread.sleep(20*1000);
        AuthToken authToken = Cache.getInstance().getCurrUserAuthToken();
        statusPresenterSpy.postStatus(authToken, testStatus);


        Mockito.verify(mockStatusView).displayInfoMessage("Posting Status...");
        Mockito.verify(mockStatusView).displayStatus();

        storyPresenterSpy = Mockito.spy(new StoryPresenter(mockStoryView, authToken, currentUser));

        storyPresenterSpy.getItems(authToken, currentUser, 10, null);
        Thread.sleep(5*1000);

    }
}
