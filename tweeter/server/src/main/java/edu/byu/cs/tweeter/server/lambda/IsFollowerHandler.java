package edu.byu.cs.tweeter.server.lambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;

import edu.byu.cs.tweeter.model.net.request.IsFollowerRequest;
import edu.byu.cs.tweeter.model.net.response.IsFollowerResponse;
import edu.byu.cs.tweeter.server.service.FollowService;

public class IsFollowerHandler implements RequestHandler<IsFollowerRequest, IsFollowerResponse> {


    @Override
    public IsFollowerResponse handleRequest(IsFollowerRequest request, Context context) {
        FollowService followService = new FollowService();
        IsFollowerResponse isFollowerResponse = null;
        try {
            isFollowerResponse = followService.isFollower(request);
            System.out.println("isFollowerResponse in handler: " + isFollowerResponse.isFollowerBool());
            return followService.isFollower(request);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new IsFollowerResponse("Error in IsFollowerHandler");
    }
}
