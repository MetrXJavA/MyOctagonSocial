package com.prometrx.myinstagramclone.Other;

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
import android.widget.EditText;
import android.widget.Toast;

import com.github.siyamed.shapeimageview.OctogonImageView;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.prometrx.myinstagramclone.MainActivity;
import com.prometrx.myinstagramclone.R;

import java.io.IOException;
import java.util.HashMap;
import java.util.UUID;

public class SharePostActivity extends AppCompatActivity {

    private OctogonImageView sharePostImageView;
    private EditText sharePostCommentEditText;
    private Button sharePostButton;
    //Firebase
    private FirebaseFirestore firestore;
    private FirebaseUser firebaseUser;
    private FirebaseStorage firebaseStorage;
    private StorageReference storageReference;
    //ActivityResultLauncher
    private ActivityResultLauncher<Intent> galleryLauncher;
    private ActivityResultLauncher<String> permissionLauncher;
    private Uri imageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share_post);
        init();
        resultLauncher();
        selectImage();
        sharePostButton();

    }

    private void init() {
        sharePostButton = findViewById(R.id.sharePostButton);
        sharePostCommentEditText = findViewById(R.id.sharePostCommentEditText);
        sharePostImageView = findViewById(R.id.sharePostImageView);
        firestore = FirebaseFirestore.getInstance();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference();
    }

    private void sharePostButton() {
        sharePostButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(!sharePostCommentEditText.getText().equals("")) {
                    sharePostButton.setClickable(false);
                    addData();

                }else{
                    Toast.makeText(SharePostActivity.this, "Please write comment", Toast.LENGTH_SHORT).show();

                }

            }
        });
    }

    private void addData() {

        UUID uuid = UUID.randomUUID();

        final String randomImageName = "postsImage/"+uuid+".jpeg";

        storageReference.child(randomImageName).putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                StorageReference storageReferenceNew = FirebaseStorage.getInstance().getReference(randomImageName);

                storageReferenceNew.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {

                        UUID uuid = UUID.randomUUID();

                        String docName = uuid.toString();

                        HashMap<String,Object> hashMap = new HashMap<>();

                        hashMap.put("date", FieldValue.serverTimestamp());
                        hashMap.put("imageUrl", uri.toString());
                        hashMap.put("userId", firebaseUser.getUid());
                        hashMap.put("comment", sharePostCommentEditText.getText().toString());
                        hashMap.put("docName", docName);

                        firestore.collection("Posts").document(docName).set(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Intent intent = new Intent(SharePostActivity.this, MainActivity.class);
                                startActivity(intent);
                                finish();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(SharePostActivity.this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(SharePostActivity.this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(SharePostActivity.this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void selectImage() {
        sharePostImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(ContextCompat.checkSelfPermission(SharePostActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    //Not Granted

                    if(ActivityCompat.shouldShowRequestPermissionRationale(SharePostActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                        //Request Permission: Snackbar
                        Snackbar.make(view, "Permission Needed for Gallery", Snackbar.LENGTH_INDEFINITE).setAction("Give Permission", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE);
                            }
                        }).show();

                    }else{
                        //Request Permission: Default
                        permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE);
                    }

                }
                else{
                    //Granted intent gallery
                    Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    galleryLauncher.launch(galleryIntent);
                }
            }
        });
    }

    private void resultLauncher() {

        galleryLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                if(result.getResultCode() == Activity.RESULT_OK) {
                    Intent intent = result.getData();
                    if(intent.getData() != null) {
                        imageUri = intent.getData();

                        try {
                            Bitmap bitmap = MediaStore.Images.Media.getBitmap(SharePostActivity.this.getContentResolver(), imageUri);
                            sharePostImageView.setImageBitmap(bitmap);
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
                    Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    galleryLauncher.launch(galleryIntent);
                }
                else{
                    Toast.makeText(SharePostActivity.this, "Permission Denied!", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }


}