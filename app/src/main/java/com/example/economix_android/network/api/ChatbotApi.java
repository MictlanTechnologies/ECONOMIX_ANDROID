package com.example.economix_android.network.api;

import com.example.economix_android.network.dto.ChatbotRequest;
import com.example.economix_android.network.dto.ChatbotResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface ChatbotApi {
    @POST("/economix/api/chatbot/message")
    Call<ChatbotResponse> enviarMensajeChatbot(@Body ChatbotRequest request);
}
