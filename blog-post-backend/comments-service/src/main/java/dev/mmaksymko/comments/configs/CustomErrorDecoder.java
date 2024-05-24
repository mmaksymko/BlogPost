package dev.mmaksymko.comments.configs;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.mmaksymko.comments.dto.ErrorResponse;
import feign.Response;
import feign.codec.ErrorDecoder;
import org.springframework.http.HttpStatus;

import java.io.IOException;
import java.util.NoSuchElementException;

public class CustomErrorDecoder implements ErrorDecoder {

    @Override
    public Exception decode(String methodKey, Response response) {
        try {
            String url = response.request().url();
            HttpStatus status = HttpStatus.valueOf(response.status());
            Response.Body body = response.body();

            String error = new ObjectMapper().readValue(body.asInputStream(), ErrorResponse.class).error() + " at " + url;

            if (status.is5xxServerError()) {
                return new Exception("Server error: " + error);
            } else {
                return new NoSuchElementException("Client error: " + error);
            }
        } catch (IOException e) {
            return new RuntimeException(e);
        }
    }
}