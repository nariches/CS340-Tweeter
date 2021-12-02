package edu.byu.cs.tweeter.server.dao;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.PutItemOutcome;
import com.amazonaws.services.dynamodbv2.document.Table;

import java.util.List;

import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.domain.UserDTO;
import edu.byu.cs.tweeter.model.net.request.GetUserRequest;
import edu.byu.cs.tweeter.model.net.request.LoginRequest;
import edu.byu.cs.tweeter.model.net.request.LogoutRequest;
import edu.byu.cs.tweeter.model.net.request.RegisterRequest;
import edu.byu.cs.tweeter.model.net.response.GetUserResponse;
import edu.byu.cs.tweeter.model.net.response.LoginResponse;
import edu.byu.cs.tweeter.model.net.response.LogoutResponse;
import edu.byu.cs.tweeter.model.net.response.RegisterResponse;
import edu.byu.cs.tweeter.util.Pair;

public interface IUserDAO {

    User getUser(String username);
    String getUserPassword(String username);
    User putUser(String firstName, String lastName, String username, String password, String image,
                 int followerCount, int followingCount);
    int getFollowerCount(String username);
    int getFollowingCount(String username);
    void incrementFollowerCount(String username);
    void incrementFollowingCount(String username);
    void decrementFollowerCount(String username);
    void decremementFollowingCount(String username);
    void addUserBatch(List<UserDTO> users);

//    LoginResponse login(LoginRequest loginRequest);
//    RegisterResponse register(RegisterRequest registerRequest);
//    LogoutResponse logout(LogoutRequest logoutRequest);
//    GetUserResponse getUser(GetUserRequest getUserRequest);

}
