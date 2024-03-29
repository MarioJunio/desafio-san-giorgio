package br.com.desafio.infraestructure.persistence;

import br.com.desafio.infraestructure.data.SellerEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SellerRepository extends JpaRepository<SellerEntity, String> {
}
