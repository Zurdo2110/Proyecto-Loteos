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
    

    List<Lote> findByLoteoIdLoteo(Integer idLoteo);
    List<Lote> findByEtapaIdEtapa(Integer idEtapa);

    @Query("SELECT l FROM Lote l WHERE l.loteo.idLoteo = :loteoId AND (LOWER(l.numeroCuenta) LIKE LOWER(CONCAT('%', :termino, '%')) OR LOWER(l.titular) LIKE LOWER(CONCAT('%', :termino, '%')))")
    List<Lote> buscarPorCuentaOTitular(@Param("loteoId") Integer loteoId, @Param("termino") String termino);

    @Query("SELECT l FROM Lote l WHERE l.etapa.idEtapa = :idEtapa AND (LOWER(l.numeroCuenta) LIKE LOWER(CONCAT('%', :buscar, '%')) OR LOWER(l.titular) LIKE LOWER(CONCAT('%', :buscar, '%')))")
    List<Lote> buscarPorEtapaYTermino(@Param("idEtapa") Integer idEtapa, @Param("buscar") String buscar);

    Optional<Lote> findByNumeroCuenta(String numeroCuenta);
}