package com.gustavo.socialmediagame.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.gustavo.socialmediagame.R;
import com.gustavo.socialmediagame.adapters.CommentAdapter;
import com.gustavo.socialmediagame.adapters.SliderAdapter;
import com.gustavo.socialmediagame.models.Comment;
import com.gustavo.socialmediagame.models.FCMBody;
import com.gustavo.socialmediagame.models.FCMResponse;
import com.gustavo.socialmediagame.models.SliderItem;
import com.gustavo.socialmediagame.models.Token;
import com.gustavo.socialmediagame.providers.AuthProvider;
import com.gustavo.socialmediagame.providers.CommentsProvider;
import com.gustavo.socialmediagame.providers.LikesProvider;
import com.gustavo.socialmediagame.providers.NotificationProvider;
import com.gustavo.socialmediagame.providers.PostProvider;
import com.gustavo.socialmediagame.providers.TokenProvider;
import com.gustavo.socialmediagame.providers.UsersProvider;
import com.gustavo.socialmediagame.utils.RelativeTime;
import com.gustavo.socialmediagame.utils.ViewebMessageHelper;
import com.smarteist.autoimageslider.IndicatorView.animation.type.IndicatorAnimationType;
import com.smarteist.autoimageslider.SliderAnimations;
import com.smarteist.autoimageslider.SliderView;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PostDetailActivity extends AppCompatActivity {


    SliderView mSliderView;
    SliderAdapter mSliderAdapter;
    List<SliderItem> mSliderItems = new ArrayList<>();

    String mExtraPostId;

    PostProvider mPostProvider;
    UsersProvider mUserProvider;
    CommentsProvider mCommentsProvider;
    AuthProvider mAuthProvider;
    CommentAdapter mCommentAdapter;
    NotificationProvider mNotificationProvider;
    TokenProvider mTokenProvider;

    CircleImageView mCircleViewImageProfile;
    TextView mTextViewUserName;
    TextView mTextViewPhone;

    TextView mTextViewTitle;
    ImageView mImageViewCategory;
    TextView mTextViewCategory;
    TextView mTextViewDecription;
    Button mButtonShowProfile;
    FloatingActionButton mFabComment;
    RecyclerView mRecyclerViewComment;
    TextView mTextViewRelativeTime;
    TextView mTextViewLikes;
    LikesProvider mLikesProvider;
    Toolbar mToolbar;

    String mIdUser;

    ListenerRegistration mListener;

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
        mFabComment = findViewById(R.id.fabComment);
        mRecyclerViewComment = findViewById(R.id.recyclerViewComment);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(PostDetailActivity.this);
        mRecyclerViewComment.setLayoutManager(linearLayoutManager);
        mTextViewRelativeTime = findViewById(R.id.textViewRelativeTime);
        mTextViewLikes = findViewById(R.id.textViewRelativeLike);
        mToolbar = findViewById(R.id.toolbar);

        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mPostProvider = new PostProvider();
        mUserProvider = new UsersProvider();
        mCommentsProvider = new CommentsProvider();
        mAuthProvider = new AuthProvider();
        mLikesProvider = new LikesProvider();
        mNotificationProvider = new NotificationProvider();
        mTokenProvider = new TokenProvider();


        mExtraPostId = getIntent().getStringExtra("id");

        mFabComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialogComment();
            }
        });

        mButtonShowProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToShowProfile();

            }
        });

        getPost();
        getNumberLikes();

    }

    private void getNumberLikes() {
     mListener = mLikesProvider.getLikeByPost(mExtraPostId).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable @org.jetbrains.annotations.Nullable QuerySnapshot value, @Nullable @org.jetbrains.annotations.Nullable FirebaseFirestoreException error) {
            /*   if (error != null){*/
                   if (value != null){
                       int numberLikes = value.size();
                       if (numberLikes == 1){
                           mTextViewLikes.setText("me gustas");
                       } else {
                           mTextViewLikes.setText(numberLikes + " me gustas");
                       }

                   }
             //  }

            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        Query query = mCommentsProvider.getCommentByPost(mExtraPostId);
        FirestoreRecyclerOptions<Comment> options =
                new  FirestoreRecyclerOptions.Builder<Comment>()
                        .setQuery(query, Comment.class)
                        .build();
        mCommentAdapter = new CommentAdapter(options, PostDetailActivity.this);
        mRecyclerViewComment.setAdapter(mCommentAdapter);
        mCommentAdapter.startListening();
        ViewebMessageHelper.updateOnline(true, PostDetailActivity.this);

    }

    @Override
    protected void onStop() {
        super.onStop();
        mCommentAdapter.stopListening();
    }

    @Override
    protected void onPause() {
        super.onPause();
        ViewebMessageHelper.updateOnline(false, PostDetailActivity.this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mListener != null){
            mListener.remove();
        }
    }

    private void showDialogComment() {
        AlertDialog.Builder alert = new AlertDialog.Builder(PostDetailActivity.this);
        alert.setTitle("!COMENTARIO!");
        alert.setMessage("Ingresa tu comentario");

        EditText editText = new EditText(PostDetailActivity.this);
        editText.setHint("texto");


        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        );
        params.setMargins(36 ,0, 36, 36);
        editText.setLayoutParams(params);

        RelativeLayout container = new RelativeLayout(PostDetailActivity.this);
        RelativeLayout.LayoutParams relativeParams = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT
        );
        container.setLayoutParams(relativeParams);
        container.addView(editText);
        alert.setView(container);


        alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String value = editText.getText().toString();

                if (!value.isEmpty()){
                    createComment(value);
                } else {
                    Toast.makeText(PostDetailActivity.this, "Debe agregar un comentario", Toast.LENGTH_SHORT).show();
                  }


            }
        });

        alert.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        alert.show();

    }

    private void createComment(String value) {
        Comment comment = new Comment();
        comment.setComment(value);
        comment.setIdUser(mAuthProvider.getUid());
        comment.setIdPost(mExtraPostId);
        comment.setTimestamp(new Date().getTime());
        mCommentsProvider.create(comment).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull @NotNull Task<Void> task) {
                if (task.isSuccessful()){
                    sendNotification(value);
                    Toast.makeText(PostDetailActivity.this, "se creo el comentario",Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(PostDetailActivity.this, "No se pudo crear el comentario",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void sendNotification(String comment) {
        if (mIdUser == null){
            return;
        }
        mTokenProvider.getToken(mIdUser).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                    if (documentSnapshot.exists()){
                        if (documentSnapshot.contains("token")){
                            String token = documentSnapshot.getString("token");
                            Map<String, String> data = new HashMap<>();
                            data.put("title", "NUEVO COMENTARIO");
                            data.put("body", comment);
                            FCMBody body = new FCMBody( token, "high", "4500s",data);

                            mNotificationProvider.sendNotification(body).enqueue(new Callback<FCMResponse>() {
                                @Override
                                public void onResponse(Call<FCMResponse> call, Response<FCMResponse> response) {
                                     if (response.body() != null){
                                         if (response.body().getSuccess() == 1){
                                             Toast.makeText(PostDetailActivity.this, "La notificacion se envio correctamente", Toast.LENGTH_SHORT).show();
                                         }else {
                                             Toast.makeText(PostDetailActivity.this, "No se pudo enviar la notificacion", Toast.LENGTH_SHORT).show();
                                         }
                                         Toast.makeText(PostDetailActivity.this, "No se pudo enviar la notificacion", Toast.LENGTH_SHORT).show();
                                     }
                                }

                                @Override
                                public void onFailure(Call<FCMResponse> call, Throwable t) {

                                }
                            });
                        }
                    } else {
                        Toast.makeText(PostDetailActivity.this, "El token del usuario no existe", Toast.LENGTH_SHORT).show();
                    }
            }
        });
    }

    private void goToShowProfile() {
        if (!mIdUser.equals("")){
            Intent intent = new Intent(PostDetailActivity.this, UserProfileActivity.class);
            intent.putExtra("idUser", mIdUser);
            startActivity(intent);
        }
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
                     mIdUser= documentSnapshot.getString("idUser");
                    getUserInfo(mIdUser);

                }

                if (documentSnapshot.contains("timestamp")){
                    long timestamp = documentSnapshot.getLong("timestamp");
                    String relativeTime = RelativeTime.getTimeAgo(timestamp, PostDetailActivity.this);
                    mTextViewRelativeTime.setText(relativeTime);

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
                       // Toast.makeText(PostDetailActivity.this,"Se encontro un error", Toast.LENGTH_LONG).show();
                    }
            }
        });
    }

}