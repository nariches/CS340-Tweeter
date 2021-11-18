package edu.byu.cs.tweeter.server.dao;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.PutItemOutcome;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.Put;
import com.amazonaws.services.dynamodbv2.model.QueryRequest;
import com.amazonaws.services.dynamodbv2.model.QueryResult;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.response.FollowingResponse;
import edu.byu.cs.tweeter.model.net.response.StoryResponse;

public class StoryDAO implements IStoryDAO {

    private AmazonDynamoDB client;
    private DynamoDB dynamoDB;
    private Table story_table;

    public StoryDAO() {
        this.client = AmazonDynamoDBClientBuilder.standard().withRegion("us-east-2").build();
        this.dynamoDB = new DynamoDB(client);
        this.story_table = dynamoDB.getTable("story");
    }


    @Override
    public StoryResponse getStory(String senderUsername, String lastStatusDateTime, int limit) {
        List<Status> story = new ArrayList<>();

        Map<String, String> attNames = new HashMap<String, String>();
        attNames.put("#send_user", "sender_username");

        Map<String, AttributeValue> attValues = new HashMap<>();
        attValues.put(":username", new AttributeValue().withS(senderUsername));

        QueryRequest queryRequest = new QueryRequest()
                .withTableName("story")
                .withKeyConditionExpression("#send_user = :username")
                .withScanIndexForward(false)
                .withExpressionAttributeNames(attNames)
                .withExpressionAttributeValues(attValues)
                .withLimit(limit);

        QueryRequest checkRequest = new QueryRequest()
                .withTableName("story")
                .withKeyConditionExpression("#send_user = :username")
                .withScanIndexForward(false)
                .withExpressionAttributeNames(attNames)
                .withExpressionAttributeValues(attValues)
                .withLimit(limit + 1);

        if (lastStatusDateTime != null) {
            Map<String, AttributeValue> lastKey = new HashMap<>();
            lastKey.put("sender_username", new AttributeValue().withS(senderUsername));
            lastKey.put("date_time", new AttributeValue().withS(lastStatusDateTime));

            queryRequest = queryRequest.withExclusiveStartKey(lastKey);
            checkRequest = checkRequest.withExclusiveStartKey(lastKey);
        }

        QueryResult res = client.query(queryRequest);
        List<Map<String, AttributeValue>> items = res.getItems();
        res = client.query(checkRequest);
        List<Map<String, AttributeValue>> checkItems = res.getItems();

        if (items != null) {
            for (Map<String, AttributeValue> item : items) {
                User user = new User(item.get("sender_first_name").getS(),
                        item.get("sender_last_name").getS(),
                        item.get("sender_username").getS(),
                        item.get("sender_image").getS());
                List<String> urls = item.get("urls").getSS();
                List<String> mentions = item.get("mentions").getSS();
                Status status = new Status(item.get("post").getS(),
                        user, item.get("date_time").getS(),
                        urls, mentions);
                story.add(status);
            }
        }

        System.out.println("Successfully got story");
        System.out.println(story);
        if (checkItems.size() > items.size()) {
            return new StoryResponse(story, true);
        }
        else {
            System.out.println("We have no more statuses to get");
            return new StoryResponse(story, false);
        }
    }

    @Override
    public void putStory(Status status) {
        HashSet<String> mentionsSet = new HashSet<>(status.getMentions());
        HashSet<String> urlsSet = new HashSet<>(status.getUrls());
        mentionsSet.add("");
        urlsSet.add("");

        PutItemOutcome outcome = story_table.putItem(new Item()
                .withPrimaryKey("sender_username", status.getUser().getAlias(),
                        "date_time", status.getDate())
                .with("mentions", mentionsSet)
                .with("post", status.getPost())
                .with("sender_first_name", status.getUser().getFirstName())
                .with("sender_last_name", status.getUser().getLastName())
                .with("sender_image", status.getUser().getImageUrl())
                .with("urls", urlsSet));
        System.out.println("putStory succeeded:\n" + outcome.getPutItemResult());
    }
}
