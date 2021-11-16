package edu.byu.cs.tweeter.server.dao;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.PutItemOutcome;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.document.spec.GetItemSpec;
import com.amazonaws.services.dynamodbv2.xspec.S;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;
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

public class UserDAO implements IUserDAO {

    private AmazonDynamoDB client;
    private DynamoDB dynamoDB;
    private AuthTokenDAO authTokenDAO;


//    public GetUserResponse getUser(AuthToken authToken, String username) {
//        assert username != null;
//        return new GetUserResponse(getFakeData().getFirstUser());
//    }

//    FakeData getFakeData() {
//        return new FakeData();
//    }

    public UserDAO() {
        this.client = AmazonDynamoDBClientBuilder.standard().withRegion("us-east-2").build();
        this.dynamoDB = new DynamoDB(client);
        this.authTokenDAO = new AuthTokenDAO();
    }

    @Override
    public LoginResponse login(LoginRequest loginRequest) {
        Table users_table = dynamoDB.getTable("users");
        try {
            GetItemSpec spec = new GetItemSpec().withPrimaryKey("username", loginRequest.getUsername());
            System.out.println("Attempting to login user...");
            Item outcome = users_table.getItem(spec);
            System.out.println("login succeeded: " + outcome);
            String storedPassword = outcome.getString(loginRequest.getPassword());
            boolean passwordsMatch = validatePassword(loginRequest.getPassword(), storedPassword, loginRequest.getUsername());
            if (passwordsMatch) {
                AuthToken authToken = authTokenDAO.createAuthToken();
                User loggedInUser = new User(outcome.getString("first_name"),
                        outcome.getString("last_name"),
                        outcome.getString("username"), outcome.getString("image"));
                return new LoginResponse(loggedInUser, authToken);
            }
            else {
                return new LoginResponse("Incorrect password");
            }
        }
        catch (Exception e) {
            System.err.println("Unable to login user: " + loginRequest.getUsername());
            System.err.println(e.getMessage());
            return new LoginResponse(e.getMessage());
        }

    }

    @Override
    public RegisterResponse register(RegisterRequest registerRequest) {
        Table users_table = dynamoDB.getTable("users");
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

            PutItemOutcome outcome = users_table.putItem(new Item()
                    .withPrimaryKey("username", registerRequest.getUsername())
                    .with("password", securePassword)
                    .with("first_name", registerRequest.getFirstName())
                    .with("last_name", registerRequest.getLastName())
                    .with("image", url));


            System.out.println("Register succeeded:\n" + outcome.getPutItemResult());

            AuthToken authToken = authTokenDAO.createAuthToken();
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

    @Override
    public LogoutResponse logout(LogoutRequest logoutRequest) {
        return new LogoutResponse();
    }

    @Override
    public GetUserResponse getUser(GetUserRequest getUserRequest) {
        Table users_table = dynamoDB.getTable("users");
        assert getUserRequest.getUsername() != null;
        try {
            GetItemSpec spec = new GetItemSpec().withPrimaryKey("username", getUserRequest.getUsername());
            System.out.println("Attempting to get user...");
            Item outcome = users_table.getItem(spec);
            System.out.println("getUser succeeded: " + outcome);

            User user = new User(outcome.getString("first_name"), outcome.getString("last_name"),
                    outcome.getString("username"), outcome.getString("image"));
            return new GetUserResponse(user);
        }
        catch (Exception e) {
            System.err.println("Unable to get user: " + getUserRequest.getUsername());
            System.err.println(e.getMessage());
            return new GetUserResponse(e.getMessage());
        }
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

    private static String getSalt() {
        try {
            SecureRandom sr = SecureRandom.getInstance("SHA1PRNG", "SUN");
            byte[] salt = new byte[16];
            sr.nextBytes(salt);
            return Base64.getEncoder().encodeToString(salt);
        } catch (NoSuchAlgorithmException | NoSuchProviderException e) {
            e.printStackTrace();
        }
        return "FAILED TO GET SALT";
    }

    private String setS3ImageFile(RegisterRequest registerRequest) throws IOException {
        Logger.getLogger("function").info("setS3ImageFile");
        AmazonS3 s3 = AmazonS3ClientBuilder
                .standard()
                .withRegion("us-east-2")
                .build();
        if(s3 == null) Logger.getLogger("error").warning("s3 null");
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
