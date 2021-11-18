package edu.byu.cs.tweeter.client.backgroundTask;

import android.os.Bundle;
import android.os.Handler;

import edu.byu.cs.tweeter.client.model.net.ServerFacade;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.request.FollowRequest;
import edu.byu.cs.tweeter.model.net.response.FollowResponse;

/**
 * Background task that establishes a following relationship between two users.
 */
public class FollowTask extends AuthorizedTask {
    /**
     * The user that is being followed.
     */
    private final User currUser;
    private final User followee;

    public FollowTask(AuthToken authToken, User currUser, User followee, Handler messageHandler) {
        super(authToken, messageHandler);
        this.currUser = currUser;
        this.followee = followee;
    }

    @Override
    protected void runTask() {
        FollowRequest followRequest = new FollowRequest(authToken, currUser.getAlias(), followee.getAlias());

        try {
            FollowResponse followResponse = new ServerFacade().follow(followRequest, "/follow");
            if (!followResponse.isSuccess()) {
                sendFailedMessage(followResponse.getMessage());
            }
        }
        catch (Exception e) {
            sendExceptionMessage(e);
        }

        // We could do this from the presenter, without a task and handler, but we will
        // eventually access the database from here when we aren't using dummy data.
    }

    @Override
    protected void loadBundle(Bundle msgBundle) {
        // Nothing to load
    }
}
