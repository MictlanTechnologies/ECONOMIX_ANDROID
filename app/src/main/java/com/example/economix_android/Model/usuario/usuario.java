package com.example.economix_android.Model.usuario;

import android.content.Intent;
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
import com.example.economix_android.Model.data.DataRepository;
import com.example.economix_android.databinding.FragmentUsuarioBinding;
import com.example.economix_android.util.ProfileImageUtils;
import com.airbnb.lottie.LottieComposition;
import com.airbnb.lottie.LottieDrawable;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

public class usuario extends Fragment {

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

        String nombreVisible = SessionManager.getDisplayName(requireContext());
        if (nombreVisible == null || nombreVisible.trim().isEmpty()) {
            nombreVisible = SessionManager.getPerfil(requireContext());
        }
        binding.tvNombre.setText(nombreVisible != null ? nombreVisible : getString(R.string.app_name));

        ProfileImageUtils.applyProfileImage(requireContext(), binding.imgAvatar, R.drawable.usuariog);

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
        binding.lottieUsuario.addLottieOnCompositionLoadedListener(this::reproducirSegmentoDeTiempo);
        binding.lottieUsuario.setFailureListener(error -> binding.lottieUsuario.setVisibility(View.GONE));
    }


    @Override
    public void onResume() {
        super.onResume();
        reproducirAnimacionUsuario();
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
            SessionManager.saveProfilePhoto(requireContext(), uri);
            ProfileImageUtils.applyProfileImage(requireContext(), binding.imgAvatar, R.drawable.usuariog);
        });
    }

    private void seleccionarFotoPerfil() {
        if (seleccionFotoLauncher != null) {
            seleccionFotoLauncher.launch(new String[]{"image/*"});
        }
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
        DataRepository.clearAll();
        SessionManager.clearSession(requireContext());
        Intent intent = new Intent(requireContext(), activity_inicio.class);
        intent.putExtra(activity_inicio.EXTRA_MOSTRAR_LOGIN, true);
        startActivity(intent);
        requireActivity().finish();
    }


    private void reproducirAnimacionUsuario() {
        if (binding == null) {
            return;
        }
        binding.lottieUsuario.setVisibility(View.VISIBLE);
        binding.lottieUsuario.setRepeatCount(0);
        binding.lottieUsuario.setRepeatMode(LottieDrawable.RESTART);
        binding.lottieUsuario.cancelAnimation();
        LottieComposition composition = binding.lottieUsuario.getComposition();
        if (composition != null) {
            reproducirSegmentoDeTiempo(composition);
        }
    }

    private void reproducirSegmentoDeTiempo(@NonNull LottieComposition composition) {
        if (binding == null) {
            return;
        }

        float duracionMs = composition.getDuration();
        float inicioMs = 5000f;
        float finMs = 9000f;

        if (duracionMs <= 0f || inicioMs >= duracionMs) {
            binding.lottieUsuario.setVisibility(View.GONE);
            return;
        }

        float finAjustadoMs = Math.min(finMs, duracionMs);
        if (finAjustadoMs <= inicioMs) {
            binding.lottieUsuario.setVisibility(View.GONE);
            return;
        }

        float frameInicial = composition.getFrameForProgress(inicioMs / duracionMs);
        float frameFinal = composition.getFrameForProgress(finAjustadoMs / duracionMs);

        binding.lottieUsuario.setMinAndMaxFrame(Math.round(frameInicial), Math.round(frameFinal));
        binding.lottieUsuario.setFrame(Math.round(frameInicial));
        binding.lottieUsuario.playAnimation();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (binding != null) {
            binding.lottieUsuario.cancelAnimation();
        }
        binding = null;
    }
}
