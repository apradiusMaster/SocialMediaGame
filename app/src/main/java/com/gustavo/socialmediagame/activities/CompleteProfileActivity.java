package com.gustavo.socialmediagame.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
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
import com.gustavo.socialmediagame.models.User;
import com.gustavo.socialmediagame.providers.AuthProvider;
import com.gustavo.socialmediagame.providers.UsersProvider;

import java.util.HashMap;
import java.util.Map;

import dmax.dialog.SpotsDialog;

public class CompleteProfileActivity extends AppCompatActivity {

    TextView mTextViewUserName;
    Button mButtonRegister;
     AuthProvider mAuthProvider;
     UsersProvider mUsersProvider;
     AlertDialog mDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_complete_profile);
        mTextViewUserName = findViewById(R.id.textInputUserName);
         mButtonRegister = findViewById(R.id.btnRegister);
         mAuthProvider = new AuthProvider();
         mUsersProvider = new UsersProvider();
        mDialog = new SpotsDialog.Builder()
                .setContext(this)
                .setMessage("Espere un momento..")
                .setCancelable(false).build();

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
        String id= mAuthProvider.getUid();
        User user = new User();
        user.setId(id);
        user.setUsername(id);
        mDialog.show();
        mUsersProvider.update(user).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull  Task<Void> task) {
                mDialog.dismiss();
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