package com.example.economix_android.Model.chatbot;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.economix_android.R;

import java.util.List;

public class ChatMessageAdapter extends RecyclerView.Adapter<ChatMessageAdapter.MessageViewHolder> {
    private final List<ChatMessage> messages;

    public ChatMessageAdapter(List<ChatMessage> messages) { this.messages = messages; }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat_message, parent, false);
        return new MessageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageViewHolder holder, int position) {
        ChatMessage message = messages.get(position);
        holder.userBubble.setVisibility(message.getSender() == ChatMessage.Sender.USER ? View.VISIBLE : View.GONE);
        holder.aiBubble.setVisibility(message.getSender() == ChatMessage.Sender.AI ? View.VISIBLE : View.GONE);
        if (message.getSender() == ChatMessage.Sender.USER) holder.userBubble.setText(message.getText());
        else holder.aiBubble.setText(message.getText());
    }

    @Override
    public int getItemCount() { return messages.size(); }

    static class MessageViewHolder extends RecyclerView.ViewHolder {
        TextView userBubble, aiBubble;
        MessageViewHolder(@NonNull View itemView) {
            super(itemView);
            userBubble = itemView.findViewById(R.id.tvUserBubble);
            aiBubble = itemView.findViewById(R.id.tvAiBubble);
        }
    }
}
