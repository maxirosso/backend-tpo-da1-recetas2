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
            
            // Check for duplicates (only for new recipes)
            if (receta.getUsuario() != null && receta.getNombreReceta() != null) {
                Optional<Recetas> existingReceta = recetasRepository.findByNombreRecetaAndUsuario(
                    receta.getNombreReceta().trim(), receta.getUsuario());
                if (existingReceta.isPresent()) {
                    throw new RuntimeException("Ya existe una receta con este nombre para el usuario");
                }
            }
        }
        
        // Set current date if not provided
        if (receta.getFecha() == null) {
            receta.setFecha(java.time.LocalDate.now());
        }
        
        // Handle ingredientes relationship
        if (receta.getIngredientes() != null && !receta.getIngredientes().isEmpty()) {
            for (com.example.demo.modelo.Ingredientes ingrediente : receta.getIngredientes()) {
                // Set the bidirectional relationship
                ingrediente.setReceta(receta);
            }
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
    
    // Función para verificar si un alias/username está disponible
    public boolean isUsernameAvailable(String nickname) {
        try {
            // Verificar en tabla usuarios usando repository
            Optional<Usuarios> usuario = usuariosRepository.findByNickname(nickname);
            return !usuario.isPresent(); // Está disponible si NO se encuentra
        } catch (Exception e) {
            System.err.println("Error verificando disponibilidad de nickname: " + e.getMessage());
            return true; // Por defecto permitir si hay error
        }
    }

    // VISITANTES: Solo email de confirmación, sin código de verificación
    public boolean registrarVisitante(String correoElectronico, String alias) {
        System.out.println("🟡 UsuariosDAO: Iniciando registro de visitante - Email: " + correoElectronico + ", Alias: " + alias);
        
        // Verificar si el correo ya está registrado
        Optional<Usuarios> usuarioExistentePorCorreo = usuariosRepository.findByMail(correoElectronico);
        if (usuarioExistentePorCorreo.isPresent()) {
            System.out.println("🔴 UsuariosDAO: Email ya registrado: " + correoElectronico);
            return false; // El correo ya está registrado
        }

        // Verificar si el alias (nickname) ya está registrado
        boolean aliasExiste = usuariosRepository.findAll().stream()
            .anyMatch(usuario -> alias.equalsIgnoreCase(usuario.getNickname()));
        if (aliasExiste) {
            System.out.println("🔴 UsuariosDAO: Alias ya registrado: " + alias);
            return false; // El alias ya está registrado
        }

        try {
            // Crear nuevo visitante
        Usuarios nuevoVisitante = new Usuarios();
            nuevoVisitante.setMail(correoElectronico);
            nuevoVisitante.setNickname(alias);
            nuevoVisitante.setPassword("NO_REQUIERE"); // Los visitantes no necesitan contraseña
            nuevoVisitante.setNombre("Visitante");
            nuevoVisitante.setHabilitado("Si"); // Habilitado inmediatamente, sin verificación
            nuevoVisitante.setTipo("visitante");
            nuevoVisitante.setDireccion("");
            nuevoVisitante.setAvatar("");
            nuevoVisitante.setRol("visitante");

        // Guardar el nuevo visitante
            System.out.println("🟡 UsuariosDAO: Guardando visitante en base de datos...");
        usuariosRepository.save(nuevoVisitante);
            System.out.println("🟢 UsuariosDAO: Visitante guardado exitosamente en base de datos");

            // Enviar email de confirmación simple (sin código) - CON TIMEOUT
            System.out.println("🟡 UsuariosDAO: Enviando email de confirmación con timeout...");
            boolean emailEnviado = false;
            try {
                // Crear un thread separado para el envío de email con timeout
                Thread emailThread = new Thread(() -> {
                    try {
                        enviarEmailConfirmacionVisitante(correoElectronico, alias);
                    } catch (Exception e) {
                        System.out.println("🔴 Error en thread de email: " + e.getMessage());
                    }
                });
                
                emailThread.start();
                emailThread.join(5000); // Timeout de 5 segundos
                
                if (emailThread.isAlive()) {
                    System.out.println("🟠 UsuariosDAO: Timeout enviando email, pero registro completado");
                    emailThread.interrupt(); // Intentar interrumpir el thread
                } else {
                    System.out.println("🟢 UsuariosDAO: Proceso de email completado");
                    emailEnviado = true;
                }
            } catch (Exception e) {
                System.out.println("🟠 UsuariosDAO: Error enviando email, pero registro completado: " + e.getMessage());
            }

            // Retornar true siempre que el visitante se haya guardado en BD, independientemente del email
            return true; 
            
        } catch (Exception e) {
            System.out.println("🔴 UsuariosDAO: Error guardando visitante: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    // Método de prueba para verificar el envío de emails
    public void testEmailSend(String correoElectronico) {
        System.out.println("🧪 TEST: Iniciando prueba de envío de email a: " + correoElectronico);
        
        try {
            // Verificar conectividad antes de intentar enviar
            if (emailSender == null) {
                System.out.println("🔴 TEST: EmailSender no está configurado");
                throw new RuntimeException("EmailSender no configurado");
            }
            
            SimpleMailMessage mensaje = new SimpleMailMessage();
            mensaje.setFrom("rossomaxi685@gmail.com");
            mensaje.setTo(correoElectronico);
            mensaje.setSubject("Prueba de Email - ChefNet");
            mensaje.setText(
                "Este es un email de prueba desde ChefNet.\n\n" +
                "Si recibiste este mensaje, la configuración de email está funcionando correctamente.\n\n" +
                "---\n" +
                "Prueba automática del sistema"
            );
            
            System.out.println("🧪 TEST: Enviando email de prueba...");
            long startTime = System.currentTimeMillis();
            emailSender.send(mensaje);
            long endTime = System.currentTimeMillis();
            
            System.out.println("🟢 TEST: Email de prueba enviado exitosamente en " + (endTime - startTime) + "ms");
            
        } catch (org.springframework.mail.MailSendException e) {
            System.out.println("🔴 TEST: Error enviando email (MailSendException): " + e.getMessage());
            throw new RuntimeException("Error de envío: " + e.getMessage());
        } catch (org.springframework.mail.MailAuthenticationException e) {
            System.out.println("🔴 TEST: Error de autenticación de email: " + e.getMessage());
            throw new RuntimeException("Error de autenticación: " + e.getMessage());
        } catch (org.springframework.mail.MailException e) {
            System.out.println("🔴 TEST: Error de configuración de email: " + e.getMessage());
            throw new RuntimeException("Error de configuración: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("🔴 TEST: Error general enviando email: " + e.getMessage());
            System.out.println("🔴 TEST: Tipo de error: " + e.getClass().getSimpleName());
            if (e.getCause() != null) {
                System.out.println("🔴 TEST: Causa del error: " + e.getCause().getMessage());
            }
            e.printStackTrace(); 
            throw new RuntimeException("Error general: " + e.getMessage());
        }
    }

    // USUARIOS: Registro en 2 etapas con código de verificación
    public boolean registrarUsuarioEtapa1(String correoElectronico, String alias) {
        // Verificar si el correo ya está registrado
        Optional<Usuarios> usuarioExistentePorCorreo = usuariosRepository.findByMail(correoElectronico);
        if (usuarioExistentePorCorreo.isPresent()) {
            Usuarios usuarioExistente = usuarioExistentePorCorreo.get();
            if ("Si".equals(usuarioExistente.getHabilitado())) {
                return false; // Ya está completamente registrado
            } else {
                return false; // Registro incompleto (manejo especial necesario)
            }
        }

        // Verificar si el alias ya está registrado
        boolean aliasExiste = usuariosRepository.findAll().stream()
            .anyMatch(usuario -> alias.equalsIgnoreCase(usuario.getNickname()));
        if (aliasExiste) {
            return false; // El alias ya está registrado
        }

        // Crear nuevo usuario en estado pendiente
        Usuarios nuevoUsuario = new Usuarios();
        nuevoUsuario.setMail(correoElectronico);
        nuevoUsuario.setNickname(alias);
        nuevoUsuario.setPassword("PENDIENTE_VERIFICACION");
        nuevoUsuario.setNombre("PENDIENTE_COMPLETAR");
        nuevoUsuario.setHabilitado("No"); // No habilitado hasta verificar código
        nuevoUsuario.setTipo("usuario");
        nuevoUsuario.setDireccion("");
        nuevoUsuario.setAvatar("");
        nuevoUsuario.setRol("user");

        // Guardar el nuevo usuario
        usuariosRepository.save(nuevoUsuario);

        // Enviar código de verificación
        return enviarCodigoVerificacionUsuario(correoElectronico);
    }

    // Email de confirmación simple para visitantes (sin código)
    private void enviarEmailConfirmacionVisitante(String correoElectronico, String alias) {
        try {
            System.out.println("🟡 Preparando email de confirmación para: " + correoElectronico + " (" + alias + ")");
            
            // Verificar conectividad antes de intentar enviar
            if (emailSender == null) {
                System.out.println("🔴 EmailSender no está configurado");
                return;
            }
            
            SimpleMailMessage mensaje = new SimpleMailMessage();
            mensaje.setFrom("rossomaxi685@gmail.com"); // Especificar remitente explícitamente
            mensaje.setTo(correoElectronico);
            mensaje.setSubject("¡Te registraste correctamente en ChefNet!");
            mensaje.setText(
                "¡Hola " + alias + "! 👨‍🍳\n\n" +
                "Te registraste correctamente como visitante en ChefNet.\n\n" +
                "Ya puedes explorar nuestras recetas y ver los cursos disponibles.\n\n" +
                "Si en algún momento deseas acceder a funcionalidades adicionales como " +
                "escalar recetas o crear listas personalizadas, puedes registrarte como usuario.\n\n" +
                "¡Gracias por unirte a ChefNet!\n\n" +
                "---\n" +
                "El equipo de ChefNet"
            );
            
            System.out.println("🟡 Enviando email de confirmación a: " + correoElectronico);
            System.out.println("🟡 Asunto: " + mensaje.getSubject());
            System.out.println("🟡 Remitente: " + mensaje.getFrom());
            
            // Intentar enviar con logging detallado
            long startTime = System.currentTimeMillis();
            emailSender.send(mensaje);
            long endTime = System.currentTimeMillis();
            
            System.out.println("🟢 Email de confirmación enviado exitosamente a: " + correoElectronico + " en " + (endTime - startTime) + "ms");
            
        } catch (org.springframework.mail.MailSendException e) {
            System.out.println("🔴 Error enviando email (MailSendException): " + e.getMessage());
            if (e.getFailedMessages() != null && !e.getFailedMessages().isEmpty()) {
                System.out.println("🔴 Mensajes fallidos: " + e.getFailedMessages().size());
            }
        } catch (org.springframework.mail.MailAuthenticationException e) {
            System.out.println("🔴 Error de autenticación de email: " + e.getMessage());
        } catch (org.springframework.mail.MailException e) {
            System.out.println("🔴 Error de configuración de email: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("🔴 Error general enviando email a " + correoElectronico + ": " + e.getMessage());
            System.out.println("🔴 Tipo de error: " + e.getClass().getSimpleName());
            // Verificar si es un error de conectividad
            if (e.getCause() != null) {
                System.out.println("🔴 Causa del error: " + e.getCause().getMessage());
                System.out.println("🔴 Tipo de causa: " + e.getCause().getClass().getSimpleName());
            }
            e.printStackTrace(); // Stack trace completo para debugging
        }
    }

    // Email con código de verificación para usuarios
    public boolean enviarCodigoVerificacionUsuario(String correoElectronico) {
        Optional<Usuarios> usuarioOpt = usuariosRepository.findByMail(correoElectronico);
        if (usuarioOpt.isPresent()) {
            Usuarios usuario = usuarioOpt.get();

            // Generar código de 6 dígitos
            String codigoVerificacion = String.format("%06d", new Random().nextInt(999999));

            // Guardar código en el campo existente 
            usuario.setCodigoRecuperacion(codigoVerificacion);
            usuariosRepository.save(usuario);

            // Enviar email con código
            try {
                SimpleMailMessage mensaje = new SimpleMailMessage();
                mensaje.setTo(correoElectronico);
                mensaje.setSubject("Código de verificación - ChefNet");
                mensaje.setText(
                    "¡Bienvenido a ChefNet! 👨‍🍳\n\n" +
                    "Para completar tu registro como usuario, necesitamos verificar tu email.\n\n" +
                    "Tu código de verificación es: " + codigoVerificacion + "\n\n" +
                    "⏰ Este código es válido por 24 horas.\n" +
                    "🔒 Por tu seguridad, no compartas este código con nadie.\n\n" +
                    "Una vez verificado, podrás completar tu perfil con contraseña y datos adicionales.\n\n" +
                    "¡Gracias por unirte a ChefNet!\n\n" +
                    "---\n" +
                    "El equipo de ChefNet"
                );
                emailSender.send(mensaje);
                return true;
            } catch (Exception e) {
                System.out.println("Error enviando código de verificación: " + e.getMessage());
                e.printStackTrace();
                return false;
            }
        }
        return false;
    }

    public boolean verificarCodigoUsuario(String correoElectronico, String codigoIngresado) {
        Optional<Usuarios> usuarioOpt = usuariosRepository.findByMail(correoElectronico);
        if (usuarioOpt.isPresent()) {
            Usuarios usuario = usuarioOpt.get();
            
            // Verificar que el código coincida
            if (usuario.getCodigoRecuperacion() != null && 
                usuario.getCodigoRecuperacion().equals(codigoIngresado)) {
                
                // Código válido - limpiar código pero NO habilitar aún
                // Se habilitará cuando complete su perfil con contraseña
                usuario.setCodigoRecuperacion(null);
                usuariosRepository.save(usuario);
                return true;
            }
        }
        return false; // Código inválido o usuario no encontrado
    }

    public boolean completarRegistroUsuario(String correoElectronico, String nombre, String password) {
        Optional<Usuarios> usuarioOpt = usuariosRepository.findByMail(correoElectronico);
        if (usuarioOpt.isPresent()) {
            Usuarios usuario = usuarioOpt.get();
            
            // Completar datos del usuario
            usuario.setNombre(nombre);
            usuario.setPassword(password); // En producción debería estar hasheada
            usuario.setHabilitado("Si"); // Ahora sí habilitado completamente
            usuariosRepository.save(usuario);
            return true;
        }
        return false;
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
        try {
            Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            
            // Check if the principal is a Usuarios object (from JWT authentication)
            if (principal instanceof Usuarios) {
                return (Usuarios) principal;
            }
            
            // Fallback for other authentication types
            if (principal instanceof User) {
                String username = ((User) principal).getUsername();
                return usuariosRepository.findByMail(username).orElse(null);
            }
            
            return null;
        } catch (Exception e) {
            return null;
        }
    }

    // Mantener función original para compatibilidad con código existente
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
