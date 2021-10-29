package edu.byu.cs.tweeter.client.backgroundTask;

import android.os.Handler;

import java.util.List;

import edu.byu.cs.tweeter.client.model.net.ServerFacade;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.request.StoryRequest;
import edu.byu.cs.tweeter.model.net.response.StoryResponse;
import edu.byu.cs.tweeter.util.Pair;

/**
 * Background task that retrieves a page of statuses from a user's story.
 */
public class GetStoryTask extends PagedStatusTask {

    public GetStoryTask(AuthToken authToken, User targetUser, int limit, Status lastStatus,
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
        StoryRequest storyRequest = new StoryRequest(authToken, getTargetUser().getAlias(),
                getLimit(), lastItemDatetime);
        try {
            StoryResponse storyResponse = new ServerFacade().getStory(storyRequest, "/getstory");
            if (storyResponse.isSuccess()) {
                return new Pair<>(storyResponse.getStory(), storyResponse.getHasMorePages());
            }
        }
        catch (Exception e) {
            sendExceptionMessage(e);
        }

        return null;
        //return getFakeData().getPageOfStatus(getLastItem(), getLimit());
    }
}
