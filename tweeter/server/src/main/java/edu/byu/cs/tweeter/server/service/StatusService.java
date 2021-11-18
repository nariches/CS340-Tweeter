package edu.byu.cs.tweeter.server.service;

import java.util.ArrayList;
import java.util.List;

import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.net.request.FeedRequest;
import edu.byu.cs.tweeter.model.net.request.PostStatusRequest;
import edu.byu.cs.tweeter.model.net.request.StoryRequest;
import edu.byu.cs.tweeter.model.net.response.FeedResponse;
import edu.byu.cs.tweeter.model.net.response.PostStatusResponse;
import edu.byu.cs.tweeter.model.net.response.StoryResponse;
import edu.byu.cs.tweeter.server.util.FakeData;

public class StatusService {

    private AWSFactory awsFactory;

    public StatusService() {
        this.awsFactory = new AWSFactory();
    }

    public FeedResponse getFeed(FeedRequest feedRequest) {
        // TODO: Generates dummy data. Replace with a real implementation.
        assert feedRequest.getLimit() > 0;
        assert feedRequest.getUsername() != null;

        List<Status> feed = getDummyFeed();
        List<Status> responseFeed = new ArrayList<>(feedRequest.getLimit());

        boolean hasMorePages = false;

        if(feedRequest.getLimit() > 0) {
            if (feed != null) {
                int feedIndex = getFeedStartingIndex(feedRequest.getLastStatusDatetime(), feed);

                for(int limitCounter = 0; feedIndex < feed.size() && limitCounter < feedRequest.getLimit(); feedIndex++, limitCounter++) {
                    responseFeed.add(feed.get(feedIndex));
                }

                hasMorePages = feedIndex < feed.size();
            }
        }

        return new FeedResponse(responseFeed, hasMorePages);
    }

    public StoryResponse getStory(StoryRequest storyRequest) {
        // TODO: Generates dummy data. Replace with a real implementation.
        assert storyRequest.getLimit() > 0;
        assert storyRequest.getUsername() != null;

        List<Status> story = getDummyStory();
        List<Status> responseStory = new ArrayList<>(storyRequest.getLimit());

        boolean hasMorePages = false;

        if(storyRequest.getLimit() > 0) {
            if (story != null) {
                int storyIndex = getStoryStartingIndex(storyRequest.getLastStatusDatetime(), story);

                for(int limitCounter = 0; storyIndex < story.size() && limitCounter < storyRequest.getLimit(); storyIndex++, limitCounter++) {
                    responseStory.add(story.get(storyIndex));
                }

                hasMorePages = storyIndex < story.size();
            }
        }

        return new StoryResponse(responseStory, hasMorePages);
    }

    public PostStatusResponse postStatus(PostStatusRequest postStatusRequest) {
        return new PostStatusResponse(postStatusRequest.getAuthToken(),
                postStatusRequest.getStatus());
    }


    private int getFeedStartingIndex(String lastStatusDatetime, List<Status> feed) {

        int feedIndex = 0;

        if(lastStatusDatetime != null) {
            // This is a paged request for something after the first page. Find the first item
            // we should return
            for (int i = 0; i < feed.size(); i++) {
                if(lastStatusDatetime.equals(feed.get(i).getDate())) {
                    // We found the index of the last item returned last time. Increment to get
                    // to the first one we should return
                    feedIndex = i + 1;
                    break;
                }
            }
        }

        return feedIndex;
    }

    private int getStoryStartingIndex(String lastStatusDatetime, List<Status> story) {

        int storyIndex = 0;

        if(lastStatusDatetime != null) {
            // This is a paged request for something after the first page. Find the first item
            // we should return
            for (int i = 0; i < story.size(); i++) {
                if(lastStatusDatetime.equals(story.get(i).getDate())) {
                    // We found the index of the last item returned last time. Increment to get
                    // to the first one we should return
                    storyIndex = i + 1;
                    break;
                }
            }
        }

        return storyIndex;
    }
//    StatusDAO getStatusDAO() {
//        return new StatusDAO();
//    }
List<Status> getDummyFeed() {
    return getFakeData().getFakeStatuses();
}

    List<Status> getDummyStory() {
        return getFakeData().getFakeStatuses();
    }


    FakeData getFakeData() {
        return new FakeData();
    }
}
