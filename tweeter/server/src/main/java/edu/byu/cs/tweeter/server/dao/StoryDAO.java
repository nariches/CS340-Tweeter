package edu.byu.cs.tweeter.server.dao;

import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.PutItemOutcome;

public class StoryDAO implements IStoryDAO {


    @Override
    public Item getStory(String username) {
        return null;
    }

    @Override
    public PutItemOutcome putStory() {
        return null;
    }
}
