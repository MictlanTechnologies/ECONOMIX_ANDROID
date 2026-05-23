package com.example.economix_android.network.dto;

import java.util.List;

public class ChatbotResponse {
    private String respuesta;
    private List<String> recomendaciones;
    private List<String> alertas;
    private String nivelRiesgoFinanciero;
    private Boolean datosInsuficientes;
    private List<String> accionesSugeridas;
    private String disclaimer;

    public String getRespuesta() { return respuesta; }
    public List<String> getRecomendaciones() { return recomendaciones; }
    public List<String> getAlertas() { return alertas; }
    public String getNivelRiesgoFinanciero() { return nivelRiesgoFinanciero; }
    public Boolean getDatosInsuficientes() { return datosInsuficientes; }
    public List<String> getAccionesSugeridas() { return accionesSugeridas; }
    public String getDisclaimer() { return disclaimer; }
}
