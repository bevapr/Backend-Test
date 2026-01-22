package com.example.notification.service;

import com.example.notification.dto.NotificationResponse;

public interface NotificationService {
    NotificationResponse sendNotification(String userId, String message);
}
