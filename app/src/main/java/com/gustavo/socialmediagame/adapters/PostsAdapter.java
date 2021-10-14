package com.gustavo.socialmediagame.adapters;

import android.content.Context;
import android.content.Intent;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
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
import com.google.firebase.firestore.QuerySnapshot;
import com.gustavo.socialmediagame.R;
import com.gustavo.socialmediagame.activities.PostDetailActivity;
import com.gustavo.socialmediagame.models.Like;
import com.gustavo.socialmediagame.models.Post;
import com.gustavo.socialmediagame.providers.AuthProvider;
import com.gustavo.socialmediagame.providers.LikesProvider;
import com.gustavo.socialmediagame.providers.UsersProvider;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import java.util.Date;

public class PostsAdapter extends FirestoreRecyclerAdapter<Post, PostsAdapter.ViewHolder> {

    Context context;
    UsersProvider mUserProvider;
    LikesProvider mLikeProvider;
    AuthProvider mAuthProvider;

    public PostsAdapter(@NonNull @NotNull FirestoreRecyclerOptions<Post> options, Context context) {
        super(options);
        this.context = context;
        mUserProvider = new UsersProvider();
        mLikeProvider = new LikesProvider();
        mAuthProvider = new AuthProvider();
    }

    @Override
    protected void onBindViewHolder(@NonNull @NotNull ViewHolder holder, int position, @NonNull @NotNull Post post) {
        DocumentSnapshot document = getSnapshots().getSnapshot(position);
        String postId = document.getId();
            holder.textViewTitle.setText(post.getTitle().toUpperCase());
            holder.textviewDescription.setText(post.getDescription());

            if (post.getImage1() != null){
                if (!post.getImage1().isEmpty()){
                    Picasso.with(context).load(post.getImage1()).into(holder.imageViewPost);
                }
            }

            holder.viewHolder.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context, PostDetailActivity.class);
                    intent.putExtra("id", postId);
                    context.startActivity(intent);
                }
            });

            holder.imageViewLike.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Like like = new Like();
                    like.setIdPost(postId);
                    like.setIdUser(mAuthProvider.getUid());
                    like.setTimestamp(new Date().getTime());
                    like(like, holder);
                }
            });


        getUserInfo(post.getIdUser(), holder);
        getNumberLikeByPost(postId, holder);
        checkIfExistLike(postId, mAuthProvider.getUid(), holder);

    }

    private void getNumberLikeByPost(String idPost, ViewHolder view){
        mLikeProvider.getLikeByPost(idPost).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable @org.jetbrains.annotations.Nullable QuerySnapshot value, @Nullable @org.jetbrains.annotations.Nullable FirebaseFirestoreException error) {

                       if (error == null){
                                if (!value.isEmpty()){
                                    int numberLikes = value.size();
                                    view.textViewLike.setText(String.valueOf(numberLikes) + " me gustas");
                                } else {
                                    view.textViewLike.setText( 0 +" me gustas");
                                }
                        }
            }
        });

    }

    private void like(Like like, ViewHolder view){

        mLikeProvider.getLikeByPostAndUser(like.getIdPost(), mAuthProvider.getUid()).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                int numberDocuments = queryDocumentSnapshots.size();

                if (numberDocuments > 0){
                    view.imageViewLike.setImageResource(R.drawable.ic_like_grey);
                    String idLike = queryDocumentSnapshots.getDocuments().get(0).getId();
                    mLikeProvider.delete(idLike);
                } else {
                    view.imageViewLike.setImageResource(R.drawable.ic_like_blue);
                    mLikeProvider.create(like);
                }
            }
        });

    }

    private void checkIfExistLike(String idPost, String idUser, ViewHolder view){

        mLikeProvider.getLikeByPostAndUser(idPost, idUser).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                int numberDocuments = queryDocumentSnapshots.size();

                if (numberDocuments > 0){
                    view.imageViewLike.setImageResource(R.drawable.ic_like_blue);

                } else {
                    view.imageViewLike.setImageResource(R.drawable.ic_like_grey);

                }
            }
        });

    }

    private void getUserInfo(String id, ViewHolder holder) {
        mUserProvider.getUser(id).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()){
                    if (documentSnapshot.contains("username")){
                        String username = documentSnapshot.getString("username");

                        holder.textViewUserName.setText("By: " +username.toUpperCase());
                    }
                }
            }
        });

    }

    @NonNull
    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from( parent.getContext()).inflate(R.layout.cardview_post, parent , false);
        return  new ViewHolder(view);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView textViewTitle;
        TextView textViewUserName;
        TextView textviewDescription;
        TextView textViewLike;
        ImageView imageViewPost;
        ImageView imageViewLike;
        View viewHolder;


        public ViewHolder(@NonNull @NotNull View view) {
            super(view);

            textViewTitle = view.findViewById(R.id.textViewTitlePostCard);
            textViewUserName = view.findViewById(R.id.textViewuserNamePostCard);
            textviewDescription = view.findViewById(R.id.textViewDescriptionPostCard);
            textViewLike = view.findViewById(R.id.textviewLike);
            imageViewPost = view.findViewById(R.id.imageViewPostCard);
            imageViewLike = view.findViewById(R.id.imageViewLike);
            viewHolder = view;
        }
    }
}
