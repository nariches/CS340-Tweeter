package edu.byu.cs.tweeter.server.service;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Index;
import com.amazonaws.services.dynamodbv2.document.Table;

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
import edu.byu.cs.tweeter.server.dao.FollowDAO;

/**
 * Contains the business logic for getting the users a user is following.
 */
public class FollowService {

    private AWSFactory awsFactory;

    /**
     * Returns the users that the user specified in the request is following. Uses information in
     * the request object to limit the number of followees returned and to return the next set of
     * followees after any that were returned in a previous request. Uses the {@link FollowDAO} to
     * get the followees.
     *
     * @param //request contains the data required to fulfill the request.
     * @return the followees.
     */




    public FollowingResponse getFollowees(FollowingRequest followingRequest) {
        //return getFollowingDAO().getFollowees(request);
        //return getFollowingDAO().queryPaginated(follows_table, request.getFollowerAlias());
        return awsFactory.getFollowDAO().getFollowees(followingRequest);
    }

    public FollowersResponse getFollowers(FollowersRequest followersRequest) {
        return awsFactory.getFollowDAO().getFollowers(followersRequest);
        //return getFollowingDAO().getFollowers(request);
    }

    public GetFollowersCountResponse getFollowersCount(GetFollowersCountRequest getFollowersCountRequest) {
        return awsFactory.getFollowDAO().getFollowersCount(getFollowersCountRequest);
        //return getFollowingDAO().getFollowersCount(getFollowersCountRequest);
    }

    public GetFollowingCountResponse getFollowingCount(GetFollowingCountRequest getFollowingCountRequest) {
        return awsFactory.getFollowDAO().getFollowingCount(getFollowingCountRequest);
        //return getFollowingDAO().getFollowingCount(getFollowingCountRequest);
    }

    public FollowResponse follow(FollowRequest followRequest) {
        return awsFactory.getFollowDAO().follow(followRequest);
        //return getFollowingDAO().follow(followRequest);
    }

    public UnfollowResponse unfollow(UnfollowRequest unfollowRequest) {
        return awsFactory.getFollowDAO().unfollow(unfollowRequest);
        //return getFollowingDAO().unfollow(unfollowRequest);
    }

    public IsFollowerResponse isFollower(IsFollowerRequest isFollowerRequest) {
        return awsFactory.getFollowDAO().isFollower(isFollowerRequest);
        //return getFollowingDAO().isFollower(isFollowerRequest);
    }

    /**
     * Returns an instance of {@link FollowDAO}. Allows mocking of the FollowDAO class
     * for testing purposes. All usages of FollowDAO should get their FollowDAO
     * instance from this method to allow for mocking of the instance.
     *
     * @return the instance.
     */
//    FollowDAO getFollowingDAO() {
//        return new FollowDAO();
//    }
}
