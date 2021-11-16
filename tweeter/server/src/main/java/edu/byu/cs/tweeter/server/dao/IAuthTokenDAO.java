package edu.byu.cs.tweeter.server.dao;

import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.PutItemOutcome;

import edu.byu.cs.tweeter.model.domain.AuthToken;

public interface IAuthTokenDAO {

    AuthToken putAuthToken(); //Change to PutItemOutcome
    Item getAuthToken(String token);

}
