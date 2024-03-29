package br.com.desafio.domain.usecase;

import br.com.desafio.domain.exception.ResourceNotFoundException;
import br.com.desafio.domain.model.PaymentItemModel;
import br.com.desafio.domain.model.PaymentModel;
import br.com.desafio.infraestructure.data.BillingEntity;
import br.com.desafio.infraestructure.data.PaymentStatus;
import br.com.desafio.infraestructure.dto.MessageSenderDto;
import br.com.desafio.infraestructure.messaging.MessageSender;
import br.com.desafio.infraestructure.persistence.BillingRepository;
import br.com.desafio.infraestructure.persistence.SellerRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import java.math.BigDecimal;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Slf4j
@Component
public class ConfirmPaymentUseCaseImpl implements ConfirmPaymentUseCase {

  private final SellerRepository sellerRepository;
  private final BillingRepository billingRepository;

  private final MessageSender paymentMessageSender;

  @Override
  public PaymentModel confirm(PaymentModel paymentModel) {

    // validations
    makeValidations(paymentModel);

    // process payment items
    paymentModel.getPayments().parallelStream().forEach(this::processPaymentItem);

    return paymentModel;
  }

  private void makeValidations(PaymentModel paymentModel) {
    // check if seller exists or throw an exception if not found
    sellerRepository
        .findById(paymentModel.getSellerId())
        .orElseThrow(() -> new ResourceNotFoundException("Vendedor não encontrado"));

    // check if all payment ids exists or throw an exception if any not found
    paymentModel
        .getPayments()
        .forEach(
            paymentItemModel -> {
              // get billing by id or throw an exception if not found
              billingRepository
                  .findById(paymentItemModel.getPaymentId())
                  .orElseThrow(
                      () ->
                          new ResourceNotFoundException(
                              "Cobrança de código: "
                                  + paymentItemModel.getPaymentId()
                                  + " não encontrada"));
            });
  }

  private void processPaymentItem(PaymentItemModel paymentItem) {
    final String paymentId = paymentItem.getPaymentId();

    final BillingEntity billing = billingRepository.findById(paymentId).orElseThrow();

    // determine paymentItem status
    final PaymentStatus paymentStatus =
        determinePaymentStatus(paymentItem.getPaymentValue(), billing.getValue());

    paymentItem.setPaymentStatus(paymentStatus);

    // send to SQS
    sendToProcessPaymentQueue(paymentItem, paymentStatus);

    log.info("M=processPaymentItem, Payment with id: {} confirmed", paymentId);
  }

  private void sendToProcessPaymentQueue(
      PaymentItemModel paymentItem, PaymentStatus paymentStatus) {
    try {
      paymentMessageSender.sendMessage(
          MessageSenderDto.builder()
              .routingKey(paymentStatus.name().toLowerCase())
              .message(paymentItem.toJson())
              .build());
    } catch (JsonProcessingException e) {
      log.error(e.getMessage(), e);
    }
  }

  private PaymentStatus determinePaymentStatus(
      final BigDecimal paymentValue, final BigDecimal billingValue) {
    return paymentValue.compareTo(billingValue) < 0
        ? PaymentStatus.PARCIAL
        : paymentValue.compareTo(billingValue) > 0 ? PaymentStatus.EXCESS : PaymentStatus.TOTAL;
  }
}
