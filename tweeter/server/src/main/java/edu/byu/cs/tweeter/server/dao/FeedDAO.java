package edu.byu.cs.tweeter.server.dao;

import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.PutItemOutcome;

public class FeedDAO implements IFeedDAO {


    @Override
    public Item getFeed(String username) {
        return null;
    }

    @Override
    public PutItemOutcome putFeed() {
        return null;
    }




//    @Override
//    public FeedResponse getFeed(FeedRequest request) {
//
//    }
//
//    @Override
//    public StoryResponse getStory(StoryRequest request) {
//
//    }
//
//    private int getFeedStartingIndex(String lastStatusDatetime, List<Status> feed) {
//
//        int feedIndex = 0;
//
//        if(lastStatusDatetime != null) {
//            // This is a paged request for something after the first page. Find the first item
//            // we should return
//            for (int i = 0; i < feed.size(); i++) {
//                if(lastStatusDatetime.equals(feed.get(i).getDate())) {
//                    // We found the index of the last item returned last time. Increment to get
//                    // to the first one we should return
//                    feedIndex = i + 1;
//                    break;
//                }
//            }
//        }
//
//        return feedIndex;
//    }
//
//    private int getStoryStartingIndex(String lastStatusDatetime, List<Status> story) {
//
//        int storyIndex = 0;
//
//        if(lastStatusDatetime != null) {
//            // This is a paged request for something after the first page. Find the first item
//            // we should return
//            for (int i = 0; i < story.size(); i++) {
//                if(lastStatusDatetime.equals(story.get(i).getDate())) {
//                    // We found the index of the last item returned last time. Increment to get
//                    // to the first one we should return
//                    storyIndex = i + 1;
//                    break;
//                }
//            }
//        }
//
//        return storyIndex;
//    }

//    @Override
//    public PostStatusResponse postStatus(PostStatusRequest postStatusRequest) {
//
//    }
//
//    List<Status> getDummyFeed() {
//        return getFakeData().getFakeStatuses();
//    }
//
//    List<Status> getDummyStory() {
//        return getFakeData().getFakeStatuses();
//    }


//    //FakeData getFakeData() {
//        return new FakeData();
//    }


}
