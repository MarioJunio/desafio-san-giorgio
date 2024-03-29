package br.com.desafio.domain.model;

import br.com.desafio.infraestructure.data.PaymentStatus;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PaymentItemModel {
  private String paymentId;
  private BigDecimal paymentValue;
  private PaymentStatus paymentStatus;

  public String toJson() throws JsonProcessingException {
    return new ObjectMapper().writeValueAsString(this);
  }

  public static PaymentItemModel fromJson(String json) throws JsonProcessingException {
    return new ObjectMapper().readValue(json, PaymentItemModel.class);
  }
}
