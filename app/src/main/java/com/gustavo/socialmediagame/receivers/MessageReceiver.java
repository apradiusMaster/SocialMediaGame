package com.gustavo.socialmediagame.receivers;

import android.app.NotificationManager;
import android.app.RemoteInput;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.gustavo.socialmediagame.activities.ChatActivity;
import com.gustavo.socialmediagame.models.Message;
import com.gustavo.socialmediagame.providers.MessagesProvider;

import org.jetbrains.annotations.NotNull;

import java.util.Date;

import static com.gustavo.socialmediagame.services.MyFirebaseMessagingClient.NOTIFICATION_REPLY;

public class MessageReceiver  extends BroadcastReceiver {


    String mExtraIdSender;
    String mExtraIdReceiver;
    String mExtraIdChat;
    int mExtraIdNotification;

    @Override
    public void onReceive(Context context, Intent intent) {

        mExtraIdSender = intent.getExtras().getString("idSender");
        mExtraIdReceiver = intent.getExtras().getString("idReceiver");
        mExtraIdChat = intent.getExtras().getString("idChat");
        mExtraIdNotification =  intent.getExtras().getInt("idNotification");

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(mExtraIdNotification);

        String message = getMessageText( intent).toString();
        sendMessage(message);



    }

    private void sendMessage( String messageText) {
        Message message = new Message();

        message.setId(mExtraIdChat);
        message.setIdSender(mExtraIdReceiver);
        message.setIdReceiver(mExtraIdSender);

        message.setTimestamp(new Date().getTime());
        message.setVieweb(false);
        message.setIdChat(mExtraIdChat);
        message.setMessage(messageText);

        MessagesProvider mMessagesProvider = new MessagesProvider();
        mMessagesProvider.create(message).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull @NotNull Task<Void> task) {

                if (task.isSuccessful()){
                   // sendToken(message);
                }
            }
        });
    }

    private  CharSequence getMessageText(Intent intent){
        Bundle remoteInput = RemoteInput.getResultsFromIntent(intent);
        if (remoteInput !=null){
            return remoteInput.getCharSequence(NOTIFICATION_REPLY);
        }
        return null;
    }
}
