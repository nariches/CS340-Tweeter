package edu.byu.cs.tweeter.client.view.presenter;

import org.junit.Before;
import org.junit.Test;

import edu.byu.cs.tweeter.client.model.net.ServerFacade;
import edu.byu.cs.tweeter.model.net.request.RegisterRequest;
import edu.byu.cs.tweeter.model.net.response.RegisterResponse;

public class RegisterIntegrationTest {

    ServerFacade serverFacade;

    @Before
    public void setup() {
        this.serverFacade = new ServerFacade();
    }

    @Test
    public void RegisterIntegrationSucceeded() {
        RegisterRequest request = new RegisterRequest("fake", "fake", "fake", "fake", null);
        String urlPath = "/register";
        try {
            RegisterResponse response = this.serverFacade.register(request, urlPath);
            assert (response.isSuccess());
        } catch (Exception e) {
            System.out.println("Exception caught!");
        }
    }

    @Test
    public void RegisterIntegrationFailed() {
        RegisterRequest request = new RegisterRequest("fake", "fake", "fake", "fake", null);
        String urlPath = "/notreal";
        try {
            RegisterResponse response = this.serverFacade.register(request, urlPath);
            assert (!response.isSuccess());
        } catch (Exception e) {
            System.out.println("Exception caught!");
        }
    }
}
