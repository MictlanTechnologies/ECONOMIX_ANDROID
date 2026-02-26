package com.example.economix_android.chat;

public class ChatMessage {

    public enum Sender {
        USER,
        IA
    }

    private final String text;
    private final Sender sender;

    public ChatMessage(String text, Sender sender) {
        this.text = text;
        this.sender = sender;
    }

    public String getText() {
        return text;
    }

    public Sender getSender() {
        return sender;
    }

    public boolean isUser() {
        return sender == Sender.USER;
    }
}
