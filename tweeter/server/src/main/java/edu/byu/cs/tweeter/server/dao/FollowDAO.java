package edu.byu.cs.tweeter.server.dao;

import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.ItemCollection;
import com.amazonaws.services.dynamodbv2.document.PutItemOutcome;
import com.amazonaws.services.dynamodbv2.document.QueryOutcome;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.document.spec.QuerySpec;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
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
public class FollowDAO implements IFollowDAO {

    /**
     * Gets the count of users from the database that the user specified is following. The
     * current implementation uses generated data and doesn't actually access a database.
     *
     * @param //follower the User whose count of how many following is desired.
     * @return said count.
     */

    @Override
    public GetFollowersCountResponse getFollowersCount(GetFollowersCountRequest getFollowersCountRequest) {
        assert getFollowersCountRequest.getUsername() != null;
        return new GetFollowersCountResponse("Replace me");
    }

    @Override
    public GetFollowingCountResponse getFollowingCount(GetFollowingCountRequest getFollowingCountRequest) {
        assert getFollowingCountRequest.getUsername() != null;
        return new GetFollowingCountResponse(getDummyFollowees().size());
    }

    @Override
    public FollowResponse follow(FollowRequest followRequest) { //put
        Table follows_table = dynamoDB.getTable("follows");
        assert followRequest.getUsername() != null;

        //PutItemOutcome outcome = follows_table.putItem(new Item().withPrimaryKey("follower_handle",
        //        followRequest.))

                return new FollowResponse();
    }

    @Override
    public UnfollowResponse unfollow(UnfollowRequest unfollowRequest) { //delete
        Table follows_table = dynamoDB.getTable("follows");
        assert unfollowRequest.getUsername() != null;

        return new UnfollowResponse();
    }

    @Override
    public IsFollowerResponse isFollower(IsFollowerRequest isFollowerRequest) { //get?
        assert isFollowerRequest.getFollowerUsername() != null;
        assert isFollowerRequest.getFolloweeUsername() != null;
        return new IsFollowerResponse();
    }

    /**
     * Gets the users from the database that the user specified in the request is following. Uses
     * information in the request object to limit the number of followees returned and to return the
     * next set of followees after any that were returned in a previous request. The current
     * implementation returns generated data and doesn't actually access a database.
     *
     * @param request contains information about the user whose followees are to be returned and any
     *                other information required to satisfy the request.
     * @return the followees.
     */

    @Override
    public FollowingResponse getFollowees(FollowingRequest request) { //get
        // TODO: Generates dummy data. Replace with a real implementation.
        assert request.getLimit() > 0;
        assert request.getFollowerAlias() != null;

        List<User> allFollowees = getDummyFollowees();
        List<User> responseFollowees = new ArrayList<>(request.getLimit());

        boolean hasMorePages = false;

        if(request.getLimit() > 0) {
            if (allFollowees != null) {
                int followeesIndex = getFolloweesStartingIndex(request.getLastFolloweeAlias(), allFollowees);

                for(int limitCounter = 0; followeesIndex < allFollowees.size() && limitCounter < request.getLimit(); followeesIndex++, limitCounter++) {
                    responseFollowees.add(allFollowees.get(followeesIndex));
                }

                hasMorePages = followeesIndex < allFollowees.size();
            }
        }

        return new FollowingResponse(responseFollowees, hasMorePages);
    }

    @Override
    public FollowersResponse getFollowers(FollowersRequest request) { //get
        assert request.getLimit() > 0;
        assert request.getFolloweeAlias() != null;

        List<User> allFollowers = getDummyFollowers();
        List<User> responseFollowers = new ArrayList<>(request.getLimit());

        boolean hasMorePages = false;

        if(request.getLimit() > 0) {
            if (allFollowers != null) {
                int followersIndex = getFollowersStartingIndex(request.getLastFollowerAlias(), allFollowers);

                for(int limitCounter = 0; followersIndex < allFollowers.size() && limitCounter < request.getLimit(); followersIndex++, limitCounter++) {
                    responseFollowers.add(allFollowers.get(followersIndex));
                }

                hasMorePages = followersIndex < allFollowers.size();
            }
        }

        return new FollowersResponse(responseFollowers, hasMorePages);

    }

    public FollowingResponse queryPaginated(Table table, String follower_handle) { //GET FOLLOWING
        HashMap<String, Object> valueMap = new HashMap<String, Object>();
        valueMap.put(":follower", follower_handle);

        HashMap<String, String> nameMap = new HashMap<String, String>();
        nameMap.put("#foll", "follower_handle");

        List<User> followeesList = null;

        Map<String, AttributeValue> lastPrimaryKeyVal = null;
        do {
            QuerySpec querySpec = new QuerySpec().withKeyConditionExpression("#foll = :follower")
                    .withScanIndexForward(true).withNameMap(nameMap)
                    .withValueMap(valueMap).withMaxResultSize(10);
            if (lastPrimaryKeyVal != null) {
                querySpec = querySpec.withExclusiveStartKey("follower_handle",
                        lastPrimaryKeyVal.get("follower_handle").getS(),
                        "followee_handle", lastPrimaryKeyVal.get("followee_handle").getS());
            }

            ItemCollection<QueryOutcome> items = null;
            Iterator<Item> iterator = null;
            Item item = null;




            try {
                System.out.println("Followers of " + follower_handle);
                items = table.query(querySpec);

                iterator = items.iterator();
                while (iterator.hasNext()) {
                    item = iterator.next();
                    System.out.println(item.getString("followee_handle"));
                    //followeesList.add(item.getString("followee_handle"));
                }

            } catch (Exception e) {
                System.err.println("Unable to query followers");
                System.err.println(e.getMessage());
            }

            //Get last primary key value
            QueryOutcome queryOutcomeRef = items.getLastLowLevelResult();
            QueryResult queryResultRef = queryOutcomeRef.getQueryResult();
            lastPrimaryKeyVal = queryResultRef.getLastEvaluatedKey();

        } while (lastPrimaryKeyVal != null);
        return new FollowingResponse(followeesList, false);
    }

    /**
     * Determines the index for the first followee in the specified 'allFollowees' list that should
     * be returned in the current request. This will be the index of the next followee after the
     * specified 'lastFollowee'.
     *
     * @param lastFolloweeAlias the alias of the last followee that was returned in the previous
     *                          request or null if there was no previous request.
     * @param allFollowees the generated list of followees from which we are returning paged results.
     * @return the index of the first followee to be returned.
     */
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
