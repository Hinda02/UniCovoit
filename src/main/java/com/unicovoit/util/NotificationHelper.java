package com.unicovoit.util;

import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;

/**
 * Utility class for showing notifications
 */
public class NotificationHelper {

    private static final int DEFAULT_DURATION = 3000; // 3 seconds

    /**
     * Show a success notification
     */
    public static void showSuccess(String message) {
        Notification notification = Notification.show(message, DEFAULT_DURATION, Notification.Position.TOP_CENTER);
        notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
    }

    /**
     * Show an error notification
     */
    public static void showError(String message) {
        Notification notification = Notification.show(message, DEFAULT_DURATION + 2000, Notification.Position.TOP_CENTER);
        notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
    }

    /**
     * Show a warning notification
     */
    public static void showWarning(String message) {
        Notification notification = Notification.show(message, DEFAULT_DURATION, Notification.Position.TOP_CENTER);
        notification.addThemeVariants(NotificationVariant.LUMO_CONTRAST);
    }

    /**
     * Show an info notification
     */
    public static void showInfo(String message) {
        Notification notification = Notification.show(message, DEFAULT_DURATION, Notification.Position.TOP_CENTER);
        notification.addThemeVariants(NotificationVariant.LUMO_PRIMARY);
    }

    /**
     * Show a custom notification with duration
     */
    public static void show(String message, int durationMs, Notification.Position position) {
        Notification.show(message, durationMs, position);
    }
}
