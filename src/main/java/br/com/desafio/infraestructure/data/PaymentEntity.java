package br.com.desafio.infraestructure.data;

import br.com.desafio.domain.model.PaymentItemModel;
import jakarta.persistence.*;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "payment")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "amount")
  private BigDecimal value;

  @Enumerated(EnumType.STRING)
  private PaymentStatus status;

  @ManyToOne
  @JoinColumn(name = "billing_id", referencedColumnName = "id")
  private BillingEntity billing;

  public static PaymentEntity fromModel(PaymentItemModel model, BillingEntity billing) {
    return PaymentEntity.builder()
        .value(model.getPaymentValue())
        .status(model.getPaymentStatus())
        .billing(billing)
        .build();
  }
}
