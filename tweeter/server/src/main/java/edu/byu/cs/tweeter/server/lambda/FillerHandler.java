package edu.byu.cs.tweeter.server.lambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;

import edu.byu.cs.tweeter.model.net.request.FillerRequest;
import edu.byu.cs.tweeter.model.net.response.FillerResponse;
import edu.byu.cs.tweeter.server.service.Filler;

public class FillerHandler implements RequestHandler<FillerRequest, FillerResponse> {


    @Override
    public FillerResponse handleRequest(FillerRequest input, Context context) {
        Filler filler = new Filler();
        Filler.fillDatabase();
        return new FillerResponse();
    }
}
