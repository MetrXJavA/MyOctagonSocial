package com.prometrx.myinstagramclone.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.github.siyamed.shapeimageview.OctogonImageView;
import com.prometrx.myinstagramclone.Model.Comments;
import com.prometrx.myinstagramclone.R;

import java.util.List;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.ViewHolder> {

    private List<Comments> commentsList;
    private Context context;

    public CommentAdapter(List<Comments> commentsList, Context context) {
        this.commentsList = commentsList;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.comment_layout, parent,false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        Comments comments = commentsList.get(position);

        holder.usernameTextView.setText(comments.getUsername());
        holder.commentTextView.setText(comments.getComment());

        Glide.with(context).load(comments.getProfileImageUrl()).into(holder.profileImageView);

    }

    @Override
    public int getItemCount() {
        return commentsList.size();
    }

    class ViewHolder extends  RecyclerView.ViewHolder {

        private OctogonImageView profileImageView;
        private TextView usernameTextView, commentTextView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            profileImageView = itemView.findViewById(R.id.commentProfileImageViewLayout);
            usernameTextView = itemView.findViewById(R.id.usernameTextView);
            commentTextView  = itemView.findViewById(R.id.commentTextView);
        }
    }
}
