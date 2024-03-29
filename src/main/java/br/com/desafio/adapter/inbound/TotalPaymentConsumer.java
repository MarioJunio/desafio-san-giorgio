package br.com.desafio.adapter.inbound;

import br.com.desafio.domain.model.PaymentItemModel;
import br.com.desafio.domain.usecase.ProcessPaymentItemUseCase;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class TotalPaymentConsumer {

  private final ProcessPaymentItemUseCase processPaymentItemUseCase;

  @RabbitListener(queues = "${totalQueue}")
  public void receiveMessage(String message) {
    try {
      final PaymentItemModel paymentItem = PaymentItemModel.fromJson(message);
      processPaymentItemUseCase.process(paymentItem);

      log.info(
          "M=receiveMessage, status: total, Payment with id: {} processed",
          paymentItem.getPaymentId());
    } catch (JsonProcessingException e) {
      log.error(e.getMessage(), e);
    }
  }
}
