package edu.byu.cs.tweeter.client.backgroundTask;

import android.os.Handler;

import edu.byu.cs.tweeter.client.model.net.ServerFacade;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.request.GetFollowersCountRequest;
import edu.byu.cs.tweeter.model.net.response.GetFollowersCountResponse;

/**
 * Background task that queries how many followers a user has.
 */
public class GetFollowersCountTask extends GetCountTask {


    public GetFollowersCountTask(AuthToken authToken, User targetUser, Handler messageHandler) {
        super(authToken, targetUser, messageHandler);
    }

    @Override
    protected int runCountTask() {
        GetFollowersCountRequest getFollowersCountRequest = new GetFollowersCountRequest(targetUser.getAlias(),
                authToken);
        try {
            GetFollowersCountResponse getFollowersCountResponse = new ServerFacade().
                    getFollowersCount(getFollowersCountRequest, "/getfollowerscount");
            if (getFollowersCountResponse.isSuccess()) {
                return getFollowersCountResponse.getFollowersCount();
            }
            else {
                sendFailedMessage(getFollowersCountResponse.getMessage());
            }
        }
        catch (Exception e) {
            sendExceptionMessage(e);
        }


//        return 20;
//        //return true;
        return -1;
    }
}
