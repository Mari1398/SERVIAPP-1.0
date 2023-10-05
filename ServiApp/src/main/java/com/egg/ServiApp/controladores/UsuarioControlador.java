package com.egg.ServiApp.controladores;

import com.egg.ServiApp.entidades.Especialidad;
import com.egg.ServiApp.entidades.Usuario;
import com.egg.ServiApp.entidades.Trabajo;
import com.egg.ServiApp.enumeraciones.Estado;
import com.egg.ServiApp.enumeraciones.Rol;

import com.egg.ServiApp.servicios.calificacionServicio;
import com.egg.ServiApp.servicios.especialidadServicio;
import com.egg.ServiApp.servicios.trabajoServicio;
import com.egg.ServiApp.servicios.usuarioServicio;
import excepciones.miException;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttribute;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 *
 * @author Ale y Choy
 */
@Controller
@PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_USUARIO', 'ROLE_PROVEEDOR')")
@RequestMapping("/usuario")

public class UsuarioControlador {

    @Autowired
    private trabajoServicio trabajoServicio;
    @Autowired
    private usuarioServicio usuarioServicio;
    @Autowired
    private calificacionServicio calificacionServicio;
    @Autowired
    private especialidadServicio especialidadServicio;

    @GetMapping("/perfilUser/{id}")
    public String cargarPerfilU(@PathVariable String id, ModelMap modelo) {
        List<Trabajo> trabajos = trabajoServicio.listarTrabajoPorUsuario(id);
        modelo.put("trabajos", trabajos);
        List<Especialidad> especialidades = especialidadServicio.listarEspecialidades();
        modelo.addAttribute("especialidades", especialidades);
        modelo.put("user", usuarioServicio.UserById(id));
        return "profileUser.html";
    }

    @GetMapping("/perfilProvider/{id}")
    public String cargarPerfilP(@PathVariable String id, ModelMap modelo) {
        List<Trabajo> trabajos = trabajoServicio.listarTrabajoPorProveedor(id);
        modelo.put("trabajos", trabajos);
        List<Especialidad> especialidades = especialidadServicio.listarEspecialidades();
        modelo.addAttribute("especialidades", especialidades);
        modelo.put("user", usuarioServicio.ProviderById(id));
        return "profileUser.html";
    }

    
    @GetMapping("/userToProv/{id}")
    public String userToProv(@PathVariable String id, HttpServletRequest request) {
        usuarioServicio.usuarioCambioProveedor(usuarioServicio.UserById(id));
        usuarioServicio.eliminarUsuarioId(id);

        // Cierra la sesión utilizando Spring Security
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null) {
            new SecurityContextLogoutHandler().logout(request, null, auth);
        }

        return "redirect:/login";
    }

    @GetMapping("/provToUser/{id}")
    public String provToUser(@PathVariable String id, HttpServletRequest request) {
        usuarioServicio.proveedorCambioUsuario(usuarioServicio.ProviderById(id));

        // Cierra la sesión utilizando Spring Security
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null) {
            new SecurityContextLogoutHandler().logout(request, null, auth);
        }

        return "redirect:/login";
    }

    @GetMapping("/perfilOtro/{id}")
    public String perfilOtro(@PathVariable String id, ModelMap modelo) {
        List<Especialidad> especialidades = especialidadServicio.listarEspecialidades();
        modelo.addAttribute("especialidades", especialidades);
        List<Trabajo> trabajos = trabajoServicio.listarTrabajoPorProveedor(id);
        modelo.put("trabajos", trabajos);
        modelo.put("user", usuarioServicio.ProviderById(id));

        return "profileOtherUser.html";
    }

    @PostMapping("/actualizarFoto")
    public String registro(@RequestParam String id, MultipartFile archivo, ModelMap modelo) {
        try {
            usuarioServicio.actualizarFoto(archivo, id);
            modelo.put("exito", "Actualización correcta");
            return "profileUser.html";
        } catch (miException e) {
            modelo.put("error", e.getMessage());

        }
        return "profileUser.html";

    }

    @GetMapping("/modificarUsuario/{id}")
    public String modificarUsuario(@PathVariable String id, ModelMap model) {
        List<Especialidad> especialidades = especialidadServicio.listarEspecialidades();
        model.addAttribute("especialidades", especialidades);
        model.put("user", usuarioServicio.UserById(id));

        return "modProfileUser.html";
    }

    @PostMapping("/modificarUsuario/{id}")
    public String modificarUsuario(MultipartFile archivo, @PathVariable String id, String nombre, Long telefono, ModelMap model) {
        try {
            usuarioServicio.modificarUsuario(archivo, id, nombre, telefono);
            return "redirect:../perfilUser/{id}";
        } catch (miException ex) {
            model.put("error", ex.getMessage());
            return "modProfileUser.html";
        }
    }

    @PostMapping("/modificarContrasenia/{id}")
    public String modificarContrasenia(@PathVariable String id, String oldPassword, String password1, String password2, ModelMap model, @SessionAttribute("usersession") Usuario usuario) throws miException {
        try {
            usuarioServicio.modificarContrasenia(id, oldPassword, password1, password2);
            model.put("exito", "Se modificó correctamente la contraseña");
            String op=null;
            if (usuario.getRol()==Rol.USUARIO) {
                op = "User";
            }else if(usuario.getRol()==Rol.PROVEEDOR){
                op = "Provider";
            }
            return "redirect:../perfil"+op+"/{id}";
        } catch (miException ex) {

            model.put("error", ex.getMessage());
            return "redirect:../perfil";
        }
    }

    @GetMapping("/modificarProveedor/{id}")
    public String modificarProveedor(@PathVariable String id, ModelMap model) {
        model.put("provider", usuarioServicio.ProviderById(id));
        List<Especialidad> especialidades = especialidadServicio.listarEspecialidades();
        model.addAttribute("especialidades", especialidades);

        return "modProfileProvider.html";
    }

    @PostMapping("/modificarProveedor/{id}")
    public String modificarProveedor(MultipartFile archivo, @PathVariable String id, String nombre, Long telefono, double costoHora, String descripcion, String idEsp, ModelMap model) {
        try {
            usuarioServicio.modificarProveedor(archivo, id, nombre, telefono, costoHora, descripcion, idEsp);
            return "redirect:../perfilProvider/{id}";
        } catch (miException ex) {
            model.put("error", ex.getMessage());
            return "modProfileProvider.html";
        }
    }

//    // Cambiar el estado del trabajo a "Realizado"
//    @GetMapping("/realizarTrabajo")
//    public String realizarTrabajo(@RequestParam String trabajoId, RedirectAttributes redirectAttributes) {
//        //trabajoServicio.modificarTrabajo(trabajoId, Estado.FINALIZADO);
//        redirectAttributes.addFlashAttribute("exito", "Trabajo marcado como realizado");
//        return "redirect:/";
//    }
//
//    // Cancelar un trabajo
//    @GetMapping("/cancelarTrabajo")
//    public String cancelarTrabajo(@RequestParam String trabajoId, RedirectAttributes redirectAttributes) {
//        //trabajoServicio.modificarTrabajo(trabajoId, Estado.CANCELADO);
//        redirectAttributes.addFlashAttribute("exito", "Trabajo cancelado con éxito");
//        return "redirect:/";
//    }
    // Crear una calificación para un trabajo con estrellas
    @PostMapping("/calificarTrabajo/{trabajoId}")
    public String calificarTrabajo(@PathVariable String trabajoId, double puntuacion, String contenido, RedirectAttributes redirectAttributes) {
        System.out.println(trabajoId + " " + contenido + " " + puntuacion);
        try {
            calificacionServicio.modificarCalificacion(trabajoId, contenido, puntuacion);
            redirectAttributes.addFlashAttribute("exito", "Calificación creada con éxito");
        } catch (miException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:../contrataciones";
    }

    @GetMapping("/bajaUsuario/{id}")
    public String bajaUsuario(@PathVariable String id, RedirectAttributes redirectAttributes) {
        usuarioServicio.bajaUsuario(usuarioServicio.UserById(id));
        redirectAttributes.addFlashAttribute("exito", "Baja éxitosa");
        return "/logout";
    }

    @GetMapping("/contrataciones")
    public String contrataciones(@SessionAttribute("usersession") Usuario usuario, ModelMap model) {
        List<Especialidad> especialidades = especialidadServicio.listarEspecialidades();
        model.addAttribute("especialidades", especialidades);

        if (usuario != null) {

            if (usuario.getRol().equals(Rol.PROVEEDOR)) {

                // Trabajo creado en estado Pendiente con x proveedor
                List<Trabajo> listaUsuariosPen = trabajoServicio.TrabajoPorProveedorEstado(usuario.getId(), Estado.PENDIENTE);
                model.addAttribute("usuariosPendientes", listaUsuariosPen);

                // Trabajo en curso en estado Aceptado con x proveedor
                List<Trabajo> listaUsuariosAcep = trabajoServicio.TrabajoPorProveedorEstado(usuario.getId(), Estado.ACEPTADO);
                model.addAttribute("usuariosAceptados", listaUsuariosAcep);

                // Trabajo terminado en estado Completo con x proveedor
                List<Trabajo> listaUsuariosCom = trabajoServicio.TrabajoPorProveedorEstado(usuario.getId(), Estado.FINALIZADO);
                model.addAttribute("usuariosCompleto", listaUsuariosCom);

                // Lista de trabajos calificados
                List<Trabajo> trabajoCalificado = trabajoServicio.TrabajoCalificadosProveedor(usuario.getId());
                model.addAttribute("trabajoCalificado", trabajoCalificado);
            }

            if (usuario.getRol().equals(Rol.USUARIO)) {

                // Trabajo creado en estado Pendiente con x proveedor
                List<Trabajo> listaUsuariosPen = trabajoServicio.TrabajoPorUsuarioEstado(usuario.getId(), Estado.PENDIENTE);
                model.addAttribute("usuariosPendientes", listaUsuariosPen);

                // Trabajo en curso en estado Aceptado con x proveedor
                List<Trabajo> listaUsuariosAcep = trabajoServicio.TrabajoPorUsuarioEstado(usuario.getId(), Estado.ACEPTADO);
                model.addAttribute("usuariosAceptados", listaUsuariosAcep);

                // Trabajo terminado en estado Completo con x proveedor
                List<Trabajo> listaUsuariosCom = trabajoServicio.TrabajoPorUsuarioEstado(usuario.getId(), Estado.FINALIZADO);
                model.addAttribute("usuariosCompleto", listaUsuariosCom);

                // Lista de trabajos calificados
                List<Trabajo> trabajoCalificado = trabajoServicio.TrabajoCalificadosUsuario(usuario.getId());
                model.addAttribute("trabajoCalificado", trabajoCalificado);
            }

            return "contrataciones.html";

        } else {
            return "redirect:/login";
        }
    }

    @PostMapping("/contrataciones/aceptar/{id}")
    public String aceptar(@PathVariable String id, RedirectAttributes redirectAttributes) {
        try {
            trabajoServicio.modificarEstado(id, Estado.ACEPTADO);
            redirectAttributes.addFlashAttribute("exito", "Trabajo aceptado con éxito");
        } catch (miException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/usuario/contrataciones";
    }

    @PostMapping("/contrataciones/rechazar/{id}")
    public String rechazar(@PathVariable String id, RedirectAttributes redirectAttributes) {
        try {
            trabajoServicio.modificarEstado(id, Estado.CANCELADO);
            redirectAttributes.addFlashAttribute("exito", "Trabajo rechazado con éxito");
        } catch (miException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/usuario/contrataciones";
    }

    @PostMapping("/contrataciones/finalizar/{id}")
    public String finalizar(@PathVariable String id, RedirectAttributes redirectAttributes) {
        try {
            trabajoServicio.modificarEstado(id, Estado.FINALIZADO);
            redirectAttributes.addFlashAttribute("exito", "Trabajo finalizado con éxito");
        } catch (miException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/usuario/contrataciones";
    }

}
