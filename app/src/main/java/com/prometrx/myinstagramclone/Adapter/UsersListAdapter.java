package com.prometrx.myinstagramclone.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.github.siyamed.shapeimageview.OctogonImageView;
import com.prometrx.myinstagramclone.Model.UsersChats;
import com.prometrx.myinstagramclone.Other.OtherUsersProfileActivity;
import com.prometrx.myinstagramclone.R;

import java.util.List;

public class UsersListAdapter extends RecyclerView.Adapter<UsersListAdapter.ViewHolder> {

    private Context context;
    private List<UsersChats> usersChatsList;

    public UsersListAdapter(Context context, List<UsersChats> usersChatsList) {
        this.context = context;
        this.usersChatsList = usersChatsList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.chats_fragment_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        UsersChats usersChats = usersChatsList.get(position);

        Glide.with(context).load(usersChats.getUserImageUrl()).into(holder.userImageView);
        holder.usernameTextView.setText(usersChats.getUserUsername());
        holder.lastMessageTextView.setText(usersChats.getUserLastMessage());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, OtherUsersProfileActivity.class);
                intent.putExtra("otherUserId", usersChats.getUserId());
                intent.putExtra("otherUserUsername", usersChats.getUserUsername());
                intent.putExtra("otherUserProfileImageUrl", usersChats.getUserImageUrl());
                context.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return usersChatsList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private TextView usernameTextView, lastMessageTextView;
        private OctogonImageView userImageView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            userImageView = itemView.findViewById(R.id.chats_fragment_layout_ImageView);
            lastMessageTextView = itemView.findViewById(R.id.chats_fragment_layout_LastMessageTextView);
            usernameTextView = itemView.findViewById(R.id.chatsFragmentUsername);

        }
    }

}
