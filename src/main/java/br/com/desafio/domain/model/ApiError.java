package br.com.desafio.domain.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ApiError {
  private String field;
  private String message;
}
