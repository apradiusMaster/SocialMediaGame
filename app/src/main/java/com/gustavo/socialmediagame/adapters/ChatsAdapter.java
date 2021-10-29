package com.gustavo.socialmediagame.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QuerySnapshot;
import com.gustavo.socialmediagame.R;
import com.gustavo.socialmediagame.activities.ChatActivity;
import com.gustavo.socialmediagame.models.Chat;
import com.gustavo.socialmediagame.models.Comment;
import com.gustavo.socialmediagame.providers.AuthProvider;
import com.gustavo.socialmediagame.providers.ChatsProvider;
import com.gustavo.socialmediagame.providers.MessagesProvider;
import com.gustavo.socialmediagame.providers.UsersProvider;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatsAdapter extends FirestoreRecyclerAdapter<Chat, ChatsAdapter.ViewHolder> {

    /**
     * Create a new RecyclerView adapter that listens to a Firestore Query.  See {@link
     * FirestoreRecyclerOptions} for configuration options.
     *
     * @param options
     */

    Context context;
    UsersProvider mUserProvider;
    AuthProvider mAuthProvider;
    ChatsProvider mChatsProvider;
    MessagesProvider mMessagesProvider;
    ListenerRegistration mListener;
    public ChatsAdapter(@NonNull @NotNull FirestoreRecyclerOptions<Chat> options, Context context) {
        super(options);
        this.context = context;
        mUserProvider = new UsersProvider();
        mAuthProvider = new AuthProvider();
        mChatsProvider = new ChatsProvider();
        mMessagesProvider = new MessagesProvider();
    }

    @Override
    protected void onBindViewHolder(@NonNull @NotNull ViewHolder holder, int position, @NonNull @NotNull Chat chat) {
        DocumentSnapshot document =  getSnapshots().getSnapshot(position);
        String chatId = document.getId();

        if (mAuthProvider.getUid().equals(chat.getIdUser1())){
            getUserInfo(chat.getIdUser2(), holder);
        } else {
             getUserInfo(chat.getIdUser1(), holder);
          }

        holder.viewHolder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                  gtoChatActivity(chatId, chat.getIdUser1(), chat.getIdUser2());
            }
        });

        getLastMessage(chatId, holder.textViewMessage);

        String idSender = "";

        if (mAuthProvider.getUid().equals(chat.getIdUser1())){
            idSender = chat.getIdUser2();
        } else {
            idSender = chat.getIdUser1();
        }

        getMessageNotRead(chatId, idSender, holder.textViewMessageNotRead, holder.frameLayoutMessageNotRead);
    }

    private void getMessageNotRead(String chatId, String idSender, TextView textViewMessageNotRead, FrameLayout frameLayoutMessageNotRead) {
        mListener =  mMessagesProvider.getMessageByChatAndSender(chatId,idSender).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable @org.jetbrains.annotations.Nullable QuerySnapshot queryDocumentSnapshots, @Nullable @org.jetbrains.annotations.Nullable FirebaseFirestoreException error) {
                if (queryDocumentSnapshots != null){
                    int size = queryDocumentSnapshots.size();
                    if (size > 0){
                        frameLayoutMessageNotRead.setVisibility(View.VISIBLE);
                        textViewMessageNotRead.setText(String.valueOf(size));
                    } else {
                        frameLayoutMessageNotRead.setVisibility(View.GONE);
                    }
                }
            }
        });

    }

    public  ListenerRegistration  getListener(){
        return mListener;
    }

    private void getLastMessage(String chatId, TextView textViewMessage) {
        mMessagesProvider.getLastMessage(chatId).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                int size = queryDocumentSnapshots.size();
                if (size >0){
                    String lastMessage = queryDocumentSnapshots.getDocuments().get(0).getString("message");
                    textViewMessage.setText(lastMessage);

                }
            }
        });
    }

    private void gtoChatActivity(String chatId, String idUser1, String idUser2) {

        Intent intent = new Intent(context, ChatActivity.class);
        intent.putExtra("idChat", chatId);
        intent.putExtra("idUser1", idUser1);
        intent.putExtra("idUser2", idUser2);
        context.startActivity(intent);
    }

    private void getUserInfo(String idUser, ViewHolder holder) {
        mUserProvider.getUser(idUser).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {

                if (documentSnapshot.exists()){
                    if (documentSnapshot.contains("username"));
                    holder.textViewCommentUserName.setText(documentSnapshot.getString("username").toUpperCase());
                }

                if (documentSnapshot.contains("image_profile"));{
                    String imageProfile = documentSnapshot.getString("image_profile");
                    if (imageProfile !=null){
                        if (!imageProfile.isEmpty()){
                            Picasso.with(context).load(imageProfile).into(holder.circleImageView);
                        }
                    }

                }

            }
        });

    }

    @NonNull
    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
       View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_chat, parent, false);

       return new ViewHolder(view);
    }

    public class ViewHolder  extends RecyclerView.ViewHolder {

         TextView textViewCommentUserName;
         TextView textViewMessage;
         TextView textViewMessageNotRead;
         CircleImageView circleImageView;
         FrameLayout frameLayoutMessageNotRead;
         View viewHolder;

        public ViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);

            textViewCommentUserName = itemView.findViewById(R.id.textViewUserNameChat);
            textViewMessage = itemView.findViewById(R.id.textViewLastMesageChat);
            textViewMessageNotRead = itemView.findViewById(R.id.textViewMessageNotRead);
            circleImageView = itemView.findViewById(R.id.circleImageviewChat);
            frameLayoutMessageNotRead = itemView.findViewById(R.id.frameLayoutmessageNotRead);
            viewHolder = itemView;

        }
    }
}
