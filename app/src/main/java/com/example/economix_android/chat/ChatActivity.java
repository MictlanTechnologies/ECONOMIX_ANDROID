package com.example.economix_android.chat;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.economix_android.BuildConfig;
import com.example.economix_android.R;
import com.example.economix_android.ai.AiModels;
import com.example.economix_android.ai.AiRepository;
import com.example.economix_android.auth.SessionManager;
import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.POST;
import retrofit2.http.Query;

public class ChatActivity extends AppCompatActivity {

    private RecyclerView rvMessages;
    private EditText etMessage;
    private Button btnSend;

    private final List<ChatMessage> chatMessages = new ArrayList<>();
    private ChatAdapter chatAdapter;

    private final AiRepository aiRepository = new AiRepository();
    private final Gson gson = new Gson();

    private GeminiApi geminiApi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        rvMessages = findViewById(R.id.rvMessages);
        etMessage = findViewById(R.id.etMessage);
        btnSend = findViewById(R.id.btnSend);

        chatAdapter = new ChatAdapter(chatMessages);
        rvMessages.setLayoutManager(new LinearLayoutManager(this));
        rvMessages.setAdapter(chatAdapter);

        addMessage("¡Hola! Soy tu asistente de ciencia de datos financiera. Puedo explicar resultados analíticos y recomendar acciones.", ChatMessage.Sender.IA);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://generativelanguage.googleapis.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .client(new OkHttpClient.Builder().build())
                .build();
        geminiApi = retrofit.create(GeminiApi.class);

        btnSend.setOnClickListener(v -> {
            String query = etMessage.getText().toString().trim();
            if (TextUtils.isEmpty(query)) {
                return;
            }
            etMessage.setText("");
            addMessage(query, ChatMessage.Sender.USER);
            performDataAnalysis(query);
        });
    }

    private void performDataAnalysis(String query) {
        Integer userId = SessionManager.getUserId(this);
        if (userId == null) {
            addMessage("No pude identificar tu sesión. Inicia sesión nuevamente para analizar tus datos.", ChatMessage.Sender.IA);
            return;
        }

        btnSend.setEnabled(false);
        addMessage("Consultando hechos analíticos en backend AI...", ChatMessage.Sender.IA);

        LocalDate to = LocalDate.now();
        LocalDate from = to.minusDays(30);

        Map<String, Object> facts = Collections.synchronizedMap(new LinkedHashMap<>());
        facts.put("userId", userId);
        facts.put("from", from.toString());
        facts.put("to", to.toString());

        AtomicInteger pending = new AtomicInteger(3);
        Runnable done = () -> {
            if (pending.decrementAndGet() == 0) {
                requestAiResponse(query, gson.toJson(facts));
            }
        };

        aiRepository.getSummary(userId, from, to, new Callback<AiModels.AISummaryResponse>() {
            @Override
            public void onResponse(@NonNull Call<AiModels.AISummaryResponse> call, @NonNull Response<AiModels.AISummaryResponse> response) {
                facts.put("summary", response.isSuccessful() ? response.body() : null);
                if (!response.isSuccessful()) facts.put("summaryError", response.code());
                done.run();
            }

            @Override
            public void onFailure(@NonNull Call<AiModels.AISummaryResponse> call, @NonNull Throwable t) {
                facts.put("summary", null);
                facts.put("summaryError", t.getMessage());
                done.run();
            }
        });

        aiRepository.getSpendPrediction(userId, 30, new Callback<AiModels.SpendForecastResponse>() {
            @Override
            public void onResponse(@NonNull Call<AiModels.SpendForecastResponse> call, @NonNull Response<AiModels.SpendForecastResponse> response) {
                facts.put("spendPrediction", response.isSuccessful() ? response.body() : null);
                if (!response.isSuccessful()) facts.put("spendPredictionError", response.code());
                done.run();
            }

            @Override
            public void onFailure(@NonNull Call<AiModels.SpendForecastResponse> call, @NonNull Throwable t) {
                facts.put("spendPrediction", null);
                facts.put("spendPredictionError", t.getMessage());
                done.run();
            }
        });

        aiRepository.getBudgetRisk(userId, to.getMonthValue(), to.getYear(), new Callback<AiModels.BudgetRiskResponse>() {
            @Override
            public void onResponse(@NonNull Call<AiModels.BudgetRiskResponse> call, @NonNull Response<AiModels.BudgetRiskResponse> response) {
                facts.put("budgetRisk", response.isSuccessful() ? response.body() : null);
                if (!response.isSuccessful()) facts.put("budgetRiskError", response.code());
                done.run();
            }

            @Override
            public void onFailure(@NonNull Call<AiModels.BudgetRiskResponse> call, @NonNull Throwable t) {
                facts.put("budgetRisk", null);
                facts.put("budgetRiskError", t.getMessage());
                done.run();
            }
        });
    }

    private void requestAiResponse(String query, String factsJson) {
        String apiKey = BuildConfig.GEMINI_API_KEY;
        if (TextUtils.isEmpty(apiKey) || "YOUR_GEMINI_API_KEY".equals(apiKey)) {
            btnSend.setEnabled(true);
            addMessage("Configura GEMINI_API_KEY en local.properties para habilitar respuestas de IA.", ChatMessage.Sender.IA);
            return;
        }

        String prompt = "Rol: analista financiero personal.\n"
                + "Hechos JSON del backend (usar solo estos hechos, no inventar datos):\n"
                + factsJson
                + "\n\nInstrucciones:\n"
                + "1) Explica la situación financiera con claridad.\n"
                + "2) Da recomendaciones accionables.\n"
                + "3) Si un dato no está disponible, dilo explícitamente.\n"
                + "4) Responde a la consulta del usuario: " + query;

        GeminiRequest requestBody = new GeminiRequest(prompt);
        geminiApi.generateContent("gemini-1.5-flash", apiKey, requestBody).enqueue(new Callback<GeminiResponse>() {
            @Override
            public void onResponse(@NonNull Call<GeminiResponse> call, @NonNull Response<GeminiResponse> response) {
                btnSend.setEnabled(true);
                if (!response.isSuccessful() || response.body() == null) {
                    addMessage("No pude obtener una respuesta del modelo en este momento. Intenta nuevamente.", ChatMessage.Sender.IA);
                    return;
                }
                String text = response.body().extractText();
                addMessage(TextUtils.isEmpty(text) ? "Recibí la solicitud, pero no hubo contenido útil." : text, ChatMessage.Sender.IA);
            }

            @Override
            public void onFailure(@NonNull Call<GeminiResponse> call, @NonNull Throwable t) {
                btnSend.setEnabled(true);
                addMessage("Error de conexión con el servicio de IA. Revisa tu conexión e intenta de nuevo.", ChatMessage.Sender.IA);
            }
        });
    }

    private void addMessage(String message, ChatMessage.Sender sender) {
        chatMessages.add(new ChatMessage(message, sender));
        chatAdapter.notifyItemInserted(chatMessages.size() - 1);
        rvMessages.scrollToPosition(chatMessages.size() - 1);
    }

    interface GeminiApi {
        @POST("v1beta/models/{model}:generateContent")
        Call<GeminiResponse> generateContent(
                @retrofit2.http.Path("model") String model,
                @Query("key") String apiKey,
                @Body GeminiRequest request
        );
    }

    static class GeminiRequest {
        @SerializedName("contents")
        private final List<Content> contents;

        GeminiRequest(String text) {
            this.contents = Collections.singletonList(new Content(text));
        }
    }

    static class Content {
        @SerializedName("parts")
        private final List<Part> parts;

        Content(String text) {
            this.parts = Collections.singletonList(new Part(text));
        }
    }

    static class Part {
        @SerializedName("text")
        private final String text;

        Part(String text) {
            this.text = text;
        }
    }

    static class GeminiResponse {
        @SerializedName("candidates")
        List<Candidate> candidates;

        String extractText() {
            if (candidates == null || candidates.isEmpty()) return null;
            Candidate candidate = candidates.get(0);
            if (candidate.content == null || candidate.content.parts == null || candidate.content.parts.isEmpty()) return null;
            return candidate.content.parts.get(0).text;
        }
    }

    static class Candidate {
        @SerializedName("content")
        CandidateContent content;
    }

    static class CandidateContent {
        @SerializedName("parts")
        List<CandidatePart> parts;
    }

    static class CandidatePart {
        @SerializedName("text")
        String text;
    }
}
