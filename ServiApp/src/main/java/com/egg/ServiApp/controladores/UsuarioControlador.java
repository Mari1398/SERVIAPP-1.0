package com.egg.ServiApp.controladores;

import com.egg.ServiApp.servicios.calificacionServicio;
import com.egg.ServiApp.servicios.trabajoServicio;
import excepciones.miException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 *
 * @author Ale y Choy
 */
@Controller
@RequestMapping("/usuario")

public class UsuarioControlador {

    @Autowired
    private trabajoServicio trabajoServicio;

    @Autowired
    private calificacionServicio calificacionServicio;

    // Cambiar el estado del trabajo a "Realizado"
    @GetMapping("/realizarTrabajo")
    public String realizarTrabajo(@RequestParam String trabajoId, RedirectAttributes redirectAttributes) {
        //trabajoServicio.modificarTrabajo(trabajoId, Estado.FINALIZADO);
        redirectAttributes.addFlashAttribute("exito", "Trabajo marcado como realizado");
        return "redirect:/";
    }

    // Cancelar un trabajo
    @GetMapping("/cancelarTrabajo")
    public String cancelarTrabajo(@RequestParam String trabajoId, RedirectAttributes redirectAttributes) {
        //trabajoServicio.modificarTrabajo(trabajoId, Estado.CANCELADO);
        redirectAttributes.addFlashAttribute("exito", "Trabajo cancelado con éxito");
        return "redirect:/";
    }

    // Crear una calificación para un trabajo con estrellas
    @PostMapping("/calificarTrabajo")
    public String calificarTrabajo(@RequestParam String trabajoId, @RequestParam String contenido, @RequestParam double puntuacion, RedirectAttributes redirectAttributes) {
        try {
            calificacionServicio.crearCalificacion(contenido, puntuacion);
            // Asociar la calificación al trabajo (debe implementarse en el servicio)
            trabajoServicio.asociarCalificacion(trabajoId, contenido);
            redirectAttributes.addFlashAttribute("exito", "Calificación creada con éxito");
        } catch (miException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/";
    }
}



