package com.example.demo.datos;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;

import org.apache.catalina.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

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
                emailThread.join(30000); // Timeout de 30 segundos (aumentado de 5000 a 30000)
                
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
        System.out.println("🟡 UsuariosDAO: Iniciando registro de usuario con verificación - Email: " + correoElectronico + ", Alias: " + alias);
        // Verificar si el correo ya está registrado
        Optional<Usuarios> usuarioExistentePorCorreo = usuariosRepository.findByMail(correoElectronico);
        if (usuarioExistentePorCorreo.isPresent()) {
            System.out.println("🔴 UsuariosDAO: Email ya registrado: " + correoElectronico);
            return false; // El correo ya está registrado
        }
        // Verificar si el alias ya está registrado
        boolean aliasExiste = usuariosRepository.findAll().stream()
            .anyMatch(usuario -> alias.equalsIgnoreCase(usuario.getNickname()));
        if (aliasExiste) {
            System.out.println("🔴 UsuariosDAO: Alias ya registrado: " + alias);
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
        // Generar código de verificación de 4 dígitos
        String codigo = String.format("%04d", new java.util.Random().nextInt(10000));
        nuevoUsuario.setCodigoRecuperacion(codigo);
        nuevoUsuario.setVerificationCodeSentAt(java.time.LocalDateTime.now());
        usuariosRepository.save(nuevoUsuario);
        // Enviar email con el código de verificación
        try {
            jakarta.mail.internet.MimeMessage message = emailSender.createMimeMessage();
            org.springframework.mail.javamail.MimeMessageHelper helper = new org.springframework.mail.javamail.MimeMessageHelper(message, true);
            helper.setTo(correoElectronico);
            helper.setSubject("Código de verificación - ChefNet");
            helper.setText("¡Bienvenido a ChefNet! 👨‍🍳\n\n" +
                "Para completar tu registro como usuario, necesitamos verificar tu email.\n\n" +
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
    }

    // VISITANTES: Registro en 2 etapas con código de verificación
    public boolean registrarVisitanteEtapa1(String correoElectronico, String alias) {
        System.out.println("🟡 UsuariosDAO: Iniciando registro de visitante con verificación - Email: " + correoElectronico + ", Alias: " + alias);
        
        // Verificar si el correo ya está registrado
        Optional<Usuarios> usuarioExistentePorCorreo = usuariosRepository.findByMail(correoElectronico);
        if (usuarioExistentePorCorreo.isPresent()) {
            System.out.println("🔴 UsuariosDAO: Email ya registrado: " + correoElectronico);
            return false; // El correo ya está registrado
        }

        // Verificar si el alias ya está registrado
        boolean aliasExiste = usuariosRepository.findAll().stream()
            .anyMatch(usuario -> alias.equalsIgnoreCase(usuario.getNickname()));
        if (aliasExiste) {
            System.out.println("🔴 UsuariosDAO: Alias ya registrado: " + alias);
            return false; // El alias ya está registrado
        }

        try {
            // Crear nuevo visitante en estado pendiente de verificación
            Usuarios nuevoVisitante = new Usuarios();
            nuevoVisitante.setMail(correoElectronico);
            nuevoVisitante.setNickname(alias);
            nuevoVisitante.setPassword("NO_REQUIERE"); // Los visitantes no necesitan contraseña
            nuevoVisitante.setNombre("Visitante");
            nuevoVisitante.setHabilitado("No"); // No habilitado hasta verificar código
            nuevoVisitante.setTipo("visitante");
            nuevoVisitante.setDireccion("");
            nuevoVisitante.setAvatar("");
            nuevoVisitante.setRol("visitante");

            // Guardar el nuevo visitante en estado pendiente
            System.out.println("🟡 UsuariosDAO: Guardando visitante pendiente en base de datos...");
            usuariosRepository.save(nuevoVisitante);
            System.out.println("🟢 UsuariosDAO: Visitante pendiente guardado exitosamente en base de datos");

            // Enviar código de verificación
            return enviarCodigoVerificacionVisitante(correoElectronico);
            
        } catch (Exception e) {
            System.out.println("🔴 UsuariosDAO: Error guardando visitante pendiente: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    // Verificar código de verificación para visitante
    public Usuarios verificarCodigoVisitante(String correoElectronico, String codigoIngresado) {
        System.out.println("🟡 UsuariosDAO: Verificando código de visitante - Email: " + correoElectronico + ", Código: " + codigoIngresado);
        
        try {
            // Buscar el visitante por email
            Optional<Usuarios> visitanteOpt = usuariosRepository.findByMail(correoElectronico);
            if (!visitanteOpt.isPresent()) {
                System.out.println("🔴 UsuariosDAO: Visitante no encontrado: " + correoElectronico);
                return null;
            }

            Usuarios visitante = visitanteOpt.get();
            
            // Verificar que sea un visitante y esté pendiente de verificación
            if (!"visitante".equals(visitante.getTipo()) || !"No".equals(visitante.getHabilitado())) {
                System.out.println("🔴 UsuariosDAO: Visitante no está en estado pendiente de verificación");
                return null;
            }

            // Verificar que el código coincida (usar el mismo patrón que usuarios normales)
            if (visitante.getCodigoRecuperacion() != null && 
                visitante.getCodigoRecuperacion().equals(codigoIngresado)) {
                
                // Código válido - habilitar visitante completamente
                visitante.setHabilitado("Si");
                visitante.setCodigoRecuperacion(null); // Limpiar código usado
                usuariosRepository.save(visitante);
                
                System.out.println("🟢 UsuariosDAO: Visitante verificado y habilitado exitosamente");
                return visitante; // Retornar el usuario completo
            } else {
                System.out.println("🔴 UsuariosDAO: Código de verificación incorrecto");
                return null;
            }
            
        } catch (Exception e) {
            System.out.println("🔴 UsuariosDAO: Error verificando código de visitante: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    // Enviar código de verificación para visitante (reutilizar lógica de usuarios)
    public boolean enviarCodigoVerificacionVisitante(String correoElectronico) {
        System.out.println("🟡 UsuariosDAO: Enviando código de verificación a visitante: " + correoElectronico);
        
        try {
            // Buscar el visitante por email
            Optional<Usuarios> visitanteOpt = usuariosRepository.findByMail(correoElectronico);
            if (!visitanteOpt.isPresent()) {
                System.out.println("🔴 UsuariosDAO: Visitante no encontrado: " + correoElectronico);
                return false;
            }

            Usuarios visitante = visitanteOpt.get();
            
            // Generar código de 4 dígitos
            String codigo = String.format("%04d", (int)(Math.random() * 10000));
            
            // Guardar código en el campo existente (como usuarios normales)
            visitante.setCodigoRecuperacion(codigo);
            usuariosRepository.save(visitante);
            
            // Enviar email con el código
            SimpleMailMessage mensaje = new SimpleMailMessage();
            mensaje.setFrom("rossomaxi685@gmail.com");
            mensaje.setTo(correoElectronico);
            mensaje.setSubject("Código de verificación - ChefNet");
            mensaje.setText(
                "¡Hola! 👨‍🍳\n\n" +
                "Tu código de verificación para completar el registro como visitante en ChefNet es:\n\n" +
                "📱 " + codigo + "\n\n" +
                "Este código es válido por 24 horas.\n\n" +
                "Si no solicitaste este código, ignora este mensaje.\n\n" +
                "¡Gracias por unirte a ChefNet!\n\n" +
                "---\n" +
                "El equipo de ChefNet"
            );
            
            System.out.println("🟡 UsuariosDAO: Enviando email con código: " + codigo);
            emailSender.send(mensaje);
            System.out.println("🟢 UsuariosDAO: Código de verificación enviado exitosamente");
            return true;
            
        } catch (Exception e) {
            System.out.println("🔴 UsuariosDAO: Error enviando código de verificación: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
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
    
    @Transactional
    public boolean cambiarAAlumno(int idUsuario, Alumnos alumnoData, String password) {
        try {
            System.out.println("🟡 UsuariosDAO: Iniciando cambio a alumno para usuario ID: " + idUsuario);
            
            // Buscar usuario con lock para evitar concurrencia
            Optional<Usuarios> usuarioOpt = usuariosRepository.findById(idUsuario);
            
            if (!usuarioOpt.isPresent()) {
                System.out.println("🔴 UsuariosDAO: Usuario no encontrado con ID: " + idUsuario);
                return false;
            }

            Usuarios usuario = usuarioOpt.get();
            System.out.println("🟡 UsuariosDAO: Usuario encontrado: " + usuario.getMail() + ", tipo: " + usuario.getTipo());

            // Verificar si ya es alumno verificando tanto la propiedad como la existencia en la tabla
            if (usuario.getAlumno() != null) {
                System.out.println("🔴 UsuariosDAO: El usuario ya tiene un alumno asociado");
                return false;
            }
            
            if (alumnosRepository.existsById(idUsuario)) {
                System.out.println("🔴 UsuariosDAO: Ya existe un registro de alumno con ID: " + idUsuario);
                return false;
            }

            // Verificar que todos los datos requeridos estén presentes
            if (alumnoData.getTramite() == null || alumnoData.getTramite().trim().isEmpty()) {
                System.out.println("🔴 UsuariosDAO: Número de trámite es requerido");
                return false;
            }

            // Verificar que la contraseña esté presente para alumnos
            if (password == null || password.trim().isEmpty()) {
                System.out.println("🔴 UsuariosDAO: Contraseña es requerida para alumnos");
                return false;
            }

            // Primero actualizar el tipo de usuario y la contraseña
            System.out.println("🟡 UsuariosDAO: Actualizando tipo de usuario a 'alumno' y estableciendo contraseña...");
            usuario.setTipo("alumno");
            usuario.setPassword(password); // Establecer la contraseña para el alumno
            usuario = usuariosRepository.save(usuario);
            
            // Luego crear el registro de alumno
            System.out.println("🟡 UsuariosDAO: Creando registro de alumno...");
            Alumnos nuevoAlumno = new Alumnos();
            // NO establecer idAlumno manualmente, dejar que @MapsId lo maneje
            nuevoAlumno.setDniFrente(alumnoData.getDniFrente());
            nuevoAlumno.setDniFondo(alumnoData.getDniFondo());
            nuevoAlumno.setTramite(alumnoData.getTramite());
            nuevoAlumno.setNroTarjeta(alumnoData.getNroTarjeta());
            nuevoAlumno.setCuentaCorriente(BigDecimal.ZERO);
            // Establecer la relación con el usuario DESPUÉS de que el usuario esté guardado
            nuevoAlumno.setUsuario(usuario);

            System.out.println("🟡 UsuariosDAO: Datos del alumno: " + nuevoAlumno);
            System.out.println("🟡 UsuariosDAO: Guardando alumno...");
            
            Alumnos alumnoGuardado = alumnosRepository.save(nuevoAlumno);
            System.out.println("🟢 UsuariosDAO: Alumno guardado con ID: " + alumnoGuardado.getIdAlumno());

            // Actualizar la referencia en el usuario
            usuario.setAlumno(alumnoGuardado);
            usuariosRepository.save(usuario);

            System.out.println("🟢 UsuariosDAO: Usuario convertido a alumno exitosamente");
            return true;
            
        } catch (Exception e) {
            System.out.println("🔴 UsuariosDAO: Error cambiando usuario a alumno: " + e.getMessage());
            System.out.println("🔴 UsuariosDAO: Tipo de error: " + e.getClass().getSimpleName());
            if (e.getCause() != null) {
                System.out.println("🔴 UsuariosDAO: Causa: " + e.getCause().getMessage());
            }
            e.printStackTrace();
            // La transacción se revierte automáticamente
            return false;
        }
    }
    
    public boolean enviarCodigoRecuperacion(String mail) {
        Optional<Usuarios> usuario = usuariosRepository.findByMail(mail);
        if (usuario.isPresent()) {
            Usuarios usuarios = usuario.get();

            // Verificar que el usuario tenga registro completo
            if (!"Si".equals(usuarios.getHabilitado())) {
                return false; // No permitir recuperación para usuarios no habilitados
            }

            // Generar código de 4 dígitos para recuperación
            String codigo = String.format("%04d", new Random().nextInt(10000));

            usuarios.setCodigoRecuperacion(codigo);
            // Guardar timestamp para validez de 30 minutos
            usuarios.setVerificationCodeSentAt(java.time.LocalDateTime.now());
            usuariosRepository.save(usuarios);

            // Enviar el mail con validez de 30 minutos
            try {
                SimpleMailMessage mensaje = new SimpleMailMessage();
                mensaje.setTo(mail);
                mensaje.setSubject("Código de recuperación de contraseña - ChefNet");
                mensaje.setText(
                    "¡Hola! 👨‍🍳\n\n" +
                    "Recibimos una solicitud para restablecer tu contraseña en ChefNet.\n\n" +
                    "Tu código de recuperación es:\n\n" +
                    "🔐 " + codigo + "\n\n" +
                    "⏰ Este código es válido por 30 minutos únicamente.\n" +
                    "🔒 Por tu seguridad, no compartas este código con nadie.\n\n" +
                    "Si no solicitaste este cambio, ignora este mensaje y tu contraseña permanecerá sin cambios.\n\n" +
                    "---\n" +
                    "El equipo de ChefNet"
                );
                emailSender.send(mensaje);
                return true;
            } catch (Exception e) {
                System.out.println("Error enviando código de recuperación: " + e.getMessage());
                e.printStackTrace();
                return false;
            }
        }

        return false;
    }

    // Verificar código de recuperación (30 minutos de validez)
    public boolean verificarCodigoRecuperacion(String mail, String codigoIngresado) {
        Optional<Usuarios> usuarioOpt = usuariosRepository.findByMail(mail);
        if (usuarioOpt.isPresent()) {
            Usuarios usuario = usuarioOpt.get();
            
            // Verificar que el código coincida
            if (usuario.getCodigoRecuperacion() != null && 
                usuario.getCodigoRecuperacion().equals(codigoIngresado)) {
                
                // Verificar validez del código (30 minutos)
                if (usuario.getVerificationCodeSentAt() != null) {
                    java.time.LocalDateTime ahora = java.time.LocalDateTime.now();
                    java.time.LocalDateTime enviadoEn = usuario.getVerificationCodeSentAt();
                    long minutosTranscurridos = java.time.Duration.between(enviadoEn, ahora).toMinutes();
                    
                    if (minutosTranscurridos <= 30) {
                        // Código válido y dentro del tiempo límite
                        return true;
                    } else {
                        // Código expirado - limpiar código
                        usuario.setCodigoRecuperacion(null);
                        usuario.setVerificationCodeSentAt(null);
                        usuariosRepository.save(usuario);
                        return false;
                    }
                }
            }
        }
        return false; // Código inválido o usuario no encontrado
    }

    // Cambiar contraseña con código válido
    public boolean cambiarContrasenaConCodigo(String mail, String codigoIngresado, String nuevaPassword) {
        // Primero verificar que el código sea válido
        if (!verificarCodigoRecuperacion(mail, codigoIngresado)) {
            return false;
        }
        
        Optional<Usuarios> usuarioOpt = usuariosRepository.findByMail(mail);
        if (usuarioOpt.isPresent()) {
            Usuarios usuario = usuarioOpt.get();
            
            // Cambiar la contraseña
            usuario.setPassword(nuevaPassword); // En producción debería estar hasheada
            
            // Limpiar código de recuperación usado
            usuario.setCodigoRecuperacion(null);
            usuario.setVerificationCodeSentAt(null);
            
            usuariosRepository.save(usuario);
            
            // Enviar email de confirmación
            try {
                SimpleMailMessage mensaje = new SimpleMailMessage();
                mensaje.setTo(mail);
                mensaje.setSubject("Contraseña cambiada exitosamente - ChefNet");
                mensaje.setText(
                    "¡Hola! 👨‍🍳\n\n" +
                    "Tu contraseña ha sido cambiada exitosamente en ChefNet.\n\n" +
                    "Si no realizaste este cambio, por favor contacta inmediatamente con nuestro soporte.\n\n" +
                    "¡Gracias por usar ChefNet!\n\n" +
                    "---\n" +
                    "El equipo de ChefNet"
                );
                emailSender.send(mensaje);
            } catch (Exception e) {
                System.out.println("Error enviando confirmación de cambio de contraseña: " + e.getMessage());
                // No fallar el cambio de contraseña por error de email
            }
            
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
