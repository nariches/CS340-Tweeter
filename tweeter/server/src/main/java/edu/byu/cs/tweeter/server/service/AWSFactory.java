package edu.byu.cs.tweeter.server.service;

import edu.byu.cs.tweeter.server.dao.AuthTokenDAO;
import edu.byu.cs.tweeter.server.dao.FeedDAO;
import edu.byu.cs.tweeter.server.dao.FollowsDAO;
import edu.byu.cs.tweeter.server.dao.IAuthTokenDAO;
import edu.byu.cs.tweeter.server.dao.IFeedDAO;
import edu.byu.cs.tweeter.server.dao.IFollowsDAO;
import edu.byu.cs.tweeter.server.dao.IStoryDAO;
import edu.byu.cs.tweeter.server.dao.IUserDAO;
import edu.byu.cs.tweeter.server.dao.StoryDAO;
import edu.byu.cs.tweeter.server.dao.UserDAO;

public class AWSFactory extends DAOFactory {

    private FollowsDAO followsDAO;
    private FeedDAO feedDAO;
    private UserDAO userDAO;
    private AuthTokenDAO authTokenDAO;
    private StoryDAO storyDAO;


    public AWSFactory() {
        this.followsDAO = new FollowsDAO();
        this.feedDAO = new FeedDAO();
        this.userDAO = new UserDAO();
        this.authTokenDAO = new AuthTokenDAO();
        this.storyDAO = new StoryDAO();
    }

    @Override
    public IFollowsDAO getFollowsDAO() {
        return followsDAO;
    }

    @Override
    public IFeedDAO getFeedDAO() {
        return feedDAO;
    }

    @Override
    public IUserDAO getUserDAO() {
        return userDAO;
    }

    @Override
    public IAuthTokenDAO getAuthTokenDAO() {
        return authTokenDAO;
    }

    @Override
    public IStoryDAO getStoryDAO() {
        return storyDAO;
    }
}
