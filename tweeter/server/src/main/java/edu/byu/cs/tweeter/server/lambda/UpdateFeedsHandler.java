package edu.byu.cs.tweeter.server.lambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.SQSEvent;

import java.util.List;

import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.UserDTO;
import edu.byu.cs.tweeter.model.net.UpdateFeedQueueMessage;
import edu.byu.cs.tweeter.server.service.AWSFactory;
import edu.byu.cs.tweeter.server.service.JsonSerializer;

public class UpdateFeedsHandler implements RequestHandler<SQSEvent, Void> {

    private static AWSFactory awsFactory = new AWSFactory();

    @Override
    public Void handleRequest(SQSEvent event, Context context) {
        for (SQSEvent.SQSMessage msg: event.getRecords()) {
            UpdateFeedQueueMessage toHandle = JsonSerializer.deserialize(msg.getBody(), UpdateFeedQueueMessage.class);
            List<UserDTO> followers = toHandle.users;
            Status status = toHandle.status;

            System.out.println("Status: " + status);
            System.out.println("followers size: " + followers.size());

            //Now we need to batch write to the feed table
            awsFactory.getFeedDAO().addFeedBatch(status, followers);

            System.out.println("Made it to the end of UpdateFeedsHandler");

        }
        return null;
    }
}
