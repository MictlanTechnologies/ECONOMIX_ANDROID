package com.example.economix_android.auth;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.InputType;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.example.economix_android.Model.data.DataRepository;
import com.example.economix_android.R;
import com.example.economix_android.Vista.menu;
import com.example.economix_android.databinding.FragmentInicioSesionBinding;
import com.example.economix_android.network.ApiClient;
import com.example.economix_android.network.ServerConnectionChecker;
import com.example.economix_android.network.ServerUrlManager;
import com.example.economix_android.network.auth.dto.LoginRequest;
import com.example.economix_android.network.auth.dto.LoginResponse;
import com.example.economix_android.network.repository.auth.AuthRepository;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class inicio_sesionFragment extends Fragment {

    private FragmentInicioSesionBinding binding;
    private AuthRepository authRepository;
    private SessionManager sessionManager;
    private final ExecutorService networkExecutor = Executors.newSingleThreadExecutor();

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentInicioSesionBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        authRepository = new AuthRepository(requireContext());
        sessionManager = new SessionManager(requireContext());

        binding.btnBack.setOnClickListener(v -> requireActivity()
                .getOnBackPressedDispatcher()
                .onBackPressed());
        binding.btnSignIn.setOnClickListener(v -> iniciarSesion());
        binding.btnServerConfig.setOnClickListener(v -> mostrarDialogoServidor());
    }

    private void iniciarSesion() {
        limpiarErrores();

        String perfil = obtenerTexto(binding.etPerfil);
        String contrasena = obtenerTexto(binding.etPassword);

        boolean hayError = false;

        if (TextUtils.isEmpty(perfil)) {
            binding.tilPerfil.setError(getString(R.string.error_perfil_obligatorio));
            hayError = true;
        }

        if (TextUtils.isEmpty(contrasena)) {
            binding.tilPassword.setError(getString(R.string.error_contrasena_obligatoria));
            hayError = true;
        }

        if (hayError) {
            return;
        }

        binding.btnSignIn.setEnabled(false);
        authRepository.login(new LoginRequest(perfil.trim(), contrasena), new Callback<>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                if (binding != null) {
                    binding.btnSignIn.setEnabled(true);
                }
                if (!isAdded()) {
                    return;
                }

                if (!response.isSuccessful() || response.body() == null) {
                    if (response.code() == 401) {
                        Toast.makeText(requireContext(), getString(R.string.error_credenciales_invalidas), Toast.LENGTH_SHORT).show();
                    } else {
                        mostrarMensajeError("Error HTTP " + response.code() + " en " + ServerUrlManager.getBaseUrl(requireContext()));
                    }
                    return;
                }

                LoginResponse loginResponse = response.body();
                if (loginResponse.isRequires2fa()) {
                    Bundle args = new Bundle();
                    args.putString("challengeId", loginResponse.getChallengeId());
                    args.putString("challengeExpiresAt", loginResponse.getChallengeExpiresAt());
                    NavHostFragment.findNavController(inicio_sesionFragment.this)
                            .navigate(R.id.action_inicio_sesionFragment_to_twoFactorFragment, args);
                    return;
                }

                DataRepository.clearAll();
                sessionManager.saveAuthSession(
                        loginResponse.getAccessToken(),
                        loginResponse.getRefreshToken(),
                        loginResponse.getUserInfo()
                );
                abrirMenu();
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                if (binding != null) {
                    binding.btnSignIn.setEnabled(true);
                }
                if (!isAdded()) {
                    return;
                }
                mostrarMensajeError(buildConnectionError(t, ServerUrlManager.getBaseUrl(requireContext())));
            }
        });
    }

    private void mostrarDialogoServidor() {
        if (!isAdded()) {
            return;
        }

        TextInputLayout inputLayout = new TextInputLayout(requireContext());
        inputLayout.setHint("IP, IP:puerto o URL");

        TextInputEditText inputEditText = new TextInputEditText(requireContext());
        inputEditText.setInputType(InputType.TYPE_CLASS_TEXT);
        inputEditText.setText(ServerUrlManager.getBaseUrl(requireContext()));
        inputEditText.setSelection(inputEditText.getText() != null ? inputEditText.getText().length() : 0);
        inputLayout.addView(inputEditText);

        LinearLayout container = new LinearLayout(requireContext());
        int padding = (int) (20 * getResources().getDisplayMetrics().density);
        container.setPadding(padding, padding / 2, padding, 0);
        container.setOrientation(LinearLayout.VERTICAL);
        container.addView(inputLayout);

        AlertDialog dialog = new AlertDialog.Builder(requireContext())
                .setTitle("Configurar servidor")
                .setMessage("URL actual: " + ServerUrlManager.getBaseUrl(requireContext()))
                .setView(container)
                .setNegativeButton("Cancelar", null)
                .setNeutralButton("Restaurar", null)
                .setPositiveButton("Guardar", null)
                .create();

        dialog.setOnShowListener(d -> {
            dialog.getButton(AlertDialog.BUTTON_NEUTRAL).setOnClickListener(v -> {
                ServerUrlManager.clearCustomUrl(requireContext());
                ApiClient.refreshBaseUrl(requireContext());
                authRepository = new AuthRepository(requireContext());
                inputEditText.setText(ServerUrlManager.getBaseUrl(requireContext()));
                probarConectividadActual("Restaurado valor por defecto");
            });

            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
                inputLayout.setError(null);
                String raw = obtenerTexto(inputEditText);
                String validation = ServerUrlManager.validate(raw);
                if (validation != null) {
                    inputLayout.setError(validation);
                    return;
                }

                String normalized = ServerUrlManager.normalize(raw);
                ServerUrlManager.saveCustomUrl(requireContext(), normalized);
                ApiClient.refreshBaseUrl(requireContext());
                authRepository = new AuthRepository(requireContext());

                probarConectividad(normalized, ok -> {
                    String msg = ok
                            ? "Servidor actualizado: " + normalized
                            : "Guardado, pero no hubo conexión inmediata con " + normalized;
                    Toast.makeText(requireContext(), msg, Toast.LENGTH_LONG).show();
                    dialog.dismiss();
                });
            });
        });

        dialog.show();
    }

    private void probarConectividadActual(String prefijo) {
        String baseUrl = ServerUrlManager.getBaseUrl(requireContext());
        probarConectividad(baseUrl, ok -> {
            String estado = ok ? "Conexión correcta" : "Sin respuesta del servidor";
            Toast.makeText(requireContext(), prefijo + ". " + estado + " usando " + baseUrl, Toast.LENGTH_LONG).show();
        });
    }

    private void probarConectividad(String baseUrl, ConnectivityCallback callback) {
        Handler mainHandler = new Handler(Looper.getMainLooper());
        networkExecutor.execute(() -> {
            ServerConnectionChecker.Result result = ServerConnectionChecker.ping(baseUrl);
            mainHandler.post(() -> {
                if (!isAdded()) {
                    return;
                }
                String detalle = (result.isSuccess() ? "OK" : "Fallo") + " - " + result.getMessage()
                        + " | URL usada: " + result.getUrl();
                Toast.makeText(requireContext(), detalle, Toast.LENGTH_LONG).show();
                callback.onResult(result.isSuccess());
            });
        });
    }

    private void abrirMenu() {
        Intent menuIntent = new Intent(requireContext(), menu.class);
        menuIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(menuIntent);
        requireActivity().finish();
    }

    private void limpiarErrores() {
        binding.tilPerfil.setError(null);
        binding.tilPassword.setError(null);
    }

    private String obtenerTexto(TextInputEditText editText) {
        return editText.getText() != null ? editText.getText().toString().trim() : "";
    }

    private String buildConnectionError(Throwable throwable, String baseUrl) {
        if (throwable instanceof SocketTimeoutException) {
            return "Timeout al conectar con " + baseUrl;
        }
        if (throwable instanceof UnknownHostException) {
            return "Host inaccesible: " + baseUrl;
        }
        if (throwable instanceof IOException) {
            return "Error de red en " + baseUrl + ": " + throwable.getClass().getSimpleName();
        }
        return "Error al conectar con " + baseUrl;
    }

    private void mostrarMensajeError(String message) {
        String texto = message != null ? message : getString(R.string.mensaje_error_servidor);
        Toast.makeText(requireContext(), texto, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        networkExecutor.shutdownNow();
    }

    private interface ConnectivityCallback {
        void onResult(boolean ok);
    }
}
