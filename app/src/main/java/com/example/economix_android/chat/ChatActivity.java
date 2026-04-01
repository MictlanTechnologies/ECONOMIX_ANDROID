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
        LocalDate toA = from.minusDays(1);
        LocalDate fromA = toA.minusDays(30);

        Map<String, Object> facts = Collections.synchronizedMap(new LinkedHashMap<>());
        facts.put("userId", userId);
        facts.put("from", from.toString());
        facts.put("to", to.toString());
        facts.put("fromA", fromA.toString());
        facts.put("toA", toA.toString());

        AtomicInteger pending = new AtomicInteger(6);
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

        aiRepository.getAnomalies(userId, from, to, new Callback<AiModels.AnomalyResponse>() {
            @Override
            public void onResponse(@NonNull Call<AiModels.AnomalyResponse> call, @NonNull Response<AiModels.AnomalyResponse> response) {
                facts.put("anomalies", response.isSuccessful() ? response.body() : null);
                if (!response.isSuccessful()) facts.put("anomaliesError", response.code());
                done.run();
            }

            @Override
            public void onFailure(@NonNull Call<AiModels.AnomalyResponse> call, @NonNull Throwable t) {
                facts.put("anomalies", null);
                facts.put("anomaliesError", t.getMessage());
                done.run();
            }
        });

        aiRepository.getConfidenceInterval(userId, from, to, new Callback<AiModels.ConfidenceIntervalResponse>() {
            @Override
            public void onResponse(@NonNull Call<AiModels.ConfidenceIntervalResponse> call, @NonNull Response<AiModels.ConfidenceIntervalResponse> response) {
                facts.put("confidenceInterval", response.isSuccessful() ? response.body() : null);
                if (!response.isSuccessful()) facts.put("confidenceIntervalError", response.code());
                done.run();
            }

            @Override
            public void onFailure(@NonNull Call<AiModels.ConfidenceIntervalResponse> call, @NonNull Throwable t) {
                facts.put("confidenceInterval", null);
                facts.put("confidenceIntervalError", t.getMessage());
                done.run();
            }
        });

        AiModels.CompareMeansRequest compareRequest = new AiModels.CompareMeansRequest();
        compareRequest.setUserId(userId);
        compareRequest.setFromA(fromA);
        compareRequest.setToA(toA);
        compareRequest.setFromB(from);
        compareRequest.setToB(to);
        compareRequest.setAlpha(new java.math.BigDecimal("0.05"));

        aiRepository.compareMeans(compareRequest, new Callback<AiModels.HypothesisTestResponse>() {
            @Override
            public void onResponse(@NonNull Call<AiModels.HypothesisTestResponse> call, @NonNull Response<AiModels.HypothesisTestResponse> response) {
                facts.put("hypothesisTest", response.isSuccessful() ? response.body() : null);
                if (!response.isSuccessful()) facts.put("hypothesisTestError", response.code());
                done.run();
            }

            @Override
            public void onFailure(@NonNull Call<AiModels.HypothesisTestResponse> call, @NonNull Throwable t) {
                facts.put("hypothesisTest", null);
                facts.put("hypothesisTestError", t.getMessage());
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

        String systemPrompt = buildSystemInstruction();
        String userContent = "Facts JSON del backend (no inventar):\n" + factsJson
                + "\n\nPregunta del usuario:\n" + query;

        GeminiRequest requestBody = new GeminiRequest(systemPrompt, userContent);
        geminiApi.generateContent("gemini-2.5-flash", apiKey, requestBody).enqueue(new Callback<GeminiResponse>() {
            @Override
            public void onResponse(@NonNull Call<GeminiResponse> call, @NonNull Response<GeminiResponse> response) {
                btnSend.setEnabled(true);
                if (!response.isSuccessful() || response.body() == null) {
                    String err = "";
                    try { err = response.errorBody() != null ? response.errorBody().string() : ""; }
                    catch (Exception ignored) {}
                    addMessage("Gemini error HTTP " + response.code() + ":\n" + err, ChatMessage.Sender.IA);
                    return;
                }
                String text = response.body().extractText();
                String cleanText = sanitizeAssistantText(text);
                addMessage(TextUtils.isEmpty(cleanText) ? "Recibí la solicitud, pero no hubo contenido útil." : cleanText, ChatMessage.Sender.IA);

            }

            @Override
            public void onFailure(@NonNull Call<GeminiResponse> call, @NonNull Throwable t) {
                btnSend.setEnabled(true);
                addMessage("Error de conexión con el servicio de IA. Revisa tu conexión e intenta de nuevo.", ChatMessage.Sender.IA);
            }
        });
    }


    private String buildSystemInstruction() {
        return "Eres el asistente analítico de ECONOMIX.\n"
                + "Recibirás como entrada un JSON con resultados calculados por el backend, incluyendo (cuando exista): "
                + "summary, forecast/spendPrediction, confidence intervals (CI), p-values, riesgos de presupuesto y anomalías.\n\n"
                + "Reglas obligatorias:\n"
                + "- Nunca inventes datos numéricos ni campos. Usa solo valores presentes en el JSON.\n"
                + "- Si un campo no está o viene nulo, dilo explícitamente (por ejemplo: 'Falta el campo pValue').\n"
                + "- No ocultes incertidumbre ni errores del backend; repórtalos si aparecen (ej. *Error, null, código HTTP*).\n"
                + "- Explica estadística inferencial en español sencillo:\n"
                + "  * Qué significa un IC 95% (rango plausible del parámetro con 95% de confianza bajo el método usado).\n"
                + "  * Cómo interpretar p-value frente a alpha (si p-value < alpha, hay evidencia para rechazar H0; si no, no alcanza la evidencia).\n\n"
                + "Formato obligatorio de respuesta (usa exactamente estas 5 secciones):\n"
                + "1) Diagnóstico actual (con números)\n"
                + "2) Predicción (con intervalo y qué significa)\n"
                + "3) Riesgos (presupuesto/anomalías)\n"
                + "4) Recomendaciones accionables (3-6)\n"
                + "5) Qué dato faltaría para mejorar precisión\n\n"
                + "Si no hay datos suficientes en alguna sección, escríbelo de forma explícita sin completar con suposiciones.\n"
                + "Devuelve texto plano legible. No uses Markdown (sin **, *, #, ni backticks).";
    }

    private String sanitizeAssistantText(String rawText) {
        if (TextUtils.isEmpty(rawText)) {
            return rawText;
        }
        return rawText
                .replace("```", "")
                .replace("**", "")
                .replace("`", "")
                .replaceAll("(?m)^\\s*[-*]\\s+", "• ")
                .trim();
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
        @SerializedName("system_instruction")
        private final SystemInstruction systemInstruction;

        @SerializedName("contents")
        private final List<Content> contents;

        GeminiRequest(String systemPrompt, String userText) {
            this.systemInstruction = new SystemInstruction(systemPrompt);
            this.contents = Collections.singletonList(new Content(userText));
        }
    }

    static class SystemInstruction {
        @SerializedName("parts")
        private final List<Part> parts;

        SystemInstruction(String text) {
            this.parts = Collections.singletonList(new Part(text));
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
