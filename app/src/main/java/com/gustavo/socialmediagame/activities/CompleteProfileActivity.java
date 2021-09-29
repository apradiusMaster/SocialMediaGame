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
import com.google.android.material.textfield.TextInputEditText;
import com.gustavo.socialmediagame.R;
import com.gustavo.socialmediagame.models.User;
import com.gustavo.socialmediagame.providers.AuthProvider;
import com.gustavo.socialmediagame.providers.UsersProvider;

import java.util.Date;

import dmax.dialog.SpotsDialog;

public class CompleteProfileActivity extends AppCompatActivity {

    TextInputEditText mTextInputUserName;
    TextInputEditText mTextInputPhone;
    Button mButtonRegister;
     AuthProvider mAuthProvider;
     UsersProvider mUsersProvider;
     AlertDialog mDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_complete_profile);
        mTextInputUserName = findViewById(R.id.textInputUserName);
        mTextInputPhone = findViewById(R.id.textInputPhone);
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
        String username= mTextInputUserName.getText().toString();
        String phone = mTextInputPhone.getText().toString();
        if (!username.isEmpty() && !phone.isEmpty()){
                  updateUser(username, phone);
        } else{
            Toast.makeText(CompleteProfileActivity.this, "Para continuar,inserta todos los campos", Toast.LENGTH_SHORT).show();
          }

    }

    private void updateUser(String username, String phone) {
        String id= mAuthProvider.getUid();
        User user = new User();
        user.setId(id);
        user.setUsername(username);
        user.setPhone(phone);
        user.setTimestamp(new Date().getTime());
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