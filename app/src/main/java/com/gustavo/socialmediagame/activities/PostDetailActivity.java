package com.gustavo.socialmediagame.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.slider.Slider;
import com.google.firebase.firestore.DocumentSnapshot;
import com.gustavo.socialmediagame.R;
import com.gustavo.socialmediagame.adapters.SliderAdapter;
import com.gustavo.socialmediagame.models.SliderItem;
import com.gustavo.socialmediagame.providers.PostProvider;
import com.smarteist.autoimageslider.IndicatorView.animation.type.IndicatorAnimationType;
import com.smarteist.autoimageslider.SliderAnimations;
import com.smarteist.autoimageslider.SliderView;

import java.util.ArrayList;
import java.util.List;

public class PostDetailActivity extends AppCompatActivity {


    SliderView mSliderView;
    SliderAdapter mSliderAdapter;
    List<SliderItem> mSliderItems = new ArrayList<>();

    String mExtraPostId;

    PostProvider mPostProvider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_detail);

        mSliderView = findViewById(R.id.imageSlider);

        mExtraPostId = getIntent().getStringExtra("id");

        mPostProvider = new PostProvider();

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

}