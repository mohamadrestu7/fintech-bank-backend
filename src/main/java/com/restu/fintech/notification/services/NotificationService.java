package com.restu.fintech.notification.services;

import com.restu.fintech.auth_users.entity.User;
import com.restu.fintech.notification.dtos.NotificationDTO;

public interface NotificationService {
    void sendEmail(NotificationDTO notificationDTO, User user);
}
