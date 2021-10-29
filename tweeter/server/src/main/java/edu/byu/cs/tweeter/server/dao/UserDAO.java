package edu.byu.cs.tweeter.server.dao;

import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.net.response.GetUserResponse;
import edu.byu.cs.tweeter.server.util.FakeData;

public class UserDAO {

    public GetUserResponse getUser(AuthToken authToken, String username) {
        assert username != null;
        return new GetUserResponse(getFakeData().getFirstUser());
    }

    FakeData getFakeData() {
        return new FakeData();
    }
}
