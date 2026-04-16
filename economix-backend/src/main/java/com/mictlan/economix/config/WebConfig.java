package com.mictlan.economix.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@Configuration
public class WebConfig {

    @Bean
    public CorsFilter corsFilter() {
        CorsConfiguration config = new CorsConfiguration();
        // CORS: addAllowedOriginPattern("*") + setAllowCredentials(true) es válido en Spring Framework 5.3+.
        // Usar addAllowedOrigin("*") con allowCredentials=true lanzaría IllegalArgumentException porque
        // la spec CORS prohíbe el wildcard literal "*" en Access-Control-Allow-Origin cuando la petición
        // incluye credenciales. Spring resuelve esto con OriginPatterns: refleja el origen concreto de
        // cada petición en lugar de devolver "*", cumpliendo la spec sin necesidad de listar dominios.
        // Para apps Android nativas las restricciones CORS no aplican (sólo en navegadores), pero esta
        // configuración también cubre Postman, Swagger UI y herramientas web de desarrollo.
        config.addAllowedOriginPattern("*");
        config.addAllowedHeader("*");
        config.addAllowedMethod("*");
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return new CorsFilter(source);
    }
}
