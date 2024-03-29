package br.com.desafio.domain.model;

import br.com.desafio.controller.request.Payment;
import java.util.List;
import java.util.stream.Collectors;

import br.com.desafio.controller.request.PaymentItem;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PaymentModel {

  private String sellerId;

  private List<PaymentItemModel> payments;

  public static PaymentModel fromRequest(final Payment request) {
    return PaymentModel.builder()
        .sellerId(request.getClientId())
        .payments(
            request.getPaymentItems().stream()
                .map(PaymentItem::toPaymentItemModel)
                .collect(Collectors.toList()))
        .build();
  }
}
