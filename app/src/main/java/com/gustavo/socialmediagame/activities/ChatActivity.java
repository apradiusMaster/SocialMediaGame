package com.gustavo.socialmediagame.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.gustavo.socialmediagame.R;
import com.gustavo.socialmediagame.models.Chat;
import com.gustavo.socialmediagame.models.Message;
import com.gustavo.socialmediagame.providers.AuthProvider;
import com.gustavo.socialmediagame.providers.ChatsProvider;
import com.gustavo.socialmediagame.providers.MessagesProvider;
import com.gustavo.socialmediagame.providers.UsersProvider;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatActivity extends AppCompatActivity {

    String mExtraIdUser1;
    String mExtraIdUser2;
    String mExtraIdChat;

    TextView mTextViewMessage;
    ImageView mImageViewSendMessage;

    CircleImageView mCircleImageProfile;
    TextView mTextViewUserName;
    TextView mTextViewRelativeTime;
    ImageView mImageViewBack;






    ChatsProvider mChatProvider;
    MessagesProvider mMessagesProvider;
    UsersProvider mUsersProvider;
    AuthProvider mAuthProvider;

    View mActionBarView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        mChatProvider = new ChatsProvider();
        mMessagesProvider = new MessagesProvider();
        mUsersProvider = new UsersProvider();
        mAuthProvider = new AuthProvider();

        mTextViewMessage = findViewById(R.id.textViewMessage);
        mImageViewSendMessage = findViewById(R.id.imageViewSendMessage);

        mExtraIdUser1 = getIntent().getStringExtra("idUser1");
        mExtraIdUser2 = getIntent().getStringExtra("idUser2");
        mExtraIdChat = getIntent().getStringExtra("idChat");


        showCustomToolbar(R.layout.custom_chat_toolbar);




        checkIfChatExist();


        mImageViewSendMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendMessage();
            }
        });
    }

    private void sendMessage() {

         String  textMessage = mTextViewMessage.getText().toString();

         if (!textMessage.isEmpty()){

             Message message = new Message();

             message.setId(mExtraIdChat);
             if (mAuthProvider.getUid().equals(mExtraIdUser1)){
                 message.setIdSender(mExtraIdUser1);
                 message.setIdReceiver(mExtraIdUser2);
             } else {
                 message.setIdSender(mExtraIdUser2);
                 message.setIdReceiver(mExtraIdUser1);
               }
             message.setTimestamp(new Date().getTime());
             message.setVieweb(false);
             message.setIdChat(mExtraIdChat);
             message.setMessage(textMessage);

             mMessagesProvider.create(message).addOnCompleteListener(new OnCompleteListener<Void>() {
                 @Override
                 public void onComplete(@NonNull @NotNull Task<Void> task) {

                     if (task.isSuccessful()){
                         mTextViewMessage.setText("");
                         Toast.makeText(ChatActivity.this, "Se envio el mensaje", Toast.LENGTH_SHORT).show();
                     } else {
                         Toast.makeText(ChatActivity.this, "No se pudo enviar el mensaje", Toast.LENGTH_SHORT).show();
                     }
                 }
             });

         } else {
             Toast.makeText(this, "Escriba el mensaje", Toast.LENGTH_SHORT).show();
         }
    }

    private void showCustomToolbar(int custom_chat_toolbar) {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("");
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayShowCustomEnabled(true);
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mActionBarView = inflater.inflate(custom_chat_toolbar, null);
        actionBar.setCustomView(mActionBarView);

        mCircleImageProfile = mActionBarView.findViewById(R.id.circleImageProfileChat);
        mImageViewBack = mActionBarView.findViewById(R.id.imageViewBack);
        mTextViewUserName = mActionBarView.findViewById(R.id.textViewUserNameChat);
        mTextViewRelativeTime = mActionBarView.findViewById(R.id.textViewRelativeTimeChat);

        mImageViewBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        getUserInfo();

    }

    private void getUserInfo() {
        String idUserInfo = "";
        if (mAuthProvider.getUid().equals(mExtraIdUser1)){
             idUserInfo = mExtraIdUser2;
        } else {
             idUserInfo = mExtraIdUser1;
          }

        mUsersProvider.getUser(idUserInfo).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                    if (documentSnapshot.exists()){
                        if (documentSnapshot.contains("username")){
                            String username = documentSnapshot.getString("username");
                            mTextViewUserName.setText(username);
                        }
                        if (documentSnapshot.contains("image_profile")){
                            String imageProfile = documentSnapshot.getString("image_profile");
                            if (imageProfile != null){
                                if (!imageProfile.isEmpty()){
                                    Picasso.with(ChatActivity.this).load(imageProfile).into(mCircleImageProfile);
                                }
                            }
                        }
                    }
            }
        });

    }

    private void checkIfChatExist(){
        mChatProvider.getChatByUser1AndUser2(mExtraIdUser1,mExtraIdUser2).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                int size = queryDocumentSnapshots.size();
                if (size == 0){
                   // Toast.makeText(ChatActivity.this, "No existe chat", Toast.LENGTH_SHORT).show();
                    mExtraIdChat = queryDocumentSnapshots.getDocuments().get(0).getId();
                    createChat();
                }
            }
        });
    }

    private void createChat() {
        Chat chat = new Chat();
        chat.setIdUser1(mExtraIdUser1);
        chat.setIdUser2(mExtraIdUser2);
        chat.setWriting(false);
        chat.setTimestamp(new Date().getTime());
        chat.setId(mExtraIdUser1 + mExtraIdUser2);
        ArrayList<String> ids = new ArrayList<>();
        ids.add(mExtraIdUser1);
        ids.add(mExtraIdUser2);
        chat.setIds(ids);
        mChatProvider.create(chat);
    }
}