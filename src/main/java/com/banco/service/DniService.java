package com.banco.service;

import com.banco.dto.DniDTO;
import com.banco.exception.NegocioException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

/** Consume la API externa de consulta de DNI (apiperu.dev). El token nunca llega al navegador. */
@Service
public class DniService {

    @Value("${app.dni.url}")
    private String url;

    @Value("${app.dni.token:}")
    private String token;

    @SuppressWarnings("unchecked")
    public DniDTO consultar(String dni) {
        if (dni == null || !dni.matches("\\d{8}")) {
            throw new NegocioException("El DNI debe tener 8 dígitos");
        }
        if (token == null || token.isBlank() || token.startsWith("PEGUE_AQUI")) {
            throw new NegocioException("Falta configurar app.dni.token en application.properties");
        }

        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(5000);
        factory.setReadTimeout(8000);
        RestTemplate restTemplate = new RestTemplate(factory);

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        headers.setContentType(MediaType.APPLICATION_JSON);
        Map<String, String> body = new HashMap<>();
        body.put("dni", dni);

        try {
            ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.POST,
                    new HttpEntity<>(body, headers), Map.class);
            Map<String, Object> cuerpo = response.getBody();
            Map<String, Object> data = cuerpo == null ? null : (Map<String, Object>) cuerpo.get("data");
            if (data == null) {
                throw new NegocioException("No se encontraron datos para el DNI " + dni);
            }
            String nombres = texto(data.get("nombres"));
            String paterno = texto(data.get("apellido_paterno"));
            String materno = texto(data.get("apellido_materno"));

            DniDTO dto = new DniDTO();
            dto.setDni(dni);
            dto.setNombres(nombres);
            dto.setApellidoPaterno(paterno);
            dto.setApellidoMaterno(materno);
            dto.setNombreCompleto((nombres + " " + paterno + " " + materno).trim().replaceAll("\\s+", " ").toUpperCase());
            return dto;
        } catch (HttpStatusCodeException e) {
            throw new NegocioException("La API de DNI respondió con error " + e.getStatusCode().value()
                    + " (revise el token o el DNI)");
        } catch (RestClientException e) {
            throw new NegocioException("No se pudo conectar con la API de DNI");
        }
    }

    private String texto(Object o) {
        return o == null ? "" : String.valueOf(o).trim();
    }
}
