package com.gustavo.socialmediagame.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.gustavo.socialmediagame.R;
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

    UsersProvider mUserProvider;
    PostProvider mPostProvider;



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

        mUserProvider = new UsersProvider();
        mPostProvider = new PostProvider();

        String  mExtraUserId = getIntent().getStringExtra("idUser");

        mCircleImageBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }

        });
        getUser(mExtraUserId);
        getPostUser(mExtraUserId);
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