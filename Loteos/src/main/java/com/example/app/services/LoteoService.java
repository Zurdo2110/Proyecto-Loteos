package com.example.app.services;

import com.example.app.models.Lote;
import com.example.app.models.Loteo;
import com.example.app.repositories.LoteRepository;
import com.example.app.repositories.LoteoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class LoteoService {

    private final LoteoRepository loteoRepository;
    private final LoteRepository loteRepository;

    public LoteoService(LoteoRepository loteoRepository, LoteRepository loteRepository) {
        this.loteoRepository = loteoRepository;
        this.loteRepository = loteRepository;
    }

    @Transactional
    public Loteo create(Loteo loteo) {
        return loteoRepository.save(loteo);
    }

    @Transactional
    public Optional<Loteo> update(Integer loteoId, Loteo updatedLoteo) {
        return loteoRepository.findById(loteoId).map(existingLoteo -> {
            existingLoteo.setNombre(updatedLoteo.getNombre());
            return loteoRepository.save(existingLoteo);
        });
    }

    @Transactional
    public boolean delete(Integer loteoId) {
        if (!loteoRepository.existsById(loteoId)) {
            return false;
        }

        loteRepository.deleteAll(loteRepository.findByLoteoIdLoteo(loteoId));
        loteoRepository.deleteById(loteoId);
        return true;
    }
}
