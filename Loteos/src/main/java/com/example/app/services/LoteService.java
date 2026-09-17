package com.example.app.services;

import com.example.app.models.Etapa;
import com.example.app.models.Lote;
import com.example.app.models.Loteo;
import com.example.app.repositories.EtapaRepository;
import com.example.app.repositories.LoteRepository;
import com.example.app.repositories.LoteoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class LoteService {

    private final LoteRepository loteRepository;
    private final LoteoRepository loteoRepository;
    private final EtapaRepository etapaRepository;

    public LoteService(
            LoteRepository loteRepository,
            LoteoRepository loteoRepository,
            EtapaRepository etapaRepository) {
        this.loteRepository = loteRepository;
        this.loteoRepository = loteoRepository;
        this.etapaRepository = etapaRepository;
    }

    @Transactional
    public boolean create(Lote lote, Integer loteoId, Integer etapaId) {
        Optional<Loteo> loteo = loteoRepository.findById(loteoId);
        if (loteo.isEmpty()) {
            return false;
        }

        lote.setLoteo(loteo.get());
        if (etapaId != null) {
            etapaRepository.findById(etapaId).ifPresent(lote::setEtapa);
        }
        loteRepository.save(lote);
        return true;
    }

    @Transactional
    public Optional<Integer> update(Integer loteId, Lote updatedLote) {
        return loteRepository.findById(loteId).map(existingLote -> {
            existingLote.setNumeroCuenta(updatedLote.getNumeroCuenta());
            existingLote.setNomenclatura(updatedLote.getNomenclatura());
            existingLote.setMatricula(updatedLote.getMatricula());
            existingLote.setSuperficie(updatedLote.getSuperficie());
            existingLote.setDesignacionOficial(updatedLote.getDesignacionOficial());
            existingLote.setSuperficieCubierta(updatedLote.getSuperficieCubierta());
            existingLote.setObservaciones(updatedLote.getObservaciones());
            existingLote.setTitular(updatedLote.getTitular());
            existingLote.setCuentaEmos(updatedLote.getCuentaEmos());
            existingLote.setCuentaMuni(updatedLote.getCuentaMuni());
            existingLote.setDomicilio(updatedLote.getDomicilio());
            existingLote.setManzana(updatedLote.getManzana());
            existingLote.setNumeroLote(updatedLote.getNumeroLote());
            loteRepository.save(existingLote);
            return existingLote.getLoteo().getIdLoteo();
        });
    }

    @Transactional
    public Optional<Integer> delete(Integer loteId) {
        return loteRepository.findById(loteId).map(lote -> {
            Integer loteoId = lote.getLoteo().getIdLoteo();
            loteRepository.delete(lote);
            return loteoId;
        });
    }
}
