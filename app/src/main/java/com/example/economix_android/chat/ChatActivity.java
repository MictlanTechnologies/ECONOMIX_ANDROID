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
import com.example.economix_android.auth.SessionManager;
import com.example.economix_android.network.dto.AhorroDto;
import com.example.economix_android.network.dto.GastoDto;
import com.example.economix_android.network.dto.IngresoDto;
import com.example.economix_android.network.repository.AhorroRepository;
import com.example.economix_android.network.repository.GastoRepository;
import com.example.economix_android.network.repository.IngresoRepository;
import com.google.gson.annotations.SerializedName;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
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

    private final GastoRepository gastoRepository = new GastoRepository();
    private final IngresoRepository ingresoRepository = new IngresoRepository();
    private final AhorroRepository ahorroRepository = new AhorroRepository();

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

        addMessage("¡Hola! Soy tu asistente de ciencia de datos financiera. "
                + "Puedo analizar tus gastos, ingresos y ahorros para darte sugerencias.", ChatMessage.Sender.IA);

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

    public void performDataAnalysis(String query) {
        Integer userId = SessionManager.getUserId(this);
        if (userId == null) {
            addMessage("No pude identificar tu sesión. Inicia sesión nuevamente para analizar tus datos.", ChatMessage.Sender.IA);
            return;
        }

        btnSend.setEnabled(false);
        addMessage("Analizando tus datos financieros para responder con contexto...", ChatMessage.Sender.IA);

        AtomicInteger pendingRequests = new AtomicInteger(3);
        List<GastoDto> gastosUsuario = Collections.synchronizedList(new ArrayList<>());
        List<IngresoDto> ingresosUsuario = Collections.synchronizedList(new ArrayList<>());
        List<AhorroDto> ahorrosRaw = Collections.synchronizedList(new ArrayList<>());

        Runnable onCompleted = () -> {
            if (pendingRequests.decrementAndGet() == 0) {
                List<AhorroDto> ahorrosUsuario = filterAhorrosByUser(ahorrosRaw, ingresosUsuario, userId);
                String financialContext = buildFinancialContext(query, gastosUsuario, ingresosUsuario, ahorrosUsuario);
                requestAiResponse(query, financialContext);
            }
        };

        gastoRepository.obtenerGastos(new Callback<List<GastoDto>>() {
            @Override
            public void onResponse(@NonNull Call<List<GastoDto>> call, @NonNull Response<List<GastoDto>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    for (GastoDto gasto : response.body()) {
                        if (gasto != null && userId.equals(gasto.getIdUsuario())) {
                            gastosUsuario.add(gasto);
                        }
                    }
                }
                onCompleted.run();
            }

            @Override
            public void onFailure(@NonNull Call<List<GastoDto>> call, @NonNull Throwable t) {
                onCompleted.run();
            }
        });

        ingresoRepository.obtenerIngresos(new Callback<List<IngresoDto>>() {
            @Override
            public void onResponse(@NonNull Call<List<IngresoDto>> call, @NonNull Response<List<IngresoDto>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    for (IngresoDto ingreso : response.body()) {
                        if (ingreso != null && userId.equals(ingreso.getIdUsuario())) {
                            ingresosUsuario.add(ingreso);
                        }
                    }
                }
                onCompleted.run();
            }

            @Override
            public void onFailure(@NonNull Call<List<IngresoDto>> call, @NonNull Throwable t) {
                onCompleted.run();
            }
        });

        ahorroRepository.obtenerAhorros(new Callback<List<AhorroDto>>() {
            @Override
            public void onResponse(@NonNull Call<List<AhorroDto>> call, @NonNull Response<List<AhorroDto>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ahorrosRaw.addAll(response.body());
                }
                onCompleted.run();
            }

            @Override
            public void onFailure(@NonNull Call<List<AhorroDto>> call, @NonNull Throwable t) {
                onCompleted.run();
            }
        });
    }

    private List<AhorroDto> filterAhorrosByUser(List<AhorroDto> ahorros, List<IngresoDto> ingresosUsuario, Integer userId) {
        List<AhorroDto> filtered = new ArrayList<>();
        for (AhorroDto ahorro : ahorros) {
            if (ahorro == null) {
                continue;
            }
            if (userId.equals(ahorro.getIdUsuario())) {
                filtered.add(ahorro);
            }
        }
        return filtered;
    }

    private String buildFinancialContext(String query, List<GastoDto> gastos, List<IngresoDto> ingresos, List<AhorroDto> ahorros) {
        BigDecimal totalGastos = sumGastos(gastos);
        BigDecimal totalIngresos = sumIngresos(ingresos);
        BigDecimal totalAhorros = sumAhorros(ahorros);

        BigDecimal balance = totalIngresos.subtract(totalGastos);
        BigDecimal tasaAhorro = BigDecimal.ZERO;
        if (totalIngresos.compareTo(BigDecimal.ZERO) > 0) {
            tasaAhorro = totalAhorros.divide(totalIngresos, 4, RoundingMode.HALF_UP)
                    .multiply(new BigDecimal("100"));
        }

        StringBuilder builder = new StringBuilder();
        builder.append("Rol del asistente: Especialista en ciencia de datos, estadística y analítica financiera personal.\n")
                .append("Objetivo: responder de forma clara, accionable y basada en datos.\n")
                .append("Consulta del usuario: ").append(query).append("\n\n")
                .append("Datos agregados del usuario:\n")
                .append("- Total ingresos: ").append(formatMoney(totalIngresos)).append("\n")
                .append("- Total gastos: ").append(formatMoney(totalGastos)).append("\n")
                .append("- Total ahorros: ").append(formatMoney(totalAhorros)).append("\n")
                .append("- Balance (ingresos - gastos): ").append(formatMoney(balance)).append("\n")
                .append("- Tasa de ahorro: ").append(tasaAhorro.setScale(2, RoundingMode.HALF_UP)).append("%\n")
                .append("- Número de registros de ingresos: ").append(ingresos.size()).append("\n")
                .append("- Número de registros de gastos: ").append(gastos.size()).append("\n")
                .append("- Número de registros de ahorros: ").append(ahorros.size()).append("\n\n")
                .append("Instrucciones de respuesta:\n")
                .append("1) Entregar una interpretación del estado financiero actual.\n")
                .append("2) Añadir recomendaciones prácticas de optimización.\n")
                .append("3) Incluir una mini-predicción o escenario probable a corto plazo con base en tendencias observadas.\n")
                .append("4) Si faltan datos para estimaciones robustas, indicarlo y proponer qué capturar.\n")
                .append("5) Mantener tono profesional, claro y en español.\n");

        return builder.toString();
    }

    private void requestAiResponse(String query, String financialContext) {
        String apiKey = BuildConfig.GEMINI_API_KEY;
        if (TextUtils.isEmpty(apiKey) || "YOUR_GEMINI_API_KEY".equals(apiKey)) {
            btnSend.setEnabled(true);
            addMessage("Configura GEMINI_API_KEY en local.properties para habilitar respuestas de IA.", ChatMessage.Sender.IA);
            return;
        }

        String prompt = financialContext + "\nResponde la siguiente pregunta de seguimiento del usuario:\n" + query;
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
                if (TextUtils.isEmpty(text)) {
                    text = "Recibí la solicitud, pero no hubo contenido de respuesta útil.";
                }
                addMessage(text, ChatMessage.Sender.IA);
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

    private BigDecimal sumGastos(List<GastoDto> gastos) {
        BigDecimal total = BigDecimal.ZERO;
        for (GastoDto gasto : gastos) {
            if (gasto != null && gasto.getMontoGasto() != null) {
                total = total.add(gasto.getMontoGasto());
            }
        }
        return total;
    }

    private BigDecimal sumIngresos(List<IngresoDto> ingresos) {
        BigDecimal total = BigDecimal.ZERO;
        for (IngresoDto ingreso : ingresos) {
            if (ingreso != null && ingreso.getMontoIngreso() != null) {
                total = total.add(ingreso.getMontoIngreso());
            }
        }
        return total;
    }

    private BigDecimal sumAhorros(List<AhorroDto> ahorros) {
        BigDecimal total = BigDecimal.ZERO;
        for (AhorroDto ahorro : ahorros) {
            if (ahorro != null && ahorro.getMontoAhorrado() != null) {
                total = total.add(ahorro.getMontoAhorrado());
            }
        }
        return total;
    }

    private String formatMoney(BigDecimal value) {
        return "$" + value.setScale(2, RoundingMode.HALF_UP).toPlainString();
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
            if (candidates == null || candidates.isEmpty()) {
                return null;
            }
            Candidate candidate = candidates.get(0);
            if (candidate.content == null || candidate.content.parts == null || candidate.content.parts.isEmpty()) {
                return null;
            }
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
