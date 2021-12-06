package edu.byu.cs.tweeter.model.net;

import java.util.List;

import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.UserDTO;

public class UpdateFeedQueueMessage {
    public List<UserDTO> users;
    public Status status;

    public UpdateFeedQueueMessage(List<UserDTO> users, Status status) {
        this.users = users;
        this.status = status;
    }
}
