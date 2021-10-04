package com.gustavo.socialmediagame.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.gustavo.socialmediagame.R;
import com.gustavo.socialmediagame.activities.EditProfileActivity;
import com.gustavo.socialmediagame.providers.AuthProvider;
import com.gustavo.socialmediagame.providers.PostProvider;
import com.gustavo.socialmediagame.providers.UsersProvider;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProfileFragment extends Fragment {

    View mView;
    LinearLayout mLinearLayoutEditProfile;
    UsersProvider mUserProvider;
    AuthProvider mAuthProvider;
    PostProvider mPostProvider;

    TextView mTextViewUserName;
    TextView mTextViewEmail;
    TextView mTextViewPhone;
    TextView mTextViewPostNumber;

    ImageView mImageViewCover;
    CircleImageView mCircleImageProfile;


    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ProfileFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ProfileFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ProfileFragment newInstance(String param1, String param2) {
        ProfileFragment fragment = new ProfileFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mView = inflater.inflate(R.layout.fragment_profile, container, false);
        mUserProvider = new UsersProvider();
        mAuthProvider = new AuthProvider();
        mPostProvider = new PostProvider();
        mTextViewUserName = mView.findViewById(R.id.textViewuserName);
        mTextViewEmail = mView.findViewById(R.id.textViewEmail);
        mTextViewPhone = mView.findViewById(R.id.textViewPhone);
        mTextViewPostNumber = mView.findViewById(R.id.textViewPostNumber);
        mCircleImageProfile = mView.findViewById(R.id.circleImageProfile);
        mImageViewCover = mView.findViewById(R.id.imageViewCover);


        mLinearLayoutEditProfile = mView.findViewById(R.id.linearLayoutEditProfile);

        mLinearLayoutEditProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToEditProfile();
            }
        });
        getUser();
        getPostUser();
        return  mView;

    }

    private void getUser() {
        mUserProvider.getUser(mAuthProvider.getUid()).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {

                if (documentSnapshot.contains("username")){
                    String username = documentSnapshot.getString("username");
                    mTextViewUserName.setText(username);
                }
                if (documentSnapshot.contains("email")){
                    String  email = documentSnapshot.getString("email");
                    mTextViewEmail.setText(email);
                }
                if(documentSnapshot.contains("phone")){
                    String phone = documentSnapshot.getString("phone");
                    mTextViewPhone.setText(String.valueOf(phone));
                }
                if (documentSnapshot.contains("image_profile")){
                    String imageProfile = documentSnapshot.getString("image_profile");
                    if (imageProfile != null){
                        if (!imageProfile.isEmpty()){
                            Picasso.with(getContext()).load(imageProfile).into(mCircleImageProfile);
                        }
                    }
                }
                if (documentSnapshot.contains("image_cover")){
                    String imageCover = documentSnapshot.getString("image_cover");
                    if (imageCover != null){
                        if (!imageCover.isEmpty()){
                            Picasso.with(getContext()).load(imageCover).into(mImageViewCover);
                        }
                    }
                }
            }
        });
    }

    private void getPostUser(){
        mPostProvider.getPostByUser(mAuthProvider.getUid()).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                int numberPost = queryDocumentSnapshots.size();
                mTextViewPostNumber.setText(String.valueOf(numberPost));
            }
        });
    }

    private void goToEditProfile() {

        Intent intent = new Intent(getContext(), EditProfileActivity.class);
        startActivity(intent);
    }
}