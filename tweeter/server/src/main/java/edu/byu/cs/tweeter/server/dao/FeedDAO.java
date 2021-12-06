package edu.byu.cs.tweeter.server.dao;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.BatchWriteItemOutcome;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.PutItemOutcome;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.document.TableWriteItems;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.QueryRequest;
import com.amazonaws.services.dynamodbv2.model.QueryResult;
import com.amazonaws.services.dynamodbv2.model.WriteRequest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.domain.UserDTO;
import edu.byu.cs.tweeter.model.net.response.FeedResponse;
import edu.byu.cs.tweeter.model.net.response.StoryResponse;

public class FeedDAO implements IFeedDAO {

    private AmazonDynamoDB client;
    private DynamoDB dynamoDB;
    private Table feed_table;

    public FeedDAO() {
        this.client = AmazonDynamoDBClientBuilder.standard().withRegion("us-east-2").build();
        this.dynamoDB = new DynamoDB(client);
        this.feed_table = dynamoDB.getTable("feed");
    }

    @Override
    public FeedResponse getFeed(String receiverUsername, String lastStatusDateTime, int limit) {
        List<Status> feed = new ArrayList<>();

        Map<String, String> attNames = new HashMap<String, String>();
        attNames.put("#rec_user", "receiver_username");

        Map<String, AttributeValue> attValues = new HashMap<>();
        attValues.put(":username", new AttributeValue().withS(receiverUsername));

        QueryRequest queryRequest = new QueryRequest()
                .withTableName("feed")
                .withKeyConditionExpression("#rec_user = :username")
                .withScanIndexForward(false)
                .withExpressionAttributeNames(attNames)
                .withExpressionAttributeValues(attValues)
                .withLimit(limit);

        QueryRequest checkRequest = new QueryRequest()
                .withTableName("feed")
                .withKeyConditionExpression("#rec_user = :username")
                .withScanIndexForward(false)
                .withExpressionAttributeNames(attNames)
                .withExpressionAttributeValues(attValues)
                .withLimit(limit + 1);

        if (lastStatusDateTime != null) {
            Map<String, AttributeValue> lastKey = new HashMap<>();
            lastKey.put("receiver_username", new AttributeValue().withS(receiverUsername));
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
                feed.add(status);
            }
        }

        System.out.println("Successfully got feed");
        System.out.println(feed);
        if (checkItems.size() > items.size()) {
            return new FeedResponse(feed, true);
        }
        else {
            System.out.println("We have no more statuses to get");
            return new FeedResponse(feed, false);
        }
    }

    @Override
    public void putFeed(Status status, String receiverUsername) {
        HashSet<String> mentionsSet = new HashSet<>(status.getMentions());
        HashSet<String> urlsSet = new HashSet<>(status.getUrls());
        mentionsSet.add("");
        urlsSet.add("");

        PutItemOutcome outcome = feed_table.putItem(new Item()
                .withPrimaryKey("receiver_username", receiverUsername,
                        "date_time", status.getDate())
                .with("sender_username", status.getUser().getAlias())
                .with("mentions", mentionsSet)
                .with("post", status.getPost())
                .with("sender_first_name", status.getUser().getFirstName())
                .with("sender_last_name", status.getUser().getLastName())
                .with("sender_image", status.getUser().getImageUrl())
                .with("urls", urlsSet));
        System.out.println("putFeed succeeded:\n" + outcome.getPutItemResult());
    }

    @Override
    public void addFeedBatch(Status status, List<UserDTO> followers) {
        TableWriteItems items = new TableWriteItems("feed");

        for (UserDTO user: followers) {
            HashSet<String> mentionsSet = new HashSet<>(status.getMentions());
            HashSet<String> urlsSet = new HashSet<>(status.getUrls());
            mentionsSet.add("");
            urlsSet.add("");
            Item item = new Item().withPrimaryKey("receiver_username", user.getAlias(),
                    "date_time", status.getDate())
                    .with("sender_username", status.getUser().getAlias())
                    .with("mentions", mentionsSet)
                    .with("post", status.getPost())
                    .with("sender_first_name", status.getUser().getFirstName())
                    .with("sender_last_name", status.getUser().getLastName())
                    .with("urls", urlsSet);
            items.addItemToPut(item);
            // 25 is the maximum number of items allowed in a single batch write.
            // Attempting to write more than 25 items will result in an exception being thrown
            if (items.getItemsToPut() != null && items.getItemsToPut().size() == 25) {
                loopBatchWrite(items);
                items = new TableWriteItems("feed");
            }

        }
    }

    private void loopBatchWrite(TableWriteItems items) {
        // The 'dynamoDB' object is of type DynamoDB and is declared statically in this example
        BatchWriteItemOutcome outcome = dynamoDB.batchWriteItem(items);
        //System.out.println("Wrote Feed Batch");

        // Check the outcome for items that didn't make it onto the table
        // If any were not added to the table, try again to write the batch
        while (outcome.getUnprocessedItems().size() > 0) {
            Map<String, List<WriteRequest>> unprocessedItems = outcome.getUnprocessedItems();
            outcome = dynamoDB.batchWriteItemUnprocessed(unprocessedItems);
            //System.out.println("Wrote more Feeds");
        }
    }
}
