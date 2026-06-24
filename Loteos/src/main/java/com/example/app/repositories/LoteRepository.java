package com.example.app.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.Optional;


import com.example.app.models.Lote;

import java.util.List;

@Repository
public interface LoteRepository extends JpaRepository<Lote, Integer> {
    
    // El método que ya tenías
    List<Lote> findByLoteoIdLoteo(Integer idLoteo);

    // El buscador nuevo (busca coincidencias en cuenta o en titular, ignorando mayúsculas)
    @Query("SELECT l FROM Lote l WHERE l.loteo.idLoteo = :loteoId AND (LOWER(l.numeroCuenta) LIKE LOWER(CONCAT('%', :termino, '%')) OR LOWER(l.titular) LIKE LOWER(CONCAT('%', :termino, '%')))")
    List<Lote> buscarPorCuentaOTitular(@Param("loteoId") Integer loteoId, @Param("termino") String termino);

    // Spring Boot arma la consulta a PostgreSQL de forma automática solo con leer este nombre:
    Optional<Lote> findByNumeroCuenta(String numeroCuenta);
}