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
        if (usuariosRepository.findByMail(mail).isPresent() || usuariosRepository.findById(idUsuario).isPresent()) {
            return false;
        }

        // Crear y guardar el usuario
        Usuarios nuevoUsuario = new Usuarios();
        nuevoUsuario.setMail(mail);
        nuevoUsuario.setIdUsuario(idUsuario);
        nuevoUsuario.setMedioPago(medioPago);
        nuevoUsuario.setTipo("alumno"); 
        usuariosRepository.save(nuevoUsuario);

        // Crear y guardar el alumno asociado
        Alumnos nuevoAlumno = new Alumnos();
        nuevoAlumno.setIdAlumno(nuevoUsuario.getIdUsuario()); 
        nuevoAlumno.setDniFrente(dniFrente);
        nuevoAlumno.setDniFondo(dniFondo);
        nuevoAlumno.setTramite(tramite);
        nuevoAlumno.setCuentaCorriente(BigDecimal.ZERO); //se la seteo en 0
        nuevoAlumno.setUsuario(nuevoUsuario); 

        alumnosRepository.save(nuevoAlumno);

        enviarCorreoDeConfirmacion(mail);

        return true;
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
