package edu.byu.cs.tweeter.server.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Matchers.any;

import com.amazonaws.services.dynamodbv2.xspec.S;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.request.FollowingRequest;
import edu.byu.cs.tweeter.model.net.request.StoryRequest;
import edu.byu.cs.tweeter.model.net.response.FollowingResponse;
import edu.byu.cs.tweeter.model.net.response.StoryResponse;
import edu.byu.cs.tweeter.server.dao.AuthTokenDAO;
import edu.byu.cs.tweeter.server.dao.FollowsDAO;
import edu.byu.cs.tweeter.server.dao.StoryDAO;
import edu.byu.cs.tweeter.server.dao.UserDAO;

public class StoryPagesTest {

    private StoryRequest request;
    private StoryResponse expectedResponse;
    private StoryDAO mockStoryDAO;
    private AuthTokenDAO mockAuthTokenDAO;
    private AWSFactory mockAWSFactory;
    private StatusService statusServiceSpy;
    private AuthToken authToken;
    private User currentUser;
    private List<Status> statusList;

    @BeforeEach
    public void setup() throws Exception {
        authToken = new AuthToken("test", "test");
        currentUser = new User("FirstName", "LastName", null);
        request = new StoryRequest(authToken, currentUser.getAlias(), 10, null);
        mockStoryDAO = Mockito.mock(StoryDAO.class);
        mockAuthTokenDAO = Mockito.mock(AuthTokenDAO.class);
        mockAWSFactory = Mockito.mock(AWSFactory.class);
        statusServiceSpy = Mockito.spy(new StatusService(mockAWSFactory));

        List<String> urlList = Arrays.asList("www.test.com", "www.fake.com");
        List<String> mentionsList = Arrays.asList("@someone", "@person");

        Status status1 = new Status("This is a post1", currentUser, "1000", urlList, mentionsList);
        Status status2 = new Status("This is a post2", currentUser, "2000", urlList, mentionsList);
        Status status3 = new Status("This is a post3", currentUser, "3000", urlList, mentionsList);
        Status status4 = new Status("This is a post4", currentUser, "4000", urlList, mentionsList);
        Status status5 = new Status("This is a post5", currentUser, "5000", urlList, mentionsList);
        Status status6 = new Status("This is a post6", currentUser, "6000", urlList, mentionsList);
        Status status7 = new Status("This is a post7", currentUser, "7000", urlList, mentionsList);
        Status status8 = new Status("This is a post8", currentUser, "8000", urlList, mentionsList);
        Status status9 = new Status("This is a post9", currentUser, "9000", urlList, mentionsList);
        Status status10 = new Status("This is a post10", currentUser, "10000", urlList, mentionsList);
        Status status11 = new Status("This is a post11", currentUser, "11000", urlList, mentionsList);
        Status status12 = new Status("This is a post12", currentUser, "12000", urlList, mentionsList);

        statusList = Arrays.asList(status1, status2, status3, status4, status5, status6, status7,
                status8, status9, status10, status11, status12);

        Mockito.doReturn(mockAuthTokenDAO).when(mockAWSFactory).getAuthTokenDAO();
        Mockito.doReturn(authToken).when(mockAuthTokenDAO).getAuthToken(Mockito.any());
        Mockito.doReturn(mockStoryDAO).when(mockAWSFactory).getStoryDAO();
        Mockito.doReturn(true).when(statusServiceSpy).authenticated(Mockito.any());

    }

    @Test
    public void testGetStory_noPosts() throws Exception {
        List<Status> emptyStory = Collections.emptyList();
        expectedResponse = new StoryResponse(emptyStory, false);
        Mockito.doReturn(expectedResponse).when(mockStoryDAO)
                .getStory(currentUser.getAlias(), null, 10);
        StoryResponse response = statusServiceSpy.getStory(request);
        assertEquals(response, expectedResponse);
    }

    @Test
    public void testGetStory_3Posts() throws Exception {
        List<Status> story3Posts = Arrays.asList(statusList.get(0), statusList.get(1), statusList.get(2));
        expectedResponse = new StoryResponse(story3Posts, false);
        Mockito.doReturn(expectedResponse).when(mockStoryDAO).getStory(currentUser.getAlias(), null, 10);
        StoryResponse response = statusServiceSpy.getStory(request);
        assertEquals(response, expectedResponse);
    }

    @Test
    public void testGetStory_12Posts() throws Exception {
        List<Status> story12Posts = statusList;
        expectedResponse = new StoryResponse(story12Posts, false);
        Mockito.doReturn(expectedResponse).when(mockStoryDAO).getStory(currentUser.getAlias(), null, 10);
        StoryResponse response = statusServiceSpy.getStory(request);
        assertEquals(response, expectedResponse);
    }

}
