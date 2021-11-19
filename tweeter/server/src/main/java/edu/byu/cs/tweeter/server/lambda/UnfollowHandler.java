package edu.byu.cs.tweeter.server.lambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;

import edu.byu.cs.tweeter.model.net.request.UnfollowRequest;
import edu.byu.cs.tweeter.model.net.response.UnfollowResponse;
import edu.byu.cs.tweeter.server.service.FollowService;

public class UnfollowHandler implements RequestHandler<UnfollowRequest, UnfollowResponse> {


    @Override
    public UnfollowResponse handleRequest(UnfollowRequest request, Context context) {
        FollowService followService = new FollowService();
        try {
            return followService.unfollow(request);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new UnfollowResponse("Error in UnfollowHandler");
    }
}
