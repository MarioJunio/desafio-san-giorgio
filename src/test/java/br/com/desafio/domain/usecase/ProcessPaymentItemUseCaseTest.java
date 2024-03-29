package br.com.desafio.domain.usecase;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

import br.com.desafio.domain.exception.ResourceNotFoundException;
import br.com.desafio.domain.model.PaymentItemModel;
import br.com.desafio.infraestructure.data.BillingEntity;
import br.com.desafio.infraestructure.data.PaymentEntity;
import br.com.desafio.infraestructure.data.PaymentStatus;
import br.com.desafio.infraestructure.data.SellerEntity;
import br.com.desafio.infraestructure.persistence.BillingRepository;
import br.com.desafio.infraestructure.persistence.PaymentRepository;
import java.math.BigDecimal;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

@SpringBootTest
public class ProcessPaymentItemUseCaseTest {

  @MockBean private PaymentRepository paymentRepository;

  @MockBean private BillingRepository billingRepository;

  @Autowired private ProcessPaymentItemUseCase processPaymentItemUseCase;

  final SellerEntity seller = SellerEntity.builder().id("1").name("Mario").build();

  final BillingEntity billing =
      BillingEntity.builder().id("1").seller(seller).value(BigDecimal.TEN).build();

  final PaymentEntity payment =
      PaymentEntity.builder()
          .id(1L)
          .billing(billing)
          .value(BigDecimal.valueOf(5))
          .status(PaymentStatus.PARCIAL)
          .build();

  @Test
  public void testProcessWithSuccess() {
    when(billingRepository.findById(anyString())).thenReturn(Optional.of(billing));

    when(paymentRepository.findByBillingId(anyString())).thenReturn(Optional.of(payment));

    when(paymentRepository.save(any(PaymentEntity.class))).thenReturn(payment);

    processPaymentItemUseCase.process(
        PaymentItemModel.builder()
            .paymentId("1")
            .paymentValue(BigDecimal.TEN)
            .paymentStatus(PaymentStatus.TOTAL)
            .build());

    verify(paymentRepository, times(1)).save(any(PaymentEntity.class));
  }

  @Test
  public void testBillingNotFound() {
    when(billingRepository.findById(anyString())).thenReturn(Optional.empty());

    assertThrows(
        ResourceNotFoundException.class,
        () ->
            processPaymentItemUseCase.process(
                PaymentItemModel.builder()
                    .paymentId("1")
                    .paymentValue(BigDecimal.TEN)
                    .paymentStatus(PaymentStatus.TOTAL)
                    .build()));
  }

  @Test
  public void testPaymentCreate() {
    when(billingRepository.findById(anyString())).thenReturn(Optional.of(billing));

    when(paymentRepository.findByBillingId(anyString())).thenReturn(Optional.empty());

    when(paymentRepository.save(any(PaymentEntity.class))).thenReturn(payment);

    processPaymentItemUseCase.process(
        PaymentItemModel.builder()
            .paymentId("1")
            .paymentValue(BigDecimal.TEN)
            .paymentStatus(PaymentStatus.TOTAL)
            .build());

    verify(paymentRepository, times(1)).save(any(PaymentEntity.class));
  }
}
