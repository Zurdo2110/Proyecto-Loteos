package com.example.app.controllers;

import org.springframework.beans.factory.annotation.Autowired;
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

import java.util.List;

@Controller
public class LoteoController {

    @Autowired
    private LoteoRepository loteoRepository;

    @Autowired
    private LoteRepository loteRepository;

    @Autowired
    private EtapaRepository etapaRepository;

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
    public String guardarLoteo(Loteo loteo) {
        loteoRepository.save(loteo);
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

        List<Etapa> etapas = etapaRepository.findByLoteoIdLoteo(id);
        boolean tieneEtapas = !etapas.isEmpty();
        model.addAttribute("etapas", etapas);
        model.addAttribute("tieneEtapas", tieneEtapas);

        model.addAttribute("tieneEtapas", tieneEtapas);
        model.addAttribute("etapas", etapas);
        model.addAttribute("etapaSeleccionada", idEtapa);

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
            @RequestParam(value = "idEtapa", required = false) Integer idEtapa,
            Model model) {
        Loteo loteo = loteoRepository.findById(id).orElse(null);
        model.addAttribute("loteo", loteo);

        List<Etapa> etapas = etapaRepository.findByLoteoIdLoteo(id);
        model.addAttribute("tieneEtapas", !etapas.isEmpty());

        model.addAttribute("idEtapa", idEtapa != null ? idEtapa : false);
        model.addAttribute("etapaSeleccionada", idEtapa != null ? idEtapa : false);

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
    public String guardarLote(@PathVariable("id") Integer id, Lote lote,
            @RequestParam(value = "idEtapa", required = false) Integer idEtapa) {
        // 1. Buscamos el loteo al que le queremos agregar esta parcela
        Loteo loteo = loteoRepository.findById(id).orElse(null);

        if (loteo != null) {
            // 2. Le decimos al lote recién creado quién es su "padre"
            lote.setLoteo(loteo);

            if (idEtapa != null) {
                Etapa etapa = etapaRepository.findById(idEtapa).orElse(null);
                lote.setEtapa(etapa);
            }

            // 3. Guardamos el lote en PostgreSQL
            loteRepository.save(lote);
        }

        // 4. Redireccionamos a la lista de lotes de este proyecto
        return "redirect:/loteos/" + id + "/lotes";
    }

    // Ver los detalles completos de una parcela en particular
    @GetMapping("/lotes/{id}")
    public String verDetalleLote(@PathVariable("id") Integer id, Model model) {
        // Buscamos el lote por su ID
        Lote lote = loteRepository.findById(id).orElse(null);

        // Lo mandamos a la vista
        model.addAttribute("lote", lote);

        return "lotes/detalle-lote";
    }

    // --- ELIMINAR ---
    @PostMapping("/lotes/{id}/eliminar")
    public String eliminarLote(@PathVariable("id") Integer id) {
        // Buscamos el lote antes de borrarlo para saber a qué loteo pertenecía
        Lote lote = loteRepository.findById(id).orElse(null);
        if (lote != null) {
            Integer idLoteo = lote.getLoteo().getIdLoteo();

            // Lo fulminamos de la base de datos
            loteRepository.deleteById(id);

            // Volvemos a la lista de lotes de ese proyecto
            return "redirect:/loteos/" + idLoteo + "/lotes";
        }
        return "redirect:/loteos";
    }

    // --- MODIFICAR (Mostrar Formulario) ---
    @GetMapping("/lotes/{id}/editar")
    public String mostrarFormularioEditar(@PathVariable("id") Integer id, Model model) {
        Lote lote = loteRepository.findById(id).orElse(null);
        model.addAttribute("lote", lote);

        if (lote != null) {
            model.addAttribute("loteo", lote.getLoteo());
        }

        return "lotes/formulario-editar-lote";
    }

    // --- MODIFICAR (Guardar los cambios) ---
    @PostMapping("/lotes/{id}/editar")
    public String actualizarLote(@PathVariable("id") Integer id, Lote loteActualizado) {
        // Buscamos el lote original en PostgreSQL
        Lote loteExistente = loteRepository.findById(id).orElse(null);

        if (loteExistente != null) {
            // Le pisamos los datos viejos con los que vinieron del formulario
            loteExistente.setNumeroCuenta(loteActualizado.getNumeroCuenta());
            loteExistente.setNomenclatura(loteActualizado.getNomenclatura());
            loteExistente.setSuperficie(loteActualizado.getSuperficie());
            loteExistente.setDesignacionOficial(loteActualizado.getDesignacionOficial());
            loteExistente.setSuperficieCubierta(loteActualizado.getSuperficieCubierta());
            loteExistente.setObservaciones(loteActualizado.getObservaciones());
            loteExistente.setTitular(loteActualizado.getTitular());
            loteExistente.setCuentaEmos(loteActualizado.getCuentaEmos());
            loteExistente.setCuentaMuni(loteActualizado.getCuentaMuni());

            // Guardamos (Como ya tiene un ID, Spring Boot sabe que es un UPDATE y no un
            // INSERT)
            loteRepository.save(loteExistente);

            // Volvemos a la ficha de detalles para ver cómo quedó
            return "redirect:/lotes/" + id;
        }
        return "redirect:/loteos";
    }

    // --- EDITAR LOTEO (Mostrar el formulario) ---
    @GetMapping("/loteos/{id}/editar")
    public String mostrarFormularioEditarLoteo(@PathVariable("id") Integer id, Model model) {
        Loteo loteo = loteoRepository.findById(id).orElse(null);
        model.addAttribute("loteo", loteo);
        return "loteos/formulario-editar-loteo";
    }

    // --- EDITAR LOTEO (Guardar los cambios en la BD) ---
    @PostMapping("/loteos/{id}/editar")
    public String actualizarLoteo(@PathVariable("id") Integer id, Loteo loteoActualizado) {
        Loteo loteoExistente = loteoRepository.findById(id).orElse(null);
        if (loteoExistente != null) {
            // Pisamos el nombre viejo con el nuevo
            loteoExistente.setNombre(loteoActualizado.getNombre());
            loteoRepository.save(loteoExistente);
        }
        // Lo mandamos a ver cómo quedó el título cambiado
        return "redirect:/loteos/" + id + "/lotes";
    }

    // --- ELIMINAR LOTEO (A prueba de balas) ---
    @PostMapping("/loteos/{id}/eliminar")
    public String eliminarLoteo(@PathVariable("id") Integer id) {
        // 1. Primero buscamos todas las parcelas que estén adentro de este loteo
        List<Lote> lotesAsociados = loteRepository.findByLoteoIdLoteo(id);

        // 2. Las fulminamos para que PostgreSQL no tire error de foreign key
        loteRepository.deleteAll(lotesAsociados);

        // 3. Ahora sí podemos borrar el loteo vacío sin problemas
        loteoRepository.deleteById(id);

        // Volvemos a la pantalla principal
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
    public String guardarEtapa(@PathVariable("id") Integer id, Etapa etapa) {
        Loteo loteo = loteoRepository.findById(id).orElse(null);
        if (loteo != null) {
            etapa.setLoteo(loteo); // Enlazamos la etapa al loteo actual
            etapaRepository.save(etapa);
        }
        return "redirect:/loteos/" + id + "/lotes"; // Volvemos a la lista
    }

    // ==========================================
    // APARTADO EXCLUSIVO PARA CLIENTES
    // ==========================================

    @GetMapping("/cliente/loteos/{id}/lotes")
    public String verLotesCliente(
            @PathVariable("id") Integer id,
            @RequestParam(value = "idEtapa", required = false) Integer idEtapa,
            @RequestParam(value = "buscar", required = false) String buscar,
            @RequestParam(value = "idLoteSeleccionado", required = false) Integer idLoteSeleccionado, // <-- ACÁ
                                                                                                      // AGREGAMOS EL
                                                                                                      // RECEPTOR DEL
                                                                                                      // CLIC
            Model model) {

        Loteo loteo = loteoRepository.findById(id).orElse(null);
        model.addAttribute("loteo", loteo);

        // Verificamos si este loteo tiene etapas
        List<Etapa> etapas = etapaRepository.findByLoteoIdLoteo(id);
        boolean tieneEtapas = !etapas.isEmpty();
        model.addAttribute("tieneEtapas", tieneEtapas);
        model.addAttribute("etapas", etapas);
        model.addAttribute("etapaSeleccionada", idEtapa);

        // Traemos los lotes con la misma lógica inteligente pero para la vista del
        // cliente
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
        model.addAttribute("buscar", buscar != null ? buscar : "");

        // --- ACÁ BUSCAMOS EL LOTE SELECCIONADO PARA EL PANEL ---
        if (idLoteSeleccionado != null) {
            Lote loteSeleccionado = loteRepository.findById(idLoteSeleccionado).orElse(null);
            model.addAttribute("loteSeleccionado", loteSeleccionado);
        }
        // -------------------------------------------------------
        return "cliente/lotes-cliente";
    }

    @GetMapping("/cliente/loteos/{id}/visor")
    public String verMapaCliente(
            @PathVariable("id") Integer id,
            @RequestParam(value = "idEtapa", required = false) Integer idEtapa,
            Model model) {

        Loteo loteo = loteoRepository.findById(id).orElse(null);
        
        // --- CONTROL DE SEGURIDAD ---
        if (loteo == null) {
            // Si el loteo no existe, lo mandamos de vuelta al inicio del portal de clientes
            return "redirect:/cliente/loteos"; 
        }
        model.addAttribute("loteo", loteo);

        List<Etapa> etapas = etapaRepository.findByLoteoIdLoteo(id);
        model.addAttribute("tieneEtapas", !etapas.isEmpty());

        // Mantenemos tu excelente lógica para el JavaScript de Leaflet
        model.addAttribute("etapaSeleccionada", idEtapa != null ? idEtapa : false);
        
        return "cliente/visor-cliente";
    }

    @GetMapping("/cliente/loteos/{id}/datos-mapa")
    @ResponseBody
    public List<Lote> obtenerDatosMapaCliente(@PathVariable("id") Integer id) {
        return loteRepository.findByLoteoIdLoteo(id);
    }

    // --- MODIFICAR (Mostrar Formulario) ---
    @GetMapping("/cliente/lotes/{idLote}/editar")
    public String mostrarFormularioEditarCliente(@PathVariable("idLote") Integer idLote, Model model) {
        Lote lote = loteRepository.findById(idLote).orElse(null);
        model.addAttribute("lote", lote);

        if (lote != null) {
            model.addAttribute("loteo", lote.getLoteo());
        }

        return "cliente/formulario-cliente-editar-lote";
    }

    // --- MODIFICAR (Guardar Cambios) ---
    @PostMapping("/cliente/lotes/{idLote}/editar")
    public String guardarEdicionCliente(
            @PathVariable("idLote") Integer idLote, 
            @RequestParam(value = "titular", required = false) String titular,
            @RequestParam(value =  "designacionOficial", required = false) String designacionOficial,
            @RequestParam(value = "observaciones", required = false) String observaciones,
            @RequestParam(value = "idEtapa", required = false) Integer idEtapa,
            @RequestParam(value = "cuentaEmos", required = false) String cuentaEmos,
            @RequestParam(value = "cuentaMuni", required = false) String cuentaMuni) {

        Lote lote = loteRepository.findById(idLote).orElse(null);

        if (lote == null) {
            // el lote no existe, no hay nada que guardar
            return "redirect:/cliente/loteos/lotes";
        }

        lote.setTitular(titular);
        lote.setDesignacionOficial(designacionOficial);
        lote.setObservaciones(observaciones);
        lote.setCuentaEmos(cuentaEmos != null ? cuentaEmos : "-");
        lote.setCuentaMuni(cuentaMuni != null ? cuentaMuni : "-");


        loteRepository.save(lote);

        Integer idLoteo = lote.getLoteo().getIdLoteo();

        if (idEtapa != null) {
            return "redirect:/cliente/loteos/" + idLoteo + "/lotes?idEtapa=" + idEtapa;
        } else {
            return "redirect:/cliente/loteos/" + idLoteo + "/lotes";
        }
    }
}