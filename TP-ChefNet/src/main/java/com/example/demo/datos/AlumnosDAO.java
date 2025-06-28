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
            System.out.println("Intentando registrar alumno con email: " + mail);
            
            // Buscar usuario existente por email
            Optional<Usuarios> existingUser = usuariosRepository.findByMail(mail);
            Usuarios usuario;
            
            if (existingUser.isPresent()) {
                // Si el usuario existe, verificar si ya es alumno
                usuario = existingUser.get();
                System.out.println("Usuario encontrado: " + usuario.getMail() + " - Tipo: " + usuario.getTipo());
                
                // Verificar si ya es alumno
                if (alumnosRepository.findById(usuario.getIdUsuario()).isPresent()) {
                    System.out.println("El usuario ya es un alumno");
                    return false; // Ya es un estudiante
                }
                
                // Actualizar usuario existente a tipo alumno
                usuario.setTipo("alumno");
                if (medioPago != null && !medioPago.trim().isEmpty()) {
                    usuario.setMedioPago(medioPago);
                }
                usuario = usuariosRepository.save(usuario);
                System.out.println("Usuario actualizado a tipo alumno");
                
            } else {
                // Crear nuevo usuario directamente como alumno
                System.out.println("Usuario no encontrado, creando nuevo usuario como alumno");
                usuario = new Usuarios();
                usuario.setMail(mail);
                usuario.setTipo("alumno");
                usuario.setHabilitado("no"); // Se habilitará después de verificar email
                usuario.setMedioPago(medioPago != null ? medioPago : "");
                usuario.setPassword("temp123"); // Contraseña temporal - se cambiará en verificación
                usuario.setNombre("Alumno Nuevo"); // Nombre temporal - se cambiará en verificación  
                usuario.setDireccion("");
                usuario.setAvatar("");
                usuario.setNickname("alumno" + System.currentTimeMillis()); // Nickname único temporal
                usuario.setRol("user");
                usuario = usuariosRepository.save(usuario);
                System.out.println("Nuevo usuario creado como alumno con ID: " + usuario.getIdUsuario());
            }
            
            // Crear registro de alumno
            Alumnos nuevoAlumno = new Alumnos();
            nuevoAlumno.setNroTarjeta(medioPago != null ? medioPago : ""); 
            nuevoAlumno.setDniFrente(dniFrente != null ? dniFrente : "");
            nuevoAlumno.setDniFondo(dniFondo != null ? dniFondo : "");
            nuevoAlumno.setTramite(tramite != null ? tramite : "");
            nuevoAlumno.setCuentaCorriente(BigDecimal.ZERO);
            nuevoAlumno.setUsuario(usuario);
            alumnosRepository.save(nuevoAlumno);
            System.out.println("Alumno registrado exitosamente con ID: " + nuevoAlumno.getIdAlumno());

            // Generar código de verificación de 4 dígitos
            String codigo = String.format("%04d", new java.util.Random().nextInt(10000));
            usuario.setCodigoRecuperacion(codigo);
            usuario.setVerificationCodeSentAt(java.time.LocalDateTime.now());
            usuariosRepository.save(usuario);

            // Enviar email con el código de verificación
            try {
                MimeMessage message = emailSender.createMimeMessage();
                MimeMessageHelper helper = new MimeMessageHelper(message, true);
                helper.setTo(mail);
                helper.setSubject("Código de verificación - ChefNet");
                helper.setText("¡Bienvenido a ChefNet! 👨‍🍳\n\n" +
                    "Para completar tu registro como alumno, necesitamos verificar tu email.\n\n" +
                    "Tu código de verificación es: " + codigo + "\n\n" +
                    "⏰ Este código es válido por 24 horas.\n" +
                    "🔒 Por tu seguridad, no compartas este código con nadie.\n\n" +
                    "Una vez verificado, podrás completar tu perfil con contraseña y datos adicionales.\n\n" +
                    "¡Gracias por unirte a ChefNet!\n\n---\nEl equipo de ChefNet");
                emailSender.send(message);
                System.out.println("Correo de verificación enviado con código: " + codigo);
            } catch (Exception emailError) {
                System.out.println("Error enviando correo de verificación: " + emailError.getMessage());
                // No fallar el registro por error de email
            }
            
            return true;
            
        } catch (Exception e) {
            System.out.println("Error en registrarAlumno: " + e.getMessage());
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
