package com.gustavo.socialmediagame.services;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.google.gson.Gson;
import com.gustavo.socialmediagame.channel.NotificationHelper;
import com.gustavo.socialmediagame.models.Message;

import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.Random;

public class MyFirebaseMessagingClient extends FirebaseMessagingService {

    @Override
    public void onNewToken(@NonNull @NotNull String s) {
        super.onNewToken(s);
    }

    @Override
    public void onMessageReceived(@NonNull @NotNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        Map<String, String> data = remoteMessage.getData();
        String title = data.get("title");
        String body = data.get("body");

        if (title != null){
            if (title.equals("NUEVO MENSAJE")){
                showNotificationMessage(data);

            } else {
                showNotification(title,body);
            }

        }
    }

    private  void showNotification(String title, String body){
        NotificationHelper notificationHelper = new NotificationHelper(getBaseContext());
        NotificationCompat.Builder builder = notificationHelper.getNotification(title,body);
        Random random = new Random();
        int n = random.nextInt(10000);
        notificationHelper.getManager().notify(n, builder.build());
    }
    private  void showNotificationMessage(Map<String, String> data){
        String title = data.get("title");
        String body = data.get("body");
        String messagesJSON = data.get("messages");
        int idNotificationChat = Integer.parseInt(data.get("idNotification")) ;
        Gson gson = new Gson();
         Message[] messages = gson.fromJson(messagesJSON, Message[].class);

        NotificationHelper notificationHelper = new NotificationHelper(getBaseContext());
        NotificationCompat.Builder builder = notificationHelper.getNotificationMessage(messages);
        notificationHelper.getManager().notify(idNotificationChat, builder.build());
    }
}
