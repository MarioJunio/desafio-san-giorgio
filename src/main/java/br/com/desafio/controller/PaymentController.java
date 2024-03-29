package br.com.desafio.controller;

import br.com.desafio.controller.request.Payment;
import br.com.desafio.domain.model.PaymentModel;
import br.com.desafio.domain.usecase.ConfirmPaymentUseCase;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@Validated
public class PaymentController {

  private final ConfirmPaymentUseCase confirmPaymentUseCase;

  @PutMapping(path = "/api/payment")
  public ResponseEntity<Payment> setPayment(@Valid @RequestBody final Payment request) {

    final PaymentModel paymentModel =
        confirmPaymentUseCase.confirm(PaymentModel.fromRequest(request));

    return ResponseEntity.status(HttpStatus.OK).body(Payment.fromPaymentModel(paymentModel));
  }
}
