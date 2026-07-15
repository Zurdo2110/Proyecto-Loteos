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

    @Query("SELECT l FROM Lote l WHERE l.loteo.idLoteo = :idLoteo AND (" +
           "LOWER(l.numeroCuenta) LIKE LOWER(:term) OR " +
           "LOWER(coalesce(l.titular, '')) LIKE LOWER(:term) OR " +
           "LOWER(coalesce(l.designacionOficial, '')) LIKE LOWER(:term))")
    List<Lote> buscarPorCuentaOTitular(@Param("idLoteo") Integer idLoteo, @Param("term") String term);

    // Búsqueda para loteos CON etapas (filtrando por etapa activa)
    @Query("SELECT l FROM Lote l WHERE l.etapa.idEtapa = :idEtapa AND (" +
           "LOWER(l.numeroCuenta) LIKE LOWER(:term) OR " +
           "LOWER(coalesce(l.titular, '')) LIKE LOWER(:term) OR " +
           "LOWER(coalesce(l.designacionOficial, '')) LIKE LOWER(:term))")
    List<Lote> buscarPorEtapaYTermino(@Param("idEtapa") Integer idEtapa, @Param("term") String term);

    Optional<Lote> findByNumeroCuenta(String numeroCuenta);
}