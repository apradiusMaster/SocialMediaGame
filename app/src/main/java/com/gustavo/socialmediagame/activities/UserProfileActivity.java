package com.gustavo.socialmediagame.activities;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.gustavo.socialmediagame.R;
import com.gustavo.socialmediagame.adapters.MyPostsAdapter;
import com.gustavo.socialmediagame.models.Post;
import com.gustavo.socialmediagame.providers.AuthProvider;
import com.gustavo.socialmediagame.providers.PostProvider;
import com.gustavo.socialmediagame.providers.UsersProvider;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserProfileActivity extends AppCompatActivity {

    CircleImageView mCircleImageBack;
    ImageView mImageViewCover;
    CircleImageView mCircleImageViewProfile;
    TextView mTextViewUserName;
    TextView mTextViewEmail;
    TextView mTextViewPhone;
    TextView mTextViewPostNumber;
    TextView  mTextViewExistPost;
    RecyclerView mRecyclerView;
    MyPostsAdapter mAdapter;

    UsersProvider mUserProvider;
    PostProvider mPostProvider;
    AuthProvider mAuthProvider;

    String mExtraUserId;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
        mCircleImageBack = findViewById(R.id.circleImageBack);
        mImageViewCover = findViewById(R.id.imageViewCover);
        mCircleImageViewProfile = findViewById(R.id.circleImageProfile);
        mTextViewUserName = findViewById(R.id.textViewuserName);
        mTextViewEmail = findViewById(R.id.textViewEmail);
        mTextViewPhone = findViewById(R.id.textViewPhone);
        mTextViewPostNumber = findViewById(R.id.textViewPostNumber);
        mTextViewExistPost = findViewById(R.id.textViewExistPost);
        mRecyclerView = findViewById(R.id.recyclerViewMyPost);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(UserProfileActivity.this);
        mRecyclerView.setLayoutManager(linearLayoutManager);

        mUserProvider = new UsersProvider();
        mPostProvider = new PostProvider();
        mAuthProvider = new AuthProvider();

         mExtraUserId = getIntent().getStringExtra("idUser");

        mCircleImageBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }

        });
        getUser(mExtraUserId);
        getPostUser(mExtraUserId);
        checkIfExistPost();
    }
    private void checkIfExistPost() {
        mPostProvider.getPostByUser(mExtraUserId).addSnapshotListener(new EventListener<QuerySnapshot>() {
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
    }

    @Override
    public void onStop() {
        super.onStop();
        mAdapter.stopListening();
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
                            Picasso.with(UserProfileActivity.this).load(documentSnapshot.getString("image_cover")).into(mImageViewCover);
                        }

                        if (documentSnapshot.contains("image_profile")){
                            Picasso.with(UserProfileActivity.this).load(documentSnapshot.getString("image_profile")).into(mCircleImageViewProfile);
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
}