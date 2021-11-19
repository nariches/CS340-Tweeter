package edu.byu.cs.tweeter.server.service;

import java.util.ArrayList;
import java.util.List;

import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.request.FollowRequest;
import edu.byu.cs.tweeter.model.net.request.FollowersRequest;
import edu.byu.cs.tweeter.model.net.request.FollowingRequest;
import edu.byu.cs.tweeter.model.net.request.GetFollowersCountRequest;
import edu.byu.cs.tweeter.model.net.request.GetFollowingCountRequest;
import edu.byu.cs.tweeter.model.net.request.IsFollowerRequest;
import edu.byu.cs.tweeter.model.net.request.UnfollowRequest;
import edu.byu.cs.tweeter.model.net.response.FollowResponse;
import edu.byu.cs.tweeter.model.net.response.FollowersResponse;
import edu.byu.cs.tweeter.model.net.response.FollowingResponse;
import edu.byu.cs.tweeter.model.net.response.GetFollowersCountResponse;
import edu.byu.cs.tweeter.model.net.response.GetFollowingCountResponse;
import edu.byu.cs.tweeter.model.net.response.IsFollowerResponse;
import edu.byu.cs.tweeter.model.net.response.UnfollowResponse;
import edu.byu.cs.tweeter.server.util.FakeData;

/**
 * Contains the business logic for getting the users a user is following.
 */
public class FollowService {

    private AWSFactory awsFactory;

    public FollowService() {
        this.awsFactory = new AWSFactory();
    }


    public FollowingResponse getFollowing(FollowingRequest followingRequest) {
        FollowingResponse followingResponse = awsFactory.getFollowsDAO().getFollowing
                (followingRequest.getFollowerAlias(), followingRequest.getLastFolloweeAlias(),
                        followingRequest.getLimit());

        return followingResponse;
    }

    public FollowersResponse getFollowers(FollowersRequest followersRequest) {
        FollowersResponse followersResponse = awsFactory.getFollowsDAO().getFollowers
                (followersRequest.getFolloweeAlias(), followersRequest.getLastFollowerAlias(),
                        followersRequest.getLimit());

        return followersResponse;
    }

    public GetFollowersCountResponse getFollowersCount(GetFollowersCountRequest getFollowersCountRequest) {
        //return awsFactory.getFollowsDAO().getFollowersCount(getFollowersCountRequest);
        //return getFollowingDAO().getFollowersCount(getFollowersCountRequest);
        int followerCount = awsFactory.getUserDAO().getFollowerCount(getFollowersCountRequest.getUsername());
        return new GetFollowersCountResponse(followerCount);
    }

    public GetFollowingCountResponse getFollowingCount(GetFollowingCountRequest getFollowingCountRequest) {
        //return awsFactory.getFollowsDAO().getFollowingCount(getFollowingCountRequest);
        //return getFollowingDAO().getFollowingCount(getFollowingCountRequest);
        int followingCount = awsFactory.getUserDAO().getFollowingCount(getFollowingCountRequest.getUsername());
        return new GetFollowingCountResponse(followingCount);
    }

    public FollowResponse follow(FollowRequest followRequest) {
        //Call getUser to get follower and followee users from users table
        User followerUser = awsFactory.getUserDAO().getUser(followRequest.getCurrUsername());
        System.out.println("This is curr user: " + followerUser);
        User followeeUser = awsFactory.getUserDAO().getUser(followRequest.getFolloweeUsername());
        System.out.println("This is followeeUser: " + followeeUser);
        //Pass in all the right parameters to putFollows
        awsFactory.getFollowsDAO().putFollows(followerUser.getAlias(), followerUser.getFirstName(),
                followerUser.getLastName(), followerUser.getImageUrl(), followeeUser.getAlias(),
                followeeUser.getFirstName(), followeeUser.getLastName(), followeeUser.getImageUrl());

        //Update following and follower counts
        awsFactory.getUserDAO().incrementFollowingCount(followRequest.getCurrUsername());
        awsFactory.getUserDAO().incrementFollowerCount(followRequest.getFolloweeUsername());

        return new FollowResponse();
    }

    public UnfollowResponse unfollow(UnfollowRequest unfollowRequest) {
        //Call getUser to get follower and followee users from users table
        User unfollowerUser = awsFactory.getUserDAO().getUser(unfollowRequest.getCurrUsername());
        System.out.println("This is curr user: " + unfollowerUser);
        User unfolloweeUser = awsFactory.getUserDAO().getUser(unfollowRequest.getUnfolloweeUsername());
        System.out.println("This is followeeUser: " + unfolloweeUser);
        //Pass in all the right parameters to deleteFollows
        awsFactory.getFollowsDAO().deleteFollows(unfollowerUser.getAlias(), unfolloweeUser.getAlias());

        //Update following and follower counts
        awsFactory.getUserDAO().decremementFollowingCount(unfollowRequest.getCurrUsername());
        awsFactory.getUserDAO().decrementFollowerCount(unfollowRequest.getUnfolloweeUsername());

        return new UnfollowResponse();
    }

    public IsFollowerResponse isFollower(IsFollowerRequest isFollowerRequest) {
        assert isFollowerRequest.getFollowerUsername() != null;
        assert isFollowerRequest.getFolloweeUsername() != null;
        boolean isFollower = false;
        isFollower = awsFactory.getFollowsDAO().getFollows(isFollowerRequest.getFollowerUsername(),
                isFollowerRequest.getFolloweeUsername());
        IsFollowerResponse isFollowerResponse = new IsFollowerResponse();
        System.out.println("isFollower in FollowService: " + isFollower);
        if (isFollower) {
            System.out.println("Setting isFollower true...");
            isFollowerResponse.setFollowerBool(true);
            System.out.println("isFollowerResp after set true: " + isFollowerResponse.isFollowerBool());
        }
        else {
            System.out.println("Setting isFollower false");
            isFollowerResponse.setFollowerBool(false);
            System.out.println("isFollowerResp after set false: " + isFollowerResponse.isFollowerBool());
        }
        return isFollowerResponse;
    }

    /**
     * Returns an instance of {@link}. Allows mocking of the FollowDAO class
     * for testing purposes. All usages of FollowDAO should get their FollowDAO
     * instance from this method to allow for mocking of the instance.
     *
     * @return the instance.
     */
//    FollowDAO getFollowingDAO() {
//        return new FollowDAO();
//    }

    private int getFolloweesStartingIndex(String lastFolloweeAlias, List<User> allFollowees) {

        int followeesIndex = 0;

        if(lastFolloweeAlias != null) {
            // This is a paged request for something after the first page. Find the first item
            // we should return
            for (int i = 0; i < allFollowees.size(); i++) {
                if(lastFolloweeAlias.equals(allFollowees.get(i).getAlias())) {
                    // We found the index of the last item returned last time. Increment to get
                    // to the first one we should return
                    followeesIndex = i + 1;
                    break;
                }
            }
        }

        return followeesIndex;
    }

    private int getFollowersStartingIndex(String lastFollowerAlias, List<User> allFollowers) {

        int followersIndex = 0;

        if(lastFollowerAlias != null) {
            // This is a paged request for something after the first page. Find the first item
            // we should return
            for (int i = 0; i < allFollowers.size(); i++) {
                if(lastFollowerAlias.equals(allFollowers.get(i).getAlias())) {
                    // We found the index of the last item returned last time. Increment to get
                    // to the first one we should return
                    followersIndex = i + 1;
                    break;
                }
            }
        }

        return followersIndex;
    }


    List<User> getDummyFollowees() {
        return getFakeData().getFakeUsers();
    }

    List<User> getDummyFollowers() {
        return getFakeData().getFakeUsers();
    }

    /**
     * Returns the {@link FakeData} object used to generate dummy followees.
     * This is written as a separate method to allow mocking of the {@link FakeData}.
     *
     * @return a {@link FakeData} instance.
     */
    FakeData getFakeData() {
        return new FakeData();
    }
}
