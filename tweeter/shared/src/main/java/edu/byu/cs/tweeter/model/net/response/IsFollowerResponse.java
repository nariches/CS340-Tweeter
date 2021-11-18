package edu.byu.cs.tweeter.model.net.response;

public class IsFollowerResponse extends Response {

    private boolean followerBool;

    public IsFollowerResponse(String message) {
        super(false, message);
    }

    public IsFollowerResponse() {
        super(true, null);
    }

    public boolean isFollowerBool() {
        return followerBool;
    }

    public void setFollowerBool(boolean followerBool) {
        this.followerBool = followerBool;
    }
}
