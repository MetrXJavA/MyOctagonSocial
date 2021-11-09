package com.prometrx.myinstagramclone.Other;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.github.siyamed.shapeimageview.OctogonImageView;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.prometrx.myinstagramclone.Adapter.CommentAdapter;
import com.prometrx.myinstagramclone.Model.Comments;
import com.prometrx.myinstagramclone.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CommentActivity extends AppCompatActivity {

    private OctogonImageView profileImageView, postImageView;
    private TextView usernameTextView, detailTextView, likeNumberTextView, commentNumberTextView;
    private EditText enterCommentEditText;
    private MaterialButton sendCommentMaterialButton;
    private RecyclerView recyclerView;
    private String userId, userProfileImageUrl, userPostImageUrl, userDetail, username, docName;
    private int likeNumber;
    private FirebaseFirestore firestore;
    private FirebaseUser firebaseUser;
    private List<Comments> commentsList;
    private CommentAdapter commentAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);
        init();
        clickButton();
        readData();
    }

    private void init(){
        profileImageView = findViewById(R.id.commentActivityProfileImageView);
        postImageView = findViewById(R.id.commentActivityPostImageView);
        likeNumberTextView = findViewById(R.id.commentActivityLikeNumberTextView);
        usernameTextView = findViewById(R.id.commentActivityUsernameTextView);
        detailTextView = findViewById(R.id.commentActivityDetailTextView);
        commentNumberTextView = findViewById(R.id.commentActivityCommentNumberTextView);
        enterCommentEditText = findViewById(R.id.commentActivityEditText);
        sendCommentMaterialButton = findViewById(R.id.sendCommentMaterialButton);
        recyclerView = findViewById(R.id.commentActivityRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(CommentActivity.this));
        commentsList = new ArrayList<>();
        firestore = FirebaseFirestore.getInstance();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        Intent intent = getIntent();
        userId = intent.getStringExtra("userId");
        username = intent.getStringExtra("Username");
        userProfileImageUrl = intent.getStringExtra("UserProfileImageUrl");
        userDetail = intent.getStringExtra("UserDetail");
        userPostImageUrl = intent.getStringExtra("UserPostImageUrl");
        docName = intent.getStringExtra("docName");
        likeNumber = intent.getIntExtra("UserLikeNumber",0);
        usernameTextView.setText(username);
        detailTextView.setText(userDetail);
        Glide.with(CommentActivity.this).load(userProfileImageUrl).into(profileImageView);
        Glide.with(CommentActivity.this).load(userPostImageUrl).into(postImageView);
        likeNumberTextView.setText(likeNumber +"");

    }

    private void clickButton() {
        sendCommentMaterialButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //if
                addData();

            }
        });
    }

    private void addData() {

        String comment = enterCommentEditText.getText().toString();
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("comment", comment);
        hashMap.put("date", FieldValue.serverTimestamp());
        hashMap.put("userId", firebaseUser.getUid());

        firestore.collection("Posts").document(docName).collection("Comments").add(hashMap).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {

                Toast.makeText(CommentActivity.this, "SuccessfuL", Toast.LENGTH_SHORT).show();

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(CommentActivity.this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void readData() {

        firestore.collection("Posts").document(docName).collection("Comments").orderBy("date", Query.Direction.ASCENDING).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                commentsList.clear();
                if(value != null) {


                    for (DocumentSnapshot dc : value.getDocuments()) {

                        Comments comments = new Comments(dc.get("comment").toString(), dc.get("userId").toString(), "", "");

                        firestore.collection("UsersData").document(dc.get("userId").toString()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                            @Override
                            public void onSuccess(DocumentSnapshot documentSnapshot) {

                                comments.setProfileImageUrl(documentSnapshot.get("imageUrl").toString());
                                comments.setUsername(documentSnapshot.get("username").toString());
                                commentsList.add(comments);
                                commentAdapter = new CommentAdapter(commentsList, CommentActivity.this);
                                recyclerView.setAdapter(commentAdapter);
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(CommentActivity.this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });




                    }


                }

            }
        });

    }



}