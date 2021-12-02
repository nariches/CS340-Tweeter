package edu.byu.cs.tweeter.model.net.response;

import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;

public class FillerResponse extends Response {

    public FillerResponse(String message) {
        super(false, message);
    }


    public FillerResponse() {
        super(true, null);
    }
}
