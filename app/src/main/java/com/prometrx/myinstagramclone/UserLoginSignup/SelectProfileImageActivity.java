package com.prometrx.myinstagramclone.UserLoginSignup;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import com.github.siyamed.shapeimageview.OctogonImageView;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.prometrx.myinstagramclone.MainActivity;
import com.prometrx.myinstagramclone.R;

import java.io.IOException;
import java.util.UUID;

public class SelectProfileImageActivity extends AppCompatActivity {

    private Button saveProfileImageButton;
    private OctogonImageView octogonImageView;
    private FirebaseFirestore firestore;
    private FirebaseStorage firebaseStorage;
    private StorageReference storageReference;
    private FirebaseUser firebaseUser;
    private ActivityResultLauncher<String> permissionLauncher;
    private ActivityResultLauncher<Intent> galleryLauncher;
    private Uri imageData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_profile_image);
        init();
        activityLauncher();
        selectImageViewClicked();
        saveButton();
    }

    private void init() {
        saveProfileImageButton = findViewById(R.id.selectedImageSaveButton);
        octogonImageView = findViewById(R.id.selectImageView);
        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference();
        firestore = FirebaseFirestore.getInstance();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
    }

    private void selectImageViewClicked() {
        octogonImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(ContextCompat.checkSelfPermission(SelectProfileImageActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    //Request Permission
                    if(ActivityCompat.shouldShowRequestPermissionRationale(SelectProfileImageActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                        //Snackbar

                        Snackbar.make(view, "Permission Needed for Gallery", Snackbar.LENGTH_INDEFINITE).setAction("Give Permission", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                //Request Permission
                                permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE);
                            }
                        }).show();


                    }else{
                        //Request Permission
                        permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE);
                    }
                }
                else{
                    //Gallery
                    Intent intentforGallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    galleryLauncher.launch(intentforGallery);
                }
            }
        });
    }

    private void saveButton() {
        saveProfileImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Storage Save
                if(imageData != null) {
                    //Storage Data

                    //UUID uuid = UUID.randomUUID();


                    final String imageRoad = "profileImages/"+firebaseUser.getUid()+".jpg";

                    storageReference.child(imageRoad).putFile(imageData).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            //Download Url

                            StorageReference newReference = FirebaseStorage.getInstance().getReference(imageRoad);

                            newReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {

                                    firestore.collection("UsersData").document(firebaseUser.getUid()).update("imageUrl", uri.toString()).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {
                                            Intent intentProfile = new Intent(SelectProfileImageActivity.this, MainActivity.class);
                                            startActivity(intentProfile);
                                            finish();
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(SelectProfileImageActivity.this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    });

                                }

                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(SelectProfileImageActivity.this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(SelectProfileImageActivity.this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });

                }else{
                    Intent intentProfile = new Intent(SelectProfileImageActivity.this, MainActivity.class);
                    startActivity(intentProfile);
                    finish();
                }

            }
        });
    }

    private void activityLauncher() {

        galleryLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                if(result.getResultCode() == Activity.RESULT_OK) {

                    Intent intentResult = result.getData();

                    if(intentResult != null) {
                        imageData = intentResult.getData();

                        try {
                            Bitmap bitmap = MediaStore.Images.Media.getBitmap(SelectProfileImageActivity.this.getContentResolver(), imageData);
                            octogonImageView.setImageBitmap(bitmap);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                    }

                }
            }
        });

        permissionLauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission(), new ActivityResultCallback<Boolean>() {
            @Override
            public void onActivityResult(Boolean result) {
                if(result) {
                    //Intent Gallery
                    Intent intentforGallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    galleryLauncher.launch(intentforGallery);

                }else{
                    Toast.makeText(SelectProfileImageActivity.this, "Permission Denied", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

}