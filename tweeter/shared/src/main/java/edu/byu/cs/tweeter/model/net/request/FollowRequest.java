package edu.byu.cs.tweeter.model.net.request;

import edu.byu.cs.tweeter.model.domain.AuthToken;

public class FollowRequest {

    private AuthToken authToken;
    private String currUsername;
    private String followeeUsername;

    public FollowRequest() {}

    public FollowRequest(AuthToken authToken, String currUsername, String followeeUsername) {
        this.authToken = authToken;
        this.currUsername = currUsername;
        this.followeeUsername = followeeUsername;
    }

    public AuthToken getAuthToken() {
        return authToken;
    }

    public void setAuthToken(AuthToken authToken) {
        this.authToken = authToken;
    }

    public String getCurrUsername() {
        return currUsername;
    }

    public void setCurrUsername(String currUsername) {
        this.currUsername = currUsername;
    }

    public String getFolloweeUsername() {
        return followeeUsername;
    }

    public void setFolloweeUsername(String followeeUsername) {
        this.followeeUsername = followeeUsername;
    }
}
