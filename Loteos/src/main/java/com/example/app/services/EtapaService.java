package com.example.app.services;

import com.example.app.models.Etapa;
import com.example.app.repositories.EtapaRepository;
import com.example.app.repositories.LoteoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class EtapaService {

    private final EtapaRepository etapaRepository;
    private final LoteoRepository loteoRepository;

    public EtapaService(EtapaRepository etapaRepository, LoteoRepository loteoRepository) {
        this.etapaRepository = etapaRepository;
        this.loteoRepository = loteoRepository;
    }

    @Transactional
    public boolean create(Etapa etapa, Integer loteoId) {
        return loteoRepository.findById(loteoId)
                .map(loteo -> {
                    etapa.setLoteo(loteo);
                    etapaRepository.save(etapa);
                    return true;
                })
                .orElse(false);
    }
}
