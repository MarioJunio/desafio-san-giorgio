package br.com.desafio.controller.request;

import br.com.desafio.domain.model.PaymentItemModel;
import br.com.desafio.infraestructure.data.PaymentStatus;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PaymentItem {

  @NotBlank(message = "Código da cobrança é obrigatório")
  @JsonProperty("payment_id")
  private String paymentId;

  @NotNull(message = "Valor do pagamento é obrigatório")
  @Min(value = 0, message = "Valor do pagamento inválido")
  @JsonProperty("payment_value")
  private BigDecimal paymentValue;

  @JsonProperty("payment_status")
  private PaymentStatus paymentStatus;

  public PaymentItemModel toPaymentItemModel() {
    return PaymentItemModel.builder()
        .paymentId(paymentId)
        .paymentValue(paymentValue)
        .paymentStatus(paymentStatus)
        .build();
  }

  public static PaymentItem fromPaymentItemModel(final PaymentItemModel paymentItemModel) {
    return PaymentItem.builder()
        .paymentId(paymentItemModel.getPaymentId())
        .paymentValue(paymentItemModel.getPaymentValue())
        .paymentStatus(paymentItemModel.getPaymentStatus())
        .build();
  }
}
