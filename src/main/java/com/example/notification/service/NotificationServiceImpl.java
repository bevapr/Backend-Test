package com.example.notification.service;

import com.example.notification.dto.NotificationResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class NotificationServiceImpl implements NotificationService {

    private final Set<String> sentTransactionIds = ConcurrentHashMap.newKeySet();
    private static final Logger logger = LoggerFactory.getLogger(NotificationServiceImpl.class);

    @Override
    public NotificationResponse sendNotification(String userId, String message) {
        String key = userId + message;
        boolean alreadySent = sentTransactionIds.contains(key);

        if (!alreadySent) {
            logger.info("Sending notification to userId {}: {}", userId, message);
            sentTransactionIds.add(key);
        } else {
            logger.info("Notification already sent to userId {} with message '{}', skipping", userId, message);
        }

        return new NotificationResponse(userId, message, !alreadySent);
    }
}
