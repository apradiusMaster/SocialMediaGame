package com.gustavo.socialmediagame.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.textfield.TextInputEditText;
import com.gustavo.socialmediagame.R;

import java.io.File;

import de.hdodenhof.circleimageview.CircleImageView;

public class EditProfileActivity extends AppCompatActivity {


    CircleImageView mCircleImageView;
    CircleImageView mCircleImageProfile;
    ImageView mImageViewEditProfile;
    TextInputEditText mTextInputEditUserName;
    TextInputEditText mTextInputEditPhone;

    File mImageFile;

    final int IMAGE_REQUEST_CODE = 1;

    String mAbsolutePhotoPath;
    String mPhotoPath;
    File mPhotoFile;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        mCircleImageView = findViewById(R.id.circleImageBack);
        mCircleImageProfile  = findViewById(R.id.circleImageProfile);
        mImageViewEditProfile = findViewById(R.id.imageViewEditProfile);
        mTextInputEditUserName = findViewById(R.id.textInputUserName);
        mTextInputEditPhone = findViewById(R.id.textInputPhone);


        mCircleImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        
    }
}