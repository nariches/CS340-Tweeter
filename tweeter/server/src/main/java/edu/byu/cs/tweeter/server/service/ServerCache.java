//package edu.byu.cs.tweeter.server.service;
//
//import edu.byu.cs.tweeter.model.domain.AuthToken;
//import edu.byu.cs.tweeter.model.domain.User;
//
//public class ServerCache {
//    private static ServerCache instance = new ServerCache();
//
//    public static ServerCache getInstance() {
//        return instance;
//    }
//
//    /**
//     * The currently logged-in user.
//     */
//    private User currUser;
//    /**
//     * The auth token for the current user session.
//     */
//    //private AuthToken currUserAuthToken;
//
//    private ServerCache() {
//        initialize();
//    }
//
//    private void initialize() {
//        currUser = new User(null, null, null);
//        //currUserAuthToken = null;
//    }
//
//    public void clearCache() {
//        initialize();
//    }
//
//    public User getCurrUser() {
//        return currUser;
//    }
//
//    public void setCurrUser(User currUser) {
//        this.currUser = currUser;
//    }
//
////    public AuthToken getCurrUserAuthToken() {
////        return currUserAuthToken;
////    }
////
////    public void setCurrUserAuthToken(AuthToken currUserAuthToken) {
////        this.currUserAuthToken = currUserAuthToken;
////    }
//}