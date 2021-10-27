package com.gustavo.socialmediagame.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter_LifecycleAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.DocumentSnapshot;
import com.gustavo.socialmediagame.R;
import com.gustavo.socialmediagame.models.Message;
import com.gustavo.socialmediagame.providers.AuthProvider;
import com.gustavo.socialmediagame.utils.RelativeTime;

import org.jetbrains.annotations.NotNull;

public class MessagesAdapter extends FirestoreRecyclerAdapter<Message, MessagesAdapter.ViewHolder> {


   Context context;
   AuthProvider mAuthProvider;


    public MessagesAdapter(@NonNull @NotNull FirestoreRecyclerOptions<Message> options, Context context) {
        super(options);
        this.context = context;
        mAuthProvider = new AuthProvider();
    }

    @SuppressLint("ResourceAsColor")
    @Override
    protected void onBindViewHolder(@NonNull @NotNull ViewHolder holder, int position, @NonNull @NotNull Message model) {

        DocumentSnapshot document = getSnapshots().getSnapshot(position);
        String MessageId = document.getId();
        holder.textViewMessage.setText(model.getMessage());

        String relativeTime = RelativeTime.getTimeAgo(model.getTimestamp(), context);
        holder.textViewDate.setText(relativeTime);

        if (model.getIdSender().equals(mAuthProvider.getUid())){
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.WRAP_CONTENT,
                    RelativeLayout.LayoutParams.WRAP_CONTENT
            );
            params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
            params.setMargins(150, 0, 0,0);
            holder.linearLayoutMessage.setLayoutParams(params);
            holder.linearLayoutMessage.setPadding(30, 20, 25, 20);
            holder.linearLayoutMessage.setBackground(context.getResources().getDrawable(R.drawable.rounded_linear_layout));
            holder.imageViewVieweb.setVisibility(View.VISIBLE);
        }
        else {
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.WRAP_CONTENT,
                    RelativeLayout.LayoutParams.WRAP_CONTENT
            );
            params.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
            params.setMargins(0, 0, 150,0);
            holder.linearLayoutMessage.setLayoutParams(params);
            holder.linearLayoutMessage.setPadding(30, 20, 30, 20);
            holder.linearLayoutMessage.setBackground(context.getResources().getDrawable(R.drawable.rounded_linear_layout_grey));
            holder.imageViewVieweb.setVisibility(View.GONE);
            holder.textViewMessage.setTextColor(R.color.black);
        }

    }

    @NonNull
    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_message,parent, false);
        return  new ViewHolder(view);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

          TextView textViewMessage;
          TextView textViewDate;
          ImageView imageViewVieweb;
          LinearLayout linearLayoutMessage;

        public ViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);

            textViewMessage = itemView.findViewById(R.id.textViewMessage);
            textViewDate = itemView.findViewById(R.id.textviewDateMessage);
            imageViewVieweb = itemView.findViewById(R.id.ImageViewViewViwebMessage);
            linearLayoutMessage = itemView.findViewById(R.id.linearLayoutMessage);
        }
    }
}
