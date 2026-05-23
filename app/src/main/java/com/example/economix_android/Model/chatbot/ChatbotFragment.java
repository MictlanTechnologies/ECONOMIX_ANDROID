package com.example.economix_android.Model.chatbot;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.economix_android.R;
import com.example.economix_android.auth.SessionManager;
import com.example.economix_android.network.ApiClient;
import com.example.economix_android.network.dto.ChatbotRequest;
import com.example.economix_android.network.dto.ChatbotResponse;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.Chip;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChatbotFragment extends Fragment {
    private static final int MAX_LEN = 280;
    private final List<ChatMessage> messages = new ArrayList<>();
    private ChatMessageAdapter adapter;
    private EditText input;
    private ProgressBar progressBar;

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
        progressBar = view.findViewById(R.id.progressChat);
        TextView disclaimer = view.findViewById(R.id.tvDisclaimer);
        disclaimer.setText("Orientación educativa, no asesoría financiera profesional.");

        adapter = new ChatMessageAdapter(messages);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerView.setAdapter(adapter);

        addAiMessage("Hola, soy el asistente financiero de ECONOMIX. Puedo ayudarte a entender tus gastos, ingresos, ahorros y presupuestos. ¿Qué quieres revisar hoy?");

        send.setOnClickListener(v -> sendMessage(input.getText().toString().trim()));

        int[] chipIds = {R.id.chipSug1, R.id.chipSug2, R.id.chipSug3, R.id.chipSug4, R.id.chipSug5};
        for (int id : chipIds) {
            Chip chip = view.findViewById(id);
            chip.setOnClickListener(v -> sendMessage(chip.getText().toString()));
        }
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

        ApiClient.getChatbotApi().enviarMensajeChatbot(new ChatbotRequest(userId, text, "ANDROID_APP")).enqueue(new Callback<ChatbotResponse>() {
            @Override
            public void onResponse(@NonNull Call<ChatbotResponse> call, @NonNull Response<ChatbotResponse> response) {
                setLoading(false);
                if (!response.isSuccessful() || response.body() == null) {
                    addAiMessage("No pude conectar con el asistente en este momento. Intenta de nuevo más tarde.");
                    return;
                }
                ChatbotResponse body = response.body();
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
                addAiMessage(sb.toString());
            }

            @Override
            public void onFailure(@NonNull Call<ChatbotResponse> call, @NonNull Throwable t) {
                setLoading(false);
                addAiMessage("No pude conectar con el asistente en este momento. Intenta de nuevo más tarde.");
            }
        });
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
        progressBar.setVisibility(loading ? View.VISIBLE : View.GONE);
    }
}
