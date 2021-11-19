package edu.byu.cs.tweeter.server.dao;

import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.PutItemOutcome;

import java.text.ParseException;

import edu.byu.cs.tweeter.model.domain.AuthToken;

public interface IAuthTokenDAO {

    AuthToken putAuthToken() throws Exception;
    AuthToken getAuthToken(AuthToken authToken);
    AuthToken validateAuthToken(AuthToken authToken) throws Exception;
    void deleteAuthToken(AuthToken authToken);

}
