package com.example.economix_android.Model.usuario;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import androidx.navigation.Navigation;

import com.example.economix_android.R;
import com.example.economix_android.auth.SessionManager;
import com.example.economix_android.databinding.FragmentUsuarioInfoBinding;
import com.example.economix_android.network.dto.ContactoDto;
import com.example.economix_android.network.dto.DomicilioDto;
import com.example.economix_android.network.dto.PersonaDto;
import com.example.economix_android.network.dto.UsuarioDto;
import com.example.economix_android.network.repository.ContactoRepository;
import com.example.economix_android.network.repository.DomicilioRepository;
import com.example.economix_android.network.repository.PersonaRepository;
import com.example.economix_android.network.repository.UsuarioRepository;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class usuario_info extends Fragment {

    private FragmentUsuarioInfoBinding binding;
    private final PersonaRepository personaRepository = new PersonaRepository();
    private final ContactoRepository contactoRepository = new ContactoRepository();
    private final DomicilioRepository domicilioRepository = new DomicilioRepository();
    private final UsuarioRepository usuarioRepository = new UsuarioRepository();
    private PersonaDto personaActual;
    private ContactoDto contactoActual;
    private DomicilioDto domicilioActual;
    private UsuarioDto usuarioActual;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentUsuarioInfoBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.btnRegresar.setOnClickListener(v ->
                Navigation.findNavController(v)
                        .navigate(R.id.usuario));
        binding.btnAyudaUsInf.setOnClickListener(v -> mostrarAyuda());
        binding.btnGuardar.setOnClickListener(v -> guardarCambios());
        binding.btnEliminar.setOnClickListener(v -> eliminarPersona());
        binding.btnLimpiar.setOnClickListener(v -> limpiarFormulario());

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

        cargarPersona();
        cargarUsuario();
    }

    private void cargarPersona() {
        Integer userId = SessionManager.getUserId(requireContext());
        if (userId == null) {
            mostrarMensaje(getString(R.string.error_usuario_no_autenticado));
            return;
        }
        personaRepository.obtenerPersonas(new Callback<List<PersonaDto>>() {
            @Override
            public void onResponse(Call<List<PersonaDto>> call, Response<List<PersonaDto>> response) {
                if (!isAdded()) {
                    return;
                }
                if (!response.isSuccessful()) {
                    mostrarMensaje(getString(R.string.error_persona_no_encontrada));
                    return;
                }
                personaActual = null;
                List<PersonaDto> personas = response.body();
                if (personas != null) {
                    for (PersonaDto persona : personas) {
                        if (userId.equals(persona.getIdUsuario())) {
                            personaActual = persona;
                            break;
                        }
                    }
                }
                if (personaActual != null) {
                    binding.etNombre.setText(personaActual.getNombrePersona());
                    String apellidos = construirApellidos(personaActual.getApellidoP(), personaActual.getApellidoM());
                    binding.etApellidos.setText(apellidos);
                    cargarContacto(personaActual.getIdPersona());
                    cargarDomicilio(personaActual.getIdPersona());
                } else {
                    limpiarFormulario();
                }
            }

            @Override
            public void onFailure(Call<List<PersonaDto>> call, Throwable t) {
                if (!isAdded()) {
                    return;
                }
                mostrarMensaje(getString(R.string.mensaje_error_servidor));
            }
        });
    }

    private void cargarContacto(Integer personaId) {
        contactoRepository.obtenerContactos(new Callback<List<ContactoDto>>() {
            @Override
            public void onResponse(Call<List<ContactoDto>> call, Response<List<ContactoDto>> response) {
                if (!isAdded()) {
                    return;
                }
                if (response.isSuccessful()) {
                    contactoActual = null;
                    List<ContactoDto> contactos = response.body();
                    if (contactos != null) {
                        for (ContactoDto contacto : contactos) {
                            if (personaId.equals(contacto.getIdPersona())) {
                                contactoActual = contacto;
                                break;
                            }
                        }
                    }
                    if (contactoActual != null) {
                        String contactoTexto = !TextUtils.isEmpty(contactoActual.getCorreoAlterno())
                                ? contactoActual.getCorreoAlterno()
                                : contactoActual.getNumCelular();
                        binding.etContacto.setText(contactoTexto);
                    }
                }
            }

            @Override
            public void onFailure(Call<List<ContactoDto>> call, Throwable t) {
                if (!isAdded()) {
                    return;
                }
                mostrarMensaje(getString(R.string.mensaje_error_servidor));
            }
        });
    }

    private void cargarDomicilio(Integer personaId) {
        domicilioRepository.obtenerDomicilios(new Callback<List<DomicilioDto>>() {
            @Override
            public void onResponse(Call<List<DomicilioDto>> call, Response<List<DomicilioDto>> response) {
                if (!isAdded()) {
                    return;
                }
                if (response.isSuccessful()) {
                    domicilioActual = null;
                    List<DomicilioDto> domicilios = response.body();
                    if (domicilios != null) {
                        for (DomicilioDto domicilio : domicilios) {
                            if (personaId.equals(domicilio.getIdPersona())) {
                                domicilioActual = domicilio;
                                break;
                            }
                        }
                    }
                    if (domicilioActual != null) {
                        binding.etDomicilio.setText(domicilioActual.getCalle());
                    }
                }
            }

            @Override
            public void onFailure(Call<List<DomicilioDto>> call, Throwable t) {
                if (!isAdded()) {
                    return;
                }
                mostrarMensaje(getString(R.string.mensaje_error_servidor));
            }
        });
    }

    private void cargarUsuario() {
        Integer userId = SessionManager.getUserId(requireContext());
        if (userId == null) {
            return;
        }
        usuarioRepository.obtenerUsuario(userId, new Callback<UsuarioDto>() {
            @Override
            public void onResponse(Call<UsuarioDto> call, Response<UsuarioDto> response) {
                if (!isAdded()) {
                    return;
                }
                if (response.isSuccessful() && response.body() != null) {
                    usuarioActual = response.body();
                    String perfil = usuarioActual.getPerfilUsuario();
                    if (!TextUtils.isEmpty(perfil)) {
                        binding.etPerfilUsuario.setText(perfil);
                    }
                }
            }

            @Override
            public void onFailure(Call<UsuarioDto> call, Throwable t) {
                if (!isAdded()) {
                    return;
                }
                mostrarMensaje(getString(R.string.mensaje_error_servidor));
            }
        });
    }

    private void guardarCambios() {
        guardarUsuario();
        guardarPersona();
    }

    private void guardarPersona() {
        String nombre = obtenerTexto(binding.etNombre);
        String apellidos = obtenerTexto(binding.etApellidos);
        if (TextUtils.isEmpty(nombre)
                && TextUtils.isEmpty(apellidos)
                && personaActual == null
                && TextUtils.isEmpty(obtenerTexto(binding.etContacto))
                && TextUtils.isEmpty(obtenerTexto(binding.etDomicilio))) {
            return;
        }
        if (TextUtils.isEmpty(nombre) || TextUtils.isEmpty(apellidos)) {
            mostrarMensaje(getString(R.string.error_persona_campos_obligatorios));
            return;
        }
        Integer userId = SessionManager.getUserId(requireContext());
        if (userId == null) {
            mostrarMensaje(getString(R.string.error_usuario_no_autenticado));
            return;
        }
        String[] partes = separarApellidos(apellidos);
        PersonaDto dto = PersonaDto.builder()
                .idPersona(personaActual != null ? personaActual.getIdPersona() : null)
                .nombrePersona(nombre)
                .apellidoP(partes[0])
                .apellidoM(partes[1])
                .idUsuario(userId)
                .build();

        setBotonesEnabled(false);
        if (personaActual == null) {
            personaRepository.crearPersona(dto, personaCallback());
        } else {
            personaRepository.actualizarPersona(personaActual.getIdPersona(), dto, personaCallback());
        }
    }

    private void guardarUsuario() {
        Integer userId = SessionManager.getUserId(requireContext());
        if (userId == null) {
            mostrarMensaje(getString(R.string.error_usuario_no_autenticado));
            return;
        }
        String nuevoPerfil = obtenerTexto(binding.etPerfilUsuario);
        String nuevaContrasena = obtenerTexto(binding.etContrasenaNueva);
        if (TextUtils.isEmpty(nuevoPerfil) && TextUtils.isEmpty(nuevaContrasena)) {
            return;
        }
        String perfilActual = usuarioActual != null ? usuarioActual.getPerfilUsuario() : SessionManager.getPerfil(requireContext());
        UsuarioDto dto = UsuarioDto.builder()
                .idUsuario(userId)
                .perfilUsuario(TextUtils.isEmpty(nuevoPerfil) ? perfilActual : nuevoPerfil)
                .contrasenaUsuario(TextUtils.isEmpty(nuevaContrasena) ? null : nuevaContrasena)
                .build();

        usuarioRepository.actualizarUsuario(userId, dto, new Callback<UsuarioDto>() {
            @Override
            public void onResponse(Call<UsuarioDto> call, Response<UsuarioDto> response) {
                if (!isAdded()) {
                    return;
                }
                if (response.isSuccessful() && response.body() != null) {
                    usuarioActual = response.body();
                    if (!TextUtils.isEmpty(nuevoPerfil)) {
                        SessionManager.saveSession(requireContext(), usuarioActual);
                    }
                    binding.etContrasenaNueva.setText("");
                    mostrarMensaje(getString(R.string.mensaje_usuario_actualizado));
                } else {
                    mostrarMensaje(getString(R.string.mensaje_error_operacion));
                }
            }

            @Override
            public void onFailure(Call<UsuarioDto> call, Throwable t) {
                if (!isAdded()) {
                    return;
                }
                mostrarMensaje(getString(R.string.mensaje_error_servidor));
            }
        });
    }

    private Callback<PersonaDto> personaCallback() {
        return new Callback<PersonaDto>() {
            @Override
            public void onResponse(Call<PersonaDto> call, Response<PersonaDto> response) {
                if (!isAdded()) {
                    return;
                }
                setBotonesEnabled(true);
                if (response.isSuccessful() && response.body() != null) {
                    personaActual = response.body();
                    actualizarContactoYDomicilio();
                    mostrarMensaje(getString(R.string.mensaje_persona_guardada));
                } else {
                    mostrarMensaje(getString(R.string.mensaje_error_operacion));
                }
            }

            @Override
            public void onFailure(Call<PersonaDto> call, Throwable t) {
                if (!isAdded()) {
                    return;
                }
                setBotonesEnabled(true);
                mostrarMensaje(getString(R.string.mensaje_error_servidor));
            }
        };
    }

    private void actualizarContactoYDomicilio() {
        if (personaActual == null || personaActual.getIdPersona() == null) {
            return;
        }
        String contactoTexto = obtenerTexto(binding.etContacto);
        if (TextUtils.isEmpty(contactoTexto)) {
            eliminarContactoSiExiste();
        } else {
            ContactoDto dto = construirContacto(personaActual.getIdPersona(), contactoTexto);
            if (contactoActual == null) {
                contactoRepository.crearContacto(dto, contactoCallback(true));
            } else {
                contactoRepository.actualizarContacto(contactoActual.getIdContactos(), dto, contactoCallback(false));
            }
        }

        String domicilioTexto = obtenerTexto(binding.etDomicilio);
        if (TextUtils.isEmpty(domicilioTexto)) {
            eliminarDomicilioSiExiste();
        } else {
            DomicilioDto dto = DomicilioDto.builder()
                    .idDomicilio(domicilioActual != null ? domicilioActual.getIdDomicilio() : null)
                    .calle(domicilioTexto)
                    .idPersona(personaActual.getIdPersona())
                    .build();
            if (domicilioActual == null) {
                domicilioRepository.crearDomicilio(dto, domicilioCallback(true));
            } else {
                domicilioRepository.actualizarDomicilio(domicilioActual.getIdDomicilio(), dto, domicilioCallback(false));
            }
        }
    }

    private Callback<ContactoDto> contactoCallback(boolean crear) {
        return new Callback<ContactoDto>() {
            @Override
            public void onResponse(Call<ContactoDto> call, Response<ContactoDto> response) {
                if (!isAdded()) {
                    return;
                }
                if (response.isSuccessful() && response.body() != null) {
                    contactoActual = response.body();
                    if (crear) {
                        mostrarMensaje(getString(R.string.mensaje_contacto_guardado));
                    }
                } else {
                    mostrarMensaje(getString(R.string.mensaje_error_operacion));
                }
            }

            @Override
            public void onFailure(Call<ContactoDto> call, Throwable t) {
                if (!isAdded()) {
                    return;
                }
                mostrarMensaje(getString(R.string.mensaje_error_servidor));
            }
        };
    }

    private Callback<DomicilioDto> domicilioCallback(boolean crear) {
        return new Callback<DomicilioDto>() {
            @Override
            public void onResponse(Call<DomicilioDto> call, Response<DomicilioDto> response) {
                if (!isAdded()) {
                    return;
                }
                if (response.isSuccessful() && response.body() != null) {
                    domicilioActual = response.body();
                    if (crear) {
                        mostrarMensaje(getString(R.string.mensaje_domicilio_guardado));
                    }
                } else {
                    mostrarMensaje(getString(R.string.mensaje_error_operacion));
                }
            }

            @Override
            public void onFailure(Call<DomicilioDto> call, Throwable t) {
                if (!isAdded()) {
                    return;
                }
                mostrarMensaje(getString(R.string.mensaje_error_servidor));
            }
        };
    }

    private void eliminarPersona() {
        if (personaActual == null || personaActual.getIdPersona() == null) {
            mostrarMensaje(getString(R.string.error_persona_no_encontrada));
            return;
        }
        setBotonesEnabled(false);
        eliminarContactoSiExiste();
        eliminarDomicilioSiExiste();
        personaRepository.eliminarPersona(personaActual.getIdPersona(), new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (!isAdded()) {
                    return;
                }
                setBotonesEnabled(true);
                if (response.isSuccessful()) {
                    personaActual = null;
                    limpiarFormulario();
                    mostrarMensaje(getString(R.string.mensaje_persona_eliminada));
                } else {
                    mostrarMensaje(getString(R.string.mensaje_error_operacion));
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                if (!isAdded()) {
                    return;
                }
                setBotonesEnabled(true);
                mostrarMensaje(getString(R.string.mensaje_error_servidor));
            }
        });
    }

    private void eliminarContactoSiExiste() {
        if (contactoActual == null || contactoActual.getIdContactos() == null) {
            return;
        }
        contactoRepository.eliminarContacto(contactoActual.getIdContactos(), new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (!isAdded()) {
                    return;
                }
                if (response.isSuccessful()) {
                    contactoActual = null;
                    binding.etContacto.setText("");
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                if (!isAdded()) {
                    return;
                }
                mostrarMensaje(getString(R.string.mensaje_error_servidor));
            }
        });
    }

    private void eliminarDomicilioSiExiste() {
        if (domicilioActual == null || domicilioActual.getIdDomicilio() == null) {
            return;
        }
        domicilioRepository.eliminarDomicilio(domicilioActual.getIdDomicilio(), new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (!isAdded()) {
                    return;
                }
                if (response.isSuccessful()) {
                    domicilioActual = null;
                    binding.etDomicilio.setText("");
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                if (!isAdded()) {
                    return;
                }
                mostrarMensaje(getString(R.string.mensaje_error_servidor));
            }
        });
    }

    private ContactoDto construirContacto(Integer personaId, String valor) {
        ContactoDto.ContactoDtoBuilder builder = ContactoDto.builder()
                .idContactos(contactoActual != null ? contactoActual.getIdContactos() : null)
                .idPersona(personaId);
        if (valor.contains("@")) {
            builder.correoAlterno(valor);
            builder.numCelular(null);
        } else {
            builder.numCelular(valor);
            builder.correoAlterno(null);
        }
        return builder.build();
    }

    private String obtenerTexto(com.google.android.material.textfield.TextInputEditText editText) {
        return editText.getText() != null ? editText.getText().toString().trim() : "";
    }

    private void limpiarFormulario() {
        binding.etNombre.setText("");
        binding.etApellidos.setText("");
        binding.etContacto.setText("");
        binding.etDomicilio.setText("");
        binding.etPerfilUsuario.setText("");
        binding.etContrasenaNueva.setText("");
    }

    private void setBotonesEnabled(boolean enabled) {
        binding.btnGuardar.setEnabled(enabled);
        binding.btnEliminar.setEnabled(enabled);
        binding.btnLimpiar.setEnabled(enabled);
    }

    private String[] separarApellidos(String apellidos) {
        String trimmed = apellidos.trim();
        if (trimmed.isEmpty()) {
            return new String[]{"", ""};
        }
        String[] partes = trimmed.split("\\s+", 2);
        String apellidoP = partes[0];
        String apellidoM = partes.length > 1 ? partes[1] : "";
        return new String[]{apellidoP, apellidoM};
    }

    private String construirApellidos(String apellidoP, String apellidoM) {
        if (TextUtils.isEmpty(apellidoP)) {
            return apellidoM != null ? apellidoM : "";
        }
        if (TextUtils.isEmpty(apellidoM)) {
            return apellidoP;
        }
        return apellidoP + " " + apellidoM;
    }

    private void mostrarMensaje(String mensaje) {
        Toast.makeText(requireContext(), mensaje, Toast.LENGTH_SHORT).show();
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
                .setTitle(R.string.titulo_ayuda_usuario_info)
                .setMessage(R.string.mensaje_ayuda_usuario_info)
                .setPositiveButton(android.R.string.ok, null)
                .show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
