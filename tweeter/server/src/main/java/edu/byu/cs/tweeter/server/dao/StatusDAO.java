package edu.byu.cs.tweeter.server.dao;

import java.util.ArrayList;
import java.util.List;

import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.net.request.FeedRequest;
import edu.byu.cs.tweeter.model.net.request.PostStatusRequest;
import edu.byu.cs.tweeter.model.net.request.StoryRequest;
import edu.byu.cs.tweeter.model.net.response.FeedResponse;
import edu.byu.cs.tweeter.model.net.response.PostStatusResponse;
import edu.byu.cs.tweeter.model.net.response.StoryResponse;
import edu.byu.cs.tweeter.server.util.FakeData;

public class StatusDAO {

    public FeedResponse getFeed(FeedRequest request) {
        // TODO: Generates dummy data. Replace with a real implementation.
        assert request.getLimit() > 0;
        assert request.getUsername() != null;

        List<Status> feed = getDummyFeed();
        List<Status> responseFeed = new ArrayList<>(request.getLimit());

        boolean hasMorePages = false;

        if(request.getLimit() > 0) {
            if (feed != null) {
                int feedIndex = getFeedStartingIndex(request.getLastStatusDatetime(), feed);

                for(int limitCounter = 0; feedIndex < feed.size() && limitCounter < request.getLimit(); feedIndex++, limitCounter++) {
                    responseFeed.add(feed.get(feedIndex));
                }

                hasMorePages = feedIndex < feed.size();
            }
        }

        return new FeedResponse(responseFeed, hasMorePages);
    }

    public StoryResponse getStory(StoryRequest request) {
        // TODO: Generates dummy data. Replace with a real implementation.
        assert request.getLimit() > 0;
        assert request.getUsername() != null;

        List<Status> story = getDummyStory();
        List<Status> responseStory = new ArrayList<>(request.getLimit());

        boolean hasMorePages = false;

        if(request.getLimit() > 0) {
            if (story != null) {
                int storyIndex = getStoryStartingIndex(request.getLastStatusDatetime(), story);

                for(int limitCounter = 0; storyIndex < story.size() && limitCounter < request.getLimit(); storyIndex++, limitCounter++) {
                    responseStory.add(story.get(storyIndex));
                }

                hasMorePages = storyIndex < story.size();
            }
        }

        return new StoryResponse(responseStory, hasMorePages);
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

    public PostStatusResponse postStatus(AuthToken authToken, Status status) {
        return new PostStatusResponse(authToken, status);
    }

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
