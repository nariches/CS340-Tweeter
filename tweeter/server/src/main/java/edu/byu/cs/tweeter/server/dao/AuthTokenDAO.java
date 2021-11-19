package edu.byu.cs.tweeter.server.dao;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.PrimaryKey;
import com.amazonaws.services.dynamodbv2.document.PutItemOutcome;
import com.amazonaws.services.dynamodbv2.document.ScanOutcome;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.document.UpdateItemOutcome;
import com.amazonaws.services.dynamodbv2.document.spec.DeleteItemSpec;
import com.amazonaws.services.dynamodbv2.document.spec.GetItemSpec;
import com.amazonaws.services.dynamodbv2.document.spec.UpdateItemSpec;
import com.amazonaws.services.dynamodbv2.document.utils.ValueMap;
import com.amazonaws.services.dynamodbv2.model.ReturnValue;

import java.security.SecureRandom;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Calendar;
import java.util.Date;

import edu.byu.cs.tweeter.model.domain.AuthToken;

public class AuthTokenDAO implements IAuthTokenDAO {

    private AmazonDynamoDB client;
    private DynamoDB dynamoDB;
    private Table auth_table;

    public AuthTokenDAO() {
        this.client = AmazonDynamoDBClientBuilder.standard().withRegion("us-east-2").build();
        this.dynamoDB = new DynamoDB(client);
        this.auth_table = dynamoDB.getTable("authtoken");
    }

    private static final SecureRandom secureRandom = new SecureRandom(); //threadsafe
    private static final Base64.Encoder base64Encoder = Base64.getUrlEncoder(); //threadsafe

    @Override
    public AuthToken putAuthToken() throws Exception {
        String token = generateNewToken();

        Date date = Calendar.getInstance().getTime();
        long currTimeLong = date.getTime();
        String currTimeStr = String.valueOf(currTimeLong);
//        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-mm-dd hh:mm:ss");
//        String strDate = dateFormat.format(date);

        AuthToken authToken = new AuthToken(token, currTimeStr);
        try {
            PutItemOutcome outcome = auth_table.putItem(new Item().withPrimaryKey("token", token)
            .with("date_time", currTimeStr));
            System.out.println("putToken succeeded:\n" + outcome.getPutItemResult());
        }
        catch (Exception e) {
            throw new Exception("Exception in putAuthToken");
        }
        return authToken;
    }

    @Override
    public AuthToken getAuthToken(AuthToken authToken) {
        GetItemSpec spec = new GetItemSpec().withPrimaryKey("token", authToken.getToken());
        System.out.println("Attempting to get authToken from authtoken table...");
        Item outcome = auth_table.getItem(spec);
        System.out.println("getUser succeeded: " + outcome);

        return new AuthToken(outcome.getString("token"), outcome.getString("date_time"));
    }

    @Override
    public AuthToken validateAuthToken(AuthToken authToken) throws Exception {
        //Date date = Calendar.getInstance().getTime();

        GetItemSpec spec = new GetItemSpec().withPrimaryKey("token", authToken.getToken());
        Item authItem = auth_table.getItem(spec);
        //SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-mm-dd hh:mm:ss");

        System.out.println("authItem: " + authItem.toString());
        System.out.println("authItem.getString(date_time): " + authItem.getString("date_time"));
        //System.out.println("authItem.getString");
        //Date storedDate = dateFormat.parse(authItem.getString("date_time"));
        String storedDateStr = authItem.getString("date_time");
        System.out.println("storedDate: " + storedDateStr);
        //System.out.println("date.getTime():" + date.getTime());
        long storedDateLong = Long.parseLong(storedDateStr);

        //Get current time
        Date date = Calendar.getInstance().getTime();
        long currTimeLong = date.getTime();

        System.out.println("currTimeLong" + currTimeLong);

        long timeDiff = currTimeLong - storedDateLong;
        System.out.println("timeDiff:" + timeDiff);
        long minDiff = (timeDiff / 1000) / 60;
        System.out.println("minDiff:" + minDiff);
        if (minDiff >= 10) {
            System.out.println("Authtoken is expired");
            DeleteItemSpec deleteItemSpec = new DeleteItemSpec()
                    .withPrimaryKey(new PrimaryKey("token", authToken.getToken()));
            auth_table.deleteItem(deleteItemSpec);
            throw new Exception();
        }
        else {
            //System.out.println("Updating authtoken: " + authToken.getToken());
            //String dateString = dateFormat.format(date);
            // Update time and return true

            String currTimeStr = String.valueOf(currTimeLong);

            UpdateItemSpec updateItemSpec = new UpdateItemSpec().withPrimaryKey("token", authToken.getToken())
                    .withUpdateExpression("set date_time = :date_time")
                    .withValueMap(new ValueMap().withString(":date_time", currTimeStr))
                    .withReturnValues(ReturnValue.UPDATED_NEW);
            try {
                System.out.println("Updating authtoken...");
                System.out.println("AuthToken token passed in: " + authToken);
                System.out.println("AuthToken token we are updating: " + authItem.getString("token"));
                System.out.println("AuthToken datetime passed in: " + authToken);
                System.out.println("AuthToken datetime we are updating: " + authItem.getString("date_time"));
                UpdateItemOutcome outcome = auth_table.updateItem(updateItemSpec);
                System.out.println("updateAuthToken succeeded:\n" + outcome.getItem().toJSONPretty());
            }
            catch (Exception e) {
                System.err.println("Unable to update Authtoken: " + authToken.getToken());
                System.err.println(e.getMessage());
            }
            return new AuthToken(authToken.getToken(), currTimeStr);
        }
    }

    @Override
    public void deleteAuthToken(AuthToken authToken) {
        DeleteItemSpec deleteItemSpec = new DeleteItemSpec()
                .withPrimaryKey(new PrimaryKey("token", authToken.getToken()));

        try {
            System.out.println("Attempting authToken delete...");
            System.out.println("AuthToken token passed in: " + authToken);
            System.out.println("AuthToken token we are deleting: " + deleteItemSpec.toString());
            System.out.println("AuthToken passed in: " + authToken);
            System.out.println("AuthToken we are deleting: " + deleteItemSpec.toString());
            auth_table.deleteItem(deleteItemSpec);
            System.out.println("DeleteItem succeeded");
        } catch (Exception e) {
            System.err.println("Unable to delete authToken: " + authToken.getToken() + " " +
                    authToken.getDatetime());
            System.err.println(e.getMessage());
        }
    }


    private static String generateNewToken() {
        byte[] randomBytes = new byte[24];
        secureRandom.nextBytes(randomBytes);
        return base64Encoder.encodeToString(randomBytes);
    }
}
