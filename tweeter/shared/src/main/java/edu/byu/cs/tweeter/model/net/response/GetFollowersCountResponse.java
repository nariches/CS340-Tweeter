package edu.byu.cs.tweeter.model.net.response;

import edu.byu.cs.tweeter.model.domain.User;

public class GetFollowersCountResponse extends Response {

    private int followersCount;

    public GetFollowersCountResponse(String message) {
        super(false, message);
    }

    public GetFollowersCountResponse(int followersCount) {
        super(true, null);
        this.followersCount = followersCount;
    }

    public int getFollowersCount() {
        return followersCount;
    }
}
