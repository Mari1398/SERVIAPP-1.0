package com.egg.ServiApp.controladores;

import com.egg.ServiApp.entidades.Especialidad;
import com.egg.ServiApp.entidades.Proveedor;
import com.egg.ServiApp.entidades.Usuario;
import com.egg.ServiApp.servicios.usuarioServicio;
import excepciones.miException;
import java.util.List;
import javax.servlet.http.HttpSession;
import com.egg.ServiApp.servicios.especialidadServicio;
import java.util.ArrayList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 *
 * @author catal
 */

@Controller
@RequestMapping("/")
public class PortalControlador {

    @Autowired
    private usuarioServicio us;

    @Autowired
    private especialidadServicio especialidadServicio;

    @GetMapping("/")
    public String index(ModelMap model) {

        List<Especialidad> especialidades = especialidadServicio.listarEspecialidades();
        model.addAttribute("especialidades", especialidades);

        List<Proveedor> listFull = us.listarProveedores();
        List<Proveedor> listProveedoresFull = new ArrayList();
        List<Proveedor> listProveedores = new ArrayList();
        for (Proveedor prov : listFull) {
            if (prov.getPuntuacion() >= 3) {
                listProveedoresFull.add(prov);
            }
        }

        for (int i = 0; i < 6; i++) {

            int randomIndex = (int) (Math.random() * listProveedoresFull.size());
            Proveedor p = listProveedoresFull.get(randomIndex);
            listProveedores.add(p);
            listProveedoresFull.remove(p);
        }

        for (Proveedor listProveedore : listProveedores) {
            System.out.println(listProveedore.getNombre() + " " + listProveedore.getEspecialidad().getNombre()
                    + " " + listProveedore.getTelefono() + " " + listProveedore.getPuntuacion());
        }

        model.addAttribute("proveedores", listProveedores);

        return "index.html";

    }

    @GetMapping("/registroUsuario")
    public String registroUsuario() {

        return "regUser.html";

    }

    @GetMapping("/registroProveedor")
    public String registroProveedor(ModelMap model) {
        List<Especialidad> especialidades = especialidadServicio.listarEspecialidades();
        model.addAttribute("especialidades", especialidades);
        return "regProvider.html";
    }

    @PreAuthorize("hasAnyRole( 'ROLE_USUARIO','ROLE_PROVEEDOR','ROLE_ADMINISTRADOR')")
    @GetMapping("/perfil")
    public String perfil(ModelMap modelo, HttpSession session) {
        Usuario usuario = (Usuario) session.getAttribute("usuariosession");
        modelo.put("usuario", usuario);
        return "usuario_modificar.html";
    }

    @PostMapping("/registrarUsuario")
    public String registarUsuario(@RequestParam String nombre, @RequestParam String email, @RequestParam String password, String password2, Long telefono, ModelMap modelo) {
        try {
            us.crearUsuario(nombre, email, password, password2, telefono);
            modelo.put("exito", "Se ha registrado con éxito!");
            return "login.html";
        } catch (miException ex) {
            modelo.put("error", ex.getMessage());
            return "regUser.html";
        }

    }

    @PostMapping("/registrarProveedor")

    public String registarProveedor(@RequestParam String nombre, @RequestParam String email, @RequestParam String password, String password2, Long telefono, double costoHora, String especialidad, ModelMap modelo) {

        try {
            us.crearProveedor(nombre, email, password, password2, telefono, costoHora, especialidad);
            modelo.put("exito", "Se ha registrado con éxito!");
            return "login.html";
        } catch (miException ex) {
            List<Especialidad> especialidades = especialidadServicio.listarEspecialidades();
            modelo.addAttribute("especialidades", especialidades);
            modelo.put("error", ex.getMessage());
            return "regProvider.html";
        }

    }

    @GetMapping("/login")
    public String login(ModelMap modelo, HttpSession session) {

        Usuario logueado = (Usuario) session.getAttribute("usuario");
        modelo.addAttribute("modelousuario", logueado);
//        if (error != null) {
//            System.out.println("Error en login");
//            modelo.put("error", "Usuario o Contrasena invalidos");
//        }
        return "login.html";
    }
    
    
}
 
