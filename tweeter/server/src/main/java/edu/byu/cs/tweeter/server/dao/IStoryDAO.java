package edu.byu.cs.tweeter.server.dao;

import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.PutItemOutcome;

import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.net.response.StoryResponse;

public interface IStoryDAO {

    StoryResponse getStory(String username, String lastStatus, int limit);
    void putStory(Status status);
}
