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
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.DocumentSnapshot;
import com.gustavo.socialmediagame.R;
import com.gustavo.socialmediagame.activities.PostDetailActivity;
import com.gustavo.socialmediagame.models.Post;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

public class PostsAdapter extends FirestoreRecyclerAdapter<Post, PostsAdapter.ViewHolder> {

    Context context;

    public PostsAdapter(@NonNull @NotNull FirestoreRecyclerOptions<Post> options, Context context) {
        super(options);
        this.context = context;
    }

    @Override
    protected void onBindViewHolder(@NonNull @NotNull ViewHolder holder, int position, @NonNull @NotNull Post post) {
        DocumentSnapshot document = getSnapshots().getSnapshot(position);
        String postId = document.getId();
            holder.textViewTitle.setText(post.getTitle());
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
        TextView textviewDescription;
        ImageView imageViewPost;
        View viewHolder;

        public ViewHolder(@NonNull @NotNull View view) {
            super(view);

            textViewTitle = view.findViewById(R.id.textViewTitlePostCard);
            textviewDescription = view.findViewById(R.id.textViewDescriptionPostCard);
            imageViewPost = view.findViewById(R.id.imageViewPostCard);
            viewHolder = view;
        }
    }
}
