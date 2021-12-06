package edu.byu.cs.tweeter.server.lambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.SQSEvent;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClientBuilder;
import com.amazonaws.services.sqs.model.SendMessageRequest;
import com.amazonaws.services.sqs.model.SendMessageResult;

import java.util.ArrayList;
import java.util.List;

import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.domain.UserDTO;
import edu.byu.cs.tweeter.model.net.UpdateFeedQueueMessage;
import edu.byu.cs.tweeter.model.net.response.FollowersResponse;
import edu.byu.cs.tweeter.server.dao.FollowsDAO;
import edu.byu.cs.tweeter.server.dao.UserDAO;
import edu.byu.cs.tweeter.server.service.JsonSerializer;

public class UpdateFeedMessagesHandler implements RequestHandler<SQSEvent, Void> {

    @Override
    public Void handleRequest(SQSEvent event, Context context) {
        for (SQSEvent.SQSMessage msg : event.getRecords()) {
            System.out.println(msg);
            String statusJson = msg.getBody();
            Status status = JsonSerializer.deserialize(statusJson, Status.class);
            User statusAuthor = status.getUser();

            //List<UserDTO> followerList = new ArrayList<>();

            List<UserDTO> followeeList = new FollowsDAO().getFolloweesDTO(statusAuthor.getAlias());

            System.out.println("followeeList size:  " + followeeList.size());


            String message = JsonSerializer.serialize(new UpdateFeedQueueMessage(followeeList, status));
            String queueUrl = "https://sqs.us-east-2.amazonaws.com/982609089467/UpdateFeedQueue";

            SendMessageRequest sendMessageRequest = new SendMessageRequest()
                    .withQueueUrl(queueUrl)
                    .withMessageBody(message)
                    .withDelaySeconds(5);

            AmazonSQS sqs = AmazonSQSClientBuilder.defaultClient();
            SendMessageResult sendMessageResult = sqs.sendMessage(sendMessageRequest);

            String msgId = sendMessageResult.getMessageId();
            System.out.println("Message ID: " + msgId);

            System.out.println("MADE IT TO END OF UPDATEFEEDMESSAGEHANDLER");

        }
        return null;
    }
}
