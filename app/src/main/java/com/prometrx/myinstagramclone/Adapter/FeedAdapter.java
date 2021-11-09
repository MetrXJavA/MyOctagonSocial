package com.prometrx.myinstagramclone.Adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.github.siyamed.shapeimageview.OctogonImageView;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.prometrx.myinstagramclone.Model.Posts;
import com.prometrx.myinstagramclone.Other.CommentActivity;
import com.prometrx.myinstagramclone.Other.OtherUsersProfileActivity;
import com.prometrx.myinstagramclone.R;

import java.util.HashMap;
import java.util.List;

public class FeedAdapter extends RecyclerView.Adapter<FeedAdapter.viewHolder> {

    private List<Posts> postsList;
    private Context context;
    private FirebaseUser fuser;
    private FirebaseFirestore firestore;
    private int likeNumber = 0;

    public FeedAdapter(List<Posts> postsList, Context context) {
        this.postsList = postsList;
        this.context = context;
    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.feed_layout, parent, false);

        return new viewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull viewHolder holder, int position) {

        Posts posts = postsList.get(position);
        Glide.with(context).load(posts.getProfileImageUrl()).into(holder.profileImageView);
        holder.usernameTextView.setText(posts.getUsername());
        Glide.with(context).load(posts.getImageUrl()).into(holder.postImageView);
        holder.commentTextView.setText(posts.getComment());

        holder.profileImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!fuser.getUid().equals(posts.getId())) {
                    Intent intent = new Intent(context, OtherUsersProfileActivity.class);
                    intent.putExtra("otherUserId", posts.getId());
                    intent.putExtra("otherUserUsername", posts.getUsername());
                    intent.putExtra("otherUserProfileImageUrl", posts.getProfileImageUrl());
                    context.startActivity(intent);
                }

            }
        });

        firestore.collection("Posts").document(posts.getDocName()).collection("LikeStatus").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {

                if (value != null) {
                    likeNumber = 0;

                    if(error != null) {
                        Toast.makeText(context, error.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                    }

                    if(value.size() == 0) {
                        holder.likeNumberTextView.setText("0");
                    }else{
                        for (DocumentSnapshot dc : value.getDocuments()) {

                            if (dc.get("id").equals(fuser.getUid())) {
                                //Negative
                                holder.likeNegativeImageButton.setVisibility(View.VISIBLE);
                                holder.likePositiveImageButton.setVisibility(View.INVISIBLE);

                            } else {
                                //Positive
                                holder.likeNegativeImageButton.setVisibility(View.INVISIBLE);
                                holder.likePositiveImageButton.setVisibility(View.VISIBLE);
                            }
                            likeNumber++;
                            holder.likeNumberTextView.setText(likeNumber + "");
                        }
                    }

                }
                else{
                    Toast.makeText(context, "error", Toast.LENGTH_SHORT).show();
                }

            }
        });

        holder.likePositiveImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                HashMap<String, Object> hashMap = new HashMap<>();
                hashMap.put("id", fuser.getUid());

                firestore.collection("Posts").document(posts.getDocName()).collection("LikeStatus").document(fuser.getUid()).set(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(context, "Liked", Toast.LENGTH_SHORT).show();

                        holder.likeNegativeImageButton.setVisibility(View.VISIBLE);
                        holder.likePositiveImageButton.setVisibility(View.INVISIBLE);

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(context, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        holder.likeNegativeImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                firestore.collection("Posts").document(posts.getDocName()).collection("LikeStatus").document(fuser.getUid()).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(context, "Unliked", Toast.LENGTH_SHORT).show();
                        holder.likeNegativeImageButton.setVisibility(View.INVISIBLE);
                        holder.likePositiveImageButton.setVisibility(View.VISIBLE);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(context, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        holder.adviseImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                Intent intent = new Intent(context, CommentActivity.class);
                intent.putExtra("UserId", posts.getId());
                intent.putExtra("Username", posts.getUsername());
                intent.putExtra("UserProfileImageUrl", posts.getProfileImageUrl());
                intent.putExtra("UserDetail", posts.getComment());
                intent.putExtra("UserPostImageUrl", posts.getImageUrl());
                intent.putExtra("UserLikeNumber", likeNumber);
                intent.putExtra("docName", posts.getDocName());
                System.out.println(likeNumber + " asddsa");
                context.startActivity(intent);

            }
        });

    }

    @Override
    public int getItemCount() {
        return postsList.size();
    }

    class viewHolder extends RecyclerView.ViewHolder {
        private TextView usernameTextView, commentTextView, likeNumberTextView;
        private OctogonImageView profileImageView, postImageView, likePositiveImageButton, likeNegativeImageButton, adviseImageButton;

        public viewHolder(@NonNull View itemView) {
            super(itemView);

            usernameTextView = itemView.findViewById(R.id.feedPostUsernameTextView);
            commentTextView = itemView.findViewById(R.id.feedPostCommentTextView);
            likeNumberTextView = itemView.findViewById(R.id.profileLikeNumberTextView);
            postImageView = itemView.findViewById(R.id.feedPostImageView);
            profileImageView = itemView.findViewById(R.id.feedPostProfileImageView);
            likePositiveImageButton = itemView.findViewById(R.id.likePositivePostProfileImageView);
            likeNegativeImageButton = itemView.findViewById(R.id.likeNegativePostProfileImageView);
            //likePositiveImageButton.setVisibility(View.INVISIBLE);
            //likeNegativeImageButton.setVisibility(View.INVISIBLE);
            adviseImageButton = itemView.findViewById(R.id.advisePostProfileImageView);
            fuser = FirebaseAuth.getInstance().getCurrentUser();
            firestore = FirebaseFirestore.getInstance();

        }
    }


}
