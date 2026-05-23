package com.example.economix_android.Model.chatbot;

public class ChatMessage {
    public enum Sender { USER, AI }

    private final Sender sender;
    private final String text;

    public ChatMessage(Sender sender, String text) {
        this.sender = sender;
        this.text = text;
    }

    public Sender getSender() { return sender; }
    public String getText() { return text; }
}
