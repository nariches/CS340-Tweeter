package edu.byu.cs.tweeter.model.net.request;

import edu.byu.cs.tweeter.model.domain.AuthToken;

public class IsFollowerRequest {

    private AuthToken authToken;
    private String followerUsername;
    private String followeeUsername;

    public IsFollowerRequest() {}

    public IsFollowerRequest(AuthToken authToken, String followerUsername, String followeeUsername) {
        this.authToken = authToken;
        this.followerUsername = followerUsername;
        this.followeeUsername = followeeUsername;
    }

    public AuthToken getAuthToken() {
        return authToken;
    }

    public void setAuthToken(AuthToken authToken) {
        this.authToken = authToken;
    }

    public String getFollowerUsername() {
        return followerUsername;
    }

    public void setFollowerUsername(String followerUsername) {
        this.followerUsername = followerUsername;
    }

    public String getFolloweeUsername() {
        return followeeUsername;
    }

    public void setFolloweeUsername(String followeeUsername) {
        this.followeeUsername = followeeUsername;
    }
}
