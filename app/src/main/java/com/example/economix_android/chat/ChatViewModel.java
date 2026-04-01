package com.example.economix_android.chat;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.economix_android.ai.AiModels;
import com.example.economix_android.ai.AiRepository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChatViewModel extends ViewModel {

    private static final int DEFAULT_CHAT_RANGE_DAYS = 30;

    private final AiRepository repository = new AiRepository();
    private final MutableLiveData<List<ChatMessage>> messages = new MutableLiveData<>(new ArrayList<>());
    private final MutableLiveData<Boolean> loading = new MutableLiveData<>(false);
    private final MutableLiveData<String> error = new MutableLiveData<>(null);
    private final MutableLiveData<Boolean> unauthorized = new MutableLiveData<>(false);

    public ChatViewModel() {
        appendMessage(new ChatMessage("Hola. Soy tu asistente de IA de ECONOMIX conectado al backend autenticado.", ChatMessage.Sender.IA));
    }

    public LiveData<List<ChatMessage>> getMessages() { return messages; }
    public LiveData<Boolean> getLoading() { return loading; }
    public LiveData<String> getError() { return error; }
    public LiveData<Boolean> getUnauthorized() { return unauthorized; }

    public void sendMessage(String userMessage) {
        if (Boolean.TRUE.equals(loading.getValue())) {
            return;
        }

        appendMessage(new ChatMessage(userMessage, ChatMessage.Sender.USER));
        loading.setValue(true);
        error.setValue(null);

        LocalDate to = LocalDate.now();
        LocalDate from = to.minusDays(DEFAULT_CHAT_RANGE_DAYS);

        AiModels.AiChatRequest request = new AiModels.AiChatRequest();
        request.setMessage(userMessage);
        request.setFrom(from);
        request.setTo(to);
        request.setFromA(from.minusDays(DEFAULT_CHAT_RANGE_DAYS));
        request.setToA(from.minusDays(1));
        request.setFromB(from);
        request.setToB(to);
        request.setHorizonDays(DEFAULT_CHAT_RANGE_DAYS);

        repository.chat(request, new Callback<AiModels.AiChatResponse>() {
            @Override
            public void onResponse(Call<AiModels.AiChatResponse> call, Response<AiModels.AiChatResponse> response) {
                loading.postValue(false);
                if (response.code() == 401) {
                    unauthorized.postValue(true);
                    error.postValue("Tu sesión expiró. Inicia sesión nuevamente.");
                    return;
                }
                if (!response.isSuccessful() || response.body() == null) {
                    error.postValue("Error del backend IA (HTTP " + response.code() + ").");
                    return;
                }

                AiModels.AiChatResponse body = response.body();
                String answer = (body.getMessage() != null && !body.getMessage().trim().isEmpty())
                        ? body.getMessage().trim()
                        : "El backend IA no devolvió contenido en esta respuesta.";
                appendMessage(new ChatMessage(answer, ChatMessage.Sender.IA));
            }

            @Override
            public void onFailure(Call<AiModels.AiChatResponse> call, Throwable t) {
                loading.postValue(false);
                error.postValue("Error de red al consultar IA. Intenta nuevamente.");
            }
        });
    }

    private void appendMessage(ChatMessage message) {
        List<ChatMessage> current = messages.getValue();
        List<ChatMessage> updated = current == null ? new ArrayList<>() : new ArrayList<>(current);
        updated.add(message);
        messages.postValue(updated);
    }
}
