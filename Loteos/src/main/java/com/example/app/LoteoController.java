package com.example.app;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class LoteoController {

    @Autowired
    private LoteoRepository loteoRepository;

    @Autowired
    private LoteRepository loteRepository;

    // --- RUTAS DE LOTEOS ---

    // Si entran a la raíz, los pateamos a la pantalla de loteos
    @GetMapping("/")
    public String inicio() {
        return "redirect:/loteos";
    }

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
        return "loteos"; 
    }

    // Mostrar el formulario para un nuevo loteo
    @GetMapping("/loteos/nuevo")
    public String mostrarFormulario() {
        return "formulario-loteo";
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
            Model model) {
        
        // 1. Buscamos el loteo para el título
        Loteo loteo = loteoRepository.findById(id).orElse(null);
        model.addAttribute("loteo", loteo);
        
        // 2. Traemos la lista de lotes (filtrada por el buscador o completa)
        List<Lote> lotes;
        if (buscar != null && !buscar.isBlank()) {
            lotes = loteRepository.buscarPorCuentaOTitular(id, buscar);
        } else {
            lotes = loteRepository.findByLoteoIdLoteo(id);
        }
        model.addAttribute("lotes", lotes);
        model.addAttribute("buscar", buscar); // Para mantener la palabra en el input
        
        // 3. Si el usuario hizo clic en un lote específico, lo buscamos para mostrarlo a la derecha
        if (idLoteSeleccionado != null) {
            Lote loteSeleccionado = loteRepository.findById(idLoteSeleccionado).orElse(null);
            model.addAttribute("loteSeleccionado", loteSeleccionado);
        }
        
        return "lotes";
    }

    // Mostrar el formulario para un lote nuevo
    @GetMapping("/loteos/{id}/lotes/nuevo")
    public String mostrarFormularioLote(@PathVariable("id") Integer id, Model model) {
        // Buscamos el loteo para mostrar su nombre en el título del formulario
        Loteo loteo = loteoRepository.findById(id).orElse(null);
        model.addAttribute("loteo", loteo);
        
        return "formulario-lote";
    }

    // Guardar el lote en la base de datos y enlazarlo
    @PostMapping("/loteos/{id}/lotes")
    public String guardarLote(@PathVariable("id") Integer id, Lote lote) {
        // 1. Buscamos el loteo al que le queremos agregar esta parcela
        Loteo loteo = loteoRepository.findById(id).orElse(null);
        
        if (loteo != null) {
            // 2. Le decimos al lote recién creado quién es su "padre"
            lote.setLoteo(loteo);
            
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
        
        return "detalle-lote";
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
        return "formulario-editar-lote";
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
            loteExistente.setEstado(loteActualizado.getEstado());
            loteExistente.setObservaciones(loteActualizado.getObservaciones());
            
            // Guardamos (Como ya tiene un ID, Spring Boot sabe que es un UPDATE y no un INSERT)
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
        return "formulario-editar-loteo";
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
}