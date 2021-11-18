package edu.byu.cs.tweeter.server.service;

import edu.byu.cs.tweeter.server.dao.IAuthTokenDAO;
import edu.byu.cs.tweeter.server.dao.IFollowsDAO;
import edu.byu.cs.tweeter.server.dao.IFeedDAO;
import edu.byu.cs.tweeter.server.dao.IStoryDAO;
import edu.byu.cs.tweeter.server.dao.IUserDAO;

public abstract class DAOFactory {


    public abstract IFollowsDAO getFollowsDAO();

    public abstract IFeedDAO getFeedDAO();

    public abstract IUserDAO getUserDAO();

    public abstract IAuthTokenDAO getAuthTokenDAO();

    public abstract IStoryDAO getStoryDAO();

}
