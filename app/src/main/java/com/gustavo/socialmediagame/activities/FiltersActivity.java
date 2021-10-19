package com.gustavo.socialmediagame.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.Query;
import com.gustavo.socialmediagame.R;
import com.gustavo.socialmediagame.adapters.PostsAdapter;
import com.gustavo.socialmediagame.models.Post;
import com.gustavo.socialmediagame.providers.AuthProvider;
import com.gustavo.socialmediagame.providers.PostProvider;

public class FiltersActivity extends AppCompatActivity {

    String mExtraCategory;

    AuthProvider mAuthProvider;
    RecyclerView mRecyclerView;
    PostProvider mPostProvider;
    PostsAdapter mPostAdapter;

    TextView mTextViewNumberFilter;
    Toolbar mToolbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filters);
        mRecyclerView = findViewById(R.id.recyclerViewFilter);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(FiltersActivity.this);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mExtraCategory = getIntent().getStringExtra("category");
        mTextViewNumberFilter = findViewById(R.id.textViewNumberFilter);
        mToolbar = findViewById(R.id.toolbar);

        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Filtro");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mRecyclerView.setLayoutManager(new GridLayoutManager( FiltersActivity.this,2));

        mAuthProvider = new AuthProvider();
        mPostProvider = new PostProvider();



    }

    public void onStart() {
        super.onStart();
        Query query = mPostProvider.getPostByCategoryAndTimestamp(mExtraCategory);
        FirestoreRecyclerOptions<Post> options =
                new  FirestoreRecyclerOptions.Builder<Post>()
                        .setQuery(query, Post.class)
                        .build();
        mPostAdapter = new PostsAdapter(options, FiltersActivity.this, mTextViewNumberFilter);
        mRecyclerView.setAdapter(mPostAdapter);
        mPostAdapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        mPostAdapter.stopListening();
    }

    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home){
            finish();
        }
        return true;
    }
}