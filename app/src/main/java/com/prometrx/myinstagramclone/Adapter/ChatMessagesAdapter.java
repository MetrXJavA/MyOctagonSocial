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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.prometrx.myinstagramclone.Model.Chats;
import com.prometrx.myinstagramclone.R;

import java.util.List;

public class ChatMessagesAdapter extends RecyclerView.Adapter<ChatMessagesAdapter.ViewHolder>{

    private FirebaseUser firebaseUser;
    private TextView messageTextView;
    private OctogonImageView profileChatImageView;
    private List<Chats> chatsList;
    private Context context;
    private final static int left = 0;
    private final static int right = 1;


    public ChatMessagesAdapter(List<Chats> chatsList, Context context) {
        this.chatsList = chatsList;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = null;
        if(viewType == left) {
            view = LayoutInflater.from(context).inflate(R.layout.left_layout_chat, parent, false);
        }
        else if (viewType == right) {
            view = LayoutInflater.from(context).inflate(R.layout.right_layout_chat, parent, false);
        }
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        Chats chats = chatsList.get(position);

        holder.messageText.setText(chats.getMessageText());

    }

    @Override
    public int getItemCount() {
        return chatsList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private TextView messageText;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            messageText = itemView.findViewById(R.id.textViewChat);
        }
    }

    @Override
    public int getItemViewType(int position) {
        int x = 0;
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if(firebaseUser.getUid().equals(chatsList.get(position).getSender())) {
            x = right;
        }else if(firebaseUser.getUid().equals(chatsList.get(position).getReceiver())) {
            x = left;
        }
        return x;
    }
}
