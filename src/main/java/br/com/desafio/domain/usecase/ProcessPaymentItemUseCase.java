package br.com.desafio.domain.usecase;

import br.com.desafio.domain.model.PaymentItemModel;

public interface ProcessPaymentItemUseCase {

  void process(PaymentItemModel paymentItemModel);
}
