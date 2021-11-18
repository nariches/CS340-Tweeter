package edu.byu.cs.tweeter.server.dao;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Index;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.ItemCollection;
import com.amazonaws.services.dynamodbv2.document.PutItemOutcome;
import com.amazonaws.services.dynamodbv2.document.QueryOutcome;
import com.amazonaws.services.dynamodbv2.document.Table;

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

public interface IFollowsDAO {

    FollowingResponse getFollowing(String followerUsername, String lastFolloweeUsername, int limit);
    FollowersResponse getFollowers(String followeeUsername, String lastFollowerUsername, int limit);
    void putFollows(String followerUsername, String followerFirstName, String followerLastName,
                    String followerImage, String followeeUsername, String followeeFirstName,
                    String followeeLastName, String followeeImage);
    void deleteFollows(String followerUsername, String followeeUsername);
    boolean getFollows(String followerUsername, String followeeUsername);



//    FollowResponse follow(FollowRequest followRequest);
//    GetFollowersCountResponse getFollowersCount(GetFollowersCountRequest getFollowersCountRequest);
//    FollowersResponse getFollowers(FollowersRequest followersRequest);
//    GetFollowingCountResponse getFollowingCount(GetFollowingCountRequest getFollowingCountRequest);
//    FollowingResponse getFollowees(FollowingRequest followingRequest);
//    IsFollowerResponse isFollower(IsFollowerRequest isFollowerRequest);
//    UnfollowResponse unfollow(UnfollowRequest unfollowRequest);
}
