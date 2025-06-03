package com.example.demo.datos;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Repository;

import com.example.demo.modelo.Alumnos;
import com.example.demo.modelo.Usuarios;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Repository
public class AlumnosDAO {
    @Autowired
	AlumnosRepository alumnosRepository;
    
    @Autowired
	UsuariosRepository usuariosRepository;
	
    @Autowired
    private JavaMailSender emailSender;
	
	public List<Alumnos> getAllAlumnos(AlumnosRepository alumnosRepository){
		return alumnosRepository.findAll();
	}
	
    public void save(Alumnos alumnos) {
    	alumnosRepository.save(alumnos);
    }
	
	public void delete(Alumnos alumnos) {
		alumnosRepository.delete(alumnos);;
	}
	
    public boolean registrarAlumno(String mail, Integer idUsuario, String medioPago, String dniFrente, String dniFondo, String tramite) {
        try {
            // Check if user already exists by email
            Optional<Usuarios> existingUser = usuariosRepository.findByMail(mail);
            Usuarios usuario;
            
            if (existingUser.isPresent()) {
                // If user exists, try to upgrade them to alumno
                usuario = existingUser.get();
                
                // Check if they are already an alumno
                if (alumnosRepository.findById(usuario.getIdUsuario()).isPresent()) {
                    return false; // Already a student
                }
            } else {
                // Create new user for student registration
                usuario = new Usuarios();
                usuario.setMail(mail);
                usuario.setTipo("alumno");
                usuario.setHabilitado("no"); // Will be enabled after email verification
                usuario.setMedioPago(medioPago);
                // Set default values for required fields
                usuario.setPassword(""); // Will be set during verification
                usuario.setNombre(""); // Will be set during verification
                usuario.setDireccion("");
                usuario.setAvatar("");
                usuario.setNickname(""); // Will be set during verification
                
                usuario = usuariosRepository.save(usuario);
            }
            
            // Upgrade existing user to alumno or set type for new user
            usuario.setTipo("alumno");
            usuario.setMedioPago(medioPago);
            usuariosRepository.save(usuario);
            
            // Create alumno record
            Alumnos nuevoAlumno = new Alumnos();
            nuevoAlumno.setIdAlumno(usuario.getIdUsuario());
            nuevoAlumno.setNroTarjeta(medioPago); // Store payment method
            nuevoAlumno.setDniFrente(dniFrente);
            nuevoAlumno.setDniFondo(dniFondo);
            nuevoAlumno.setTramite(tramite);
            nuevoAlumno.setCuentaCorriente(BigDecimal.ZERO);
            nuevoAlumno.setUsuario(usuario);
            alumnosRepository.save(nuevoAlumno);
            
            // Send confirmation email
            enviarCorreoDeConfirmacion(mail);
            
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
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
	

}
