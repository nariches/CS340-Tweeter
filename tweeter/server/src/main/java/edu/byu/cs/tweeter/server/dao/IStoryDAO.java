package edu.byu.cs.tweeter.server.dao;

import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.PutItemOutcome;

public interface IStoryDAO {

    Item getStory(String username);
    PutItemOutcome putStory();
}
