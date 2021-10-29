package edu.byu.cs.tweeter.model.net.response;

import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.Status;

public class PostStatusResponse extends Response {

    private AuthToken authToken;
    private Status status;

    public PostStatusResponse(String message) {
        super(false, message);
    }

    public PostStatusResponse(AuthToken authToken, Status status) {
        super(true, null);
        this.authToken = authToken;
        this.status = status;
    }

    public AuthToken getAuthToken() {
        return authToken;
    }

    public Status getStatus() {
        return status;
    }
}
