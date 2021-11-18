package edu.byu.cs.tweeter.model.net.request;

import edu.byu.cs.tweeter.model.domain.AuthToken;

public class UnfollowRequest {

    private AuthToken authToken;
    private String currUsername;
    private String unfolloweeUsername;

    public UnfollowRequest() {}

    public UnfollowRequest(AuthToken authToken, String currUsername, String unfolloweeUsername) {
        this.authToken = authToken;
        this.currUsername = currUsername;
        this.unfolloweeUsername = unfolloweeUsername;
    }

    public AuthToken getAuthToken() {
        return authToken;
    }

    public void setAuthToken(AuthToken authToken) {
        this.authToken = authToken;
    }

    public String getUnfolloweeUsername() {
        return unfolloweeUsername;
    }

    public void setUnfolloweeUsername(String username) {
        this.unfolloweeUsername = username;
    }

    public String getCurrUsername() {
        return currUsername;
    }

    public void setCurrUsername(String currUsername) {
        this.currUsername = currUsername;
    }
}
