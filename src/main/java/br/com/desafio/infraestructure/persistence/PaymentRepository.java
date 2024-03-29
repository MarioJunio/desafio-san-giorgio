package br.com.desafio.infraestructure.persistence;

import br.com.desafio.domain.model.PaymentModel;
import br.com.desafio.infraestructure.data.PaymentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<PaymentEntity, Long> {

    Optional<PaymentEntity> findByBillingId(String id);
}
