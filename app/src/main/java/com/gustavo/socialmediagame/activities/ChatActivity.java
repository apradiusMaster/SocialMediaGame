package com.gustavo.socialmediagame.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.Gson;
import com.gustavo.socialmediagame.R;
import com.gustavo.socialmediagame.adapters.MessagesAdapter;
import com.gustavo.socialmediagame.adapters.MyPostsAdapter;
import com.gustavo.socialmediagame.models.Chat;
import com.gustavo.socialmediagame.models.FCMBody;
import com.gustavo.socialmediagame.models.FCMResponse;
import com.gustavo.socialmediagame.models.Message;
import com.gustavo.socialmediagame.models.Post;
import com.gustavo.socialmediagame.providers.AuthProvider;
import com.gustavo.socialmediagame.providers.ChatsProvider;
import com.gustavo.socialmediagame.providers.MessagesProvider;
import com.gustavo.socialmediagame.providers.NotificationProvider;
import com.gustavo.socialmediagame.providers.TokenProvider;
import com.gustavo.socialmediagame.providers.UsersProvider;
import com.gustavo.socialmediagame.utils.RelativeTime;
import com.gustavo.socialmediagame.utils.ViewebMessageHelper;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChatActivity extends AppCompatActivity {

    String mExtraIdUser1;
    String mExtraIdUser2;
    String mExtraIdChat;
    long mIdNotificationChat;

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
    NotificationProvider mNotificationProvider;
    TokenProvider mTokenProvider;

    RecyclerView mRecyclerView;
    MessagesAdapter mMessagesAdapter;
    LinearLayoutManager mLinearLayoutManager;
    ListenerRegistration mListener;

    View mActionBarView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        mChatProvider = new ChatsProvider();
        mMessagesProvider = new MessagesProvider();
        mUsersProvider = new UsersProvider();
        mAuthProvider = new AuthProvider();
        mNotificationProvider = new NotificationProvider();
        mTokenProvider = new TokenProvider();

        mRecyclerView = findViewById(R.id.recyclerViewMessage);
        mTextViewMessage = findViewById(R.id.textViewMessage);
        mImageViewSendMessage = findViewById(R.id.imageViewSendMessage);

        mExtraIdUser1 = getIntent().getStringExtra("idUser1");
        mExtraIdUser2 = getIntent().getStringExtra("idUser2");
        mExtraIdChat = getIntent().getStringExtra("idChat");

         mLinearLayoutManager = new LinearLayoutManager(ChatActivity.this);
         mLinearLayoutManager.setStackFromEnd(true);
        mRecyclerView.setLayoutManager(mLinearLayoutManager);

        showCustomToolbar(R.layout.custom_chat_toolbar);




        checkIfChatExist();


        mImageViewSendMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendMessage();
            }
        });
    }

    public void onStart() {
        super.onStart();

        if (mExtraIdChat !=  null){
            if (!mExtraIdChat.isEmpty()){
                getMessageChat();
            }
        }

        ViewebMessageHelper.updateOnline(true, ChatActivity.this);

    }
    @Override
    public void onStop() {
        super.onStop();
        mMessagesAdapter.stopListening();
    }

    @Override
    protected void onPause() {
        super.onPause();
        ViewebMessageHelper.updateOnline(false, ChatActivity.this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mListener != null){
            mListener.remove();
        }
    }

    private void getMessageChat(){
        Query query = mMessagesProvider.getMessageByChat(mExtraIdChat);
        FirestoreRecyclerOptions<Message> options =
                new  FirestoreRecyclerOptions.Builder<Message>()
                        .setQuery(query, Message.class)
                        .build();
        mMessagesAdapter = new MessagesAdapter(options, ChatActivity.this);
        mRecyclerView.setAdapter(mMessagesAdapter);
        mMessagesAdapter.startListening();
        mMessagesAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);
                updatedVieweb();
                int numberMessage = mMessagesAdapter.getItemCount();
                int lastMessagePosition = mLinearLayoutManager.findLastVisibleItemPosition();

                if (lastMessagePosition == -1 || (positionStart >= (numberMessage -1) && lastMessagePosition == (positionStart -1))){
                    mRecyclerView.scrollToPosition(positionStart);
                }

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
                         mMessagesAdapter.notifyDataSetChanged();
                         sendNotification(message);
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

        mListener = mUsersProvider.getUserRealTime(idUserInfo).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable @org.jetbrains.annotations.Nullable DocumentSnapshot documentSnapshot, @Nullable @org.jetbrains.annotations.Nullable FirebaseFirestoreException error) {
                if (documentSnapshot.exists()){
                    if (documentSnapshot.contains("username")){
                        String username = documentSnapshot.getString("username");
                        mTextViewUserName.setText(username);
                    }
                    if (documentSnapshot.contains("online")){
                        Boolean online = documentSnapshot.getBoolean("online");
                        if (online){
                            mTextViewRelativeTime.setText("En linea");
                        } else {
                            long lastConnect = documentSnapshot.getLong("lastConnect");
                            String relativetime = RelativeTime.getTimeAgo(lastConnect, ChatActivity.this);
                            mTextViewRelativeTime.setText(relativetime);
                        }
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

                    createChat();
                } else {
                    mExtraIdChat = queryDocumentSnapshots.getDocuments().get(0).getId();
                    mIdNotificationChat = queryDocumentSnapshots.getDocuments().get(0).getLong("idNotification");
                    getMessageChat();
                    updatedVieweb();
                }
            }
        });
    }

    private void updatedVieweb() {

        String idSender = "";

        if (mAuthProvider.getUid().equals(mExtraIdUser1)){
            idSender = mExtraIdUser2;
        } else {
            idSender = mExtraIdUser1;
        }

        mMessagesProvider.getMessageByChatAndSender(mExtraIdChat, idSender).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                    for (DocumentSnapshot document :queryDocumentSnapshots.getDocuments()){
                            mMessagesProvider.updateVieweb(document.getId(), true);
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

         Random random = new Random();
         int n = random.nextInt(100000);
         chat.setIdNotification(n);
         mIdNotificationChat = n;
        chat.setIds(ids);
        mChatProvider.create(chat);
        mExtraIdChat = chat.getId();
        getMessageChat();
    }

    private void sendNotification(Message message) {
        String idUserChat = "";

        if (mAuthProvider.getUid().equals(mExtraIdUser1)){
            idUserChat = mExtraIdUser2;
        } else {
            idUserChat = mExtraIdUser1;
        }
        mTokenProvider.getToken(idUserChat).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()){
                    if (documentSnapshot.contains("token")){
                        String token = documentSnapshot.getString("token");

                        ArrayList<Message> messageArrayList = new ArrayList<>();
                        messageArrayList.add(message);
                        Gson gson = new Gson();
                        String messages = gson.toJson(messageArrayList);

                        Map<String, String> data = new HashMap<>();
                        data.put("title", "NUEVO MENSAJE");
                        data.put("body", message.getMessage());
                        data.put("idNotification", String.valueOf(mIdNotificationChat));
                        data.put("messages", messages);
                        FCMBody body = new FCMBody( token, "high", "4500s",data);

                        mNotificationProvider.sendNotification(body).enqueue(new Callback<FCMResponse>() {
                            @Override
                            public void onResponse(Call<FCMResponse> call, Response<FCMResponse> response) {
                                if (response.body() != null){
                                    if (response.body().getSuccess() == 1){
                                        Toast.makeText(ChatActivity.this, "La notificacion se envio correctamente", Toast.LENGTH_SHORT).show();
                                    }else {
                                        Toast.makeText(ChatActivity.this, "No se pudo enviar la notificacion", Toast.LENGTH_SHORT).show();
                                    }
                                    Toast.makeText(ChatActivity.this, "No se pudo enviar la notificacion", Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onFailure(Call<FCMResponse> call, Throwable t) {

                            }
                        });
                    }
                } else {
                    Toast.makeText(ChatActivity.this, "El token del usuario no existe", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }



}