package br.com.desafio.controller.request;

import br.com.desafio.domain.model.PaymentModel;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Payment {

  @NotBlank(message = "Código do vendedor é obrigatório")
  @JsonProperty("client_id")
  private String clientId;

  @NotEmpty(message = "Nenhum pagamento informado")
  @JsonProperty("payment_items")
  @Valid
  private List<PaymentItem> paymentItems;

  public static Payment fromPaymentModel(PaymentModel model) {
    return Payment.builder()
        .clientId(model.getSellerId())
        .paymentItems(
            model.getPayments().stream()
                .map(PaymentItem::fromPaymentItemModel)
                .collect(Collectors.toList()))
        .build();
  }
}
