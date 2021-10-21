package com.gustavo.socialmediagame.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.gustavo.socialmediagame.R;
import com.gustavo.socialmediagame.models.Chat;
import com.gustavo.socialmediagame.providers.ChatsProvider;

import java.util.Date;

public class ChatActivity extends AppCompatActivity {

    String mExtraIdUser1;
    String mExtraIdUser2;
    ChatsProvider mChatProvider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        mExtraIdUser1 = getIntent().getStringExtra("idUser1");
        mExtraIdUser2 = getIntent().getStringExtra("idUser2");

        mChatProvider = new ChatsProvider();
        createChat();
    }

    private void createChat() {
        Chat chat = new Chat();
        chat.setIdUser1(mExtraIdUser1);
        chat.setIdUser2(mExtraIdUser2);
        chat.setWriting(false);
        chat.setTimestamp(new Date().getTime());
        mChatProvider.create(chat);
    }
}