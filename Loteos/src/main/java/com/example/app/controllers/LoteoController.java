package com.example.app.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.example.app.repositories.LoteoRepository;
import com.example.app.repositories.EtapaRepository;
import com.example.app.repositories.LoteRepository;
import com.example.app.models.Etapa;
import com.example.app.models.Lote;
import com.example.app.models.Loteo;
import com.example.app.services.LoteService;
import com.example.app.services.LoteoService;
import com.example.app.services.EtapaService;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.validation.BindingResult;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
public class LoteoController {

    private final LoteoRepository loteoRepository;
    private final LoteRepository loteRepository;
    private final EtapaRepository etapaRepository;
    private final LoteService loteService;
    private final LoteoService loteoService;
    private final EtapaService etapaService;

    public LoteoController(
            LoteoRepository loteoRepository,
            LoteRepository loteRepository,
            EtapaRepository etapaRepository,
            LoteService loteService,
            LoteoService loteoService,
            EtapaService etapaService) {
        this.loteoRepository = loteoRepository;
        this.loteRepository = loteRepository;
        this.etapaRepository = etapaRepository;
        this.loteService = loteService;
        this.loteoService = loteoService;
        this.etapaService = etapaService;
    }

    // --- RUTAS DE LOTEOS ---

    // Si entran a la raíz, los pateamos a la pantalla de loteos
    // @GetMapping("/")
    // public String inicio() {
    // return "redirect:/loteos";
    // }

    // Listar todos los loteos
    @GetMapping("/loteos")
    public String listarLoteos(@RequestParam(value = "buscar", required = false) String buscar, Model model) {
        List<Loteo> loteos;

        // Si el usuario escribió algo en el buscador, filtramos; si no, traemos todos
        if (buscar != null && !buscar.isBlank()) {
            loteos = loteoRepository.findByNombreContainingIgnoreCase(buscar);
        } else {
            loteos = loteoRepository.findAll();
        }

        model.addAttribute("loteos", loteos);
        model.addAttribute("buscar", buscar); // Devolvemos el texto para que quede escrito en el input
        return "loteos/loteos";
    }

    // Mostrar el formulario para un nuevo loteo
    @GetMapping("/loteos/nuevo")
    public String mostrarFormulario() {
        return "loteos/formulario-loteo";
    }

    // Guardar el loteo nuevo en la base de datos
    @PostMapping("/loteos")
    public String guardarLoteo(
            @Valid Loteo loteo,
            BindingResult bindingResult,
            Model model) {
        if (bindingResult.hasErrors()) {
            addValidationErrors(model, bindingResult);
            return "loteos/formulario-loteo";
        }
        loteoService.create(loteo);
        return "redirect:/loteos";
    }

    // --- RUTAS DE LOTES (PARCELAS) ---

    // Mostrar los lotes de un loteo específico
    @GetMapping("/loteos/{id}/lotes")
    public String verLotes(
            @PathVariable("id") Integer id,
            @RequestParam(value = "buscar", required = false) String buscar,
            @RequestParam(value = "idLoteSeleccionado", required = false) Integer idLoteSeleccionado,
            @RequestParam(value = "idEtapa", required = false) Integer idEtapa,
            Model model) {

        // 1. Buscamos el loteo para el título
        Loteo loteo = loteoRepository.findById(id).orElse(null);
        model.addAttribute("loteo", loteo);

        List <Etapa> todasLasEtapas = etapaRepository.findByLoteoIdLoteo(id);
        
        List<Etapa> etapasActivas = todasLasEtapas.stream()
                .filter(etapa -> loteRepository.existsByEtapaIdEtapa(etapa.getIdEtapa()))
                .toList();

        boolean tieneEtapas = !etapasActivas.isEmpty();
        model.addAttribute("etapas", etapasActivas);
        model.addAttribute("tieneEtapas", tieneEtapas);
        model.addAttribute("etapaSeleccionada", idEtapa);
        model.addAttribute("mostrarContenido", !tieneEtapas || idEtapa != null); // Si tiene etapas y no seleccionó ninguna, no mostramos contenido

        // 2. Traemos la lista de lotes (filtrada por el buscador o completa)
        List<Lote> lotes;
        if (tieneEtapas && idEtapa != null) {
            // ESTAMOS ADENTRO DE UNA ETAPA
            if (buscar != null && !buscar.isBlank()) {
                // Si escribió algo en el buscador, filtramos solo en esta etapa
                String buscarSql = "%" + buscar.trim().replaceAll("\\s+", "%") + "%";
                lotes = loteRepository.buscarPorEtapaYTermino(idEtapa, buscarSql);
            } else {
                // Si no buscó nada, mostramos todos los de la etapa
                lotes = loteRepository.findByEtapaIdEtapa(idEtapa);
            }
        } else if (tieneEtapas && idEtapa == null) {
            // Tiene etapas pero no seleccionó ninguna pestaña, dejamos la lista vacía
            lotes = List.of();
        } else {
            // COMPORTAMIENTO NORMAL: Loteos sin etapas
            if (buscar != null && !buscar.isBlank()) {
                String buscarSql = "%" + buscar.trim().replaceAll("\\s+", "%") + "%";
                lotes = loteRepository.buscarPorCuentaOTitular(id, buscarSql);
            } else {
                lotes = loteRepository.findByLoteoIdLoteo(id);
            }
        }
        model.addAttribute("lotes", lotes);
        model.addAttribute("buscar", buscar); // Para mantener la palabra en el input

        // 3. Si el usuario hizo clic en un lote específico, lo buscamos para mostrarlo
        // a la derecha
        if (idLoteSeleccionado != null) {
            Lote loteSeleccionado = loteRepository.findById(idLoteSeleccionado).orElse(null);
            model.addAttribute("loteSeleccionado", loteSeleccionado);
        }

        return "lotes/lotes";
    }

    // Mostrar el visor web del mapa interactivo
    @GetMapping("/loteos/{id}/visor")
    public String verMapa(@PathVariable("id") Integer id,
            @RequestParam(value = "idEtapa", required = false) Integer idEtapa, Authentication authentication, 
            Model model) {
        Loteo loteo = loteoRepository.findById(id).orElse(null);
        model.addAttribute("loteo", loteo);

        List<Etapa> etapas = etapaRepository.findByLoteoIdLoteo(id);
        model.addAttribute("tieneEtapas", !etapas.isEmpty());

        model.addAttribute("idEtapa", idEtapa != null ? idEtapa : false);
        model.addAttribute("etapaSeleccionada", idEtapa != null ? idEtapa : false);

        boolean esAdmin = false;
        boolean esCliente = false;

        if (authentication != null) {
            for (GrantedAuthority authority : authentication.getAuthorities()) {
                if (authority.getAuthority().equals("ROLE_ADMIN")) {
                    esAdmin = true;
                } else if (authority.getAuthority().equals("ROLE_CLIENTE")) {
                    esCliente = true;
                }
            }
        }

        model.addAttribute("esAdmin", esAdmin);
        model.addAttribute("esCliente", esCliente);

        return "lotes/visor";
    }

    // Mostrar el formulario para un lote nuevo
    @GetMapping("/loteos/{id}/lotes/nuevo")
    public String mostrarFormularioLote(@PathVariable("id") Integer id, Model model) {
        // Buscamos el loteo para mostrar su nombre en el título del formulario
        Loteo loteo = loteoRepository.findById(id).orElse(null);
        model.addAttribute("loteo", loteo);

        model.addAttribute("lote", new Lote());

        List<Etapa> etapas = etapaRepository.findByLoteoIdLoteo(id);
        model.addAttribute("tieneEtapas", !etapas.isEmpty());
        model.addAttribute("etapas", etapas);

        return "lotes/formulario-lote";
    }

    // Guardar el lote en la base de datos y enlazarlo
    @PostMapping("/loteos/{id}/lotes")
    public String guardarLote(
            @PathVariable("id") Integer id,
            @Valid Lote lote,
            BindingResult bindingResult,
            @RequestParam(value = "idEtapa", required = false) Integer idEtapa,
            Model model) {
        if (bindingResult.hasErrors()) {
            prepararFormularioLote(id, lote, model);
            addValidationErrors(model, bindingResult);
            return "lotes/formulario-lote";
        }
        loteService.create(lote, id, idEtapa);
        return "redirect:/loteos/" + id + "/lotes";
    }

    // --- ELIMINAR ---
    @PostMapping("/lotes/{id}/eliminar")
    public String eliminarLote(@PathVariable("id") Integer id) {
        return loteService.delete(id)
                .map(idLoteo -> "redirect:/loteos/" + idLoteo + "/lotes")
                .orElse("redirect:/loteos");
    }

    // --- MODIFICAR (Mostrar Formulario) ---
    @GetMapping("/lotes/{id}/editar")
    public String mostrarFormularioEditar(
            @PathVariable("id") Integer id,
            @RequestParam(value = "idEtapa", required = false) Integer idEtapa,
            Model model) {
        Lote lote = loteRepository.findByIdWithLoteo(id).orElse(null);
        if (lote == null) {
            return "redirect:/loteos";
        }

        model.addAttribute("lote", lote);
        model.addAttribute("loteo", lote.getLoteo());
        model.addAttribute("etapaSeleccionada", idEtapa);

        return "lotes/formulario-editar-lote";
    }

    // --- MODIFICAR (Guardar los cambios) ---
    @PostMapping("/lotes/{id}/editar")
    public String actualizarLote(
            @PathVariable("id") Integer id,
            @RequestParam(value = "idEtapa", required = false) Integer idEtapa,
            @Valid Lote loteActualizado,
            BindingResult bindingResult,
            Model model) {
        if (bindingResult.hasErrors()) {
            Lote loteExistente = loteRepository.findByIdWithLoteo(id).orElse(null);
            if (loteExistente == null) {
                return "redirect:/loteos";
            }
            loteActualizado.setIdLote(id);
            loteActualizado.setLoteo(loteExistente.getLoteo());
            model.addAttribute("lote", loteActualizado);
            model.addAttribute("loteo", loteExistente.getLoteo());
            model.addAttribute("etapaSeleccionada", idEtapa);
            addValidationErrors(model, bindingResult);
            return "lotes/formulario-editar-lote";
        }
        Optional<Integer> idLoteo = loteService.update(id, loteActualizado);
        if (idLoteo.isPresent()) {
            Integer loteoId = idLoteo.get();
            String redirect = "redirect:/loteos/" + loteoId + "/lotes";
            if (idEtapa != null) {
                redirect += "?idEtapa=" + idEtapa;
            }
            return redirect;
        }
        return "redirect:/loteos";
    }

    // --- EDITAR LOTEO (Mostrar el formulario) ---
    @GetMapping("/loteos/{id}/editar")
    public String mostrarFormularioEditarLoteo(@PathVariable("id") Integer id, Model model) {
        Loteo loteo = loteoRepository.findById(id).orElse(null);
        if (loteo == null) {
            return "redirect:/loteos";
        }

        model.addAttribute("loteo", loteo);
        return "loteos/formulario-editar-loteo";
    }

    // --- EDITAR LOTEO (Guardar los cambios en la BD) ---
    @PostMapping("/loteos/{id}/editar")
    public String actualizarLoteo(
            @PathVariable("id") Integer id,
            @Valid Loteo loteoActualizado,
            BindingResult bindingResult,
            Model model) {
        if (bindingResult.hasErrors()) {
            loteoActualizado.setIdLoteo(id);
            model.addAttribute("loteo", loteoActualizado);
            addValidationErrors(model, bindingResult);
            return "loteos/formulario-editar-loteo";
        }
        loteoService.update(id, loteoActualizado);
        // Lo mandamos a ver cómo quedó el título cambiado
        return "redirect:/loteos/" + id + "/lotes";
    }

    // --- ELIMINAR LOTEO (A prueba de balas) ---
    @PostMapping("/loteos/{id}/eliminar")
    public String eliminarLoteo(@PathVariable("id") Integer id) {
        loteoService.delete(id);
        return "redirect:/loteos";
    }

    // Ruta REST para que el mapa consulte los datos de un lote por su cuenta
    @GetMapping("/api/lotes/{cuenta}")
    @ResponseBody
    public ResponseEntity<Lote> obtenerDetalleLote(@PathVariable("cuenta") String cuenta) {
        // Buscamos en PostgreSQL usando el método que creamos en el Paso 1
        return loteRepository.findByNumeroCuenta(cuenta)
                .map(lote -> ResponseEntity.ok().body(lote)) // Si existe, devuelve 200 OK con el lote
                .orElse(ResponseEntity.notFound().build()); // Si no existe, devuelve 404 Not Found
    }

    // ==========================================
    // SECCIÓN ETAPAS
    // ==========================================

    @GetMapping("/loteos/{id}/etapas/nueva")
    public String mostrarFormularioEtapa(@PathVariable("id") Integer id, Model model) {
        Loteo loteo = loteoRepository.findById(id).orElse(null);
        model.addAttribute("loteo", loteo);
        model.addAttribute("etapa", new Etapa());
        return "lotes/formulario-etapa";
    }

    @PostMapping("/loteos/{id}/etapas/nueva")
    public String guardarEtapa(
            @PathVariable("id") Integer id,
            @Valid Etapa etapa,
            BindingResult bindingResult,
            Model model) {
        if (bindingResult.hasErrors()) {
            Loteo loteo = loteoRepository.findById(id).orElse(null);
            if (loteo == null) {
                return "redirect:/loteos";
            }
            model.addAttribute("loteo", loteo);
            addValidationErrors(model, bindingResult);
            return "lotes/formulario-etapa";
        }
        etapaService.create(etapa, id);
        return "redirect:/loteos/" + id + "/lotes"; // Volvemos a la lista
    }

    private void prepararFormularioLote(Integer id, Lote lote, Model model) {
        Loteo loteo = loteoRepository.findById(id).orElse(null);
        model.addAttribute("loteo", loteo);
        model.addAttribute("lote", lote);
        List<Etapa> etapas = etapaRepository.findByLoteoIdLoteo(id);
        model.addAttribute("tieneEtapas", !etapas.isEmpty());
        model.addAttribute("etapas", etapas);
    }

    private void addValidationErrors(Model model, BindingResult bindingResult) {
        model.addAttribute("errores", bindingResult.getAllErrors().stream()
                .map(error -> error.getDefaultMessage())
                .collect(Collectors.toList()));
    }

}