package edu.byu.cs.tweeter.server.service;

import java.util.ArrayList;
import java.util.List;

import edu.byu.cs.tweeter.model.domain.UserDTO;
import edu.byu.cs.tweeter.server.dao.FollowsDAO;
import edu.byu.cs.tweeter.server.dao.UserDAO;

public class Filler {

    private static AWSFactory awsFactory;

    public Filler() {
        this.awsFactory = new AWSFactory();
    }

    // How many follower users to add
    // We recommend you test this with a smaller number first, to make sure it works for you
    private final static int NUM_USERS = 10000;

    // The alias of the user to be followed by each user created
    // This example code does not add the target user, that user must be added separately.
    private final static String FOLLOW_TARGET = "@test";

    public static void fillDatabase() {

        // Get instance of DAOs by way of the Abstract Factory Pattern
//        UserDAO userDAO = awsFactory.getUserDAO(); AWSFactory FactoryManager.getDAOFactory().makeUserDAO();
//        FollowsDAO followDAO = FactoryManager.getDAOFactory().makeFollowDAO();

        List<String> followers = new ArrayList<>();
        List<UserDTO> users = new ArrayList<>();

        // Iterate over the number of users you will create
        for (int i = 1; i <= NUM_USERS; i++) {

            String name = "Guy " + i;
            String alias = "guy" + i;

            // Note that in this example, a UserDTO only has a name and an alias.
            // The url for the profile image can be derived from the alias in this example
            UserDTO user = new UserDTO();
            user.setAlias(alias);
            user.setName(name);
            users.add(user);

            // Note that in this example, to represent a follows relationship, only the aliases
            // of the two users are needed
            followers.add(alias);
        }

        // Call the DAOs for the database logic
        if (users.size() > 0) {
            awsFactory.getUserDAO().addUserBatch(users);
        }
        if (followers.size() > 0) {
            awsFactory.getFollowsDAO().addFollowersBatch(followers, FOLLOW_TARGET);
        }
    }
}
