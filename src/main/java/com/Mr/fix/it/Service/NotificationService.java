package com.Mr.fix.it.Service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;

import com.Mr.fix.it.Response.NotificationMessage;

@Service
@RequiredArgsConstructor
public class NotificationService
{
    private final FirebaseMessaging firebaseMessaging;

    public void sendNotificationByToken(NotificationMessage notificationMessage)
    {
        Notification notification = Notification
            .builder()
            .setTitle(notificationMessage.getTitle())
            .setBody(notificationMessage.getBody())
            .build();

        Message message = Message
            .builder()
            .setToken(notificationMessage.getRecipientToken())
            .setNotification(notification)
            .putAllData(notificationMessage.getData())
            .build();

        try {
            firebaseMessaging.send(message);
        } catch (FirebaseMessagingException e) {
            throw new RuntimeException(e);
        }
    }
}
