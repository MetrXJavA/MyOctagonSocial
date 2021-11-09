package com.prometrx.myinstagramclone.Chats;

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
import com.google.firebase.Timestamp;
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
import com.prometrx.myinstagramclone.Adapter.ChatMessagesAdapter;
import com.prometrx.myinstagramclone.Model.Chats;
import com.prometrx.myinstagramclone.Other.OtherUsersProfileActivity;
import com.prometrx.myinstagramclone.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ChatActivity extends AppCompatActivity {

    //Init
    private OctogonImageView profileImageView;
    private String userId, profileImageUrl, username;
    private MaterialButton addPhotoMaterialButton, sendMessageMaterialButton;
    private EditText messageEditText;
    private TextView usernameTextView;
    private RecyclerView recyclerViewMessages;

    //Firebase
    private FirebaseUser firebaseUser;
    private FirebaseFirestore firestore;

    //Chats
    private List<Chats> chatsList;
    private ChatMessagesAdapter chatMessagesAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        init();
        sendMessageButtonClick();
        readMessagesData();

    }

    private void init() {
        Intent intent = getIntent();
        userId = intent.getStringExtra("userId");
        username = intent.getStringExtra("username");
        profileImageUrl = intent.getStringExtra("userProfileImageUrl");

        profileImageView = findViewById(R.id.chatActivityProfileImageView);
        addPhotoMaterialButton = findViewById(R.id.addPhotoMaterialButton);
        sendMessageMaterialButton = findViewById(R.id.sendMessageMaterialButton);
        messageEditText = findViewById(R.id.chatActivityEditText);
        usernameTextView = findViewById(R.id.chatActivityProfileTextView);
        recyclerViewMessages = findViewById(R.id.chatActivityRecyclerView);
        recyclerViewMessages.setLayoutManager(new LinearLayoutManager(ChatActivity.this));
        chatsList = new ArrayList<>();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        firestore = FirebaseFirestore.getInstance();

        Glide.with(ChatActivity.this).load(profileImageUrl).into(profileImageView);
        usernameTextView.setText(username);
    }

    private void sendMessageButtonClick() {

        sendMessageMaterialButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //if
                if (messageEditText.getText().equals("")) {
                    Toast.makeText(ChatActivity.this, "Please Fill MessageBox", Toast.LENGTH_SHORT).show();
                } else {
                    sendMessageData();
                }
            }
        });

    }

    private void sendMessageData() {

        HashMap<String, Object> hashMap = new HashMap<>();

        hashMap.put("message", messageEditText.getText().toString());
        hashMap.put("date", FieldValue.serverTimestamp());
        hashMap.put("senderId", firebaseUser.getUid());
        hashMap.put("receiver", userId);

        firestore.collection("Chats").add(hashMap).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {

                messageEditText.setText("");

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(ChatActivity.this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void readMessagesData() {

        firestore.collection("Chats").orderBy("date", Query.Direction.ASCENDING).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                chatsList.clear();
                if(error != null) {
                    Toast.makeText(ChatActivity.this, error.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                }
                if(value != null) {

                    for(DocumentSnapshot documentSnapshot : value.getDocuments()) {

                        if(documentSnapshot.get("senderId").equals(firebaseUser.getUid()) || documentSnapshot.get("receiver").equals(firebaseUser.getUid())) {

                            Chats chats = new Chats(documentSnapshot.get("senderId").toString(), documentSnapshot.get("receiver").toString(), documentSnapshot.get("message").toString(), profileImageUrl);
                            chatsList.add(chats);
                        }
                        chatMessagesAdapter = new ChatMessagesAdapter(chatsList, ChatActivity.this);
                        recyclerViewMessages.setAdapter(chatMessagesAdapter);
                    }



                }else{
                    Toast.makeText(ChatActivity.this, "Value null ! Error", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}