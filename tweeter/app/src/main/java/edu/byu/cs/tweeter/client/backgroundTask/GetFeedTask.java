package edu.byu.cs.tweeter.client.backgroundTask;

import android.os.Handler;

import java.util.List;

import edu.byu.cs.tweeter.client.model.net.ServerFacade;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.request.FeedRequest;
import edu.byu.cs.tweeter.model.net.response.FeedResponse;
import edu.byu.cs.tweeter.util.Pair;

/**
 * Background task that retrieves a page of statuses from a user's feed.
 */
public class GetFeedTask extends PagedStatusTask {

    public GetFeedTask(AuthToken authToken, User targetUser, int limit, Status lastStatus,
                       Handler messageHandler) {
        super(authToken, targetUser, limit, lastStatus, messageHandler);
    }

    @Override
    protected Pair<List<Status>, Boolean> getItems() {
        String lastItemDatetime;
        if (getLastItem() != null) {
            lastItemDatetime = getLastItem().getDate();
        }
        else {
            lastItemDatetime = null;
        }
        FeedRequest feedRequest = new FeedRequest(authToken, getTargetUser().getAlias(),
                getLimit(), lastItemDatetime);
        try {
            FeedResponse feedResponse = new ServerFacade().getFeed(feedRequest, "/getfeed");
            if (feedResponse.isSuccess()) {
                return new Pair<>(feedResponse.getFeed(), feedResponse.getHasMorePages());
            }
        }
        catch (Exception e) {
            sendExceptionMessage(e);
        }

        return null;
        // return getFakeData().getPageOfStatus(getLastItem(), getLimit());
    }
}
