package com.example.app.repositories;

import com.example.app.models.Loteo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface LoteoRepository extends JpaRepository<Loteo, Integer> {
    // Al extender JpaRepository, ya tenemos gratis métodos como:
    // save(), findAll(), findById(), deleteById()
    // Busca loteos cuyo nombre contenga la palabra clave, ignorando mayúsculas/minúsculas
    List<Loteo> findByNombreContainingIgnoreCase(String nombre);
}