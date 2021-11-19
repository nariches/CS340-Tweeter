package edu.byu.cs.tweeter.server.service;

import java.util.ArrayList;
import java.util.List;

import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.request.FeedRequest;
import edu.byu.cs.tweeter.model.net.request.PostStatusRequest;
import edu.byu.cs.tweeter.model.net.request.StoryRequest;
import edu.byu.cs.tweeter.model.net.response.FeedResponse;
import edu.byu.cs.tweeter.model.net.response.FollowersResponse;
import edu.byu.cs.tweeter.model.net.response.PostStatusResponse;
import edu.byu.cs.tweeter.model.net.response.StoryResponse;
import edu.byu.cs.tweeter.server.util.FakeData;

public class StatusService {

    private AWSFactory awsFactory;

    public StatusService() {
        this.awsFactory = new AWSFactory();
    }

    public FeedResponse getFeed(FeedRequest feedRequest) throws Exception {
        AuthToken currAuthToken = awsFactory.getAuthTokenDAO().getAuthToken(feedRequest.getAuthToken());
        if (authenticated(currAuthToken)) {
            FeedResponse feedResponse = awsFactory.getFeedDAO().getFeed
                    (feedRequest.getUsername(), feedRequest.getLastStatusDatetime(),
                            feedRequest.getLimit());
            return feedResponse;
        }
        else {
            return new FeedResponse("AuthToken invalid or expired");
        }
    }

    public StoryResponse getStory(StoryRequest storyRequest) throws Exception {
        AuthToken currAuthToken = awsFactory.getAuthTokenDAO().getAuthToken(storyRequest.getAuthToken());
        if (authenticated(currAuthToken)) {
            StoryResponse storyResponse = awsFactory.getStoryDAO().getStory
                    (storyRequest.getUsername(), storyRequest.getLastStatusDatetime(),
                            storyRequest.getLimit());

            return storyResponse;
        }
        else {
            return new StoryResponse("AuthToken invalid or expired");
        }
    }

    public PostStatusResponse postStatus(PostStatusRequest postStatusRequest) throws Exception {
        AuthToken currAuthToken = awsFactory.getAuthTokenDAO().getAuthToken(postStatusRequest.getAuthToken());
        if (authenticated(currAuthToken)) {
            List<User> followerList = new ArrayList<>();
            awsFactory.getStoryDAO().putStory(postStatusRequest.getStatus());

            FollowersResponse followersResponse = awsFactory.getFollowsDAO().getFollowers
                    (postStatusRequest.getStatus().getUser().getAlias(), null, 1000000);
            for (User follower : followersResponse.getFollowers()) {
                followerList.add(follower);
            }

            System.out.println("Number of followers!!: " + followerList.size());

            for (User receiver : followerList) {
                awsFactory.getFeedDAO().putFeed(postStatusRequest.getStatus(), receiver.getAlias());
            }
            return new PostStatusResponse(postStatusRequest.getAuthToken(),
                    postStatusRequest.getStatus());
        }
        else {
            return new PostStatusResponse("AuthToken invalid or expired");
        }
    }

    public boolean authenticated(AuthToken authToken) throws Exception {
        AuthToken storedAuthToken = awsFactory.getAuthTokenDAO().validateAuthToken(authToken);
        if (storedAuthToken == null) {
            return false;
        }
        else {
            return true;
        }
    }
}
