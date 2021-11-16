package edu.byu.cs.tweeter.server.lambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;

import edu.byu.cs.tweeter.model.net.request.RegisterRequest;
import edu.byu.cs.tweeter.model.net.response.RegisterResponse;
import edu.byu.cs.tweeter.server.service.UserService;

public class RegisterHandler implements RequestHandler<RegisterRequest, RegisterResponse> {

    @Override
    public RegisterResponse handleRequest(RegisterRequest registerRequest, Context context) {
        UserService userService = new UserService();
        System.out.println("In regsiterHandler");
        RegisterResponse registerResponse = userService.register(registerRequest);
        System.out.println("This is registerResponse.getRegisteredUser: " + registerResponse.getRegisteredUser());
        System.out.println("This is registerResponse.getAuthToken: " + registerResponse.getAuthToken());
        return registerResponse;
        //return userService.register(registerRequest);
    }
}
