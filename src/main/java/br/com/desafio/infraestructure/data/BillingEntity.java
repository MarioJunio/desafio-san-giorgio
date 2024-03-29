package br.com.desafio.infraestructure.data;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Table(name = "billing")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BillingEntity {

    @Id
    private String id;

    @Column(name = "amount")
    private BigDecimal value;

    @ManyToOne
    @JoinColumn(name = "seller_id", referencedColumnName = "id")
    private SellerEntity seller;
}
