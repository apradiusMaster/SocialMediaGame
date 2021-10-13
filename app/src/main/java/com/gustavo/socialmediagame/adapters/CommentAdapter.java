package com.gustavo.socialmediagame.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.gustavo.socialmediagame.R;
import com.gustavo.socialmediagame.models.Comment;
import com.gustavo.socialmediagame.providers.UsersProvider;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import de.hdodenhof.circleimageview.CircleImageView;

public class CommentAdapter extends FirestoreRecyclerAdapter<Comment, CommentAdapter.ViewHolder> {

    /**
     * Create a new RecyclerView adapter that listens to a Firestore Query.  See {@link
     * FirestoreRecyclerOptions} for configuration options.
     *
     * @param options
     */

    Context context;
    UsersProvider mUserProvider;
    public CommentAdapter(@NonNull @NotNull FirestoreRecyclerOptions<Comment> options, Context context) {
        super(options);
        this.context = context;
        mUserProvider = new UsersProvider();
    }

    @Override
    protected void onBindViewHolder(@NonNull @NotNull ViewHolder holder, int position, @NonNull @NotNull Comment model) {
        DocumentSnapshot document =  getSnapshots().getSnapshot(position);
        String commentId = document.getId();
        String idUser = document.getString("idUser");

        holder.textViewComment.setText(model.getComment());

        getUserInfo(idUser, holder);




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
       View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_comment, parent, false);

       return new ViewHolder(view);
    }

    public class ViewHolder  extends RecyclerView.ViewHolder {

         TextView textViewCommentUserName;
         TextView textViewComment;
         CircleImageView circleImageView;

        public ViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);

            textViewCommentUserName = itemView.findViewById(R.id.textViewCommentUserName);
            textViewComment = itemView.findViewById(R.id.textViewComment);
            circleImageView = itemView.findViewById(R.id.circleImageComment);


        }
    }
}
