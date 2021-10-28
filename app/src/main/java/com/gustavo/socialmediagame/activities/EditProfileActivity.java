package com.gustavo.socialmediagame.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.storage.UploadTask;
import com.gustavo.socialmediagame.R;
import com.gustavo.socialmediagame.models.User;
import com.gustavo.socialmediagame.providers.AuthProvider;
import com.gustavo.socialmediagame.providers.ImageProvider;
import com.gustavo.socialmediagame.providers.UsersProvider;
import com.gustavo.socialmediagame.utils.FileUtil;
import com.gustavo.socialmediagame.utils.ViewebMessageHelper;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;
import dmax.dialog.SpotsDialog;

public class EditProfileActivity extends AppCompatActivity {


    CircleImageView mCircleImageView;
    CircleImageView mCircleImageProfile;
    ImageView mImageViewEditProfile;
    TextInputEditText mTextInputEditUserName;
    TextInputEditText mTextInputEditPhone;
    Button mButtonEdit;

    File mImageFile;
    File mImageFile2;

    android.app.AlertDialog mDialog;
    AlertDialog.Builder mBuilderSelector;
    CharSequence options[];


    private final int GALLERY_REQUEST_CODE = 1;
    private final int GALLEY_REQUEST_CODE_2 = 2;
    private final int PHOTO_REQUEST_CODE = 3;
    private final int PHOTO_REQUEST_CODE_2 = 4;

    //FOTO 1

    String mAbsolutePhotoPath;
    String mPhotoPath;
    File mPhotoFile;

    //FOTO 2

    String mAbsolutePhotoPatch2;
    String mPhotoPath2;
    File mPhotoFile2;

    String mUserName = "";
    String mPhone = "";

    ImageProvider mImageProvider;
    UsersProvider mUserProvider;
    AuthProvider mAuthProvider;

    String mImageProfile ="";
    String mImageCover = "";




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        mCircleImageView = findViewById(R.id.circleImageBack);
        mCircleImageProfile  = findViewById(R.id.circleImageProfile);
        mImageViewEditProfile = findViewById(R.id.imageViewEditProfile);
        mTextInputEditUserName = findViewById(R.id.textInputUserEditUserName);
        mTextInputEditPhone = findViewById(R.id.textInputEditPhone);
        mButtonEdit = findViewById(R.id.btnEditProfile);
        mBuilderSelector = new  AlertDialog.Builder(this);
        mImageProvider = new ImageProvider();
        mUserProvider = new UsersProvider();
        mAuthProvider = new AuthProvider();
        mDialog = new SpotsDialog.Builder()
                .setContext(this)
                .setMessage("Espere un momento..")
                .setCancelable(false).build();
        mBuilderSelector.setTitle("Seleccione alguna opcion");
        options = new CharSequence[]{"imagen de galeria", "Tomar foto"};
        
        mButtonEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clickEditProfile();
            }
        });


        mCircleImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        mCircleImageProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectOptionImage( 1);
            }
        });
        mImageViewEditProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectOptionImage(2);
            }
        });

        getUser();
        
    }

    @Override
    protected void onStart() {
        super.onStart();
        ViewebMessageHelper.updateOnline(true, EditProfileActivity.this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        ViewebMessageHelper.updateOnline(false, EditProfileActivity.this);
    }

    private void getUser(){
        mUserProvider.getUser(mAuthProvider.getUid()).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()){
                    mUserName = documentSnapshot.getString("username");
                    mPhone = documentSnapshot.getString("phone");
                    mTextInputEditUserName.setText(mUserName);
                    mTextInputEditPhone.setText(mPhone);
                    if (documentSnapshot.contains("image_profile")){
                        mImageProfile = documentSnapshot.getString("image_profile");
                        if (mImageProfile != null){
                            if (!mImageProfile .isEmpty()){
                                Picasso.with(EditProfileActivity.this).load(mImageProfile).into(mCircleImageProfile);
                            }
                        }

                    }
                    if (documentSnapshot.contains("image_cover")){
                        mImageCover = documentSnapshot.getString("image_cover");
                        if (mImageCover != null){
                            if (!mImageCover.isEmpty()){
                                Picasso.with(EditProfileActivity.this).load(mImageCover).into(mImageViewEditProfile);
                            }
                        }

                    }

                }
            }
        });
    }

    private void clickEditProfile() {
       // Toast.makeText(this, "Prueba",Toast.LENGTH_SHORT).show();
        mUserName = mTextInputEditUserName.getText().toString();
        mPhone = mTextInputEditPhone.getText().toString();

        if (!mUserName.isEmpty() && !mPhone.isEmpty()){
            // SE HA SELECCIONADO AMBAS IMAGENES
            if (mImageFile != null && mImageFile2 != null){
                saveImageCoverAndProfile(mImageFile, mImageFile2);

            } else if (mPhotoFile != null && mPhotoFile2 != null){
                saveImageCoverAndProfile(mPhotoFile,mPhotoFile2);
            } else if (mImageFile != null && mPhotoFile2 != null){
                saveImageCoverAndProfile(mImageFile, mPhotoFile2);
            } else if (mPhotoFile != null && mImageFile2 != null){
                saveImageCoverAndProfile(mPhotoFile, mImageFile2);
            } else if(mPhotoFile != null){
                  saveImage(mPhotoFile, true);
                 }
                 else if (mPhotoFile2 != null){
                     saveImage( mPhotoFile2, false);
            } else if (mImageFile != null){
                     saveImage(mImageFile, true);
            } else if (mImageFile2 != null){
                     saveImage(mImageFile2, false);
            } else
                {
                    User user = new User();
                    user.setUsername(mUserName);
                    user.setPhone(mPhone);
                    user.setId(mAuthProvider.getUid());
                    updateInfo(user);
                Toast.makeText(this, "debe seleccionar  una imagen",Toast.LENGTH_SHORT).show();
            }

        } else {
            Toast.makeText(this, "complete todos los campos para publicar", Toast.LENGTH_SHORT).show();
        }
    }


    private void saveImageCoverAndProfile(File imageFile, File imageFile2) {
        mDialog.show();
        mImageProvider.save(EditProfileActivity.this, imageFile).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete( Task<UploadTask.TaskSnapshot> task) {
                if(task.isSuccessful()){
                    mImageProvider.getStorage().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            String url = uri.toString();
                            mImageProvider.save(EditProfileActivity.this, imageFile2).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onComplete( Task<UploadTask.TaskSnapshot> taskImage) {
                                    mDialog.dismiss();
                                    if (taskImage.isSuccessful()){
                                        mImageProvider.getStorage().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                            @Override
                                            public void onSuccess(Uri uri2) {
                                                String url2 = uri2.toString();

                                                User user = new User();
                                                user.setUsername(mUserName);
                                                user.setPhone(mPhone);
                                                user.setImageProfile(url);
                                                user.setImageCover(url2);
                                                user.setTimestamp(new Date().getTime());
                                                user.setId(mAuthProvider.getUid());

                                                updateInfo(user);
                                            }
                                        });
                                    } else {
                                        mDialog.dismiss();
                                        Toast.makeText(EditProfileActivity.this , "no se pudo guardar la imagen  numero 2", Toast.LENGTH_LONG).show();
                                    }
                                }
                            });

                        }
                    });
                    // Toast.makeText(PostActivity.this, "La imagen se almaceno correctamente",Toast.LENGTH_LONG).show();

                } else {
                    mDialog.dismiss();
                    Toast.makeText(EditProfileActivity.this, "Hubo un error al almacenar la imagen", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void saveImage(File imagefile , Boolean isProfileImage){
        mDialog.show();
        mImageProvider.save(EditProfileActivity.this, imagefile).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete( Task<UploadTask.TaskSnapshot> task) {
                if(task.isSuccessful()){
                    mImageProvider.getStorage().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            String url = uri.toString();

                            User user = new User();
                            user.setUsername(mUserName);
                            user.setPhone(mPhone);

                            if (isProfileImage){
                                user.setImageProfile(url);
                                user.setImageCover(mImageCover);
                            } else {
                                user.setImageCover(url);
                                user.setImageProfile(mImageProfile);
                            }
                            user.setTimestamp(new Date().getTime());
                            user.setId(mAuthProvider.getUid());
                             updateInfo(user);

                        }
                    });
                    // Toast.makeText(PostActivity.this, "La imagen se almaceno correctamente",Toast.LENGTH_LONG).show();

                } else {
                    mDialog.dismiss();
                    Toast.makeText(EditProfileActivity.this, "Hubo un error al almacenar la imagen", Toast.LENGTH_LONG).show();
                }
            }
        });


    }

    private void updateInfo(User user){
        if(mDialog.isShowing()){
            mDialog.show();
        }
        mUserProvider.update(user).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull @NotNull Task<Void> task) {
                mDialog.dismiss();
                if (task.isSuccessful()){
                    Toast.makeText(EditProfileActivity.this, "Se ha actualizado la informacion ", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(EditProfileActivity.this, "no se ha podido actulizar la informacion", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void selectOptionImage(int numberImage) {

        mBuilderSelector.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (i == 0){
                    if (numberImage == 1){
                        openGallery(GALLERY_REQUEST_CODE);
                    } else if(numberImage == 2){
                        openGallery(GALLEY_REQUEST_CODE_2);

                    }

                } else if( i == 1 ) {
                    if (numberImage == 1){
                        takePhoto(PHOTO_REQUEST_CODE);
                    } else if (numberImage == 2 ){
                        takePhoto(PHOTO_REQUEST_CODE_2);
                    }

                }
            }
        });
        mBuilderSelector.show();

    }

    private void takePhoto(int requestCode) {
        // Toast.makeText(this, "selecionaste la option tomar foto", Toast.LENGTH_LONG).show();

        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager() )!=null){
            File photoFile = null;

            try {
                photoFile = createPhotoFile(requestCode);
            } catch(Exception e){
                Toast.makeText(this, "Hubo un error en el archivo" +e.getMessage(), Toast.LENGTH_LONG).show();

            }

            if (photoFile != null){
                Uri photoUri = FileProvider.getUriForFile(EditProfileActivity.this, "com.gustavo.socialmediagame", photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                startActivityForResult(takePictureIntent, requestCode);

            }

        }
    }

    private File createPhotoFile(int requestCode) throws IOException {
        File  storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File  photoFile = File.createTempFile( new Date() + "_photo", ".jpg", storageDir);

        if (requestCode == PHOTO_REQUEST_CODE){
            mPhotoPath = "file:" +photoFile.getAbsolutePath();
            mAbsolutePhotoPath = photoFile.getAbsolutePath();
        } else {
            if (requestCode == PHOTO_REQUEST_CODE_2) {
                mPhotoPath2 = "file:" + photoFile.getAbsolutePath();
                mAbsolutePhotoPatch2 = photoFile.getAbsolutePath();
            }
        }


        return  photoFile;
    }

    private void openGallery(int requestCode) {
        Intent galleyIntent = new Intent(Intent.ACTION_GET_CONTENT);
        galleyIntent.setType("image/*");
        startActivityForResult(galleyIntent, requestCode);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        /***
         *  SELECCION DE IMAGEN DESDE LA GALERIA
         */

        if (requestCode == GALLERY_REQUEST_CODE &&  resultCode == RESULT_OK){
            try {
                mPhotoFile = null;
                mImageFile = FileUtil.from(this, data.getData());
                mCircleImageProfile.setImageBitmap(BitmapFactory.decodeFile(mImageFile.getAbsolutePath()));

            } catch (Exception e){
                Log.d("ERROR" ,"Se produjo un error" + e.getMessage());
                Toast.makeText(this, "Se produjo un error", Toast.LENGTH_LONG).show();
            }
        } else {
            if (requestCode == GALLEY_REQUEST_CODE_2 && resultCode == RESULT_OK) {
                try {
                    mPhotoFile2 = null;
                    mImageFile2 = FileUtil.from(this, data.getData());
                    mImageViewEditProfile.setImageBitmap(BitmapFactory.decodeFile(mImageFile2.getAbsolutePath()));

                } catch (Exception e) {
                    Log.d("ERROR", "Se produjo un error" + e.getMessage());
                    Toast.makeText(this, "Se produjo un error", Toast.LENGTH_LONG).show();
                }
            }
        }


        /****
         *  SELECCION DE FOTOGRAFIA
         */
        if (requestCode == PHOTO_REQUEST_CODE && resultCode == RESULT_OK){
            mImageFile = null;
            mPhotoFile = new File(mAbsolutePhotoPath);
            Picasso.with(EditProfileActivity.this).load(mPhotoPath).into(mCircleImageProfile);

        } else {
            /****
             *  SELECCION DE FOTOGRAFIA 2
             */
            if (requestCode == PHOTO_REQUEST_CODE_2 && resultCode == RESULT_OK) {
                mImageFile2 = null;
                mPhotoFile2 = new File(mAbsolutePhotoPatch2);
                Picasso.with(EditProfileActivity.this).load(mPhotoPath2).into(mImageViewEditProfile);

            }
        }
    }
}