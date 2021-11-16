package edu.byu.cs.tweeter.server.dao;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Index;
import com.amazonaws.services.dynamodbv2.document.Table;

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

public interface IFollowDAO {

    AmazonDynamoDB client = AmazonDynamoDBClientBuilder.standard().withRegion("us-east-2").build();

    DynamoDB dynamoDB = new DynamoDB(client);

    Table follows_table = dynamoDB.getTable("follows");
    Index index = follows_table.getIndex("follows_index");

    FollowResponse follow(FollowRequest followRequest);
    GetFollowersCountResponse getFollowersCount(GetFollowersCountRequest getFollowersCountRequest);
    FollowersResponse getFollowers(FollowersRequest followersRequest);
    GetFollowingCountResponse getFollowingCount(GetFollowingCountRequest getFollowingCountRequest);
    FollowingResponse getFollowees(FollowingRequest followingRequest);
    IsFollowerResponse isFollower(IsFollowerRequest isFollowerRequest);
    UnfollowResponse unfollow(UnfollowRequest unfollowRequest);
}