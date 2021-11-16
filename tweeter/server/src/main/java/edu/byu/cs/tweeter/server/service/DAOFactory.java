package edu.byu.cs.tweeter.server.service;

import edu.byu.cs.tweeter.server.dao.FollowDAO;
import edu.byu.cs.tweeter.server.dao.IFollowDAO;
import edu.byu.cs.tweeter.server.dao.IStatusDAO;
import edu.byu.cs.tweeter.server.dao.IUserDAO;
import edu.byu.cs.tweeter.server.dao.StatusDAO;
import edu.byu.cs.tweeter.server.dao.UserDAO;

public abstract class DAOFactory {

//    protected FollowDAO followDAO;
//    protected StatusDAO statusDAO;
//    protected UserDAO userDAO;

    public abstract IFollowDAO getFollowDAO();

    public abstract IStatusDAO getStatusDAO();

    public abstract IUserDAO getUserDAO();

}
