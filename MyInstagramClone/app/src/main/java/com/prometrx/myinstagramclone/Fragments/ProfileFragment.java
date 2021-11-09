package com.prometrx.myinstagramclone.Fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.github.siyamed.shapeimageview.OctogonImageView;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.prometrx.myinstagramclone.Adapter.FeedAdapter;
import com.prometrx.myinstagramclone.Model.Posts;
import com.prometrx.myinstagramclone.Other.SharePostActivity;
import com.prometrx.myinstagramclone.R;
import com.prometrx.myinstagramclone.UserLoginSignup.LoginActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ProfileFragment extends Fragment {

    private TextView profileUsernameTextView, profileFollowingTextView, profileFollowersTextView;
    private RecyclerView profilePostsRecyclerView;
    private OctogonImageView profileImageView;
    private MaterialButton addPostMaterialButton,settingMaterialButton;
    private List<Posts> profilePostsList;
    private FeedAdapter feedAdapter;
    //Firebase
    private FirebaseUser firebaseUser;
    private FirebaseFirestore firestore;
    private FirebaseAuth mAuth;

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        profileImageView = view.findViewById(R.id.profileImageView);
        profileUsernameTextView = view.findViewById(R.id.profileUsernameTextView);
        profileFollowingTextView = view.findViewById(R.id.profileFollowingTextView);
        profileFollowersTextView = view.findViewById(R.id.profileFollowersTextView);
        profilePostsRecyclerView = view.findViewById(R.id.profilePostsRecyclerView);
        addPostMaterialButton = view.findViewById(R.id.addPostMaterialButton);
        settingMaterialButton = view.findViewById(R.id.settingMaterialButton);
        profilePostsRecyclerView = view.findViewById(R.id.profilePostsRecyclerView);
        profilePostsRecyclerView.setLayoutManager(new LinearLayoutManager(requireActivity()));
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        firestore = FirebaseFirestore.getInstance();
        profilePostsList = new ArrayList<>();

        getCurrentUserData();
        getFollowData();
        getProfilePostsData();
        addPost();
        setting();
        return view;
    }

    private void getCurrentUserData() {

        firestore.collection("UsersData").document(firebaseUser.getUid()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {

                //HashMap<String,Object> hashMap = (HashMap<String, Object>) documentSnapshot.getData();
                profileUsernameTextView.setText(documentSnapshot.get("username").toString());
                if(documentSnapshot.get("imageUrl").toString().equals("0")) {
                    profileImageView.setImageResource(R.drawable.searchicon);
                }else{
                    //Glide
                    Glide.with(requireActivity()).load(documentSnapshot.get("imageUrl").toString()).into(profileImageView);

                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(requireActivity(), e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void getFollowData(){
        firestore.collection("Follow").document("Following").collection(firebaseUser.getUid()).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if(error != null) {
                    Toast.makeText(requireActivity(), error.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                }
                if(value != null) {
                    int x = 0;
                    for (DocumentSnapshot documentSnapshot : value.getDocuments()) {
                        x++;
                    }
                    profileFollowingTextView.setText("Following: " + x);
                }
                else{
                    profileFollowersTextView.setText("Following: 0");
                }
            }
        });

        firestore.collection("Follow").document("Followers").collection(firebaseUser.getUid()).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if(error != null) {
                    Toast.makeText(requireActivity(), error.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                }
                if(value != null) {
                    int x = 0;
                    for (DocumentSnapshot documentSnapshot : value.getDocuments()) {
                        x++;
                    }
                    profileFollowersTextView.setText("Followers: " + x);
                }
                else{
                    profileFollowersTextView.setText("Followers: 0");
                }
            }
        });
    }

    private void getProfilePostsData() {

        firestore.collection("Posts").whereEqualTo("userId", firebaseUser.getUid()).orderBy("date", Query.Direction.DESCENDING).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {

                if (error != null) {
                    Toast.makeText(requireActivity(), error.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                }

                if(value != null) {
                    profilePostsList.clear();

                    for (DocumentSnapshot documentSnapshot : value.getDocuments()) {

                        String imageUrl = documentSnapshot.get("imageUrl").toString();
                        String comment = documentSnapshot.get("comment").toString();
                        String docName = documentSnapshot.get("docName").toString();
                        //String id = documentSnapshot.get("userId").toString();
                        Posts posts = new Posts(firebaseUser.getUid(), comment, "", "", imageUrl,docName);

                        firestore.collection("UsersData").document(firebaseUser.getUid()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                            @Override
                            public void onSuccess(DocumentSnapshot documentSnapshot) {

                                posts.setUsername(documentSnapshot.get("username").toString());
                                posts.setProfileImageUrl(documentSnapshot.get("imageUrl").toString());
                                profilePostsList.add(posts);
                                feedAdapter = new FeedAdapter(profilePostsList, requireActivity());
                                profilePostsRecyclerView.setAdapter(feedAdapter);

                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(requireActivity(), e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });

                    }

                }

            }
        });

    }

    private void addPost() {
        addPostMaterialButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(requireActivity(), SharePostActivity.class);
                startActivity(intent);
            }
        });
    }

    private void setting() {
        settingMaterialButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Go Settings
                mAuth = FirebaseAuth.getInstance();
                mAuth.signOut();
                Intent intent = new Intent(requireActivity(), LoginActivity.class);
                startActivity(intent);
                requireActivity().finish();
            }
        });
    }

}