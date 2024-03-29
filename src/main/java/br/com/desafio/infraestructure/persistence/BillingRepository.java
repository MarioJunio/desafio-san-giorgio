package br.com.desafio.infraestructure.persistence;

import br.com.desafio.infraestructure.data.BillingEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BillingRepository extends JpaRepository<BillingEntity, String> {
}
