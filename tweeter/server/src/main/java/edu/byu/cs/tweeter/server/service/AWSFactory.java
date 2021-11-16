package edu.byu.cs.tweeter.server.service;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Index;
import com.amazonaws.services.dynamodbv2.document.Table;

import edu.byu.cs.tweeter.server.dao.AuthTokenDAO;
import edu.byu.cs.tweeter.server.dao.FollowDAO;
import edu.byu.cs.tweeter.server.dao.StatusDAO;
import edu.byu.cs.tweeter.server.dao.UserDAO;

public class AWSFactory extends DAOFactory {

    protected FollowDAO followDAO;
    protected StatusDAO statusDAO;
    protected UserDAO userDAO;
    protected AuthTokenDAO authTokenDAO;


    public AWSFactory() {
        this.followDAO = new FollowDAO();
        this.statusDAO = new StatusDAO();
        this.userDAO = new UserDAO();
        this.authTokenDAO = new AuthTokenDAO();
    }

    @Override
    public FollowDAO getFollowDAO() {
        return followDAO;
    }

    @Override
    public StatusDAO getStatusDAO() {
        return statusDAO;
    }

    @Override
    public UserDAO getUserDAO() {
        return userDAO;
    }

    @Override
    public AuthTokenDAO getAuthTokenDAO() {
        return authTokenDAO;
    }
}
