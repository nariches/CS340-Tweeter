package edu.byu.cs.tweeter.model.net.request;

import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;

public class GetFollowersCountRequest {

    private String username;
    private AuthToken authToken;

    public GetFollowersCountRequest() {
    }

    public GetFollowersCountRequest(String username, AuthToken authToken) {
        this.username = username;
        this.authToken = authToken;
    }


    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public AuthToken getAuthToken() {
        return authToken;
    }

    public void setAuthToken(AuthToken authToken) {
        this.authToken = authToken;
    }
}


