package com.gustavo.socialmediagame.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.Query;
import com.gustavo.socialmediagame.R;
import com.gustavo.socialmediagame.activities.MainActivity;
import com.gustavo.socialmediagame.activities.PostActivity;
import com.gustavo.socialmediagame.adapters.PostsAdapter;
import com.gustavo.socialmediagame.models.Post;
import com.gustavo.socialmediagame.providers.AuthProvider;
import com.gustavo.socialmediagame.providers.PostProvider;
import com.mancj.materialsearchbar.MaterialSearchBar;

import java.util.zip.Inflater;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment implements MaterialSearchBar.OnSearchActionListener {




    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    View mView;
    MaterialSearchBar mSearchBar;
    FloatingActionButton mFab;
    Toolbar mToolbar;
    AuthProvider mAuthProvider;
    RecyclerView mRecyclerView;
    PostProvider  mPostProvider;
    PostsAdapter mPostAdapter;
    PostsAdapter mPostAdapterSearch;



    public HomeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HomeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public  void searchByTitle(String title){

        Query query = mPostProvider.getPostByTitle(title);
        FirestoreRecyclerOptions<Post> options =
                new  FirestoreRecyclerOptions.Builder<Post>()
                        .setQuery(query, Post.class)
                        .build();
        mPostAdapterSearch = new PostsAdapter(options, getContext());
        mPostAdapterSearch.notifyDataSetChanged();
        mRecyclerView.setAdapter(mPostAdapterSearch);
        mPostAdapterSearch.startListening();

    }

    public void getAllPost(){
        Query query = mPostProvider.getAll();
        FirestoreRecyclerOptions<Post> options =
                new  FirestoreRecyclerOptions.Builder<Post>()
                        .setQuery(query, Post.class)
                        .build();
        mPostAdapter = new PostsAdapter(options, getContext());
        mPostAdapter.notifyDataSetChanged();
        mRecyclerView.setAdapter(mPostAdapter);
        mPostAdapter.startListening();

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mView = inflater.inflate(R.layout.fragment_home, container, false);
        mFab = mView.findViewById(R.id.fab);
        mToolbar = mView.findViewById(R.id.toolbar);
        mRecyclerView = mView.findViewById(R.id.recyclerViewHome);
        mSearchBar = mView.findViewById(R.id.searchBar);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(linearLayoutManager);

        setHasOptionsMenu(true);
        mAuthProvider = new AuthProvider();
        mPostProvider = new PostProvider();

        mSearchBar.setOnSearchActionListener(this);

        mSearchBar.inflateMenu(R.menu.main_menu);
        mSearchBar.getMenu().setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (item.getItemId() == R.id.itemLogout){
                    logout();

                }
                return true;
            }
        });


        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToPost();
            }

        });
        return mView;
    }

    @Override
    public void onStart() {
        super.onStart();
        Query query = mPostProvider.getAll();
        FirestoreRecyclerOptions<Post> options =
                new  FirestoreRecyclerOptions.Builder<Post>()
                .setQuery(query, Post.class)
                .build();
        mPostAdapter = new PostsAdapter(options, getContext());
        mRecyclerView.setAdapter(mPostAdapter);
        mPostAdapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        mPostAdapter.stopListening();
        if (mPostAdapterSearch != null){
            mPostAdapterSearch.stopListening();
        }
    }

    private void goToPost() {
        Intent intent = new Intent(getContext(), PostActivity.class);
        startActivity(intent);
    }

    private void logout() {
        mAuthProvider.logout();
        Intent intent = new Intent(getContext(), MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    @Override
    public void onSearchStateChanged(boolean enabled) {
        getAllPost();
    }

    @Override
    public void onSearchConfirmed(CharSequence text) {

        searchByTitle(text.toString().toLowerCase());
    }

    @Override
    public void onButtonClicked(int buttonCode) {

    }
}