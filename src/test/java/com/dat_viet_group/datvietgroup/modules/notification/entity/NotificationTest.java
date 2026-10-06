package com.dat_viet_group.datvietgroup.modules.notification.entity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

class NotificationTest {

    @Test
    void onCreateShouldSetCreatedAtWhenMissing() {
        Notification notification = new Notification();

        notification.onCreate();

        assertNotNull(notification.getCreatedAt());
    }

    @Test
    void onCreateShouldKeepExistingCreatedAt() {
        Notification notification = new Notification();
        LocalDateTime createdAt = LocalDateTime.of(2026, 10, 6, 4, 0);
        notification.setCreatedAt(createdAt);

        notification.onCreate();

        assertEquals(createdAt, notification.getCreatedAt());
    }
}
