package com.prometrx.myinstagramclone.Fragments;

import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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
import com.prometrx.myinstagramclone.R;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


public class FeedFragment extends Fragment {
    private RecyclerView recyclerView;
    private List<Posts> postsList;
    private FeedAdapter feedAdapter;
    //Firebase
    private FirebaseFirestore firestore;
    private FirebaseUser firebaseUser;
    private String id;

    public FeedFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_feed, container, false);
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        firestore = FirebaseFirestore.getInstance();
        recyclerView = view.findViewById(R.id.feedFragmentRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireActivity()));
        postsList = new ArrayList<>();
        getData();


        return view;
    }

    private void getData() {

        firestore.collection("Posts").orderBy("date", Query.Direction.DESCENDING).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {

                if (error != null) {
                    Toast.makeText(requireActivity(), error.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                }

                if (value != null) {

                    postsList.clear();
                    for (DocumentSnapshot documentSnapshot : value.getDocuments()) {

                        String imageUrl = documentSnapshot.get("imageUrl").toString();
                        String comment = documentSnapshot.get("comment").toString();
                        String docName = documentSnapshot.get("docName").toString();
                        id = documentSnapshot.get("userId").toString();
                        Posts posts = new Posts(id, comment, "", "", imageUrl, docName);

                        firestore.collection("UsersData").document(id).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                            @Override
                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                posts.setUsername(documentSnapshot.get("username").toString());
                                posts.setProfileImageUrl(documentSnapshot.get("imageUrl").toString());
                                postsList.add(posts);
                                feedAdapter = new FeedAdapter(postsList, requireActivity());
                                recyclerView.setAdapter(feedAdapter);
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



}