package com.example.app.controllers;

import com.example.app.models.Etapa;
import com.example.app.models.Lote;
import com.example.app.models.Loteo;
import com.example.app.repositories.EtapaRepository;
import com.example.app.repositories.LoteRepository;
import com.example.app.repositories.LoteoRepository;
import com.example.app.services.ClientAccessService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
public class ClienteController {

    private final LoteoRepository loteoRepository;
    private final LoteRepository loteRepository;
    private final EtapaRepository etapaRepository;
    private final ClientAccessService clientAccessService;

    public ClienteController(
            LoteoRepository loteoRepository,
            LoteRepository loteRepository,
            EtapaRepository etapaRepository,
            ClientAccessService clientAccessService) {
        this.loteoRepository = loteoRepository;
        this.loteRepository = loteRepository;
        this.etapaRepository = etapaRepository;
        this.clientAccessService = clientAccessService;
    }

    @GetMapping("/cliente/loteos/{id}/lotes")
    public String verLotes(
            @PathVariable("id") Integer id,
            @RequestParam(value = "idEtapa", required = false) Integer idEtapa,
            @RequestParam(value = "buscar", required = false) String buscar,
            @RequestParam(value = "idLoteSeleccionado", required = false) Integer idLoteSeleccionado,
            Model model,
            Authentication authentication) {

        Loteo loteo = loteoRepository.findById(id).orElse(null);
        if (loteo == null || !clientAccessService.canAccessLoteo(authentication, id)) {
            return "redirect:/";
        }
        model.addAttribute("loteo", loteo);

        List<Etapa> todasLasEtapas = etapaRepository.findByLoteoIdLoteo(id);
        List<Etapa> etapasActivas = todasLasEtapas.stream()
                .filter(etapa -> loteRepository.existsByEtapaIdEtapa(etapa.getIdEtapa()))
                .toList();

        boolean tieneEtapas = !etapasActivas.isEmpty();
        model.addAttribute("tieneEtapas", tieneEtapas);
        model.addAttribute("etapas", etapasActivas);
        model.addAttribute("etapaSeleccionada", idEtapa);
        model.addAttribute("mostrarContenido", !tieneEtapas || idEtapa != null);

        List<Lote> lotes;
        if (tieneEtapas && idEtapa != null) {
            if (buscar != null && !buscar.isBlank()) {
                String buscarSql = "%" + buscar.trim().replaceAll("\\s+", "%") + "%";
                lotes = loteRepository.buscarPorEtapaYTermino(idEtapa, buscarSql);
            } else {
                lotes = loteRepository.findByEtapaIdEtapa(idEtapa);
            }
        } else if (tieneEtapas) {
            lotes = List.of();
        } else if (buscar != null && !buscar.isBlank()) {
            String buscarSql = "%" + buscar.trim().replaceAll("\\s+", "%") + "%";
            lotes = loteRepository.buscarPorCuentaOTitular(id, buscarSql);
        } else {
            lotes = loteRepository.findByLoteoIdLoteo(id);
        }

        model.addAttribute("lotes", lotes);
        model.addAttribute("buscar", buscar != null ? buscar : "");

        if (idLoteSeleccionado != null) {
            Lote loteSeleccionado = loteRepository.findById(idLoteSeleccionado).orElse(null);
            if (clientAccessService.canAccessLote(authentication, loteSeleccionado)
                    && id.equals(loteSeleccionado.getLoteo().getIdLoteo())) {
                model.addAttribute("loteSeleccionado", loteSeleccionado);
            }
        }

        return "cliente/lotes-cliente";
    }

    @GetMapping("/cliente/loteos/{id}/visor")
    public String verMapa(
            @PathVariable("id") Integer id,
            @RequestParam(value = "idEtapa", required = false) Integer idEtapa,
            Authentication authentication,
            Model model) {

        Loteo loteo = loteoRepository.findById(id).orElse(null);
        if (loteo == null || !clientAccessService.canAccessLoteo(authentication, id)) {
            return "redirect:/";
        }

        model.addAttribute("loteo", loteo);
        List<Etapa> etapas = etapaRepository.findByLoteoIdLoteo(id);
        model.addAttribute("tieneEtapas", !etapas.isEmpty());
        model.addAttribute("etapaSeleccionada", idEtapa != null ? idEtapa : false);
        model.addAttribute("esAdmin", authentication.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals("ROLE_ADMIN")));
        model.addAttribute("esCliente", authentication.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals("ROLE_CLIENTE")));

        return "lotes/visor";
    }

    @GetMapping("/cliente/loteos/{id}/datos-mapa")
    @ResponseBody
    public ResponseEntity<List<Lote>> obtenerDatosMapa(
            @PathVariable("id") Integer id,
            Authentication authentication) {
        if (!clientAccessService.canAccessLoteo(authentication, id)) {
            return ResponseEntity.status(403).build();
        }
        return ResponseEntity.ok(loteRepository.findByLoteoIdLoteo(id));
    }

    @GetMapping("/cliente/lotes/{idLote}/editar")
    public String mostrarFormularioEditar(
            @PathVariable("idLote") Integer idLote,
            @RequestParam(value = "idEtapa", required = false) Integer idEtapa,
            Model model,
            Authentication authentication) {
        Lote lote = loteRepository.findByIdWithLoteo(idLote).orElse(null);
        if (!clientAccessService.canAccessLote(authentication, lote)) {
            return "redirect:/";
        }

        model.addAttribute("lote", lote);
        model.addAttribute("loteo", lote.getLoteo());
        model.addAttribute("etapaSeleccionada", idEtapa);
        return "cliente/formulario-cliente-editar-lote";
    }

    @PostMapping("/cliente/lotes/{idLote}/editar")
    public String guardarEdicion(
            @PathVariable("idLote") Integer idLote,
            @RequestParam(value = "titular", required = false) String titular,
            @RequestParam(value = "designacionOficial", required = false) String designacionOficial,
            @RequestParam(value = "observaciones", required = false) String observaciones,
            @RequestParam(value = "idEtapa", required = false) Integer idEtapa,
            @RequestParam(value = "cuentaEmos", required = false) String cuentaEmos,
            @RequestParam(value = "cuentaMuni", required = false) String cuentaMuni,
            @RequestParam(value = "domicilio", required = false) String domicilio,
            Authentication authentication) {

        Lote lote = loteRepository.findByIdWithLoteo(idLote).orElse(null);
        if (!clientAccessService.canAccessLote(authentication, lote)) {
            return "redirect:/";
        }

        lote.setTitular(titular);
        lote.setDesignacionOficial(designacionOficial);
        lote.setObservaciones(observaciones);
        lote.setCuentaEmos(cuentaEmos);
        lote.setCuentaMuni(cuentaMuni);
        lote.setDomicilio(domicilio);
        loteRepository.save(lote);

        Integer idLoteo = lote.getLoteo().getIdLoteo();
        return idEtapa != null
                ? "redirect:/cliente/loteos/" + idLoteo + "/lotes?idEtapa=" + idEtapa
                : "redirect:/cliente/loteos/" + idLoteo + "/lotes";
    }
}
