 package com.gustavo.socialmediagame.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.gustavo.socialmediagame.R;
import com.gustavo.socialmediagame.models.User;
import com.gustavo.socialmediagame.providers.AuthProvider;
import com.gustavo.socialmediagame.providers.UsersProvider;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.hdodenhof.circleimageview.CircleImageView;
import dmax.dialog.SpotsDialog;

 public class RegisterActivity extends AppCompatActivity {

    TextInputEditText mTextInputUserName;
    TextInputEditText mTextInputEmail;
    TextInputEditText mTextInputPassword;
    TextInputEditText mTextInputConfirmPassword;
    Button mButtonRegister;
    AuthProvider mAuthProvider;
    UsersProvider mUsersProvider;
    AlertDialog mDialog;

    CircleImageView mCircleImageViewBack;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mCircleImageViewBack = findViewById(R.id.cicleImageBack);
        mTextInputUserName = findViewById(R.id.textInputUserName);
        mTextInputEmail = findViewById(R.id.textInputEmail);
        mTextInputPassword = findViewById(R.id.textInputPassword);
        mTextInputConfirmPassword = findViewById(R.id.textInputCofirmPassword);
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
        mCircleImageViewBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
    private  void register(){
        String userName = mTextInputUserName.getText().toString();
        String email = mTextInputEmail.getText().toString();
        String password = mTextInputPassword.getText().toString();
        String confirmPassword = mTextInputConfirmPassword.getText().toString();
        if(!userName.isEmpty() && !email.isEmpty() && !password.isEmpty() && !confirmPassword.isEmpty()){
            if (isEmailValid(email)){

                if (password.equals(confirmPassword)){
                    if (password.length() >=6){
                        createUser(userName,email,password);
                    } else {
                        Toast.makeText(this, "la contrase.ña debe tener al menos 6 caracteres", Toast.LENGTH_SHORT).show();
                    }
                } else{
                   Toast.makeText(this, "Las contraseña no coinciden", Toast.LENGTH_SHORT).show();
                  }
                Toast.makeText(this, "Has insertado todos los campos y el email es valido", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, "Has insertado todos los campos pero el correo no es valido", Toast.LENGTH_LONG).show();
            }

        }
         else {
             Toast.makeText(this, "Para continuar inserta todos los campos", Toast.LENGTH_SHORT).show();
        }

    }

    public boolean isEmailValid(String email) {
        String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }
    private void createUser( String username ,final String email, final String password){
        mDialog.show();
        mAuthProvider.register(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull  Task<AuthResult> task) {

                if (task.isSuccessful()){
                    String id = mAuthProvider.getUid();
                    User user = new User();
                    user.setId(id);
                    user.setEmail(email);
                    user.setUsername(username);
                    mUsersProvider.create(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull  Task<Void> task) {
                            mDialog.dismiss();
                            if (task.isSuccessful()){
                                Intent intent = new Intent(RegisterActivity.this, HomeActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                            } else {
                                 Toast.makeText(RegisterActivity.this, "No se pudo almacenar el usuario ala base de datos", Toast.LENGTH_SHORT).show();
                               }
                        }
                    });
                } else {
                    mDialog.dismiss();
                    Toast.makeText(RegisterActivity.this, "No se pudo registrar el usuario", Toast.LENGTH_SHORT).show();
                  }
            }
        });
    }

}