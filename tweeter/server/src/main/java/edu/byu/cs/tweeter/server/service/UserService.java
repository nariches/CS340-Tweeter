package edu.byu.cs.tweeter.server.service;

import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.PutItemOutcome;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.document.spec.GetItemSpec;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.logging.Logger;

import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.request.GetUserRequest;
import edu.byu.cs.tweeter.model.net.request.LoginRequest;
import edu.byu.cs.tweeter.model.net.request.LogoutRequest;
import edu.byu.cs.tweeter.model.net.request.RegisterRequest;
import edu.byu.cs.tweeter.model.net.response.GetUserResponse;
import edu.byu.cs.tweeter.model.net.response.LoginResponse;
import edu.byu.cs.tweeter.model.net.response.LogoutResponse;
import edu.byu.cs.tweeter.model.net.response.RegisterResponse;
import edu.byu.cs.tweeter.server.util.FakeData;

public class UserService {

    private AWSFactory awsFactory;

    public UserService() {
        this.awsFactory = new AWSFactory();
    }

    public LoginResponse login(LoginRequest loginRequest) {

        try {
            Item userItem = awsFactory.userDAO.getUser(loginRequest.getUsername());
            User loggedInUser = new User(userItem.getString("first_name"),
                    userItem.getString("last_name"),
                    userItem.getString("username"), userItem.getString("image"));

            String storedPassword = userItem.getString("password");
            boolean passwordsMatch = validatePassword(loginRequest.getPassword(), storedPassword, loginRequest.getUsername());
            if (passwordsMatch) {
                AuthToken authToken = awsFactory.getAuthTokenDAO().putAuthToken(); //fix
                return new LoginResponse(loggedInUser, authToken);
            } else {
                return new LoginResponse("Incorrect password");
            }
        } catch (Exception e) {
            System.err.println("Unable to login user: " + loginRequest.getUsername());
            System.err.println(e.getMessage());
            return new LoginResponse(e.getMessage());
        }
    }

    public RegisterResponse register(RegisterRequest registerRequest) {
        String securePassword = getHashedPassword(registerRequest.getPassword(), registerRequest.getUsername());
        try {

            System.out.println("Made it to register!");
            //Upload image to S3
            String url = setS3ImageFile(registerRequest);

            System.out.println("This is the username: " + registerRequest.getUsername());
            System.out.println("This is the password: " + registerRequest.getPassword());
            System.out.println("This is the firstname: " + registerRequest.getFirstName());
            System.out.println("This is the lastname: " + registerRequest.getLastName());
            System.out.println("This is the URL: " + url);

            PutItemOutcome outcome = awsFactory.userDAO.putUser(registerRequest.getFirstName(),
                    registerRequest.getLastName(), registerRequest.getUsername(),
                    securePassword, url);

            System.out.println("Register succeeded:\n" + outcome.getPutItemResult());

            AuthToken authToken = awsFactory.authTokenDAO.putAuthToken();
            User registeredUser = new User(registerRequest.getFirstName(), registerRequest.getLastName(),
                    registerRequest.getUsername(), url);
            System.out.println("This is registeredUser: " + registeredUser);
            System.out.println("This is authToken: " + authToken);
            return new RegisterResponse(registeredUser, authToken);
        }
        catch (Exception e) {
            System.err.println("Unable to register user: " + registerRequest.getFirstName() + " "
                    + registerRequest.getLastName());
            System.err.println(e.getMessage());
            return new RegisterResponse(e.getMessage());
        }
    }

    public GetUserResponse getUser(GetUserRequest getUserRequest) {
        //Table users_table = dynamoDB.getTable("users");
        assert getUserRequest.getUsername() != null;
        try {
            Item userItem = awsFactory.userDAO.getUser(getUserRequest.getUsername());
            User user = new User(userItem.getString("first_name"), userItem.getString("last_name"),
                    userItem.getString("username"), userItem.getString("image"));
            return new GetUserResponse(user);
        } catch (Exception e) {
            System.err.println("Unable to get user: " + getUserRequest.getUsername());
            System.err.println(e.getMessage());
            return new GetUserResponse(e.getMessage());
        }
    }

    public LogoutResponse logout(LogoutRequest logoutRequest) {
        return new LogoutResponse();
    }

    private boolean validatePassword(String givenPassword, String storedPassword, String username) {
        String givenHashed = getHashedPassword(givenPassword, username);
        return givenHashed.equals(storedPassword);
    }

    private static String getHashedPassword(String password, String username) {
        String salt = username;
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(salt.getBytes());
            byte[] bytes = md.digest(password.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte aByte : bytes) {
                sb.append(Integer.toString((aByte & 0xff) + 0x100, 16).substring(1));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "FAILED TO HASH PASSWORD";
    }

    private String setS3ImageFile(RegisterRequest registerRequest) throws IOException {
        Logger.getLogger("function").info("setS3ImageFile");
        AmazonS3 s3 = AmazonS3ClientBuilder
                .standard()
                .withRegion("us-east-2")
                .build();
        if (s3 == null) Logger.getLogger("error").warning("s3 null");
        String bucket = "nathanawsbucket";
        String fileName = registerRequest.getUsername() + "-image.png";
        byte[] imageBytes = Base64.getDecoder().decode(registerRequest.getImage());
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(imageBytes);

        //create PutObjectRequest
        PutObjectRequest putObjectRequest = new PutObjectRequest(bucket, fileName, byteArrayInputStream, new ObjectMetadata())
                .withCannedAcl(CannedAccessControlList.PublicReadWrite);
        s3.putObject(putObjectRequest);
        String imageUrl = s3.getUrl(bucket, fileName).toString();
        Logger.getLogger("imageUrl").info(imageUrl);
        return imageUrl;
    }
}
