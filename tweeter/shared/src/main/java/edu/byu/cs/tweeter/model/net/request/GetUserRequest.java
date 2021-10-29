package edu.byu.cs.tweeter.model.net.request;

import edu.byu.cs.tweeter.model.domain.AuthToken;

public class GetUserRequest {

    private AuthToken authToken;
    private String username;

    public GetUserRequest() {}

    public GetUserRequest(AuthToken authToken, String username) {
        this.authToken = authToken;
        this.username = username;
    }

    public AuthToken getAuthToken() {
        return authToken;
    }

    public void setAuthToken(AuthToken authToken) {
        this.authToken = authToken;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
