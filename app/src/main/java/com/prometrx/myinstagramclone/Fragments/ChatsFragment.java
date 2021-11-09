package com.prometrx.myinstagramclone.Fragments;

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
import com.prometrx.myinstagramclone.Adapter.UsersListAdapter;
import com.prometrx.myinstagramclone.Model.UsersChats;
import com.prometrx.myinstagramclone.R;

import java.util.ArrayList;
import java.util.List;


public class ChatsFragment extends Fragment {

    private RecyclerView recyclerViewUsers;
    private FirebaseUser firebaseUser;
    private FirebaseFirestore firestore;
    private List<UsersChats> usersChatsList;
    private UsersListAdapter usersListAdapter;

    public ChatsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_chats, container, false);
        recyclerViewUsers = view.findViewById(R.id.chatsFragmentRecyclerView);
        recyclerViewUsers.setLayoutManager(new LinearLayoutManager(requireActivity()));
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        firestore = FirebaseFirestore.getInstance();
        usersChatsList = new ArrayList<>();
        getUsers();
        return view;
    }

    private void getUsers() {

        firestore.collection("Chats").orderBy("date", Query.Direction.ASCENDING).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {

                if(value != null) {
                    usersChatsList.clear();

                    List<String> usersid = new ArrayList<>();
                    for (DocumentSnapshot dc : value.getDocuments()) {

                        if(dc.get("senderId").equals(firebaseUser.getUid()) || dc.get("receiver").equals(firebaseUser.getUid())) {

                            if(dc.get("senderId").equals(firebaseUser.getUid())) {
                                //I am sender;
                                String userId = dc.get("receiver").toString();

                                firestore.collection("UsersData").document(userId).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                    @Override
                                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                                        String userName = documentSnapshot.get("username").toString();
                                        String userImageUrl = documentSnapshot.get("imageUrl").toString();
                                        UsersChats usersChats = new UsersChats(userId, userImageUrl, userName, "");

                                        if(!usersChats.getUserId().contains(firebaseUser.getUid())) {

                                            if(!usersid.contains(usersChats.getUserId())) {

                                                firestore.collection("Chats").orderBy("date", Query.Direction.ASCENDING).whereEqualTo("senderId", usersChats.getUserId()).addSnapshotListener(new EventListener<QuerySnapshot>() {
                                                    @Override
                                                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {

                                                        if(value != null) {
                                                            String x = value.getDocuments().get(0).get("message").toString();

                                                            usersChats.setUserLastMessage(x);

                                                        }
                                                    }
                                                });
                                                usersid.add(usersChats.getUserId());
                                                usersChatsList.add(usersChats);
                                            }

                                        }

                                        usersListAdapter = new UsersListAdapter(requireActivity(), usersChatsList);
                                        recyclerViewUsers.setAdapter(usersListAdapter);

                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(requireActivity(), e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                            else {
                                //I am receiver;
                                String userId = dc.get("senderId").toString();
                                firestore.collection("UsersData").document(userId).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                    @Override
                                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                                        String userName = documentSnapshot.get("username").toString();
                                        String userImageUrl = documentSnapshot.get("imageUrl").toString();
                                        UsersChats usersChats = new UsersChats(userId, userImageUrl, userName, "");

                                        if(!usersChats.getUserId().contains(firebaseUser.getUid())) {



                                            if(!usersid.contains(usersChats.getUserId())) {


                                                firestore.collection("Chats").orderBy("date", Query.Direction.ASCENDING).whereEqualTo("receiver", usersChats.getUserId()).addSnapshotListener(new EventListener<QuerySnapshot>() {
                                                    @Override
                                                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {

                                                        if(value != null) {
                                                            String x = value.getDocuments().get(0).get("message").toString();
                                                            usersChats.setUserLastMessage(x);

                                                        }

                                                    }

                                                });
                                                usersid.add(usersChats.getUserId());
                                                usersChatsList.add(usersChats);

                                            }

                                        }

                                        usersListAdapter = new UsersListAdapter(requireActivity(), usersChatsList);
                                        recyclerViewUsers.setAdapter(usersListAdapter);

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
                }
            }
        });

    }

}