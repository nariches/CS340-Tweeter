package edu.byu.cs.tweeter.client.backgroundTask;

import android.os.Bundle;
import android.os.Handler;

import java.util.Random;

import edu.byu.cs.tweeter.client.model.net.ServerFacade;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.request.IsFollowerRequest;
import edu.byu.cs.tweeter.model.net.response.IsFollowerResponse;

/**
 * Background task that determines if one user is following another.
 */
public class IsFollowerTask extends AuthorizedTask {

    public static final String IS_FOLLOWER_KEY = "is-follower";

    /**
     * The alleged follower.
     */
    private final User follower;

    /**
     * The alleged followee.
     */
    private final User followee;

    private boolean isFollower;

    public IsFollowerTask(AuthToken authToken, User follower, User followee, Handler messageHandler) {
        super(authToken, messageHandler);
        this.follower = follower;
        this.followee = followee;
    }

    @Override
    protected void runTask() {
        IsFollowerRequest isFollowerRequest = new IsFollowerRequest(authToken, follower.getAlias(),
                followee.getAlias());

        try {
            IsFollowerResponse isFollowerResponse = new ServerFacade().isFollower(isFollowerRequest,
                    "/isfollower");
            if (!isFollowerResponse.isSuccess()) {
                sendFailedMessage(isFollowerResponse.getMessage());
            }
            else {
                isFollower = isFollowerResponse.isFollowerBool();

            }
        }
        catch (Exception e) {
            sendExceptionMessage(e);
        }



        //isFollower = new Random().nextInt() > 0;
    }

    @Override
    protected void loadBundle(Bundle msgBundle) {
        System.out.println("IS_FOLLOWER_KEY1: " + IS_FOLLOWER_KEY);
        System.out.println("isFollower1: " + isFollower);
        msgBundle.putBoolean(IS_FOLLOWER_KEY, isFollower);
        System.out.println("IS_FOLLOWER_KEY2: " + IS_FOLLOWER_KEY);
        System.out.println("isFollower2: " + isFollower);
    }
}
