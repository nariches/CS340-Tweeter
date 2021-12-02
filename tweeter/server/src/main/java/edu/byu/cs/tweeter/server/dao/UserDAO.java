package edu.byu.cs.tweeter.server.dao;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.BatchWriteItemOutcome;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.PutItemOutcome;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.document.TableWriteItems;
import com.amazonaws.services.dynamodbv2.document.UpdateItemOutcome;
import com.amazonaws.services.dynamodbv2.document.spec.GetItemSpec;
import com.amazonaws.services.dynamodbv2.document.spec.UpdateItemSpec;
import com.amazonaws.services.dynamodbv2.document.utils.ValueMap;
import com.amazonaws.services.dynamodbv2.model.ReturnValue;
import com.amazonaws.services.dynamodbv2.model.WriteRequest;
import com.amazonaws.services.dynamodbv2.xspec.S;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.domain.UserDTO;
import edu.byu.cs.tweeter.model.net.request.GetUserRequest;
import edu.byu.cs.tweeter.model.net.request.LoginRequest;
import edu.byu.cs.tweeter.model.net.request.LogoutRequest;
import edu.byu.cs.tweeter.model.net.request.RegisterRequest;
import edu.byu.cs.tweeter.model.net.response.GetUserResponse;
import edu.byu.cs.tweeter.model.net.response.LoginResponse;
import edu.byu.cs.tweeter.model.net.response.LogoutResponse;
import edu.byu.cs.tweeter.model.net.response.RegisterResponse;
import edu.byu.cs.tweeter.server.util.FakeData;
import edu.byu.cs.tweeter.util.Pair;

public class UserDAO implements IUserDAO {

    private AmazonDynamoDB client;
    private DynamoDB dynamoDB;
    private Table users_table;



    public UserDAO() {
        this.client = AmazonDynamoDBClientBuilder.standard().withRegion("us-east-2").build();
        this.dynamoDB = new DynamoDB(client);
        this.users_table = dynamoDB.getTable("users");
    }

    @Override
    public User getUser(String username) {
        GetItemSpec spec = new GetItemSpec().withPrimaryKey("username", username);
        System.out.println("Attempting to get user from users_table...");
        Item outcome = users_table.getItem(spec);
        System.out.println("getUser succeeded: " + outcome);

        User user = new User(outcome.getString("first_name"),
                outcome.getString("last_name"),
                outcome.getString("username"),
                outcome.getString("image"));
        return user;
    }

    @Override
    public String getUserPassword(String username) {
        GetItemSpec spec = new GetItemSpec().withPrimaryKey("username", username);
        System.out.println("Attempting to get user password from users_table...");
        Item outcome = users_table.getItem(spec);
        System.out.println("getUser succeeded: " + outcome);

        String userPassword = outcome.getString("password");
        return userPassword;
    }

    @Override
    public User putUser(String firstName, String lastName, String username, String password, String image,
                        int followerCount, int followingCount) {
        PutItemOutcome outcome = users_table.putItem(new Item()
                .withPrimaryKey("username", username)
                .with("password", password)
                .with("first_name", firstName)
                .with("last_name", lastName)
                .with("image", image)
                .with("follower_count", followerCount)
                .with("following_count", followingCount));
        System.out.println("Register succeeded:\n" + outcome.getPutItemResult());
        return new User(firstName, lastName, username, image);
    }

    @Override
    public int getFollowerCount(String username) {
        GetItemSpec spec = new GetItemSpec().withPrimaryKey("username", username);
        System.out.println("Attempting to get user followerCount from users_table...");
        Item outcome = users_table.getItem(spec);
        System.out.println("getFollowerCount succeeded: " + outcome);

        int followerCount = outcome.getInt("follower_count");
        return followerCount;
    }

    @Override
    public int getFollowingCount(String username) {
        GetItemSpec spec = new GetItemSpec().withPrimaryKey("username", username);
        System.out.println("Attempting to get user followingCount from users_table...");
        Item outcome = users_table.getItem(spec);
        System.out.println("getFollowingCount succeeded: " + outcome);

        int followingCount = outcome.getInt("following_count");
        return followingCount;
    }

    @Override
    public void incrementFollowerCount(String username) {
        int numFollowers = getFollowerCount(username);
        numFollowers += 1;

        UpdateItemSpec updateItemSpec = new UpdateItemSpec().withPrimaryKey("username",
                username)
                .withUpdateExpression("set follower_count = :count")
                .withValueMap(new ValueMap().withNumber(":count", numFollowers))
                .withReturnValues(ReturnValue.UPDATED_NEW);
        try {
            System.out.println("Incrementing follower_count...");
            UpdateItemOutcome outcome = users_table.updateItem(updateItemSpec);
            System.out.println("incrementFollowerCount succeeded:\n" + outcome.getItem().toJSONPretty());

        }
        catch (Exception e) {
            System.err.println("Unable to increment follower_count: " + username);
            System.err.println(e.getMessage());
        }
    }

    @Override
    public void incrementFollowingCount(String username) {
        int numFollowing = getFollowingCount(username);
        numFollowing += 1;

        UpdateItemSpec updateItemSpec = new UpdateItemSpec().withPrimaryKey("username",
                username)
                .withUpdateExpression("set following_count = :count")
                .withValueMap(new ValueMap().withNumber(":count", numFollowing))
                .withReturnValues(ReturnValue.UPDATED_NEW);
        try {
            System.out.println("Incrementing following_count...");
            UpdateItemOutcome outcome = users_table.updateItem(updateItemSpec);
            System.out.println("incrementFollowingCount succeeded:\n" + outcome.getItem().toJSONPretty());

        }
        catch (Exception e) {
            System.err.println("Unable to increment following_count: " + username);
            System.err.println(e.getMessage());
        }
    }

    @Override
    public void decrementFollowerCount(String username) {
        int numFollowers = getFollowerCount(username);
        numFollowers -= 1;

        UpdateItemSpec updateItemSpec = new UpdateItemSpec().withPrimaryKey("username",
                username)
                .withUpdateExpression("set follower_count = :count")
                .withValueMap(new ValueMap().withNumber(":count", numFollowers))
                .withReturnValues(ReturnValue.UPDATED_NEW);
        try {
            System.out.println("Decrementing follower_count...");
            UpdateItemOutcome outcome = users_table.updateItem(updateItemSpec);
            System.out.println("decrementFollowerCount succeeded:\n" + outcome.getItem().toJSONPretty());

        }
        catch (Exception e) {
            System.err.println("Unable to decrement follower_count: " + username);
            System.err.println(e.getMessage());
        }
    }

    @Override
    public void decremementFollowingCount(String username) {
        int numFollowing = getFollowingCount(username);
        numFollowing -= 1;

        UpdateItemSpec updateItemSpec = new UpdateItemSpec().withPrimaryKey("username",
                username)
                .withUpdateExpression("set following_count = :count")
                .withValueMap(new ValueMap().withNumber(":count", numFollowing))
                .withReturnValues(ReturnValue.UPDATED_NEW);
        try {
            System.out.println("Decrementing following_count...");
            UpdateItemOutcome outcome = users_table.updateItem(updateItemSpec);
            System.out.println("decrementFollowingCount succeeded:\n" + outcome.getItem().toJSONPretty());

        }
        catch (Exception e) {
            System.err.println("Unable to decrement following_count: " + username);
            System.err.println(e.getMessage());
        }
    }

    @Override
    public void addUserBatch(List<UserDTO> users) {
        // Constructor for TableWriteItems takes the name of the table, which I have stored in TABLE_USER
        TableWriteItems items = new TableWriteItems(users_table.getTableName());

        // Add each user into the TableWriteItems object
        for (UserDTO user : users) {
            Item item = new Item()
                    .withPrimaryKey("username", user.getAlias())
                    .withString("name", user.getName());
            items.addItemToPut(item);

            // 25 is the maximum number of items allowed in a single batch write.
            // Attempting to write more than 25 items will result in an exception being thrown
            if (items.getItemsToPut() != null && items.getItemsToPut().size() == 25) {
                loopBatchWrite(items);
                items = new TableWriteItems(users_table.getTableName());
            }
        }

        // Write any leftover items
        if (items.getItemsToPut() != null && items.getItemsToPut().size() > 0) {
            loopBatchWrite(items);
        }
    }

    private void loopBatchWrite(TableWriteItems items) {

        // The 'dynamoDB' object is of type DynamoDB and is declared statically in this example
        BatchWriteItemOutcome outcome = dynamoDB.batchWriteItem(items);
        System.out.println("Wrote User Batch");

        // Check the outcome for items that didn't make it onto the table
        // If any were not added to the table, try again to write the batch
        while (outcome.getUnprocessedItems().size() > 0) {
            Map<String, List<WriteRequest>> unprocessedItems = outcome.getUnprocessedItems();
            outcome = dynamoDB.batchWriteItemUnprocessed(unprocessedItems);
            System.out.println("Wrote more Users");
        }
    }

}
