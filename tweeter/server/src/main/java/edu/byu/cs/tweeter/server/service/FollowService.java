package edu.byu.cs.tweeter.server.service;

import java.util.ArrayList;
import java.util.List;

import edu.byu.cs.tweeter.model.domain.AuthToken;
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


    public FollowingResponse getFollowing(FollowingRequest followingRequest) throws Exception {
        AuthToken currAuthToken = awsFactory.getAuthTokenDAO().getAuthToken(followingRequest.getAuthToken());
        if (authenticated(currAuthToken)) {
            FollowingResponse followingResponse = awsFactory.getFollowsDAO().getFollowing
                    (followingRequest.getFollowerAlias(), followingRequest.getLastFolloweeAlias(),
                            followingRequest.getLimit());
            return followingResponse;
        }
        else {
            return new FollowingResponse("AuthToken is expired or invalid");
        }
    }

    public FollowersResponse getFollowers(FollowersRequest followersRequest) throws Exception {
        AuthToken currAuthToken = awsFactory.getAuthTokenDAO().getAuthToken(followersRequest.getAuthToken());
        if (authenticated(currAuthToken)) {
            FollowersResponse followersResponse = awsFactory.getFollowsDAO().getFollowers
                    (followersRequest.getFolloweeAlias(), followersRequest.getLastFollowerAlias(),
                            followersRequest.getLimit());
            return followersResponse;
        }
        else {
            return new FollowersResponse("AuthToken invalid or expired");
        }
    }

    public GetFollowersCountResponse getFollowersCount(GetFollowersCountRequest getFollowersCountRequest) throws Exception {
        AuthToken currAuthToken = awsFactory.getAuthTokenDAO().getAuthToken(getFollowersCountRequest.getAuthToken());
        if(authenticated(currAuthToken)) {
            int followerCount = awsFactory.getUserDAO().getFollowerCount(getFollowersCountRequest.getUsername());
            return new GetFollowersCountResponse(followerCount);
        }
        else {
            return new GetFollowersCountResponse("AuthToken invalid or expired");
        }
    }

    public GetFollowingCountResponse getFollowingCount(GetFollowingCountRequest getFollowingCountRequest) throws Exception {
        AuthToken currAuthToken = awsFactory.getAuthTokenDAO().getAuthToken(getFollowingCountRequest.getAuthToken());
        if (authenticated(currAuthToken)) {
            int followingCount = awsFactory.getUserDAO().getFollowingCount(getFollowingCountRequest.getUsername());
            return new GetFollowingCountResponse(followingCount);
        }
        else {
            return new GetFollowingCountResponse("AuthToken invalid or expired");
        }
    }

    public FollowResponse follow(FollowRequest followRequest) throws Exception {
        AuthToken currAuthToken = awsFactory.getAuthTokenDAO().getAuthToken(followRequest.getAuthToken());
        if (authenticated(currAuthToken)) {
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
        else {
            return new FollowResponse("AuthToken invalid or expired");
        }
    }

    public UnfollowResponse unfollow(UnfollowRequest unfollowRequest) throws Exception {
        AuthToken currAuthToken = awsFactory.getAuthTokenDAO().getAuthToken(unfollowRequest.getAuthToken());
        if (authenticated(currAuthToken)) {
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
        else {
            return new UnfollowResponse("AuthToken expired or invalid");
        }
    }

    public IsFollowerResponse isFollower(IsFollowerRequest isFollowerRequest) throws Exception {
        AuthToken currAuthToken = awsFactory.getAuthTokenDAO().getAuthToken(isFollowerRequest.getAuthToken());
        if (authenticated(currAuthToken)) {
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
            } else {
                System.out.println("Setting isFollower false");
                isFollowerResponse.setFollowerBool(false);
                System.out.println("isFollowerResp after set false: " + isFollowerResponse.isFollowerBool());
            }
            return isFollowerResponse;
        }
        else {
            return new IsFollowerResponse("AuthToken is invalid or expired");
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

