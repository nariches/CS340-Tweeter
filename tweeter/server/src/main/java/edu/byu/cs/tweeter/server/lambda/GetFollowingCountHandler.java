package edu.byu.cs.tweeter.server.lambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;

import edu.byu.cs.tweeter.model.net.request.GetFollowingCountRequest;
import edu.byu.cs.tweeter.model.net.response.GetFollowingCountResponse;
import edu.byu.cs.tweeter.server.service.FollowService;

public class GetFollowingCountHandler implements RequestHandler<GetFollowingCountRequest, GetFollowingCountResponse> {


    @Override
    public GetFollowingCountResponse handleRequest(GetFollowingCountRequest request, Context context) {
        FollowService followService = new FollowService();
        try {
            return followService.getFollowingCount(request);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new GetFollowingCountResponse("Error in GetFollowingCountHandler");
    }
}
