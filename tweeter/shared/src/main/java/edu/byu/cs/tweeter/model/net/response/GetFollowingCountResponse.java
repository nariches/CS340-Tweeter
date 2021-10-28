package edu.byu.cs.tweeter.model.net.response;

public class GetFollowingCountResponse extends Response {

    private int followingCount;

    public GetFollowingCountResponse(String message) {
        super(false, message);
    }

    public GetFollowingCountResponse(int followingCount) {
        super(true, null);
        this.followingCount = followingCount;
    }

    public int getFollowingCount() {
        return followingCount;
    }
}
