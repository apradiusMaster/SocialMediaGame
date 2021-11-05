package com.gustavo.socialmediagame.services;

import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Looper;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.RemoteInput;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.google.gson.Gson;
import com.gustavo.socialmediagame.R;
import com.gustavo.socialmediagame.channel.NotificationHelper;
import com.gustavo.socialmediagame.models.Message;
import com.gustavo.socialmediagame.receivers.MessageReceiver;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.Random;

public class MyFirebaseMessagingClient extends FirebaseMessagingService {

    public static final  String NOTIFICATION_REPLY = "NotificationReply";

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

        String imageSender = data.get("imageSender");
        String imageReceiver = data.get("imageReceiver");
        getImageSender(data,imageSender,imageReceiver);

    }

    private void getImageSender( Map<String,String> data, String imageSender, String imageReceiver){
        new Handler(Looper.getMainLooper())
                .post(new Runnable() {
                    @Override
                    public void run() {
                        Picasso.with(getApplicationContext())
                                .load(imageSender)
                                .into(new Target() {
                                    @Override
                                    public void onBitmapLoaded(Bitmap bitmapSender, Picasso.LoadedFrom from) {
                                        getImageReceiver(data, imageReceiver, bitmapSender);
                                    }

                                    @Override
                                    public void onBitmapFailed(Drawable errorDrawable) {
                                        getImageReceiver(data, imageReceiver, null);
                                    }

                                    @Override
                                    public void onPrepareLoad(Drawable placeHolderDrawable) {

                                    }
                                });
                    }
                });

    }

    private  void  getImageReceiver(Map<String,String> data, String imageReceiver, Bitmap bitmapSender){
        Picasso.with(getApplicationContext())
                .load(imageReceiver)
                .into(new Target() {
                    @Override
                    public void onBitmapLoaded(Bitmap bitmapReceiver, Picasso.LoadedFrom from) {
                        notifyMessage(data, bitmapSender, bitmapReceiver);

                    }

                    @Override
                    public void onBitmapFailed(Drawable errorDrawable) {
                        notifyMessage(data, bitmapSender, null);
                    }

                    @Override
                    public void onPrepareLoad(Drawable placeHolderDrawable) {

                    }
                });
    }


    private void notifyMessage(Map<String,String> data, Bitmap bitmapSender, Bitmap bitmapReceiver ){
        String usernameSender = data.get("usernameSender");
        String usernameReceiver = data.get("usernameReceiver");
        String lastMessage = data.get("lastMessage");
        String messagesJSON = data.get("messages");

        String idSender = data.get("idSender");
        String idReceiver = data.get("idReceiver");
        String idChat = data.get("idChat");

        int idNotificationChat = Integer.parseInt(data.get("idNotification")) ;

        Intent intent = new Intent(this, MessageReceiver.class);
        intent.putExtra("idSender", idSender);
        intent.putExtra("idReceiver", idReceiver);
        intent.putExtra("idChat", idChat);
        intent.putExtra("idNotification", idNotificationChat);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 1, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        RemoteInput remoteInput = new RemoteInput.Builder(NOTIFICATION_REPLY).setLabel("Tu mensaje...").build();

        NotificationCompat.Action action = new NotificationCompat.Action.Builder(
                R.mipmap.ic_launcher,
                "Responder",
                pendingIntent)
                .addRemoteInput(remoteInput)
                .build();

        Gson gson = new Gson();
        Message[] messages = gson.fromJson(messagesJSON, Message[].class);
        NotificationHelper notificationHelper = new NotificationHelper(getBaseContext());
        NotificationCompat.Builder builder =
                notificationHelper.getNotificationMessage(
                        messages,
                        usernameSender,
                        usernameReceiver,
                        lastMessage,
                        bitmapSender,
                        bitmapReceiver,
                        action);
        notificationHelper.getManager().notify(idNotificationChat, builder.build());
    }
}
