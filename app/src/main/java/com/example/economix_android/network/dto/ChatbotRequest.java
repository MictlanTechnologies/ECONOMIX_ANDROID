package com.example.economix_android.network.dto;

public class ChatbotRequest {
    private Integer idUsuario;
    private String mensaje;
    private String contextoOpcional;

    public ChatbotRequest(Integer idUsuario, String mensaje, String contextoOpcional) {
        this.idUsuario = idUsuario;
        this.mensaje = mensaje;
        this.contextoOpcional = contextoOpcional;
    }

    public Integer getIdUsuario() { return idUsuario; }
    public String getMensaje() { return mensaje; }
    public String getContextoOpcional() { return contextoOpcional; }
}
