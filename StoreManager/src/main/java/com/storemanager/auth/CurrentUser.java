package com.storemanager.auth;

import com.storemanager.model.users.User; // Assuming User is in the models package

/**
 * Singleton class to manage the currently logged-in user.
 */
public class CurrentUser {
    private static CurrentUser instance; // Singleton instance
    private User currentUser; // The logged-in user

    // Private constructor to prevent instantiation
    private CurrentUser() {}

    /**
     * Get the singleton instance of CurrentUser.
     *
     * @return the singleton instance
     */
    public static CurrentUser getInstance() {
        if (instance == null) {
            instance = new CurrentUser();
        }
        return instance;
    }

    /**
     * Get the current user.
     *
     * @return the logged-in user, or null if no user is logged in
     */
    public User getUser() {
        return currentUser;
    }

    /**
     * Set the current user.
     *
     * @param user the user to set as the current user
     */
    public void setUser(User user) {
        this.currentUser = user;
    }

    /**
     * Clear the current user, typically used on logout.
     */
    public void clearUser() {
        this.currentUser = null;
    }

    /**
     * Check if a user is logged in.
     *
     * @return true if a user is logged in, false otherwise
     */
    public boolean isLoggedIn() {
        return currentUser != null;
    }
}
