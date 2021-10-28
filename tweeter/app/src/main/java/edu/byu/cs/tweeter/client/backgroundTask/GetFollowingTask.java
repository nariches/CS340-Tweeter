package edu.byu.cs.tweeter.client.backgroundTask;

import android.os.Handler;

import java.util.List;

import edu.byu.cs.tweeter.client.model.net.ServerFacade;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.request.FollowingRequest;
import edu.byu.cs.tweeter.model.net.response.FollowingResponse;
import edu.byu.cs.tweeter.util.Pair;

/**
 * Background task that retrieves a page of other users being followed by a specified user.
 */
public class GetFollowingTask extends PagedUserTask {

    public GetFollowingTask(AuthToken authToken, User targetUser, int limit, User lastFollowee,
                            Handler messageHandler) {
        super(authToken, targetUser, limit, lastFollowee, messageHandler);
    }

    @Override
    protected Pair<List<User>, Boolean> getItems() {
//        FollowingRequest followingRequest = new FollowingRequest(authToken,
//                getTargetUser().getAlias(), getLimit(), getLastItem().getAlias());
//        try {
//            FollowingResponse followingResponse = new ServerFacade().getFollowees(followingRequest,
//                    "/getfollowing");
//            if (followingResponse.isSuccess()) {
//                return new Pair<>(followingResponse.getFollowees(),
//                        followingResponse.getHasMorePages());
//            }
//        }
//        catch (Exception e) {
//            sendExceptionMessage(e);
//        }
//        return null;
        return getFakeData().getPageOfUsers(getLastItem(), getLimit(), getTargetUser());
    }
}
