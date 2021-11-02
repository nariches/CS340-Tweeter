package edu.byu.cs.tweeter.server.service;

import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.request.FollowersRequest;
import edu.byu.cs.tweeter.model.net.request.FollowingRequest;
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

    /**
     * Returns the users that the user specified in the request is following. Uses information in
     * the request object to limit the number of followees returned and to return the next set of
     * followees after any that were returned in a previous request. Uses the {@link FollowDAO} to
     * get the followees.
     *
     * @param request contains the data required to fulfill the request.
     * @return the followees.
     */
    public FollowingResponse getFollowees(FollowingRequest request) {
        return getFollowingDAO().getFollowees(request);
    }

    public FollowersResponse getFollowers(FollowersRequest request) {
        return getFollowingDAO().getFollowers(request);
    }

    public GetFollowersCountResponse getFollowersCount(String username, AuthToken authToken) {
        return getFollowingDAO().getFollowersCount(username, authToken);
    }

    public GetFollowingCountResponse getFollowingCount(String username, AuthToken authToken) {
        return getFollowingDAO().getFollowingCount(username, authToken);
    }

    public FollowResponse follow(AuthToken authToken, String username) {
        return getFollowingDAO().follow(authToken, username);
    }

    public UnfollowResponse unfollow(AuthToken authToken, String username) {
        return getFollowingDAO().unfollow(authToken, username);
    }

    public IsFollowerResponse isFollower(AuthToken authToken, String followerUsername,
                                         String followeeUsername) {
        return getFollowingDAO().isFollower(authToken, followerUsername, followeeUsername);
    }

    /**
     * Returns an instance of {@link FollowDAO}. Allows mocking of the FollowDAO class
     * for testing purposes. All usages of FollowDAO should get their FollowDAO
     * instance from this method to allow for mocking of the instance.
     *
     * @return the instance.
     */
    FollowDAO getFollowingDAO() {
        return new FollowDAO();
    }
}
