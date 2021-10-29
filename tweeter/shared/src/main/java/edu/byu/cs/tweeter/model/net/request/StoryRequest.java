package edu.byu.cs.tweeter.model.net.request;

import edu.byu.cs.tweeter.model.domain.AuthToken;

public class StoryRequest {

    private AuthToken authToken;
    private String username;
    private int limit;
    private String lastStatusDatetime;

    private StoryRequest() {}

    public StoryRequest(AuthToken authToken, String username, int limit, String lastStatusDatetime) {
        this.authToken = authToken;
        this.username = username;
        this.limit = limit;
        this.lastStatusDatetime = lastStatusDatetime;
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

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }

    public String getLastStatusDatetime() {
        return lastStatusDatetime;
    }

    public void setLastStatusDatetime(String lastStatusDatetime) {
        this.lastStatusDatetime = lastStatusDatetime;
    }
}
