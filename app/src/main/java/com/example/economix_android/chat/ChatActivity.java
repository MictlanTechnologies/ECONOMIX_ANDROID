package com.example.economix_android.chat;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.economix_android.R;
import com.example.economix_android.auth.SessionManager;
import com.example.economix_android.auth.ui.LoginActivity;

import java.util.List;

public class ChatActivity extends AppCompatActivity {

    private RecyclerView rvMessages;
    private EditText etMessage;
    private Button btnSend;
    private ChatAdapter chatAdapter;
    private ChatViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        rvMessages = findViewById(R.id.rvMessages);
        etMessage = findViewById(R.id.etMessage);
        btnSend = findViewById(R.id.btnSend);

        chatAdapter = new ChatAdapter();
        rvMessages.setLayoutManager(new LinearLayoutManager(this));
        rvMessages.setAdapter(chatAdapter);

        viewModel = new ViewModelProvider(this).get(ChatViewModel.class);

        viewModel.getMessages().observe(this, this::renderMessages);
        viewModel.getLoading().observe(this, this::renderLoading);
        viewModel.getError().observe(this, error -> {
            if (error != null && !error.trim().isEmpty()) {
                Toast.makeText(this, error, Toast.LENGTH_SHORT).show();
            }
        });
        viewModel.getUnauthorized().observe(this, unauthorized -> {
            if (Boolean.TRUE.equals(unauthorized)) {
                SessionManager.clearSession(this);
                Intent intent = new Intent(this, LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });

        btnSend.setOnClickListener(v -> {
            String query = etMessage.getText().toString().trim();
            if (TextUtils.isEmpty(query)) {
                return;
            }
            etMessage.setText("");
            viewModel.sendMessage(query);
        });
    }

    private void renderMessages(List<ChatMessage> messages) {
        chatAdapter.setMessages(messages);
        if (messages != null && !messages.isEmpty()) {
            rvMessages.scrollToPosition(messages.size() - 1);
        }
    }

    private void renderLoading(Boolean isLoading) {
        boolean loading = Boolean.TRUE.equals(isLoading);
        btnSend.setEnabled(!loading);
        etMessage.setEnabled(!loading);
        btnSend.setText(loading ? "Enviando..." : "Enviar");
    }
}
