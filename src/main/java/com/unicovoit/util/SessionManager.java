package com.unicovoit.util;

import com.unicovoit.entity.UserAccount;
import com.vaadin.flow.server.VaadinSession;

/**
 * Utility class for managing user session
 */
public class SessionManager {

    private static final String USER_SESSION_ATTRIBUTE = "currentUser";

    /**
     * Store the current user in the session
     */
    public static void setCurrentUser(UserAccount user) {
        VaadinSession.getCurrent().setAttribute(USER_SESSION_ATTRIBUTE, user);
    }

    /**
     * Get the current logged-in user from the session
     */
    public static UserAccount getCurrentUser() {
        return VaadinSession.getCurrent().getAttribute(UserAccount.class);
    }

    /**
     * Check if a user is logged in
     */
    public static boolean isLoggedIn() {
        return getCurrentUser() != null;
    }

    /**
     * Logout the current user
     */
    public static void logout() {
        VaadinSession.getCurrent().getSession().invalidate();
        VaadinSession.getCurrent().close();
    }

    /**
     * Get the current user ID
     */
    public static Long getCurrentUserId() {
        UserAccount user = getCurrentUser();
        return user != null ? user.getId() : null;
    }

    /**
     * Get the current user's full name
     */
    public static String getCurrentUserFullName() {
        UserAccount user = getCurrentUser();
        return user != null ? user.getFirstName() + " " + user.getLastName() : "";
    }
}
