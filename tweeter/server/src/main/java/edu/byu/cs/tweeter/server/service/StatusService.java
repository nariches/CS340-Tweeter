package edu.byu.cs.tweeter.server.service;

import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.net.request.FeedRequest;
import edu.byu.cs.tweeter.model.net.request.PostStatusRequest;
import edu.byu.cs.tweeter.model.net.request.StoryRequest;
import edu.byu.cs.tweeter.model.net.response.FeedResponse;
import edu.byu.cs.tweeter.model.net.response.PostStatusResponse;
import edu.byu.cs.tweeter.model.net.response.StoryResponse;
import edu.byu.cs.tweeter.server.dao.StatusDAO;

public class StatusService {

    private AWSFactory awsFactory;

    public FeedResponse getFeed(FeedRequest feedRequest) {
        return awsFactory.getStatusDAO().getFeed(feedRequest);
        //return getStatusDAO().getFeed(request);
    }

    public StoryResponse getStory(StoryRequest storyRequest) {
        return awsFactory.getStatusDAO().getStory(storyRequest);
        //return getStatusDAO().getStory(request);
    }

    public PostStatusResponse postStatus(PostStatusRequest postStatusRequest) {
        return awsFactory.getStatusDAO().postStatus(postStatusRequest);
        //return getStatusDAO().postStatus(postStatusRequest);
    }

//    StatusDAO getStatusDAO() {
//        return new StatusDAO();
//    }
}
