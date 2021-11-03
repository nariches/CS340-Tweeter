package edu.byu.cs.tweeter.client.view.presenter;

import static org.junit.Assert.assertNotNull;

import org.junit.Before;
import org.junit.Test;

import edu.byu.cs.tweeter.client.model.net.ServerFacade;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.net.request.GetFollowingCountRequest;
import edu.byu.cs.tweeter.model.net.request.RegisterRequest;
import edu.byu.cs.tweeter.model.net.response.GetFollowingCountResponse;
import edu.byu.cs.tweeter.model.net.response.RegisterResponse;

public class GetFollowingCountIntegrationTest {

    ServerFacade serverFacade;
    AuthToken fakeToken;

    @Before
    public void setup() {
        this.serverFacade = new ServerFacade();
        this.fakeToken = new AuthToken();
    }

    @Test
    public void RegisterIntegrationSucceeded() {
        GetFollowingCountRequest request = new GetFollowingCountRequest("fake", fakeToken);
        String urlPath = "/getfollowingcount";
        try {
            GetFollowingCountResponse response = this.serverFacade.getFollowingCount(request, urlPath);
            assert (response.isSuccess());
            assertNotNull (response.getFollowingCount());
        } catch (Exception e) {
            System.out.println("Exception caught!");
        }
    }

    @Test
    public void RegisterIntegrationFailed() {
        GetFollowingCountRequest request = new GetFollowingCountRequest("fake", fakeToken);
        String urlPath = "/notreal";
        try {
            GetFollowingCountResponse response = this.serverFacade.getFollowingCount(request, urlPath);
            assert (!response.isSuccess());
        } catch (Exception e) {
            System.out.println("Exception caught!");
        }
    }
}
