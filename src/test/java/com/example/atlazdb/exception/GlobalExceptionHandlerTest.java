package com.example.atlazdb.exception;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import com.example.atlazdb.dto.CityRequestDTO;
import com.example.atlazdb.dto.ErrorResponseDTO;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void shouldMapNotFoundTo404() {
        ResponseEntity<ErrorResponseDTO> response = handler.handleNotFound(new ResourceNotFoundException("City not found"));

        assertError(response, HttpStatus.NOT_FOUND, "City not found");
    }

    @Test
    void shouldMapBusinessRuleTo422() {
        ResponseEntity<ErrorResponseDTO> response = handler.handleBusinessRule(new BusinessRuleException("Not allowed"));

        assertError(response, HttpStatus.UNPROCESSABLE_ENTITY, "Not allowed");
    }

    @Test
    void shouldMapInvalidCredentialsTo401() {
        ResponseEntity<ErrorResponseDTO> response =
                handler.handleInvalidCredentials(new InvalidCredentialsException("Credenciais inválidas"));

        assertError(response, HttpStatus.UNAUTHORIZED, "Credenciais inválidas");
    }

    @Test
    void shouldMapDataIntegrityViolationTo409() {
        assertError(handler.handleDataIntegrity(), HttpStatus.CONFLICT,
                "Operation violates a data integrity constraint");
    }

    @Test
    void shouldMapMalformedRequestsTo400() {
        assertError(handler.handleBadRequest(), HttpStatus.BAD_REQUEST, "Invalid request parameters or body");
    }

    @Test
    void shouldListInvalidFieldsSortedByName() throws NoSuchMethodException {
        BeanPropertyBindingResult result = new BeanPropertyBindingResult(new CityRequestDTO(), "city");
        result.addError(new FieldError("city", "uf", "size must be between 2 and 2"));
        result.addError(new FieldError("city", "name", "must not be blank"));
        MethodParameter parameter = new MethodParameter(
                CityRequestDTO.class.getMethod("setName", String.class), 0);

        ResponseEntity<ErrorResponseDTO> response =
                handler.handleValidation(new MethodArgumentNotValidException(parameter, result));

        assertError(response, HttpStatus.BAD_REQUEST, "name: must not be blank; uf: size must be between 2 and 2");
    }

    private static void assertError(ResponseEntity<ErrorResponseDTO> response, HttpStatus status, String message) {
        assertThat(response.getStatusCode()).isEqualTo(status);
        assertThat(response.getBody()).isEqualTo(new ErrorResponseDTO(status.value(), status.getReasonPhrase(), message));
    }
}
