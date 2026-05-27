package com.example.economix_android.Model.chatbot;

import android.os.Bundle;
import android.util.Log;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.example.economix_android.R;
import com.example.economix_android.auth.SessionManager;
import com.example.economix_android.network.ApiClient;
import com.example.economix_android.network.dto.ChatbotRequest;
import com.example.economix_android.network.dto.ChatbotResponse;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.Chip;

import java.io.IOException;
import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import javax.net.ssl.SSLException;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChatbotFragment extends Fragment {
    private static final String TAG = "ECONOMIX_CHATBOT";
    private static final int MAX_LEN = 280;
    private final List<ChatMessage> messages = new ArrayList<>();
    private ChatMessageAdapter adapter;
    private EditText input;
    private LottieAnimationView progressBar;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_chatbot, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        RecyclerView recyclerView = view.findViewById(R.id.recyclerChat);
        input = view.findViewById(R.id.etMensaje);
        MaterialButton send = view.findViewById(R.id.btnEnviar);
        MaterialButton volver = view.findViewById(R.id.btnVolverChat);
        progressBar = view.findViewById(R.id.progressChat);
        TextView disclaimer = view.findViewById(R.id.tvDisclaimer);
        disclaimer.setText("Orientación educativa, no asesoría financiera profesional.");

        adapter = new ChatMessageAdapter(messages);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerView.setAdapter(adapter);

        addAiMessage("Hola, soy el asistente financiero de ECONOMIX. Puedo ayudarte a entender tus gastos, ingresos, ahorros y presupuestos. ¿Qué quieres revisar hoy?");

        send.setOnClickListener(v -> sendMessage(input.getText().toString().trim()));
        volver.setOnClickListener(v -> Navigation.findNavController(v).navigate(R.id.menu));

        int[] chipIds = {R.id.chipSug1, R.id.chipSug2, R.id.chipSug3, R.id.chipSug4, R.id.chipSug5};
        for (int id : chipIds) {
            Chip chip = view.findViewById(id);
            chip.setOnClickListener(v -> sendMessage(chip.getText().toString()));
        }

        ViewCompat.setOnApplyWindowInsetsListener(view, (v, insets) -> {
            Insets ime = insets.getInsets(WindowInsetsCompat.Type.ime());
            Insets bars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(
                    v.getPaddingLeft(),
                    v.getPaddingTop(),
                    v.getPaddingRight(),
                    Math.max(ime.bottom, bars.bottom)
            );
            return insets;
        });
    }

    private void sendMessage(String text) {
        if (TextUtils.isEmpty(text)) {
            input.setError("Escribe una consulta.");
            return;
        }
        if (text.length() > MAX_LEN) {
            input.setError("Máximo " + MAX_LEN + " caracteres.");
            return;
        }
        Integer userId = SessionManager.getUserId(requireContext());
        if (userId == null) {
            Toast.makeText(requireContext(), "No se encontró sesión de usuario.", Toast.LENGTH_SHORT).show();
            return;
        }

        input.setText("");
        addUserMessage(text);
        setLoading(true);

        ChatbotRequest request = new ChatbotRequest(userId, text, "ANDROID_APP");
        Call<ChatbotResponse> requestCall = ApiClient.getChatbotApi().enviarMensajeChatbot(request);
        String endpointUrl = requestCall.request().url().toString();
        Log.i(TAG, "POST " + endpointUrl + " | idUsuario=" + userId + " | mensajeLength=" + text.length() + " | contextoOpcional=ANDROID_APP");

        requestCall.enqueue(new Callback<ChatbotResponse>() {
            @Override
            public void onResponse(@NonNull Call<ChatbotResponse> call, @NonNull Response<ChatbotResponse> response) {
                setLoading(false);
                if (response.isSuccessful()) {
                    ChatbotResponse body = response.body();
                    if (body == null) {
                        Log.w(TAG, "HTTP " + response.code() + " con body nulo en " + endpointUrl);
                        addAiMessage("El servidor respondió vacío. Revisa el backend.");
                        return;
                    }
                    addAiMessage(buildAssistantMessage(body));
                    return;
                }

                String errorBodyText = readErrorBody(response);
                Log.e(TAG, "HTTP ERROR | code=" + response.code() + " | endpoint=" + endpointUrl + " | errorBody=" + errorBodyText);
                addAiMessage("El asistente no pudo responder. Código del servidor: " + response.code() + ".");
            }

            @Override
            public void onFailure(@NonNull Call<ChatbotResponse> call, @NonNull Throwable t) {
                setLoading(false);
                String category = categorizeFailure(t);
                Log.e(TAG, "NETWORK FAILURE | type=" + t.getClass().getSimpleName() + " | category=" + category + " | message=" + t.getMessage() + " | endpoint=" + endpointUrl, t);
                addAiMessage("No pude conectar con el asistente en este momento. Intenta de nuevo más tarde.");
            }
        });
    }


    private String buildAssistantMessage(ChatbotResponse body) {
        StringBuilder sb = new StringBuilder();
        sb.append(body.getRespuesta() != null ? body.getRespuesta() : "Sin respuesta");
        if (body.getNivelRiesgoFinanciero() != null) sb.append("\n\nRiesgo: ").append(body.getNivelRiesgoFinanciero());
        appendList(sb, "Alertas", body.getAlertas());
        appendList(sb, "Recomendaciones", body.getRecomendaciones());
        appendList(sb, "Acciones sugeridas", body.getAccionesSugeridas());
        if (Boolean.TRUE.equals(body.getDatosInsuficientes())) {
            sb.append("\n\nDatos insuficientes: registra más ingresos, gastos o presupuestos para un análisis más preciso.");
        }
        if (!TextUtils.isEmpty(body.getDisclaimer())) {
            sb.append("\n\n").append(body.getDisclaimer());
        }
        return sb.toString();
    }

    private String readErrorBody(Response<ChatbotResponse> response) {
        if (response.errorBody() == null) {
            return "<sin errorBody>";
        }
        try {
            return response.errorBody().string();
        } catch (IOException e) {
            Log.e(TAG, "No se pudo leer errorBody", e);
            return "<error al leer errorBody: " + e.getMessage() + ">";
        }
    }

    private String categorizeFailure(Throwable t) {
        if (t instanceof SocketTimeoutException) return "TIMEOUT";
        if (t instanceof UnknownHostException) return "HOST_UNREACHABLE";
        if (t instanceof ConnectException) return "CONNECTION_REFUSED_OR_UNREACHABLE";
        if (t instanceof SSLException) return "SSL_ERROR";
        return "OTHER_NETWORK_ERROR";
    }

    private void appendList(StringBuilder sb, String title, List<String> values) {
        if (values == null || values.isEmpty()) return;
        sb.append("\n\n").append(title).append(":");
        for (String item : values) sb.append("\n• ").append(item);
    }

    private void addUserMessage(String text) { addMessage(new ChatMessage(ChatMessage.Sender.USER, text)); }
    private void addAiMessage(String text) { addMessage(new ChatMessage(ChatMessage.Sender.AI, text)); }

    private void addMessage(ChatMessage message) {
        messages.add(message);
        adapter.notifyItemInserted(messages.size() - 1);
    }

    private void setLoading(boolean loading) {
        if (loading) {
            progressBar.setVisibility(View.VISIBLE);
            progressBar.playAnimation();
        } else {
            progressBar.cancelAnimation();
            progressBar.setVisibility(View.GONE);
        }
    }
}
