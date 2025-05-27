package com.example.demo.datos;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.Random;

import org.apache.catalina.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Repository;

import com.example.demo.modelo.Alumnos;
import com.example.demo.modelo.Recetas;
import com.example.demo.modelo.Usuarios;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Repository
public class UsuariosDAO {
    @Autowired
	UsuariosRepository usuariosRepository;
	
    @Autowired
    private RecetasRepository recetasRepository;
    
    @Autowired
    private AlumnosRepository alumnosRepository;
    
    @Autowired
    private JavaMailSender emailSender;
	
	public List<Usuarios> getAllUsuarios(UsuariosRepository usuariosRepository){
		return usuariosRepository.findAll();
	}
	
    public void save(Usuarios usuarios) {
    	usuariosRepository.save(usuarios);
    }
	
	public void delete(Usuarios usuarios) {
		usuariosRepository.delete(usuarios);;
	}
	
    public List<Recetas> obtenerRecetas(Usuarios usuario) {
        return recetasRepository.findByUsuario(usuario);  
    }
    
    public Recetas cargarReceta(Recetas receta) {
        // Ensure new recipes are not authorized by default (pending approval)
        if (receta.getIdReceta() == null) {
            receta.setAutorizada(false);
        }
        return recetasRepository.save(receta);
    }
    
    public Usuarios findById(int id) {
        return usuariosRepository.findById(id).orElse(null);
    }
    
    public Recetas escalarReceta(Integer idReceta, int factor) {
        Recetas receta = recetasRepository.findById(idReceta)
                                          .orElseThrow(() -> new RuntimeException("Receta no encontrada"));

        receta.setPorciones(receta.getPorciones() * factor);
        receta.setCantidadPersonas(receta.getCantidadPersonas() * factor);
        recetasRepository.save(receta);
        return receta;
    }
    
    public List<Recetas> buscarRecetasPorIngredientes(List<String> ingredientes, List<String> ingredientesAusentes) {
        if (ingredientesAusentes != null && !ingredientesAusentes.isEmpty()) {
            return recetasRepository.findBySinIngredientes(ingredientesAusentes);
        } 
        else if (ingredientes != null && !ingredientes.isEmpty()) {
            return recetasRepository.findByIngredientesNombre(ingredientes);
        }
        return recetasRepository.findAll();
    }
    
    public boolean registrarVisitante(String mail, Integer idUsuario) {
        // Verificar si el correo o alias ya están registrados
        Optional<Usuarios> usuarioExistente = usuariosRepository.findByMail(mail);
        if (usuarioExistente.isPresent()) {
            return false; // El correo ya está registrado
        }

        // Verificar si el alias ya está registrado
        Optional<Usuarios> idUsuarioExistente = usuariosRepository.findById(idUsuario);
        if (idUsuarioExistente.isPresent()) {
            return false; // El alias ya está registrado
        }

        // Si el correo y el alias son válidos, crear un nuevo visitante
        Usuarios nuevoVisitante = new Usuarios();
        nuevoVisitante.setMail(mail);
        nuevoVisitante.setIdUsuario(idUsuario);

        // Guardar el nuevo visitante
        usuariosRepository.save(nuevoVisitante);

        enviarCorreoDeConfirmacion(mail);

        return true; // Registro exitoso
    }


    public void enviarCorreoDeConfirmacion(String toEmail) {
        try {
            MimeMessage message = emailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setTo(toEmail);
            helper.setSubject("Confirmación de registro");
            helper.setText("¡Gracias por registrarte! Por favor, confirma tu correo haciendo clic en el siguiente enlace.");
            emailSender.send(message);
        } catch (MessagingException e) {
            e.printStackTrace(); 
        }
    }
    
    public boolean cambiarAAlumno(int idUsuario, Alumnos alumnoData) {
        Optional<Usuarios> usuarioOpt = usuariosRepository.findById(idUsuario);
        
        if (usuarioOpt.isPresent()) {
            Usuarios usuario = usuarioOpt.get();

            // Verificamos si ya es alumno
            if (usuario.getAlumno() != null) {
                return false;
            }

            // Creamos un nuevo objeto Alumnos y lo vinculamos al usuario
            Alumnos nuevoAlumno = new Alumnos();
            nuevoAlumno.setIdAlumno(idUsuario); // porque usan el mismo ID
            nuevoAlumno.setDniFrente(alumnoData.getDniFrente());
            nuevoAlumno.setDniFondo(alumnoData.getDniFondo());
            nuevoAlumno.setTramite(alumnoData.getTramite());
            nuevoAlumno.setCuentaCorriente(BigDecimal.ZERO); //se la seteo en 0 
            nuevoAlumno.setUsuario(usuario);

            alumnosRepository.save(nuevoAlumno);

            usuario.setTipo("alumno");
            usuariosRepository.save(usuario);

            return true;
        }

        return false;
    }
    
    public boolean enviarCodigoRecuperacion(String mail) {
    	Optional<Usuarios> usuario = usuariosRepository.findByMail(mail);
        if (usuario.isPresent()) {
            Usuarios usuarios = usuario.get();

            String codigo = String.format("%06d", new Random().nextInt(999999));

            usuarios.setCodigoRecuperacion(codigo);
            usuariosRepository.save(usuarios);

            // Enviar el mail
            SimpleMailMessage mensaje = new SimpleMailMessage();
            mensaje.setTo(mail);
            mensaje.setSubject("Código de recuperación de contraseña");
            mensaje.setText("Tu código de recuperación es: " + codigo);
            emailSender.send(mensaje);

            return true;
        }

        return false;
    }
    
    public void agregarAListaRecetas(Usuarios usuario, Recetas receta) {
        List<Recetas> recetasAIntentar = usuario.getRecetasAIntentar();
        if (!recetasAIntentar.contains(receta)) {
            recetasAIntentar.add(receta);
            usuariosRepository.save(usuario);
        }
    }
    
    public Usuarios getUsuarioAutenticado() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        
        if (principal instanceof User) {
            String username = ((User) principal).getUsername();
            
            return usuariosRepository.findByMail(username).orElse(null); // O el campo que estés usando para autenticar
        }
        return null; 
    }


}
