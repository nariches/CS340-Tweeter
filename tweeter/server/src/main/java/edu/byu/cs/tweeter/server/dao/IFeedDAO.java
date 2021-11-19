package edu.byu.cs.tweeter.server.dao;

import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.PutItemOutcome;

import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.net.request.FeedRequest;
import edu.byu.cs.tweeter.model.net.request.PostStatusRequest;
import edu.byu.cs.tweeter.model.net.request.StoryRequest;
import edu.byu.cs.tweeter.model.net.response.FeedResponse;
import edu.byu.cs.tweeter.model.net.response.PostStatusResponse;
import edu.byu.cs.tweeter.model.net.response.StoryResponse;

public interface IFeedDAO {

    FeedResponse getFeed(String username, String lastStatusDateTime, int limit);
    void putFeed(Status status, String receiverUsername);

//    FeedResponse getFeed(FeedRequest feedRequest);
//    StoryResponse getStory(StoryRequest storyRequest);
//    PostStatusResponse postStatus(PostStatusRequest postStatusRequest);
}
