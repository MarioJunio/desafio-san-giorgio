package br.com.desafio.domain.usecase;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

import br.com.desafio.domain.exception.ResourceNotFoundException;
import br.com.desafio.domain.model.PaymentItemModel;
import br.com.desafio.domain.model.PaymentModel;
import br.com.desafio.infraestructure.data.BillingEntity;
import br.com.desafio.infraestructure.data.PaymentStatus;
import br.com.desafio.infraestructure.data.SellerEntity;
import br.com.desafio.infraestructure.messaging.MessageSender;
import br.com.desafio.infraestructure.persistence.BillingRepository;
import br.com.desafio.infraestructure.persistence.SellerRepository;
import com.fasterxml.jackson.databind.JsonMappingException;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

@SpringBootTest
public class ConfirmPaymentUseCaseTest {

  @MockBean private SellerRepository mockSellerRepository;

  @MockBean private BillingRepository mockBillingRepository;

  @MockBean private MessageSender mockMessageSender;

  @Autowired private ConfirmPaymentUseCase confirmPaymentUseCase;

  final SellerEntity seller = SellerEntity.builder().id("1").name("Mario").build();

  final BillingEntity billing =
      BillingEntity.builder().id("1").seller(seller).value(BigDecimal.TEN).build();

  @Test
  public void testConfirmWithSuccess() {
    when(mockSellerRepository.findById(anyString())).thenReturn(Optional.of(seller));

    when(mockBillingRepository.findById("1")).thenReturn(Optional.of(billing));

    doNothing().when(mockMessageSender).sendMessage(any());

    final PaymentModel paymentParcial =
        confirmPaymentUseCase.confirm(
            PaymentModel.builder()
                .sellerId(seller.getId())
                .payments(
                    List.of(
                        PaymentItemModel.builder()
                            .paymentId("1")
                            .paymentValue(BigDecimal.valueOf(9.5))
                            .build()))
                .build());

    final PaymentModel paymentTotal =
        confirmPaymentUseCase.confirm(
            PaymentModel.builder()
                .sellerId(seller.getId())
                .payments(
                    List.of(
                        PaymentItemModel.builder()
                            .paymentId("1")
                            .paymentValue(BigDecimal.valueOf(10))
                            .build()))
                .build());

    final PaymentModel paymentExcess =
        confirmPaymentUseCase.confirm(
            PaymentModel.builder()
                .sellerId(seller.getId())
                .payments(
                    List.of(
                        PaymentItemModel.builder()
                            .paymentId("1")
                            .paymentValue(BigDecimal.valueOf(12))
                            .build()))
                .build());

    assertEquals(PaymentStatus.PARCIAL, paymentParcial.getPayments().get(0).getPaymentStatus());
    assertEquals(PaymentStatus.TOTAL, paymentTotal.getPayments().get(0).getPaymentStatus());
    assertEquals(PaymentStatus.EXCESS, paymentExcess.getPayments().get(0).getPaymentStatus());

    verify(mockMessageSender, times(3)).sendMessage(any());
  }

  @Test
  public void testSellerNotFound() {
    when(mockSellerRepository.findById(anyString())).thenReturn(Optional.empty());

    assertThrows(
        ResourceNotFoundException.class,
        () ->
            confirmPaymentUseCase.confirm(
                PaymentModel.builder()
                    .sellerId(seller.getId())
                    .payments(
                        List.of(
                            PaymentItemModel.builder()
                                .paymentId("1")
                                .paymentValue(BigDecimal.valueOf(9.5))
                                .build()))
                    .build()));
  }

  @Test
  public void testProcessPaymentWithJsonConverterException() {
    when(mockSellerRepository.findById(anyString())).thenReturn(Optional.of(seller));

    when(mockBillingRepository.findById("1")).thenReturn(Optional.of(billing));

    doAnswer(
            invocation -> {
              throw JsonMappingException.fromUnexpectedIOE(new IOException());
            })
        .when(mockMessageSender)
        .sendMessage(any());

    confirmPaymentUseCase.confirm(
        PaymentModel.builder()
            .sellerId(seller.getId())
            .payments(
                List.of(
                    PaymentItemModel.builder()
                        .paymentId("1")
                        .paymentValue(BigDecimal.valueOf(9.5))
                        .build()))
            .build());

    verify(mockMessageSender, times(1)).sendMessage(any());
  }
}
