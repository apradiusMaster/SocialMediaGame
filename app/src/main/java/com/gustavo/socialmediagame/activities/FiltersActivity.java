package com.gustavo.socialmediagame.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Toast;
import android.widget.Toolbar;

import com.gustavo.socialmediagame.R;

public class FiltersActivity extends AppCompatActivity {

    String mExtraCategory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filters);
        mExtraCategory = getIntent().getStringExtra("category");
        Toast.makeText(FiltersActivity.this, "la categoria que seleccionaste es " + mExtraCategory, Toast.LENGTH_SHORT).show();
    }
}