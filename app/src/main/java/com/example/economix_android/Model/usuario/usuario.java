package com.example.economix_android.Model.usuario;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import androidx.navigation.Navigation;

import com.example.economix_android.R;
import com.example.economix_android.activity_inicio;
import com.example.economix_android.auth.SessionManager;
import com.example.economix_android.databinding.FragmentUsuarioBinding;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

public class usuario extends Fragment {

    private static final String PREFS_USUARIO = "usuario_prefs";
    private static final String KEY_FOTO_URI = "foto_perfil_uri";

    private FragmentUsuarioBinding binding;
    private ActivityResultLauncher<String[]> seleccionFotoLauncher;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentUsuarioBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        String perfil = SessionManager.getPerfil(requireContext());
        binding.tvNombre.setText(perfil != null ? perfil : getString(R.string.app_name));

        cargarFotoPerfil();

        binding.avatarContainer.setOnClickListener(v -> seleccionarFotoPerfil());
        binding.imgAvatar.setOnClickListener(v -> seleccionarFotoPerfil());

        binding.btnInfo.setOnClickListener(v ->
                Navigation.findNavController(v)
                        .navigate(R.id.usuario_info));
        binding.btnAyudaUs.setOnClickListener(v -> mostrarAyuda());
        binding.btnGuardar.setOnClickListener(v -> cerrarSesion());

        View.OnClickListener bottomNavListener = v -> {
            int viewId = v.getId();
            if (viewId == R.id.navGastos) {
                navigateSafely(v, R.id.navigation_gastos);
            } else if (viewId == R.id.navIngresos) {
                navigateSafely(v, R.id.navigation_ingresos);
            } else if (viewId == R.id.navAhorro) {
                navigateSafely(v, R.id.navigation_ahorro);
            } else if (viewId == R.id.navGraficas) {
                navigateSafely(v, R.id.navigation_graficas);
            }
        };

        binding.navGastos.setOnClickListener(bottomNavListener);
        binding.navIngresos.setOnClickListener(bottomNavListener);
        binding.navAhorro.setOnClickListener(bottomNavListener);
        binding.navGraficas.setOnClickListener(bottomNavListener);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        seleccionFotoLauncher = registerForActivityResult(new ActivityResultContracts.OpenDocument(), uri -> {
            if (uri == null) {
                return;
            }
            requireContext().getContentResolver().takePersistableUriPermission(
                    uri, Intent.FLAG_GRANT_READ_URI_PERMISSION);
            guardarFotoPerfil(uri);
            binding.imgAvatar.setImageURI(uri);
        });
    }

    private void seleccionarFotoPerfil() {
        if (seleccionFotoLauncher != null) {
            seleccionFotoLauncher.launch(new String[]{"image/*"});
        }
    }

    private void cargarFotoPerfil() {
        SharedPreferences prefs = requireContext().getSharedPreferences(PREFS_USUARIO, android.content.Context.MODE_PRIVATE);
        String uriString = prefs.getString(KEY_FOTO_URI, null);
        if (uriString != null && !uriString.isEmpty()) {
            binding.imgAvatar.setImageURI(Uri.parse(uriString));
        }
    }

    private void guardarFotoPerfil(Uri uri) {
        if (uri == null) {
            return;
        }
        SharedPreferences prefs = requireContext().getSharedPreferences(PREFS_USUARIO, android.content.Context.MODE_PRIVATE);
        prefs.edit().putString(KEY_FOTO_URI, uri.toString()).apply();
    }

    private void navigateSafely(View view, int destinationId) {
        NavController navController = Navigation.findNavController(view);
        NavDestination currentDestination = navController.getCurrentDestination();
        if (currentDestination == null || currentDestination.getId() != destinationId) {
            navController.navigate(destinationId);
        }
    }

    private void mostrarAyuda() {
        new MaterialAlertDialogBuilder(requireContext())
                .setTitle(R.string.titulo_ayuda_usuario)
                .setMessage(R.string.mensaje_ayuda_usuario)
                .setPositiveButton(android.R.string.ok, null)
                .show();
    }

    private void cerrarSesion() {
        SessionManager.clearSession(requireContext());
        Intent intent = new Intent(requireContext(), activity_inicio.class);
        intent.putExtra(activity_inicio.EXTRA_MOSTRAR_LOGIN, true);
        startActivity(intent);
        requireActivity().finish();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
