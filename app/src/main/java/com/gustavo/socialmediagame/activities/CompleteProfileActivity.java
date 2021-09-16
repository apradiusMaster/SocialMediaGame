package com.gustavo.socialmediagame.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.gustavo.socialmediagame.R;

import java.util.HashMap;
import java.util.Map;

public class CompleteProfileActivity extends AppCompatActivity {

    TextView mTextViewUserName;
    Button mButtonRegister;
     FirebaseAuth mAuth;
     FirebaseFirestore mFirebaseFirestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_complete_profile);
        mTextViewUserName = findViewById(R.id.textInputUserName);
         mButtonRegister = findViewById(R.id.btnRegister);
         mAuth = FirebaseAuth.getInstance();
         mFirebaseFirestore = FirebaseFirestore.getInstance();
         mButtonRegister.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View view) {
                 register();
             }
         });
    }

    private void register() {
        String username= mTextViewUserName.getText().toString();
        if (!username.isEmpty()){
                  updateUser(username);
        } else{
            Toast.makeText(CompleteProfileActivity.this, "Para continuar,inserta todos los campos", Toast.LENGTH_SHORT).show();
          }

    }

    private void updateUser(String username) {
        String id= mAuth.getCurrentUser().getUid();
        Map<String, Object> map = new HashMap<>();
        map.put("username", username);
        mFirebaseFirestore.collection("Users").document(id).update(map).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull  Task<Void> task) {
                if (task.isSuccessful()){
                    Intent intent = new Intent(CompleteProfileActivity.this, HomeActivity.class);
                    startActivity(intent);
                } else{
                     Toast.makeText(CompleteProfileActivity.this, "No se pudo almacenar la informacion del usuario a la base de datos",Toast.LENGTH_SHORT).show();

                   }
            }
        });
    }
}