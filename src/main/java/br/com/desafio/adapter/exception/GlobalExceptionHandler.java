package br.com.desafio.adapter.exception;

import br.com.desafio.domain.exception.ResourceNotFoundException;
import br.com.desafio.domain.model.ApiError;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<List<ApiError>> handleValidationExceptions(MethodArgumentNotValidException ex) {
    final List<ApiError> apiErrors =
        ex.getBindingResult().getFieldErrors().stream()
            .map(
                fieldError ->
                    ApiError.builder()
                        .field(fieldError.getField())
                        .message(fieldError.getDefaultMessage())
                        .build())
            .toList();

    return ResponseEntity.badRequest().body(apiErrors);
  }

  @ExceptionHandler(ResourceNotFoundException.class)
  public ResponseEntity<ApiError> handleResourceNotFoundException(ResourceNotFoundException ex) {
    return ResponseEntity.status(HttpStatus.NOT_FOUND)
        .body(ApiError.builder().message(ex.getMessage()).build());
  }
}
