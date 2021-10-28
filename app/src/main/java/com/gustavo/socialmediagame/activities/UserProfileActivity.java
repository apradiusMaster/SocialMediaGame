package com.gustavo.socialmediagame.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.gustavo.socialmediagame.R;
import com.gustavo.socialmediagame.adapters.MyPostsAdapter;
import com.gustavo.socialmediagame.models.Post;
import com.gustavo.socialmediagame.providers.AuthProvider;
import com.gustavo.socialmediagame.providers.PostProvider;
import com.gustavo.socialmediagame.providers.UsersProvider;
import com.gustavo.socialmediagame.utils.ViewebMessageHelper;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserProfileActivity extends AppCompatActivity {


    ImageView mImageViewCover;
    CircleImageView mCircleImageViewProfile;
    TextView mTextViewUserName;
    TextView mTextViewEmail;
    TextView mTextViewPhone;
    TextView mTextViewPostNumber;
    TextView  mTextViewExistPost;
    RecyclerView mRecyclerView;
    ListenerRegistration mListener;
    MyPostsAdapter mAdapter;
    FloatingActionButton  mFabChat;

    UsersProvider mUserProvider;
    PostProvider mPostProvider;
    AuthProvider mAuthProvider;
    Toolbar mToolbar;

    String mExtraUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        mImageViewCover = findViewById(R.id.imageViewCover);
        mCircleImageViewProfile = findViewById(R.id.circleImageProfile);
        mTextViewUserName = findViewById(R.id.textViewuserName);
        mTextViewEmail = findViewById(R.id.textViewEmail);
        mTextViewPhone = findViewById(R.id.textViewPhone);
        mTextViewPostNumber = findViewById(R.id.textViewPostNumber);
        mTextViewExistPost = findViewById(R.id.textViewExistPost);
        mRecyclerView = findViewById(R.id.recyclerViewMyPost);
        mFabChat = findViewById(R.id.fabChat);
        mToolbar = findViewById(R.id.toolbar);

        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(UserProfileActivity.this);
        mRecyclerView.setLayoutManager(linearLayoutManager);

        mUserProvider = new UsersProvider();
        mPostProvider = new PostProvider();
        mAuthProvider = new AuthProvider();

         mExtraUserId = getIntent().getStringExtra("idUser");

         if (mAuthProvider.getUid().equals(mExtraUserId)){
             mFabChat.setVisibility(View.GONE);
         }

         mFabChat.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View view) {
                 gotoChatActivity();
             }
         });


        getUser(mExtraUserId);
        getPostUser(mExtraUserId);
        checkIfExistPost();
    }

    private void gotoChatActivity() {

        Intent intent = new Intent(UserProfileActivity.this, ChatActivity.class);
        intent.putExtra("idUser1", mAuthProvider.getUid());
        intent.putExtra("idUser2", mExtraUserId);
        startActivity(intent);
    }

    private void checkIfExistPost() {
       mListener =  mPostProvider.getPostByUser(mExtraUserId).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable @org.jetbrains.annotations.Nullable QuerySnapshot value, @Nullable @org.jetbrains.annotations.Nullable FirebaseFirestoreException error) {

               /* if (error != null){ */
                    if (value != null){

                        int numberPost = value.size();
                        if (numberPost > 0){
                            mTextViewExistPost.setText("Publicaciones");
                            mTextViewExistPost.setTextColor(getResources().getColor(R.color.colorPrimary));

                        } else {
                            mTextViewExistPost.setText("No hay publicaciones");
                            mTextViewExistPost.setTextColor(getResources().getColor(R.color.colorGray));

                        }

                   }
              //  }

            }
        });
    }

    public void onStart() {
        super.onStart();
        Query query = mPostProvider.getPostByUser(mExtraUserId);
        FirestoreRecyclerOptions<Post> options =
                new  FirestoreRecyclerOptions.Builder<Post>()
                        .setQuery(query, Post.class)
                        .build();
        mAdapter = new MyPostsAdapter(options, UserProfileActivity.this);
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.startListening();
        ViewebMessageHelper.updateOnline(true, UserProfileActivity.this);
    }

    @Override
    public void onStop() {
        super.onStop();
        mAdapter.stopListening();
    }

    @Override
    protected void onPause() {
        super.onPause();
        ViewebMessageHelper.updateOnline(false, UserProfileActivity.this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mListener != null){
            mListener.remove();
        }
    }

    public  void getUser(String userId){

        mUserProvider.getUser(userId).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()){

                        if (documentSnapshot.contains("phone")){
                            mTextViewPhone.setText(documentSnapshot.getString("phone"));
                        }
                        if (documentSnapshot.contains("username")){
                            mTextViewUserName.setText(documentSnapshot.getString("username"));
                        }

                        if (documentSnapshot.contains("email")){
                            mTextViewEmail.setText(documentSnapshot.getString("email"));
                        }
                        if (documentSnapshot.contains("image_cover")){
                            String imageCover = documentSnapshot.getString("image_cover");
                            if (imageCover != null){
                                if (!imageCover.isEmpty()){
                                    Picasso.with(UserProfileActivity.this).load(imageCover).into(mImageViewCover);
                                }
                            }

                        }

                        if (documentSnapshot.contains("image_profile")){
                            String imageProfile = documentSnapshot.getString("image_profile");
                            if (imageProfile != null){
                                if (!imageProfile.isEmpty()){
                                    Picasso.with(UserProfileActivity.this).load(imageProfile).into(mCircleImageViewProfile);
                                }
                            }
                        }

                } else{
                    Toast.makeText(UserProfileActivity.this, "Hubo un error en la obtencion de datos", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public  void  getPostUser(String idUser){

        mPostProvider.getPostByUser(idUser).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                    int postNumber = queryDocumentSnapshots.size();

                    mTextViewPostNumber.setText(String.valueOf(postNumber));

            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home){
            finish();
        }
        return true;
    }
}