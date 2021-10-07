package com.gustavo.socialmediagame.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.slider.Slider;
import com.google.firebase.firestore.DocumentSnapshot;
import com.gustavo.socialmediagame.R;
import com.gustavo.socialmediagame.adapters.SliderAdapter;
import com.gustavo.socialmediagame.models.SliderItem;
import com.gustavo.socialmediagame.providers.PostProvider;
import com.gustavo.socialmediagame.providers.UsersProvider;
import com.smarteist.autoimageslider.IndicatorView.animation.type.IndicatorAnimationType;
import com.smarteist.autoimageslider.SliderAnimations;
import com.smarteist.autoimageslider.SliderView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class PostDetailActivity extends AppCompatActivity {


    SliderView mSliderView;
    SliderAdapter mSliderAdapter;
    List<SliderItem> mSliderItems = new ArrayList<>();

    String mExtraPostId;

    PostProvider mPostProvider;
    UsersProvider mUserProvider;

    CircleImageView mCircleViewImageProfile;
    TextView mTextViewUserName;
    TextView mTextViewPhone;

    TextView mTextViewTitle;
    ImageView mImageViewCategory;
    TextView mTextViewCategory;
    TextView mTextViewDecription;
    Button mButtonShowProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_detail);

        mSliderView = findViewById(R.id.imageSlider);
        mCircleViewImageProfile = findViewById(R.id.circleImageViewProfile);
        mTextViewUserName = findViewById(R.id.textViewuserName);
        mTextViewPhone = findViewById(R.id.textViewPhone);
        mTextViewTitle = findViewById(R.id.textViewTitle);
        mImageViewCategory = findViewById(R.id.imageViewCategory);
        mTextViewCategory = findViewById(R.id.textViewCategory);
        mTextViewDecription = findViewById(R.id.textViewDescription);
        mButtonShowProfile = findViewById(R.id.btnShowProfile);

        mPostProvider = new PostProvider();
        mUserProvider = new UsersProvider();

        mExtraPostId = getIntent().getStringExtra("id");



        getPost();



    }

    public void getPost(){
        mPostProvider.getPostById(mExtraPostId).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists());

                if (documentSnapshot.contains("image1")){
                    String image1 = documentSnapshot.getString("image1");
                    SliderItem item = new SliderItem();
                    item.setImageUrl(image1);
                    mSliderItems.add(item);
                }

                if (documentSnapshot.contains("image2")){
                    String image2 = documentSnapshot.getString("image2");
                    SliderItem item = new SliderItem();
                    item.setImageUrl(image2);
                    mSliderItems.add(item);
                }

                if (documentSnapshot.contains("title")){
                    String title = documentSnapshot.getString("title");
                    mTextViewTitle.setText(title.toUpperCase());
                }

                if (documentSnapshot.contains("category")){
                    String category = documentSnapshot.getString("category");
                    mTextViewCategory.setText(category);
                    if (category.equals("PS4")){
                        mImageViewCategory.setImageResource(R.drawable.icon_ps4);
                    }  else if (category.equals("XBOX")){
                        mImageViewCategory.setImageResource(R.drawable.icon_xbox);
                    } else if (category.equals("PC")){
                        mImageViewCategory.setImageResource(R.drawable.icon_pc);
                    } else if (category.equals("NINTENDO")){
                        mImageViewCategory.setImageResource(R.drawable.icon_nintendo);
                    }
                }

                if (documentSnapshot.contains("description")){
                    String description = documentSnapshot.getString("description");
                    mTextViewDecription.setText(description);

                }

                if (documentSnapshot.contains("idUser")){
                    String idUser = documentSnapshot.getString("idUser");
                    getUserInfo(idUser);

                }

                mSliderAdapter = new SliderAdapter(PostDetailActivity.this, mSliderItems);
                mSliderView.setSliderAdapter(mSliderAdapter);
                mSliderView.setIndicatorAnimation(IndicatorAnimationType.THIN_WORM);
                mSliderView.setSliderTransformAnimation(SliderAnimations.SIMPLETRANSFORMATION);
                mSliderView.setAutoCycleDirection(SliderView.AUTO_CYCLE_DIRECTION_RIGHT);
                mSliderView.setIndicatorSelectedColor(Color.WHITE);
                mSliderView.setIndicatorUnselectedColor(Color.GRAY);
                mSliderView.setScrollTimeInSec(3);
                mSliderView.setAutoCycle(true);
                mSliderView.startAutoCycle();

            }
        });
    }

    private void getUserInfo(String idUser) {

        mUserProvider.getUser(idUser).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                    if (documentSnapshot.exists()){
                        if (documentSnapshot.contains("username")){
                            String username = documentSnapshot.getString("username");
                            mTextViewUserName.setText(username);
                        }
                        if (documentSnapshot.contains("phone")){
                            mTextViewPhone.setText(documentSnapshot.getString("phone"));
                        }
                        if (documentSnapshot.contains("image_profile")){
                            String imageProfile = documentSnapshot.getString("image_profile");
                            Picasso.with(PostDetailActivity.this).load(imageProfile).into(mCircleViewImageProfile);
                        }
                    }
                    else{
                        Toast.makeText(PostDetailActivity.this,"Se encontro un error", Toast.LENGTH_LONG).show();
                    }
            }
        });
    }

}