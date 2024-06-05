package dev.mmaksymko.reactions.configs;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.mmaksymko.reactions.dto.ErrorResponse;
import dev.mmaksymko.reactions.configs.exceptions.ExternalApiClientException;
import dev.mmaksymko.reactions.configs.exceptions.ExternalApiServerException;
import feign.Response;
import feign.codec.ErrorDecoder;
import org.springframework.http.HttpStatus;

import java.io.IOException;

public class ExternalApiErrorDecoder implements ErrorDecoder {
    private final ObjectMapper objectMapper;

    public ExternalApiErrorDecoder(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public Exception decode(String methodKey, Response response) {
        String url = response.request().url();
        HttpStatus status = HttpStatus.valueOf(response.status());
        Response.Body body = response.body();

        String errorMessage = getErrorMessage(url, status, body);
        if (status.is5xxServerError()) {
            return new ExternalApiServerException("Server error: " + errorMessage);
        } else {
            return new ExternalApiClientException("Client error: " + errorMessage);
        }
    }

    private String getErrorMessage(String url, HttpStatus status, Response.Body body) {
        String error;
        try {
            error = objectMapper.readValue(body.asInputStream(), ErrorResponse.class).error();
        } catch (IOException e) {
            error = status.name();
        }

        return error + " at " + url;
    }
}