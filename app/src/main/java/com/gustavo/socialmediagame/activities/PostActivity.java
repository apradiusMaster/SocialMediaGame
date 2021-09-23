package com.gustavo.socialmediagame.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.storage.UploadTask;
import com.gustavo.socialmediagame.R;
import com.gustavo.socialmediagame.models.Post;
import com.gustavo.socialmediagame.providers.AuthProvider;
import com.gustavo.socialmediagame.providers.ImageProvider;
import com.gustavo.socialmediagame.providers.PostProvider;
import com.gustavo.socialmediagame.utils.FileUtil;



import java.io.File;

import de.hdodenhof.circleimageview.CircleImageView;
import dmax.dialog.SpotsDialog;

public class PostActivity extends AppCompatActivity {


    ImageView mImageviewPost1;
    ImageView mImageViewPost2;

    File mImageFile;
    File mImageFile2;
    Button mButtonPost;
    CircleImageView mCircleImageBack;
    ImageProvider mImageProvider;
    PostProvider mPostProvider;
    AuthProvider mAuthProvider;
    TextInputEditText mTextInputTitle;
    TextInputEditText mTextInputDescription;
    ImageView mImageViewPc;
    ImageView mImageViewPs4;
    ImageView mImageViewXbox;
    ImageView mImageViewNintendo;
    TextView mTextViewCategory;

    String mCategory = "";
    String mTitle = "";
    String mDescription = "";
    AlertDialog mDialog;
    AlertDialog.Builder mBuilderSelector;
    CharSequence options[];

    private final int GALLERY_REQUEST_CODE = 1;
    private final int GALLEY_REQUEST_CODE_2 = 2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);
        mImageviewPost1 = findViewById(R.id.imageViewPost1);
        mImageViewPost2 = findViewById(R.id.imageViewPost2);
        mButtonPost = findViewById(R.id.btnPost);
        mImageProvider = new ImageProvider();
        mPostProvider = new PostProvider();
        mAuthProvider = new AuthProvider();
        mTextInputTitle = findViewById(R.id.textInputVideoGame);
        mTextInputDescription = findViewById(R.id.textInputDescription);
        mImageViewPc = findViewById(R.id.imageViewPc);
        mImageViewPs4 = findViewById(R.id.imageViewPs4);
        mImageViewXbox = findViewById(R.id.imageViewXbox);
        mImageViewNintendo = findViewById(R.id.imageViewNintendo);
        mTextViewCategory = findViewById(R.id.textViewCategory);
        mDialog = new SpotsDialog.Builder()
                .setContext(this)
                .setMessage("Espere un momento..")
                .setCancelable(false).build();
        mBuilderSelector = new AlertDialog.Builder(this);
        mBuilderSelector.setTitle("Seleccione alguna opcion");
        options = new CharSequence[]{"imagen de galeria", "Tomar foto"};



        mCircleImageBack = findViewById(R.id.cicleImageBack);
        mCircleImageBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        mImageViewPc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCategory = "pc";
                mTextViewCategory.setText(mCategory);
            }
        });

        mImageViewPs4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCategory = "PS4";
                mTextViewCategory.setText(mCategory);
            }
        });

        mImageViewXbox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCategory = "XBOX";
                mTextViewCategory.setText(mCategory);
            }
        });

        mImageViewNintendo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCategory = "NINTENDO";
                mTextViewCategory.setText(mCategory);
            }
        });

        mButtonPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                clickPost();
            }
        });

        mImageviewPost1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectOptionImage(GALLERY_REQUEST_CODE);
                //openGallery(GALLERY_REQUEST_CODE);
            }
        });
        mImageViewPost2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectOptionImage(GALLEY_REQUEST_CODE_2);
                //openGallery(GALLEY_REQUEST_CODE_2);
            }
        });
    }

    private void selectOptionImage(int requestCode) {
        AlertDialog alert = mBuilderSelector.create();
        alert.show();
        mBuilderSelector.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (i == 0){
                    openGallery(requestCode);
                } else if( i == 1 ) {
                    takePhoto();
                }
            }
        });

    }

    private void takePhoto() {
        Toast.makeText(this, "selecionaste la option tomar foto", Toast.LENGTH_LONG).show();
    }

    private void clickPost() {
        mTitle = mTextInputTitle.getText().toString();
        mDescription = mTextInputDescription.getText().toString();

        if (!mTitle.isEmpty() && !mDescription.isEmpty()){
            if (mImageFile != null){
                saveImage();

            } else {
                Toast.makeText(this, "debe seleccionar  una imagen",Toast.LENGTH_SHORT).show();
             }

        } else {
            Toast.makeText(this, "complete todos los campos para publicar", Toast.LENGTH_SHORT).show();
         }
    }

    private void saveImage() {
                mDialog.show();
                mImageProvider.save(PostActivity.this, mImageFile).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete( Task<UploadTask.TaskSnapshot> task) {
                        if(task.isSuccessful()){
                            mImageProvider.getStorage().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    String url = uri.toString();
                                    mImageProvider.save(PostActivity.this, mImageFile2).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                        @Override
                                        public void onComplete( Task<UploadTask.TaskSnapshot> taskImage) {
                                            mDialog.dismiss();
                                            if (taskImage.isSuccessful()){
                                                mImageProvider.getStorage().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                    @Override
                                                    public void onSuccess(Uri uri2) {
                                                        String url2 = uri2.toString();

                                                        Post post = new Post();
                                                        post.setImage1(url);
                                                        post.setImage2(url2);
                                                        post.setTitle(mTitle);
                                                        post.setDescription(mDescription);
                                                        post.setIdUser(mAuthProvider.getUid());
                                                        mPostProvider.save(post).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull  Task<Void> task) {
                                                                if (task.isSuccessful()){
                                                                    clearForm();
                                                                    Toast.makeText(PostActivity.this, "La informacion se almaceno correctamente",Toast.LENGTH_SHORT).show();
                                                                } else {
                                                                    Toast.makeText(PostActivity.this, "No se pudo almacenar la informacion",Toast.LENGTH_SHORT).show();
                                                                }
                                                            }
                                                        });

                                                    }
                                                });
                                            } else {
                                                mDialog.dismiss();
                                                    Toast.makeText(PostActivity.this , "no se pudo guardar la imagen  numero 2", Toast.LENGTH_LONG).show();
                                              }
                                        }
                                    });

                                }
                            });
                           // Toast.makeText(PostActivity.this, "La imagen se almaceno correctamente",Toast.LENGTH_LONG).show();

                        } else {
                            mDialog.dismiss();
                            Toast.makeText(PostActivity.this, "Hubo un error al almacenar la imagen", Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    private void clearForm() {
        mTextInputTitle.setText("");
        mTextInputDescription.setText("");
        mTextViewCategory.setText("");
        mImageviewPost1.setImageResource(R.drawable.upload_image);
        mImageViewPost2.setImageResource(R.drawable.upload_image);
        mTitle = "";
        mDescription = "";
        mCategory = "";
        mImageFile = null;
        mImageFile2 = null;
    }

    private void openGallery(int requestCode) {
        Intent galleyIntent = new Intent(Intent.ACTION_GET_CONTENT);
        galleyIntent.setType("image/*");
        startActivityForResult(galleyIntent, requestCode);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable  Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GALLERY_REQUEST_CODE &&  resultCode == RESULT_OK){
            try {
             mImageFile = FileUtil.from(this, data.getData());
             mImageviewPost1.setImageBitmap(BitmapFactory.decodeFile(mImageFile.getAbsolutePath()));

            } catch (Exception e){
                Log.d("ERROR" ,"Se produjo un error" + e.getMessage());
                Toast.makeText(this, "Se produjo un error", Toast.LENGTH_LONG).show();
            }
        }
        if (requestCode == GALLEY_REQUEST_CODE_2 &&  resultCode == RESULT_OK){
            try {
                mImageFile2 = FileUtil.from(this, data.getData());
                mImageViewPost2.setImageBitmap(BitmapFactory.decodeFile(mImageFile2.getAbsolutePath()));

            } catch (Exception e){
                Log.d("ERROR" ,"Se produjo un error" + e.getMessage());
                Toast.makeText(this, "Se produjo un error", Toast.LENGTH_LONG).show();
            }
        }
    }
}