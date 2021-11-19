package edu.byu.cs.tweeter.server.lambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;

import edu.byu.cs.tweeter.model.net.request.FollowRequest;
import edu.byu.cs.tweeter.model.net.response.FollowResponse;
import edu.byu.cs.tweeter.server.service.FollowService;

public class FollowHandler implements RequestHandler<FollowRequest, FollowResponse> {


    @Override
    public FollowResponse handleRequest(FollowRequest request, Context context) {
        FollowService followService = new FollowService();
        System.out.println("In followHandler");
        //System.out.println("Curr user in followHandler: " + ServerCache.getInstance().getCurrUser());
        try {
            return followService.follow(request);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new FollowResponse("Error in FollowHandler");
    }
}
