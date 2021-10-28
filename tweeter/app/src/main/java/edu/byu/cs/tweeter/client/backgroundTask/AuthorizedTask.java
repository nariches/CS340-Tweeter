package edu.byu.cs.tweeter.client.backgroundTask;

import android.os.Handler;

import edu.byu.cs.tweeter.model.domain.AuthToken;

public abstract class AuthorizedTask extends BackgroundTask {
    /**
     * Auth token for logged-in user.
     * This user is the "follower" in the relationship.
     */
    protected final AuthToken authToken; //Changed from private to protected

    protected AuthorizedTask(AuthToken authToken, Handler messageHandler) {
        super(messageHandler);
        this.authToken = authToken;
    }
}
