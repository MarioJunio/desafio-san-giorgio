package br.com.desafio.domain.usecase;

import br.com.desafio.controller.request.Payment;
import br.com.desafio.domain.exception.ResourceNotFoundException;
import br.com.desafio.domain.model.PaymentItemModel;
import br.com.desafio.infraestructure.data.BillingEntity;
import br.com.desafio.infraestructure.data.PaymentEntity;
import br.com.desafio.infraestructure.persistence.BillingRepository;
import br.com.desafio.infraestructure.persistence.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProcessPaymentItemUseCaseImpl implements ProcessPaymentItemUseCase {

  private final PaymentRepository paymentRepository;
  private final BillingRepository billingRepository;

  @Override
  public void process(PaymentItemModel paymentItemModel) {
    final BillingEntity billing =
        billingRepository
            .findById(paymentItemModel.getPaymentId())
            .orElseThrow(
                () ->
                    new ResourceNotFoundException(
                        "Cobrança de id: " + paymentItemModel.getPaymentId() + " não encontrada"));

    final PaymentEntity payment =
        paymentRepository
            .findByBillingId(paymentItemModel.getPaymentId())
            .orElse(PaymentEntity.builder().billing(billing).build());

    payment.setValue(paymentItemModel.getPaymentValue());
    payment.setStatus(paymentItemModel.getPaymentStatus());

    paymentRepository.save(payment);
  }
}
