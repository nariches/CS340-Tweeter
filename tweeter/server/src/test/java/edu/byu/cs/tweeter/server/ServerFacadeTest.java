package edu.byu.cs.tweeter.server;

import static org.mockito.ArgumentMatchers.any;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.io.IOException;

import edu.byu.cs.tweeter.client.backgroundTask.GetFollowingCountTask;
import edu.byu.cs.tweeter.client.model.net.ServerFacade;
import edu.byu.cs.tweeter.client.model.service.FollowService;
import edu.byu.cs.tweeter.model.net.TweeterRemoteException;

public class ServerFacadeTest {
    private GetFollowingCountTask mockGetFollowingCountTask;
    private FollowService.GetFollowingCountHandler mockGetFollowingCountHandler;
    private ServerFacade serverFacadeSpy;

    @Before
    public void setup() {
        mockGetFollowingCountTask = Mockito.mock(GetFollowingCountTask.class);
        mockGetFollowingCountHandler = Mockito.mock(FollowService.GetFollowingCountHandler.class);


        //define authToken
        //define User
        serverFacadeSpy = Mockito.spy(new ServerFacade());

    }

    @Test
    public void testServerFacade_getFollowingCountSucceeds() throws IOException, TweeterRemoteException {

        //Setup test case
        Answer<Void> getFollowingCountSucceedsAnswer = new Answer<Void>() {
            @Override
            public Void answer(InvocationOnMock invocation) throws Throwable {
                mockGetFollowingCountTask.run();
                return null;
            }
        };

        Mockito.doAnswer(getFollowingCountSucceedsAnswer).when(mockGetFollowingCountTask).
                run();

        serverFacadeSpy.getFollowingCount(any(), any());

        Mockito.verify(mockGetFollowingCountTask).run();
    }
}
