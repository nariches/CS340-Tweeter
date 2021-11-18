package edu.byu.cs.tweeter.server.dao;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Index;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.ItemCollection;
import com.amazonaws.services.dynamodbv2.document.PrimaryKey;
import com.amazonaws.services.dynamodbv2.document.PutItemOutcome;
import com.amazonaws.services.dynamodbv2.document.QueryOutcome;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.document.spec.DeleteItemSpec;
import com.amazonaws.services.dynamodbv2.document.spec.GetItemSpec;
import com.amazonaws.services.dynamodbv2.document.spec.QuerySpec;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.QueryRequest;
import com.amazonaws.services.dynamodbv2.model.QueryResult;
import com.sun.tools.javac.util.Name;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

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
 * A DAO for accessing 'following' data from the database.
 */
public class FollowsDAO implements IFollowsDAO {

    private AmazonDynamoDB client;
    private DynamoDB dynamoDB;
    private Table follows_table;
    private Index follows_index;

    public FollowsDAO() {
        this.client = AmazonDynamoDBClientBuilder.standard().withRegion("us-east-2").build();
        this.dynamoDB = new DynamoDB(client);
        this.follows_table = dynamoDB.getTable("follows");
        this.follows_index = follows_table.getIndex("follows_index");
    }

    @Override
    public FollowingResponse getFollowing(String followerUsername, String lastFolloweeUsername, int limit) {
        List<User> followees = new ArrayList<>();

        Map<String, String> attNames = new HashMap<String, String>();
        attNames.put("#handle", "follower_handle");

        Map<String, AttributeValue> attValues = new HashMap<>();
        attValues.put(":username", new AttributeValue().withS(followerUsername));

        QueryRequest queryRequest = new QueryRequest()
                .withTableName("follows")
                .withKeyConditionExpression("#handle = :username")
                .withExpressionAttributeNames(attNames)
                .withExpressionAttributeValues(attValues)
                .withLimit(limit);

        QueryRequest checkRequest = new QueryRequest()
                .withTableName("follows")
                .withKeyConditionExpression("#handle = :username")
                .withExpressionAttributeNames(attNames)
                .withExpressionAttributeValues(attValues)
                .withLimit(limit + 1);

        if (lastFolloweeUsername != null) {
            Map<String, AttributeValue> lastKey = new HashMap<>();
            lastKey.put("follower_handle", new AttributeValue().withS(followerUsername));
            lastKey.put("followee_handle", new AttributeValue().withS(lastFolloweeUsername));

            queryRequest = queryRequest.withExclusiveStartKey(lastKey);
            checkRequest = checkRequest.withExclusiveStartKey(lastKey);
        }

        QueryResult res = client.query(queryRequest);
        List<Map<String, AttributeValue>> items = res.getItems();
        res = client.query(checkRequest);
        List<Map<String, AttributeValue>> checkItems = res.getItems();

        if (items != null) {
            for (Map<String, AttributeValue> item : items) {
                System.out.println(item.toString());
                System.out.println(item.get("followee_first_name").getS());
                System.out.println(item.get("followee_last_name").getS());
                System.out.println(item.get("followee_handle").getS());
                System.out.println(item.get("followee_image").getS());
                User user = new User(item.get("followee_first_name").getS(),
                        item.get("followee_last_name").getS(),
                        item.get("followee_handle").getS(),
                        item.get("followee_image").getS()
                );
                followees.add(user);
            }
        }

        System.out.println("Successfully got following");
        System.out.println(followees);
        if (checkItems.size() > items.size()) {
            return new FollowingResponse(followees, true);
        }
        else {
            System.out.println("We have no more followers to get");
            return new FollowingResponse(followees, false);
        }

//        HashMap<String, Object> valueMap = new HashMap<String, Object>();
//        valueMap.put(":follower", followerUsername);
//
//        HashMap<String, String> nameMap = new HashMap<String, String>();
//        nameMap.put("#foll", "follower_handle");
//
//        List<User> followeesList = null;
//
////        Map<String, AttributeValue> lastPrimaryKeyVal = null;
//
//        QuerySpec querySpec = new QuerySpec().withKeyConditionExpression("#foll = :follower")
//                .withScanIndexForward(true).withNameMap(nameMap)
//                .withValueMap(valueMap).withMaxResultSize(10);
//        if (lastFolloweeUsername != null) {
//            querySpec = querySpec.withExclusiveStartKey("follower_handle",
//                    followerUsername,
//                    "followee_handle", lastFolloweeUsername);
//        }
//
//        ItemCollection<QueryOutcome> items = null;
//        Iterator<Item> iterator = null;
//        Item item = null;
//
//        try {
//            System.out.println("Followers of " + followerUsername);
//            items = follows_table.query(querySpec);
//
//            iterator = items.iterator();
//            while (iterator.hasNext()) {
//                item = iterator.next();
//                System.out.println(item.getString("followee_handle"));
//                followeesList.add(new User(
//                        item.getString("followee_first_name"),
//                        item.getString("followee_last_name"),
//                        item.getString("followee_handle"),
//                        item.getString("followee_image")));
//            }
//
//        } catch (Exception e) {
//            System.err.println("Unable to query followers");
//            System.err.println(e.getMessage());
//        }
//        return followeesList;
    }

    @Override
    public FollowersResponse getFollowers(String followeeUsername, String lastFollowerAlias, int limit) {
        List<User> followers = new ArrayList<>();

        Map<String, String> attNames = new HashMap<String, String>();
        attNames.put("#handle", "followee_handle");

        Map<String, AttributeValue> attValues = new HashMap<>();
        attValues.put(":username", new AttributeValue().withS(followeeUsername));

        QueryRequest queryRequest = new QueryRequest()
                .withTableName("follows")
                .withIndexName("follows_index")
                .withKeyConditionExpression("#handle = :username")
                .withExpressionAttributeNames(attNames)
                .withExpressionAttributeValues(attValues)
                .withLimit(limit);

        QueryRequest checkRequest = new QueryRequest()
                .withTableName("follows")
                .withIndexName("follows_index")
                .withKeyConditionExpression("#handle = :username")
                .withExpressionAttributeNames(attNames)
                .withExpressionAttributeValues(attValues)
                .withLimit(limit + 1);

        if (lastFollowerAlias != null) {
            Map<String, AttributeValue> lastKey = new HashMap<>();
            lastKey.put("followee_handle", new AttributeValue().withS(followeeUsername));
            lastKey.put("follower_handle", new AttributeValue().withS(lastFollowerAlias));

            queryRequest = queryRequest.withExclusiveStartKey(lastKey);
            checkRequest = checkRequest.withExclusiveStartKey(lastKey);
        }

        QueryResult res = client.query(queryRequest);
        List<Map<String, AttributeValue>> items = res.getItems();
        res = client.query(checkRequest);
        List<Map<String, AttributeValue>> checkItems = res.getItems();

        if (items != null) {
            for (Map<String, AttributeValue> item : items) {
                User user = new User(item.get("follower_first_name").getS(),
                        item.get("follower_last_name").getS(),
                        item.get("follower_handle").getS(),
                        item.get("follower_image").getS()
                );
                followers.add(user);
            }
        }

        System.out.println("Successfully got followers");
        System.out.println(followers);
        if (checkItems.size() > items.size()) {
            return new FollowersResponse(followers, true);
        }
        else {
            System.out.println("We have no more followers to get");
            return new FollowersResponse(followers, false);
        }
    }

    @Override
    public void putFollows(String followerUsername, String followerFirstName, String followerLastName,
                           String followerImage, String followeeUsername, String followeeFirstName,
                           String followeeLastName, String followeeImage) {
        try {
            System.out.println("Adding a new follows relationship...");
            PutItemOutcome outcome = follows_table
                    .putItem(new Item().withPrimaryKey("follower_handle",
                            followerUsername, "followee_handle", followeeUsername)
                            .with("follower_first_name", followerFirstName)
                            .with("followee_first_name", followeeFirstName)
                            .with("follower_last_name", followerLastName)
                            .with("followee_last_name", followeeLastName)
                            .with("follower_image", followerImage)
                            .with("followee_image", followeeImage));
            System.out.println("putFollows succeeded:\n" + outcome.getPutItemResult());
        }
        catch (Exception e) {
            System.err.println("Unable to add follows: " + followerUsername + " " + followeeUsername);
            System.err.println(e.getMessage());
        }
    }

    @Override
    public void deleteFollows(String followerUsername, String followeeUsername) {
        DeleteItemSpec deleteItemSpec = new DeleteItemSpec()
                .withPrimaryKey(new PrimaryKey("follower_handle", followerUsername,
                        "followee_handle", followeeUsername));

        try {
            System.out.println("Attempting a delete...");
            follows_table.deleteItem(deleteItemSpec);
            System.out.println("DeleteItem succeeded");
        } catch (Exception e) {
            System.err.println("Unable to delete item: " + followerUsername + " " + followeeUsername);
            System.err.println(e.getMessage());
        }
    }

    @Override
    public boolean getFollows(String followerUsername, String followeeUsername) {
        boolean isFollower = false;
        GetItemSpec spec = new GetItemSpec().withPrimaryKey("follower_handle", followerUsername,
                "followee_handle", followeeUsername);

        try {
            System.out.println("Attempting to read follows relationship...");
            Item outcome = follows_table.getItem(spec);
            System.out.println("getFollows succeeded: " + outcome);
            if (outcome != null) {
                isFollower = true;
            }
        }
        catch (Exception e) {
            System.err.println("Unable to read item: " + followerUsername + " " + followeeUsername);
            System.err.println(e.getMessage());
            isFollower = false;
        }
        return isFollower;
    }


    /**
     * Returns the list of dummy followee data. This is written as a separate method to allow
     * mocking of the followees.
     *
     * @return the followees.
     */
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
