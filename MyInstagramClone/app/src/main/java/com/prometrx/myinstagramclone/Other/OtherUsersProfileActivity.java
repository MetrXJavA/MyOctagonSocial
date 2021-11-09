package com.prometrx.myinstagramclone.Other;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
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
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.prometrx.myinstagramclone.Adapter.FeedAdapter;
import com.prometrx.myinstagramclone.Chats.ChatActivity;
import com.prometrx.myinstagramclone.Model.Posts;
import com.prometrx.myinstagramclone.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class OtherUsersProfileActivity extends AppCompatActivity {
    private OctogonImageView otherUserProfileImageView;
    private MaterialButton followingMaterialButton, messageMaterialButton, unfollowingMaterialButton;
    private TextView usernameTextView, followersTextView, followingTextView;
    private RecyclerView otherProfilePostrecyclerView;
    private String userId, username, userProfileImageUrl;
    //Firebase
    private FirebaseFirestore firestore;
    private FirebaseUser firebaseUser;
    //Adapter, PostsList ...
    private FeedAdapter feedAdapter;
    private List<Posts> postsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_other_users_profile);
        init();
        otherProfilePostrecyclerView.setLayoutManager(new LinearLayoutManager(OtherUsersProfileActivity.this));
        postsList = new ArrayList<>();
        control();
        getPostData();
        followUser();
        unFollowUser();
        messageButton();
    }

    private void init() {
        otherUserProfileImageView = findViewById(R.id.otherUserProfileImageView);
        followingMaterialButton = findViewById(R.id.followingOtherProfilePostMaterialButton);
        unfollowingMaterialButton = findViewById(R.id.unFollowingOtherProfilePostMaterialButton);
        unfollowingMaterialButton.setVisibility(View.INVISIBLE);
        messageMaterialButton = findViewById(R.id.messageMaterialButton);
        usernameTextView = findViewById(R.id.otherProfileUsernameTextView);
        followersTextView = findViewById(R.id.otherUserProfileFollowersTextView);
        followingTextView = findViewById(R.id.otherUserProfileFollowingTextView);
        otherProfilePostrecyclerView = findViewById(R.id.otherProfilePostsRecyclerView);
        firestore = FirebaseFirestore.getInstance();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        Intent intent = getIntent();
        userId = intent.getStringExtra("otherUserId");
        username = intent.getStringExtra("otherUserUsername");
        userProfileImageUrl = intent.getStringExtra("otherUserProfileImageUrl");

        Glide.with(OtherUsersProfileActivity.this).load(userProfileImageUrl).into(otherUserProfileImageView);
        usernameTextView.setText(username);

    }

    private void control() {
        firestore.collection("Follow").document("Following").collection(firebaseUser.getUid()).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                for(DocumentSnapshot ds : queryDocumentSnapshots.getDocuments()) {
                    String tryId = ds.get("following").toString();
                    if(tryId.equals(userId)) {
                        followingMaterialButton.setVisibility(View.INVISIBLE);
                        unfollowingMaterialButton.setVisibility(View.VISIBLE);
                    }
                }

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(OtherUsersProfileActivity.this, e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void getPostData() {

        firestore.collection("Posts").orderBy("date", Query.Direction.DESCENDING).whereEqualTo("userId", userId).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {

                if (error != null) {
                    Toast.makeText(OtherUsersProfileActivity.this, error.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                }
                if (value != null) {
                    postsList.clear();
                    for (DocumentSnapshot dc : value.getDocuments()) {
                        String postImageUrl = dc.get("imageUrl").toString();
                        String comment = dc.get("comment").toString();
                        String docName = dc.get("docName").toString();

                        Posts posts = new Posts(userId, comment, username, userProfileImageUrl, postImageUrl,docName);

                        postsList.add(posts);
                        feedAdapter = new FeedAdapter(postsList, OtherUsersProfileActivity.this);
                        otherProfilePostrecyclerView.setAdapter(feedAdapter);

                    }
                }
            }
        });
    }

    private void followUser() {
        followingMaterialButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                HashMap<String, Object> hashMap = new HashMap<>();
                hashMap.put("following", userId);

                firestore.collection("Follow").document("Following").collection(firebaseUser.getUid()).document(userId).set(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {

                        followingMaterialButton.setVisibility(View.INVISIBLE);
                        unfollowingMaterialButton.setVisibility(View.VISIBLE);

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(OtherUsersProfileActivity.this, e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                    }
                });

                HashMap<String, Object> hashMap2 = new HashMap<>();
                hashMap2.put("follower", firebaseUser.getUid());

                firestore.collection("Follow").document("Followers").collection(userId).document(firebaseUser.getUid()).set(hashMap2).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(OtherUsersProfileActivity.this, e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                    }
                });
            }

        });

    }

    private void unFollowUser() {

        unfollowingMaterialButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                firestore.collection("Follow").document("Followers").collection(userId).document(firebaseUser.getUid()).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {

                        followingMaterialButton.setVisibility(View.VISIBLE);
                        unfollowingMaterialButton.setVisibility(View.INVISIBLE);

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(OtherUsersProfileActivity.this, e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                    }
                });

                firestore.collection("Follow").document("Following").collection(firebaseUser.getUid()).document(userId).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        followingMaterialButton.setVisibility(View.VISIBLE);
                        unfollowingMaterialButton.setVisibility(View.INVISIBLE);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(OtherUsersProfileActivity.this, e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                    }
                });

            }
        });
    }

    private void messageButton() {
        messageMaterialButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intentMessage = new Intent(OtherUsersProfileActivity.this, ChatActivity.class);
                intentMessage.putExtra("userId", userId);
                intentMessage.putExtra("username", username);
                intentMessage.putExtra("userProfileImageUrl", userProfileImageUrl);
                startActivity(intentMessage);

            }
        });
    }

}