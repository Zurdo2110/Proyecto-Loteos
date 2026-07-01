package com.example.app.repositories;

import com.example.app.models.Etapa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface EtapaRepository extends JpaRepository<Etapa, Integer> {
    // Método mágico para traer todas las etapas de un loteo específico
    List<Etapa> findByLoteoIdLoteo(Integer idLoteo);
}