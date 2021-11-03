package edu.byu.cs.tweeter.client.view.presenter;

import static org.junit.Assert.assertNotNull;

import org.junit.Before;
import org.junit.Test;

import edu.byu.cs.tweeter.client.model.net.ServerFacade;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.net.request.FollowersRequest;
import edu.byu.cs.tweeter.model.net.request.RegisterRequest;
import edu.byu.cs.tweeter.model.net.response.FollowersResponse;
import edu.byu.cs.tweeter.model.net.response.RegisterResponse;

public class GetFollowersIntegrationTest {

    ServerFacade serverFacade;
    AuthToken fakeAuth;

    @Before
    public void setup() {
        this.serverFacade = new ServerFacade();
        this.fakeAuth = new AuthToken();
    }

    @Test
    public void GetFollowersIntegrationSucceeded() {
        FollowersRequest request = new FollowersRequest(fakeAuth, "fake", 10, "fake");
        String urlPath = "/getfollowers";
        try {
            FollowersResponse response = this.serverFacade.getFollowers(request, urlPath);
            assert (response.isSuccess());
            assertNotNull (response.getFollowers());
        } catch (Exception e) {
            System.out.println("Exception caught!");
        }
    }

    @Test
    public void GetFollowersIntegrationFailed() {
        FollowersRequest request = new FollowersRequest(fakeAuth, "fake", 10, "fake");
        String urlPath = "/notreal";
        try {
            FollowersResponse response = this.serverFacade.getFollowers(request, urlPath);
            assert (!response.isSuccess());
        } catch (Exception e) {
            System.out.println("Exception caught!");
        }
    }
}
