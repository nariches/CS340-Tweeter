package edu.byu.cs.tweeter.client.backgroundTask;

import android.os.Handler;

import java.util.List;

import edu.byu.cs.tweeter.client.model.net.ServerFacade;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.request.FollowersRequest;
import edu.byu.cs.tweeter.model.net.response.FollowersResponse;
import edu.byu.cs.tweeter.util.Pair;

/**
 * Background task that retrieves a page of followers.
 */
public class GetFollowersTask extends PagedUserTask {

    public GetFollowersTask(AuthToken authToken, User targetUser, int limit, User lastFollower,
                            Handler messageHandler) {
        super(authToken, targetUser, limit, lastFollower, messageHandler);
    }

    @Override
    protected Pair<List<User>, Boolean> getItems() {
        String lastItemAlias;
        if (getLastItem() != null) {
            lastItemAlias = getLastItem().getAlias();
        }
        else {
            lastItemAlias = null;
        }
        FollowersRequest followersRequest = new FollowersRequest(authToken,
                getTargetUser().getAlias(), getLimit(), lastItemAlias);
        try {
            FollowersResponse followersResponse = new ServerFacade().getFollowers(followersRequest,
                    "/getfollowers");
            if (followersResponse.isSuccess()) {
                BackgroundTaskUtils backgroundTaskUtils = new BackgroundTaskUtils();
                backgroundTaskUtils.loadImage(getTargetUser());
                return new Pair<>(followersResponse.getFollowers(),
                        followersResponse.getHasMorePages());

            }
        }
        catch (Exception e) {
            sendExceptionMessage(e);
        }

        return null;
        //return getFakeData().getPageOfUsers(getLastItem(), getLimit(), getTargetUser());
    }
}
