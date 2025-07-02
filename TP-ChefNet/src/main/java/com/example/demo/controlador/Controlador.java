package com.example.demo.controlador;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Scanner;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.multipart.MultipartFile;

import com.example.demo.datos.*;
import com.example.demo.modelo.Alumnos;
import com.example.demo.modelo.RecetasAIntentar;
import com.example.demo.modelo.Calificaciones;
import com.example.demo.modelo.CronogramaCursos;
import com.example.demo.modelo.Cursos;
import com.example.demo.modelo.Ingredientes;
import com.example.demo.modelo.Inscripcion;
import com.example.demo.modelo.Multimedia;
import com.example.demo.modelo.Pasos;
import com.example.demo.modelo.Recetas;
import com.example.demo.modelo.RecetasGuardadas;
import com.example.demo.modelo.RecetasGuardadasId;
import com.example.demo.modelo.Sedes;
import com.example.demo.modelo.TiposReceta;
import com.example.demo.modelo.Usuarios;
import com.example.demo.util.JwtUtil;

@RestController
@RequestMapping("/")
public class Controlador {
	private static Controlador instancia;
	private Controlador() {}
	public static Controlador getInstancia() {
		if(instancia == null) {
			instancia = new Controlador();
		}
		return instancia;
	}
	
	@Autowired
	AlumnosRepository alumnosRepository;
	@Autowired
	AlumnosDAO alumnosDAO;

	@Autowired
	AsistenciaCursosRepository asistenciaCursosRepository;
	@Autowired
	AsistenciaCursosDAO asistenciaCursosDAO;
	
	@Autowired
	CalificacionesRepository calificacionesRepository;
	@Autowired
	CalificacionesDAO calificacionesDAO;
	
	@Autowired
	ConversionesRepository conversionesRepository;
	@Autowired
	ConversionesDAO conversionesDAO;
	
	@Autowired
	CronogramaCursosRepository cronogramaCursosRepository;
	@Autowired
	CronogramaCursosDAO cronogramaCursoDAO;
	
	@Autowired
	CursosRepository cursosRepository;
	@Autowired
	CursosDAO cursosDAO;
	
	@Autowired
	FotosRepository fotosRepository;
	@Autowired
	FotosDAO fotosDAO;
	
	@Autowired
	IngredientesRepository ingredientesRepository;
	@Autowired
	IngredientesDAO ingredientesDAO;
	
	@Autowired
	MultimediaRepository multimediaRepository;
	@Autowired
	MultimediaDAO multimediaDAO;
	
	@Autowired
	PasosRepository pasosRepository;
	@Autowired
	PasosDAO pasosDAO;
	
	@Autowired
	RecetasRepository recetasRepository;
	@Autowired
	RecetasDAO recetasDAO;
	
	@Autowired
	SedesRepository sedesRepository;
	@Autowired
	SedesDAO sedesDAO;
	
	@Autowired
	TiposRecetaRepository tiposRecetaRepository;
	@Autowired
	TiposRecetaDAO tiposRecetaDAO;
	
	@Autowired
	UnidadesRepository unidadesRepository;
	@Autowired
	UnidadesDAO unidadesDAO;
	
	@Autowired
	UsuariosRepository usuariosRepository;
	@Autowired
	UsuariosDAO usuariosDAO;
	
	@Autowired
	UtilizadosRepository utilizadosRepository;
	@Autowired
	UtilizadosDAO utilizadosDAO;
	
	@Autowired
	InscripcionRepository inscripcionRepository;
	@Autowired
	InscripcionDAO inscripcionDAO;
	
	@Autowired
	RecetasAIntentarRepository recetasAIntentarRepository;
	
	@Autowired
	RecetasGuardadasRepository recetasGuardadasRepository;
	
	@Autowired
	private JwtUtil jwtUtil;
	
	// Métodos utilitarios para validar permisos de usuario
	private boolean isVisitanteWithRestrictedAccess(Integer idUsuario) {
		if (idUsuario == null) return false;
		Usuarios usuario = usuariosDAO.findById(idUsuario);
		return usuario != null && "visitante".equals(usuario.getTipo());
	}
	
	private ResponseEntity<String> createVisitorRestrictionResponse() {
		return new ResponseEntity<>("Esta funcionalidad no está disponible para visitantes. Regístrate como usuario o alumno para acceder a todas las características.", HttpStatus.FORBIDDEN);
	}
	
	@GetMapping("/")
	public String mensaje() {
		return ("Comenzamos a ejecutar");
	}
    
    // Endpoint de prueba para verificar email
    @PostMapping("/test-email")
    public ResponseEntity<String> testEmail(@RequestParam String mail) {
        System.out.println("Probando envío de email a: " + mail);
        try {
            usuariosDAO.testEmailSend(mail);
            return new ResponseEntity<>("Email de prueba enviado a " + mail, HttpStatus.OK);
        } catch (Exception e) {
            System.out.println("Error en test de email: " + e.getMessage());
            return new ResponseEntity<>("Error enviando email de prueba: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
	
	//login usuario
	@PostMapping("/login")
	public ResponseEntity<?> login(@RequestParam String mail, @RequestParam String password) {
	    Usuarios user = usuariosRepository.findByMailAndPassword(mail, password);
	    if (user != null) {
	        // Genera JWT token
	        String token = jwtUtil.generateToken(
	            user.getMail(), 
	            user.getIdUsuario(), 
	            user.getTipo() != null ? user.getTipo() : "comun",
	            user.getRol() != null ? user.getRol() : "user"
	        );
	        
	        
	        Map<String, Object> response = new HashMap<>();
	        response.put("user", user);
	        response.put("token", token);
	        response.put("tokenType", "Bearer");
	        
	        return ResponseEntity.ok(response); 
	    }
	    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Credenciales inválidas");
	}
	
	//si tiene conexión se le mostrarán las 3 últimas recetas autorizadas cargadas por los usuarios (por ID más reciente)
	@GetMapping("/ultimasRecetas")
	public ResponseEntity<List<Map<String, Object>>> getUltimasRecetas() {
	    // Usar el método que trae las recetas autorizadas ordenadas por ID descendente (más nuevas primero)
	    List<Recetas> recetas = recetasRepository.findTop3ByAutorizadaTrueOrderByIdRecetaDesc();
	    
	    // Log para depuración
	    System.out.println("Últimas recetas solicitadas, encontradas: " + recetas.size());
	    for (Recetas r : recetas) {
	        System.out.println("ID: " + r.getIdReceta() + ", Nombre: " + r.getNombreReceta() + ", Autorizada: " + r.isAutorizada());
	    }
	    
	    
	    List<Map<String, Object>> recetasDTO = recetas.stream()
	        .map(receta -> {
	            Map<String, Object> dto = new HashMap<>();
	            dto.put("idReceta", receta.getIdReceta());
	            dto.put("nombreReceta", receta.getNombreReceta());
	            dto.put("descripcionReceta", receta.getDescripcionReceta());
	            dto.put("fotoPrincipal", receta.getFotoPrincipal());
	            dto.put("porciones", receta.getPorciones());
	            dto.put("cantidadPersonas", receta.getCantidadPersonas());
	            dto.put("fecha", receta.getFecha());
	            dto.put("autorizada", receta.isAutorizada());
	            dto.put("instrucciones", receta.getInstrucciones());
	            
	            // Calcular calificación promedio
	            List<Calificaciones> calificaciones = calificacionesRepository.findByIdReceta(receta);
	            if (!calificaciones.isEmpty()) {
	                double totalRating = calificaciones.stream()
	                    .filter(cal -> cal != null && cal.getCalificacion() > 0) // Filtrar calificaciones válidas
	                    .mapToDouble(cal -> cal.getCalificacion())
	                    .sum();
	                long validRatingsCount = calificaciones.stream()
	                    .filter(cal -> cal != null && cal.getCalificacion() > 0)
	                    .count();
	                
	                if (validRatingsCount > 0) {
	                    double averageRating = totalRating / validRatingsCount;
	                    dto.put("calificacionPromedio", Math.round(averageRating * 10.0) / 10.0); // Redondear a 1 decimal
	                } else {
	                    dto.put("calificacionPromedio", 0.0);
	                }
	                dto.put("totalCalificaciones", (int) validRatingsCount);
	            } else {
	                dto.put("calificacionPromedio", 0.0);
	                dto.put("totalCalificaciones", 0);
	            }
	            
	            if (receta.getIdTipo() != null) {
	                Map<String, Object> tipoDTO = new HashMap<>();
	                tipoDTO.put("idTipo", receta.getIdTipo().getIdTipo());
	                tipoDTO.put("descripcion", receta.getIdTipo().getDescripcion());
	                dto.put("tipo", tipoDTO);
	                dto.put("tipoReceta", tipoDTO); 
	            }
	            
	            if (receta.getUsuario() != null) {
	                Map<String, Object> usuarioDTO = new HashMap<>();
	                usuarioDTO.put("idUsuario", receta.getUsuario().getIdUsuario());
	                usuarioDTO.put("nombre", receta.getUsuario().getNombre());
	                usuarioDTO.put("mail", receta.getUsuario().getMail());
	                usuarioDTO.put("tipo", receta.getUsuario().getTipo());
	                dto.put("usuario", usuarioDTO);
	            }
	            
	            return dto;
	        })
	        .collect(Collectors.toList());
	    
	    return ResponseEntity.ok(recetasDTO);
	}
	
	
	//recuperar clave
	@PostMapping("/recuperarClave")
	public ResponseEntity<String> recuperarClave(@RequestParam String mail) {
	    Optional<Usuarios> usuario = usuariosRepository.findByMail(mail);

	    if (usuario.isPresent()) {
	        Usuarios usuarios = usuario.get();
	        String codigo = String.valueOf((int)(Math.random()*900000 + 100000)); 
	        usuarios.setCodigoRecuperacion(codigo);
	        usuariosRepository.save(usuarios);

	        usuariosDAO.enviarCorreoDeConfirmacion(mail);

	        return ResponseEntity.ok("Código enviado al correo.");
	    }

	    return ResponseEntity.badRequest().body("Correo no encontrado en el sistema.");
	}
	
	//registrar usuario
	@PostMapping("/registrarUsuario")
	public ResponseEntity<String> registrarUsuario(@RequestBody Usuarios usuario) {
	    if (usuario.getMail() == null || usuario.getMail().trim().isEmpty()) {
	        return ResponseEntity.badRequest().body("El correo electrónico es obligatorio");
	    }
	    if (usuario.getPassword() == null || usuario.getPassword().trim().isEmpty()) {
	        return ResponseEntity.badRequest().body("La contraseña es obligatoria");
	    }
	    if (usuario.getNickname() == null || usuario.getNickname().trim().isEmpty()) {
	        return ResponseEntity.badRequest().body("El nombre de usuario (nickname) es obligatorio");
	    }
	    if (usuariosRepository.existsByMail(usuario.getMail())) {
	        return ResponseEntity.badRequest().body("Ya existe un usuario con ese mail");
	    }
	    usuario.setTipo("comun"); 
	    usuario.setHabilitado("no"); 
	    usuariosRepository.save(usuario);
	    return ResponseEntity.ok("Usuario registrado exitosamente");
	}
	
	//get recetas usuario
    @GetMapping("/getRecetasUsuario")
    public ResponseEntity<List<Map<String, Object>>> obtenerRecetasUsuario(@RequestParam Integer idUsuario) {
        try {
            Usuarios usuario = usuariosDAO.findById(idUsuario);
            if (usuario == null) {
                return ResponseEntity.notFound().build();
            }
            
            List<Recetas> recetas = usuariosDAO.obtenerRecetas(usuario);
            
            
            List<Map<String, Object>> recetasDTO = recetas.stream()
                .map(receta -> {
                    Map<String, Object> dto = new HashMap<>();
                    dto.put("idReceta", receta.getIdReceta());
                    dto.put("nombreReceta", receta.getNombreReceta());
                    dto.put("descripcionReceta", receta.getDescripcionReceta());
                    dto.put("fotoPrincipal", receta.getFotoPrincipal());
                    dto.put("porciones", receta.getPorciones());
                    dto.put("cantidadPersonas", receta.getCantidadPersonas());
                    dto.put("fecha", receta.getFecha());
                    dto.put("autorizada", receta.isAutorizada());
                    dto.put("instrucciones", receta.getInstrucciones());
                    
                    if (receta.getIdTipo() != null) {
                        Map<String, Object> tipoDTO = new HashMap<>();
                        tipoDTO.put("idTipo", receta.getIdTipo().getIdTipo());
                        tipoDTO.put("descripcion", receta.getIdTipo().getDescripcion());
                        dto.put("tipo", tipoDTO);
                    }
                    
                    if (receta.getUsuario() != null) {
                        Map<String, Object> usuarioDTO = new HashMap<>();
                        usuarioDTO.put("idUsuario", receta.getUsuario().getIdUsuario());
                        usuarioDTO.put("nombre", receta.getUsuario().getNombre());
                        usuarioDTO.put("mail", receta.getUsuario().getMail());
                        usuarioDTO.put("tipo", receta.getUsuario().getTipo());
                        dto.put("usuario", usuarioDTO);
                    }
                    
                    // Agregar ingredientes
                    if (receta.getIngredientes() != null && !receta.getIngredientes().isEmpty()) {
                        List<Map<String, Object>> ingredientesDTO = receta.getIngredientes().stream()
                            .map(ingrediente -> {
                                Map<String, Object> ingDTO = new HashMap<>();
                                ingDTO.put("idIngrediente", ingrediente.getIdIngrediente());
                                ingDTO.put("nombre", ingrediente.getNombre());
                                ingDTO.put("cantidad", ingrediente.getCantidad());
                                ingDTO.put("unidadMedida", ingrediente.getUnidadMedida());
                                return ingDTO;
                            })
                            .collect(Collectors.toList());
                        dto.put("ingredientes", ingredientesDTO);
                    }
                    
                    // Agregar pasos
                    List<Pasos> pasos = pasosRepository.findByIdRecetaOrderByNroPaso(receta);
                    List<Map<String, Object>> pasosDTO = new ArrayList<>();
                    for (Pasos paso : pasos) {
                        Map<String, Object> pasoMap = new HashMap<>();
                        pasoMap.put("idPaso", paso.getIdPaso());
                        pasoMap.put("nroPaso", paso.getNroPaso());
                        pasoMap.put("texto", paso.getTexto());
                        pasosDTO.add(pasoMap);
                    }
                    dto.put("pasos", pasosDTO);
                    
                    
                    StringBuilder instruccionesStr = new StringBuilder();
                    for (Pasos paso : pasos) {
                        if (instruccionesStr.length() > 0) {
                            instruccionesStr.append("\n");
                        }
                        instruccionesStr.append(paso.getTexto());
                    }
                    dto.put("instrucciones", instruccionesStr.toString());
                    
                    return dto;
                })
                .collect(Collectors.toList());
                
            return new ResponseEntity<>(recetasDTO, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

 
    //Cargar nuevas recetas
    @PostMapping("/CargarNuevasRecetas")
    public ResponseEntity<Recetas> cargarNuevasRecetas(@RequestBody Recetas receta) {
        try {
            
            if (receta.getIngredientes() != null && !receta.getIngredientes().isEmpty()) {
                receta.getIngredientes().forEach(ingrediente -> {
                    ingrediente.setReceta(receta);
                });
            }
            
            
            if (receta.getUsuario() != null && "comun".equals(receta.getUsuario().getTipo())) {
                receta.setAutorizada(true);
                System.out.println("Receta autorizada automáticamente para usuario común: " + receta.getNombreReceta());
            }
            
            Recetas recetaGuardada = usuariosDAO.cargarReceta(receta);
            return new ResponseEntity<>(recetaGuardada, HttpStatus.CREATED);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    
    
    @PostMapping("/lista/{idUsuario}")
    public ResponseEntity<String> agregarAListaRecetas(@PathVariable Usuarios idUsuario, @RequestBody Recetas receta) {
        usuariosDAO.agregarAListaRecetas(idUsuario, receta);
        return new ResponseEntity<>("Receta agregada a la lista de recetas a intentar.", HttpStatus.OK);
    }
    
  
    //Multiplicar o dividir una receta (por personas o ingredientes)
    @PutMapping("/recetas/{idReceta}/escalar")
    public ResponseEntity<Recetas> escalarReceta(@PathVariable int idReceta, @RequestParam int factor) {
        Recetas recetaEscalada = usuariosDAO.escalarReceta(idReceta, factor);
        return new ResponseEntity<>(recetaEscalada, HttpStatus.OK);
    }
    
   
    //Buscar recetas por uno o más ingredientes o por su ausencia
    @GetMapping("/buscarRecetasSinIngredientes")
    public ResponseEntity<List<Recetas>> buscarRecetasPorIngredientesAusentes(
            @RequestParam List<String> ingredientes, 
            @RequestParam(required = false) List<String> ingredientesAusentes) {
        List<Recetas> recetas = usuariosDAO.buscarRecetasPorIngredientes(ingredientes, ingredientesAusentes);
        return new ResponseEntity<>(recetas, HttpStatus.OK);
    }
    
   
    //Registro de Visitantes (sin código de verificación)
    @PostMapping("/registrarVisitante")
    public ResponseEntity<String> registrarVisitante(@RequestParam String mail, @RequestParam String alias) {
        System.out.println("Iniciando registro de visitante - Email: " + mail + ", Alias: " + alias);
        
        // Verificar primero qué campo está duplicado para dar mensaje específico
        Optional<Usuarios> usuarioExistentePorCorreo = usuariosRepository.findByMail(mail);
        if (usuarioExistentePorCorreo.isPresent()) {
            System.out.println("Email ya registrado: " + mail);
            return new ResponseEntity<>("El email ya está registrado. Por favor elija otro.", HttpStatus.BAD_REQUEST);
        }

        // Verificar alias
        boolean aliasExiste = usuariosRepository.findAll().stream()
            .anyMatch(usuario -> alias.equalsIgnoreCase(usuario.getNickname()));
        if (aliasExiste) {
            System.out.println("Alias ya registrado: " + alias);
            return new ResponseEntity<>("El alias ya está registrado. Por favor elija otro.", HttpStatus.BAD_REQUEST);
        }

        // Si ambos están disponibles, proceder con el registro
        System.out.println("Validaciones pasadas, registrando visitante...");
        boolean registrado = usuariosDAO.registrarVisitante(mail, alias);
        if (registrado) {
            System.out.println("Visitante registrado exitosamente - Email: " + mail + ", Alias: " + alias);
            return new ResponseEntity<>("Te registraste correctamente como visitante. Se enviará un email de confirmación si hay conectividad.", HttpStatus.OK);
        }
        System.out.println("Error interno en el registro del visitante");
        return new ResponseEntity<>("Error interno en el registro. Intente nuevamente.", HttpStatus.INTERNAL_SERVER_ERROR);
    }

    //Registro de Visitantes con Verificación (Etapa 1: envío de código)
    @PostMapping("/registrarVisitanteEtapa1")
    public ResponseEntity<Map<String, Object>> registrarVisitanteEtapa1(@RequestParam String mail, @RequestParam String alias) {
        System.out.println("Iniciando registro de visitante con verificación - Email: " + mail + ", Alias: " + alias);
        
        Map<String, Object> response = new HashMap<>();
        
        // Verificar si el correo ya está registrado
        Optional<Usuarios> usuarioExistentePorCorreo = usuariosRepository.findByMail(mail);
        if (usuarioExistentePorCorreo.isPresent()) {
            Usuarios usuarioExistente = usuarioExistentePorCorreo.get();
            System.out.println("Email ya registrado: " + mail + ", Estado: " + usuarioExistente.getHabilitado());
            
            // Verificar si el registro previo se completó o quedó pendiente
            if ("Si".equals(usuarioExistente.getHabilitado())) {
                // Registro completado anteriormente
                response.put("error", "El email ya está registrado y el proceso se completó anteriormente.");
                response.put("success", false);
                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
            } else {
                // Registro incompleto - no puede usar este email
                response.put("error", "Este email tiene un proceso de registración incompleto. Para liberarlo deberás enviar un mail a la empresa para realizar un proceso por fuera de la aplicación móvil.");
                response.put("success", false);
                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
            }
        }

        // Verificar alias y generar sugerencias si está en uso
        boolean aliasExiste = usuariosRepository.findAll().stream()
            .anyMatch(usuario -> alias.equalsIgnoreCase(usuario.getNickname()));
        if (aliasExiste) {
            System.out.println("Alias ya registrado: " + alias + ", generando sugerencias...");
            
            // Generar sugerencias automáticamente
            List<String> sugerencias = generarSugerenciasAliasInterno(alias);
            
            response.put("error", "El alias ya está registrado.");
            response.put("aliasUnavailable", true);
            response.put("suggestions", sugerencias);
            response.put("success", false);
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }

        // Si todo está bien, proceder con el registro
        boolean registrado = usuariosDAO.registrarVisitanteEtapa1(mail, alias);
        if (registrado) {
            response.put("message", "Se ha enviado un código de verificación de 4 dígitos a tu correo. El código tiene una validez de 24 horas.");
            response.put("success", true);
            return new ResponseEntity<>(response, HttpStatus.OK);
        }
        
        response.put("error", "Error interno en el registro. Intente nuevamente.");
        response.put("success", false);
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    
    private List<String> generarSugerenciasAliasInterno(String baseAlias) {
        List<String> sugerencias = new ArrayList<>();
        String base = baseAlias.replaceAll("\\d+$", ""); 
        
        try {
            
            for (int i = 0; i < 10; i++) { 
                String sugerencia;
                switch (i % 6) {
                    case 0:
                        sugerencia = base + ((int)(Math.random() * 1000));
                        break;
                    case 1:
                        sugerencia = base + (new java.util.Date().getYear() + 1900);
                        break;
                    case 2:
                        sugerencia = base + "_chef";
                        break;
                    case 3:
                        sugerencia = "chef_" + base;
                        break;
                    case 4:
                        sugerencia = base + "_" + ((int)(Math.random() * 100));
                        break;
                    default:
                        sugerencia = base + ((int)(Math.random() * 10000));
                }
                
                
                boolean disponible = usuariosRepository.findAll().stream()
                    .noneMatch(usuario -> sugerencia.equalsIgnoreCase(usuario.getNickname()));
                
                if (disponible && !sugerencias.contains(sugerencia)) {
                    sugerencias.add(sugerencia);
                }
                
                
                if (sugerencias.size() >= 5) {
                    break;
                }
            }
        } catch (Exception e) {
            System.out.println("Error generando sugerencias internas: " + e.getMessage());
        }
        
        return sugerencias;
    }

    //Verificar código de visitante
    @PostMapping("/verificarCodigoVisitante")
    public ResponseEntity<Map<String, Object>> verificarCodigoVisitante(@RequestParam String mail, @RequestParam String codigo) {
        System.out.println("Verificando código de visitante - Email: " + mail + ", Código: " + codigo);
        Usuarios visitante = usuariosDAO.verificarCodigoVisitante(mail, codigo);
        
        Map<String, Object> response = new HashMap<>();
        
        if (visitante != null) {
            response.put("success", true);
            response.put("message", "Registro completado exitosamente. Ya puedes acceder como visitante a ChefNet.");
            response.put("user", Map.of(
                "idUsuario", visitante.getIdUsuario(),
                "mail", visitante.getMail(),
                "nickname", visitante.getNickname(),
                "tipo", visitante.getTipo(),
                "habilitado", visitante.getHabilitado()
            ));
            return new ResponseEntity<>(response, HttpStatus.OK);
        }
        
        response.put("success", false);
        response.put("message", "Código inválido o expirado.");
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    //Reenviar código de verificación para visitante
    @PostMapping("/reenviarCodigoVisitante") 
    public ResponseEntity<String> reenviarCodigoVisitante(@RequestParam String mail) {
        System.out.println("Reenviando código de visitante - Email: " + mail);
        boolean enviado = usuariosDAO.enviarCodigoVerificacionVisitante(mail);
        if (enviado) {
            return new ResponseEntity<>("Se ha reenviado el código de verificación a tu correo.", HttpStatus.OK);
        }
        return new ResponseEntity<>("No se pudo reenviar el código. Verifica tu email.", HttpStatus.BAD_REQUEST);
    }

    //Generar sugerencias de alias disponibles
    @GetMapping("/sugerenciasAlias")
    public ResponseEntity<Map<String, Object>> generarSugerenciasAlias(@RequestParam String baseAlias) {
        System.out.println("Generando sugerencias para alias: " + baseAlias);
        
        Map<String, Object> response = new HashMap<>();
        List<String> sugerencias = new ArrayList<>();
        
        try {
            String base = baseAlias.replaceAll("\\d+$", ""); 
            
            // Generar diferentes tipos de sugerencias
            for (int i = 0; i < 6; i++) {
                String sugerencia;
                switch (i) {
                    case 0:
                        sugerencia = base + Math.floor(Math.random() * 1000);
                        break;
                    case 1:
                        sugerencia = base + new java.util.Date().getYear() + 1900;
                        break;
                    case 2:
                        sugerencia = base + "_chef";
                        break;
                    case 3:
                        sugerencia = "chef_" + base;
                        break;
                    case 4:
                        sugerencia = base + "_" + Math.floor(Math.random() * 100);
                        break;
                    default:
                        sugerencia = base + Math.floor(Math.random() * 10000);
                }
                
                // Verificar si la sugerencia está disponible
                boolean disponible = usuariosRepository.findAll().stream()
                    .noneMatch(usuario -> sugerencia.equalsIgnoreCase(usuario.getNickname()));
                
                if (disponible && !sugerencias.contains(sugerencia)) {
                    sugerencias.add(sugerencia);
                }
                
                // Si ya tenemos suficientes sugerencias, salir del bucle
                if (sugerencias.size() >= 4) {
                    break;
                }
            }
            
            response.put("sugerencias", sugerencias);
            response.put("success", true);
            
            return new ResponseEntity<>(response, HttpStatus.OK);
            
        } catch (Exception e) {
            System.out.println("Error generando sugerencias: " + e.getMessage());
            response.put("sugerencias", new ArrayList<>());
            response.put("success", false);
            response.put("error", "Error generando sugerencias");
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    
    @PostMapping("/registrarUsuarioEtapa1")
    public ResponseEntity<Map<String, Object>> registrarUsuarioEtapa1(@RequestParam String mail, @RequestParam String alias) {
        System.out.println("Iniciando registro de USUARIO con verificación - Email: " + mail + ", Alias: " + alias);
        
        Map<String, Object> response = new HashMap<>();
        
        
        Optional<Usuarios> usuarioExistentePorCorreo = usuariosRepository.findByMail(mail);
        if (usuarioExistentePorCorreo.isPresent()) {
            Usuarios usuarioExistente = usuarioExistentePorCorreo.get();
            System.out.println("Email ya registrado: " + mail + ", Estado: " + usuarioExistente.getHabilitado());
            
            // Verificar si el registro previo se completó o quedó pendiente
            if ("Si".equals(usuarioExistente.getHabilitado())) {
                // Registro completado anteriormente
                response.put("error", "El email ya está registrado. Si olvidaste tu contraseña, puedes recuperarla.");
                response.put("success", false);
                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
            } else {
                // Registro incompleto - no puede usar este email
                response.put("error", "Este email tiene un proceso de registro incompleto. Para continuar, revisa tu correo y busca el código de verificación. Si no lo encuentras, puedes solicitar uno nuevo. Si deseas usar otro correo, deberás contactar a soporte para liberar este.");
                response.put("success", false);
                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
            }
        }

        // Verificar alias y generar sugerencias si está en uso
        boolean aliasExiste = usuariosRepository.findAll().stream()
            .anyMatch(usuario -> alias.equalsIgnoreCase(usuario.getNickname()));
        if (aliasExiste) {
            System.out.println("Alias ya registrado: " + alias + ", generando sugerencias...");
            
            // Generar sugerencias automáticamente
            List<String> sugerencias = generarSugerenciasAliasInterno(alias);
            
            response.put("error", "El alias ya está registrado.");
            response.put("aliasUnavailable", true);
            response.put("suggestions", sugerencias);
            response.put("success", false);
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }

        // Si todo está bien, proceder con el registro
        boolean registrado = usuariosDAO.registrarUsuarioEtapa1(mail, alias);
        if (registrado) {
            
            boolean emailEnviado = usuariosDAO.enviarCodigoVerificacionUsuario(mail);
            
            if (emailEnviado) {
                response.put("message", "Se ha enviado un código de verificación de 4 dígitos a tu correo. El código tiene una validez de 24 horas.");
                response.put("success", true);
                return new ResponseEntity<>(response, HttpStatus.OK);
            } else {
                response.put("error", "Se creó tu usuario, pero no pudimos enviar el código de verificación. Por favor, solicítalo de nuevo desde la pantalla de login.");
                response.put("success", false); 
                return new ResponseEntity<>(response, HttpStatus.OK); 
            }
        }
        
        response.put("error", "Error interno en el registro. Intente nuevamente.");
        response.put("success", false);
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    //Verificar código de usuario
    @PostMapping("/verificarCodigoUsuario")
    public ResponseEntity<Map<String, Object>> verificarCodigoUsuario(@RequestParam String mail, @RequestParam String codigo) {
        boolean verificado = usuariosDAO.verificarCodigoUsuario(mail, codigo);
        Map<String, Object> response = new HashMap<>();
        if (verificado) {
            response.put("success", true);
            response.put("message", "Código verificado. Ahora completa tu perfil con contraseña.");
            return new ResponseEntity<>(response, HttpStatus.OK);
        }
        response.put("success", false);
        response.put("message", "Código inválido o expirado.");
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    //Completar registro de usuario (Etapa 2: datos + contraseña)
    @PostMapping("/completarRegistroUsuario")
    public ResponseEntity<String> completarRegistroUsuario(@RequestParam String mail, @RequestParam String nombre, @RequestParam String password) {
        boolean completado = usuariosDAO.completarRegistroUsuario(mail, nombre, password);
        if (completado) {
            return new ResponseEntity<>("Registro completado exitosamente. Ya puedes iniciar sesión.", HttpStatus.OK);
        }
        return new ResponseEntity<>("No se pudo completar el registro.", HttpStatus.BAD_REQUEST);
    }

    //Reenviar código de verificación para usuario
    @PostMapping("/reenviarCodigoUsuario") 
    public ResponseEntity<Map<String, Object>> reenviarCodigoUsuario(@RequestParam String mail) {
        boolean enviado = usuariosDAO.enviarCodigoVerificacionUsuario(mail);
        Map<String, Object> response = new HashMap<>();
        if (enviado) {
            response.put("success", true);
            response.put("message", "Se ha reenviado el código de verificación a tu correo.");
            return new ResponseEntity<>(response, HttpStatus.OK);
        }
        response.put("success", false);
        response.put("message", "No se pudo reenviar el código. Verifica tu email.");
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }
  
    //Registro de Alumnos
    @PostMapping("/registrarAlumno")
    public ResponseEntity<String> registrarAlumno(@RequestParam String mail, @RequestParam(required = false) Integer idUsuario, 
                                                  @RequestParam String medioPago, @RequestParam String dniFrente, 
                                                  @RequestParam String dniFondo, @RequestParam String tramite) {
        boolean registrado = alumnosDAO.registrarAlumno(mail, idUsuario, medioPago, dniFrente, dniFondo, tramite);
        if (registrado) {
            return new ResponseEntity<>("Registro como alumno exitoso, se ha enviado un correo para completar el proceso.", HttpStatus.OK);
        }
        return new ResponseEntity<>("No se pudo registrar como estudiante. Es posible que el usuario no exista o ya sea un estudiante.", HttpStatus.BAD_REQUEST);
    }
   
    //Cambiar registro a Alumno
    @PutMapping(value = "/cambiarAAlumno/{idUsuario}", consumes = "multipart/form-data")
    public ResponseEntity<String> cambiarAAlumno(
            @PathVariable String idUsuario,
            @RequestParam(required = false) String tramite,
            @RequestParam(required = false) String nroTarjeta,
            @RequestParam(required = false) String password,
            @RequestParam(required = false) MultipartFile dniFrente,
            @RequestParam(required = false) MultipartFile dniFondo) {
        try {
            System.out.println("Recibiendo solicitud de upgrade para usuario: " + idUsuario);
            System.out.println("Tramite: " + tramite);
            System.out.println("Número tarjeta: " + nroTarjeta);
            System.out.println("Password: " + (password != null ? "***" : "null"));
            System.out.println("DNI frente: " + (dniFrente != null ? dniFrente.getOriginalFilename() : "null"));
            System.out.println("DNI fondo: " + (dniFondo != null ? dniFondo.getOriginalFilename() : "null"));
            
           
            Alumnos alumnos = new Alumnos();
            alumnos.setTramite(tramite);
            
            
            if (nroTarjeta != null) {
                alumnos.setNroTarjeta(nroTarjeta.replaceAll("\\s", ""));
            }
            
            
            if (dniFrente != null && !dniFrente.isEmpty()) {
                alumnos.setDniFrente("imagen_dni_frente_" + System.currentTimeMillis() + "_" + dniFrente.getOriginalFilename());
            }
            
            if (dniFondo != null && !dniFondo.isEmpty()) {
                alumnos.setDniFondo("imagen_dni_fondo_" + System.currentTimeMillis() + "_" + dniFondo.getOriginalFilename());
            }
            
            System.out.println("Objeto Alumnos creado: " + alumnos);
            
            
            try {
                int idUsuarioInt = Integer.parseInt(idUsuario);
                boolean cambioExitoso = usuariosDAO.cambiarAAlumno(idUsuarioInt, alumnos, password);
                if (cambioExitoso) {
                    return new ResponseEntity<>("Usuario convertido a alumno exitosamente.", HttpStatus.OK);
                }
            } catch (NumberFormatException e) {
                // Si no es un número válido, podría ser un email de visitante
                System.out.println("ID no válido, intentando buscar por email: " + idUsuario);
                
                Optional<Usuarios> usuarioOpt = usuariosRepository.findByMail(idUsuario);
                if (usuarioOpt.isPresent()) {
                    Usuarios usuario = usuarioOpt.get();
                    if ("visitante".equals(usuario.getTipo()) || "comun".equals(usuario.getTipo())) {
                        boolean cambioExitoso = usuariosDAO.cambiarAAlumno(usuario.getIdUsuario(), alumnos, password);
                        if (cambioExitoso) {
                            return new ResponseEntity<>("Usuario convertido a alumno exitosamente.", HttpStatus.OK);
                        }
                    } else {
                        return new ResponseEntity<>("Solo los visitantes y usuarios pueden hacer upgrade a alumno.", HttpStatus.BAD_REQUEST);
                    }
                } else {
                    return new ResponseEntity<>("Usuario no encontrado con email: " + idUsuario, HttpStatus.NOT_FOUND);
                }
            }
        } catch (Exception e) {
            System.out.println("Error procesando upgrade: " + e.getMessage());
            e.printStackTrace();
            return new ResponseEntity<>("Error interno del servidor: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>("No es posible cambiar de usuario a alumno.", HttpStatus.BAD_REQUEST);
    }
    
   
    //Recuperar contraseña para usuarios registrados
    @PostMapping("/recuperarContrasena")
    public ResponseEntity<String> recuperarContrasena(@RequestParam String mail) {
        boolean enviado = usuariosDAO.enviarCodigoRecuperacion(mail);
        if (enviado) {
            return new ResponseEntity<>("Se ha enviado un código de recuperación de contraseña.", HttpStatus.OK);
        }
        return new ResponseEntity<>("Correo no encontrado en el sistema.", HttpStatus.BAD_REQUEST);
    }

    // Verificar código de recuperación de contraseña
    @PostMapping("/verificarCodigoRecuperacion")
    public ResponseEntity<Map<String, Object>> verificarCodigoRecuperacion(@RequestParam String mail, @RequestParam String codigo) {
        boolean valido = usuariosDAO.verificarCodigoRecuperacion(mail, codigo);
        Map<String, Object> response = new HashMap<>();
        
        if (valido) {
            response.put("success", true);
            response.put("message", "Código válido. Puedes proceder a cambiar tu contraseña.");
            return new ResponseEntity<>(response, HttpStatus.OK);
        } else {
            response.put("success", false);
            response.put("message", "Código inválido o expirado. Los códigos de recuperación tienen una validez de 30 minutos.");
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
    }

    // Cambiar contraseña con código de recuperación
    @PostMapping("/cambiarContrasenaConCodigo")
    public ResponseEntity<Map<String, Object>> cambiarContrasenaConCodigo(
        @RequestParam String mail, 
        @RequestParam String codigo, 
        @RequestParam String nuevaPassword) {
        
        boolean cambiado = usuariosDAO.cambiarContrasenaConCodigo(mail, codigo, nuevaPassword);
        Map<String, Object> response = new HashMap<>();
        
        if (cambiado) {
            response.put("success", true);
            response.put("message", "Tu contraseña ha sido cambiada exitosamente. Ya puedes iniciar sesión con la nueva contraseña.");
            return new ResponseEntity<>(response, HttpStatus.OK);
        } else {
            response.put("success", false);
            response.put("message", "No se pudo cambiar la contraseña. Verifica que el código sea válido y no haya expirado.");
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
    }

	
	//publicar/cargar recetas
	@PostMapping("/publicarRecetas")
	public ResponseEntity<String> publicarReceta(@RequestBody Recetas recetas) {
        try {
            // Asegurar que cada ingrediente tenga la referencia a la receta
            if (recetas.getIngredientes() != null && !recetas.getIngredientes().isEmpty()) {
                recetas.getIngredientes().forEach(ingrediente -> {
                    ingrediente.setReceta(recetas);
                });
            }
            
            // Establecer fecha actual si no está definida
            if (recetas.getFecha() == null) {
                recetas.setFecha(java.time.LocalDate.now());
            }
            
            recetasDAO.save(recetas);
            return ResponseEntity.ok("Receta publicada exitosamente");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body("Error al publicar la receta: " + e.getMessage());
        }
	}
	
	
	@PostMapping("/crearCurso")
	public ResponseEntity<String> crearCurso(@RequestBody Cursos cursos) {
        try {
            cursosDAO.save(cursos);
            return ResponseEntity.ok("Curso creado exitosamente");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error al crear el curso: " + e.getMessage());
        }
	}
	
	
	@PostMapping("/crearCronograma")
	public ResponseEntity<String> crearCronograma(@RequestBody CronogramaCursos cronogramaCursos) {
        try {
        	cronogramaCursoDAO.save(cronogramaCursos);   
            return ResponseEntity.ok("Cronograma creado exitosamente");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error al crear el cronograma: " + e.getMessage());
        }
	}
	
	
	@GetMapping("/sugerenciasRecetas")
	public ResponseEntity<List<Recetas>> sugerenciasCulinarias(@RequestParam(required = false) Integer idTipo) {
	    TiposReceta tipo = null;
	    if (idTipo != null) {
	        tipo = new TiposReceta();
	        tipo.setIdTipo(idTipo);
	    }
	    List<Recetas> sugeridas = recetasDAO.obtenerSugerencias(tipo);
	    return ResponseEntity.ok(sugeridas);
	}
	
	//buscar recetas
	@GetMapping("/buscarRecetas")
	public ResponseEntity<List<Recetas>> buscarRecetas(@RequestParam String nombre) {
	    List<Recetas> recetas = recetasDAO.buscarPorNombre(nombre);
	    return ResponseEntity.ok(recetas);
	}
	
	// Endpoint de depuración para listar todos los nombres de recetas
	@GetMapping("/debug/listRecipeNames")
	public ResponseEntity<List<String>> listAllRecipeNames() {
	    List<Recetas> allRecetas = recetasRepository.findAll();
	    List<String> names = allRecetas.stream()
	        .map(Recetas::getNombreReceta)
	        .collect(Collectors.toList());
	    
	    return ResponseEntity.ok(names);
	}
	
	
	@GetMapping("/debug/testSearch")
	public ResponseEntity<Map<String, Object>> testSearch(@RequestParam String searchTerm) {
	    Map<String, Object> result = new HashMap<>();
	    
	    // Probar coincidencia exacta
	    List<Recetas> exactMatch = recetasRepository.findByNombreReceta(searchTerm);
	    result.put("exactMatch", exactMatch.size());
	    
	    // Probar coincidencia parcial
	    List<Recetas> partialMatch = recetasRepository.findByNombreRecetaContainingIgnoreCase(searchTerm);
	    result.put("partialMatch", partialMatch.size());
	    
	    if (!partialMatch.isEmpty()) {
	        result.put("foundRecipes", partialMatch.stream()
	            .map(Recetas::getNombreReceta)
	            .collect(Collectors.toList()));
	    }
	    
	    
	    return ResponseEntity.ok(result);
	}
	
	//inscribirse a un curso
	@PostMapping("/inscripcionCurso")
	public ResponseEntity<String> inscribirseCurso(@RequestParam int idAlumno, @RequestParam int idCronograma) {
	    asistenciaCursosDAO.inscribirse(idAlumno, idCronograma);
	    return ResponseEntity.ok("Inscripción exitosa.");
	}

	//darse de baja de un curso
	@DeleteMapping("/cancelarInscripcion")
	public ResponseEntity<String> cancelarInscripcion(@RequestParam int idAlumno, @RequestParam int idCronograma) {
	    asistenciaCursosDAO.cancelarInscripcion(idAlumno, idCronograma);
	    return ResponseEntity.ok("Inscripción cancelada.");
	}
	
	//registrar asistencia
	@PostMapping("/registrarAsistencia")
	public ResponseEntity<String> registrarAsistencia(@RequestParam int idAlumno, @RequestParam int idCronograma) {
	    try {
	        asistenciaCursosDAO.registrarAsistencia(idAlumno, idCronograma);
	        return ResponseEntity.ok("Asistencia registrada exitosamente.");
	    } catch (RuntimeException e) {
	        return ResponseEntity.badRequest().body("Error al registrar asistencia: " + e.getMessage());
	    }
	}
	
	
	
	
	@GetMapping("/getNombrereceta")
	public ResponseEntity<List<Map<String, Object>>> consultarRecetaPorNombre(@RequestParam String nombrePlato, @RequestParam(required = false) String orden) {
	    System.out.println("Search request for recipe name: '" + nombrePlato + "' with order: '" + orden + "'");
	    
	    List<Recetas> recetas;
	    if (orden != null && orden.equals("nueva")) {
	        recetas = recetasRepository.findByNombreRecetaContainingIgnoreCaseOrderByFechaDesc(nombrePlato);
	    } else if (orden != null && orden.equals("usuario")) {
	        recetas = recetasRepository.findByNombreRecetaContainingIgnoreCaseOrderByUsuario(nombrePlato);
	    } else {
	        recetas = recetasRepository.findByNombreRecetaContainingIgnoreCaseOrderByNombreReceta(nombrePlato);
	    }
	    
	    System.out.println("Found " + recetas.size() + " recipes matching '" + nombrePlato + "'");
	    if (!recetas.isEmpty()) {
	        System.out.println("First recipe found: " + recetas.get(0).getNombreReceta());
	    }
	    
	    if (recetas.isEmpty()) {
	        return ResponseEntity.notFound().build();
	    }
	    
	    
	    List<Map<String, Object>> recetasDTO = recetas.stream()
	        .map(receta -> {
	            Map<String, Object> dto = new HashMap<>();
	            dto.put("idReceta", receta.getIdReceta());
	            dto.put("nombreReceta", receta.getNombreReceta());
	            dto.put("descripcionReceta", receta.getDescripcionReceta());
	            dto.put("fotoPrincipal", receta.getFotoPrincipal());
	            dto.put("porciones", receta.getPorciones());
	            dto.put("cantidadPersonas", receta.getCantidadPersonas());
	            dto.put("fecha", receta.getFecha());
	            dto.put("autorizada", receta.isAutorizada());
	            dto.put("instrucciones", receta.getInstrucciones());
	            
	            if (receta.getIdTipo() != null) {
	                Map<String, Object> tipoDTO = new HashMap<>();
	                tipoDTO.put("idTipo", receta.getIdTipo().getIdTipo());
	                tipoDTO.put("descripcion", receta.getIdTipo().getDescripcion());
	                dto.put("tipo", tipoDTO);
	            }
	            
	            if (receta.getUsuario() != null) {
	                Map<String, Object> usuarioDTO = new HashMap<>();
	                usuarioDTO.put("idUsuario", receta.getUsuario().getIdUsuario());
	                usuarioDTO.put("nombre", receta.getUsuario().getNombre());
	                usuarioDTO.put("mail", receta.getUsuario().getMail());
	                usuarioDTO.put("tipo", receta.getUsuario().getTipo());
	                dto.put("usuario", usuarioDTO);
	            }
	            
	            return dto;
	        })
	        .collect(Collectors.toList());
	    
	    return ResponseEntity.ok(recetasDTO);
	}
	
	//Tipo
	@GetMapping("/getTiporeceta")
	public ResponseEntity<List<Map<String, Object>>> consultarRecetaPorTipo(@RequestParam TiposReceta tipoPlato, @RequestParam(required = false) String orden) {
	    List<Recetas> recetas;
	    if (orden != null && orden.equals("nueva")) {
	        recetas = recetasRepository.findByIdTipoOrderByFechaDesc(tipoPlato);
	    } else {
	        recetas = recetasRepository.findByIdTipoOrderByIdTipo(tipoPlato);
	    }
	    if (recetas.isEmpty()) {
	        return ResponseEntity.notFound().build();
	    }

	    
	    List<Map<String, Object>> recetasDTO = recetas.stream()
	        .map(receta -> {
	            Map<String, Object> dto = new HashMap<>();
	            dto.put("idReceta", receta.getIdReceta());
	            dto.put("nombreReceta", receta.getNombreReceta());
	            dto.put("descripcionReceta", receta.getDescripcionReceta());
	            dto.put("fotoPrincipal", receta.getFotoPrincipal());
	            dto.put("porciones", receta.getPorciones());
	            dto.put("cantidadPersonas", receta.getCantidadPersonas());
	            dto.put("fecha", receta.getFecha());
	            dto.put("autorizada", receta.isAutorizada());
	            dto.put("instrucciones", receta.getInstrucciones());
	            
	            if (receta.getIdTipo() != null) {
	                Map<String, Object> tipoDTO = new HashMap<>();
	                tipoDTO.put("idTipo", receta.getIdTipo().getIdTipo());
	                tipoDTO.put("descripcion", receta.getIdTipo().getDescripcion());
	                dto.put("tipo", tipoDTO);
	            }
	            
	            if (receta.getUsuario() != null) {
	                Map<String, Object> usuarioDTO = new HashMap<>();
	                usuarioDTO.put("idUsuario", receta.getUsuario().getIdUsuario());
	                usuarioDTO.put("nombre", receta.getUsuario().getNombre());
	                usuarioDTO.put("mail", receta.getUsuario().getMail());
	                usuarioDTO.put("tipo", receta.getUsuario().getTipo());
	                dto.put("usuario", usuarioDTO);
	            }
	            
	            return dto;
	        })
	        .collect(Collectors.toList());

	    return ResponseEntity.ok(recetasDTO);
	}
	
	//Ingrediente
	@GetMapping("/getIngredienteReceta")
	public ResponseEntity<List<Map<String, Object>>> consultarRecetaPorIngrediente(@RequestParam String ingrediente, @RequestParam(required = false) String orden) {
	    List<Recetas> recetas;
	    if (orden != null && orden.equals("nueva")) {
	        recetas = recetasRepository.findByIngredientesOrderByFechaDesc(ingrediente);
	    } else if (orden != null && orden.equals("usuario")) {
	        recetas = recetasRepository.findByIngredientesOrderByUsuario(ingrediente);
	    } else {
	        recetas = recetasRepository.findByIngredientesOrderByIngredientes(ingrediente);
	    }
	    if (recetas.isEmpty()) {
	        return ResponseEntity.notFound().build();
	    }
	    
	   
	    List<Map<String, Object>> recetasDTO = recetas.stream()
	        .map(receta -> {
	            Map<String, Object> dto = new HashMap<>();
	            dto.put("idReceta", receta.getIdReceta());
	            dto.put("nombreReceta", receta.getNombreReceta());
	            dto.put("descripcionReceta", receta.getDescripcionReceta());
	            dto.put("fotoPrincipal", receta.getFotoPrincipal());
	            dto.put("porciones", receta.getPorciones());
	            dto.put("cantidadPersonas", receta.getCantidadPersonas());
	            dto.put("fecha", receta.getFecha());
	            dto.put("autorizada", receta.isAutorizada());
	            dto.put("instrucciones", receta.getInstrucciones());
	            
	            if (receta.getIdTipo() != null) {
	                Map<String, Object> tipoDTO = new HashMap<>();
	                tipoDTO.put("idTipo", receta.getIdTipo().getIdTipo());
	                tipoDTO.put("descripcion", receta.getIdTipo().getDescripcion());
	                dto.put("tipo", tipoDTO);
	            }
	            
	            if (receta.getUsuario() != null) {
	                Map<String, Object> usuarioDTO = new HashMap<>();
	                usuarioDTO.put("idUsuario", receta.getUsuario().getIdUsuario());
	                usuarioDTO.put("nombre", receta.getUsuario().getNombre());
	                usuarioDTO.put("mail", receta.getUsuario().getMail());
	                usuarioDTO.put("tipo", receta.getUsuario().getTipo());
	                dto.put("usuario", usuarioDTO);
	            }
	            
	            return dto;
	        })
	        .collect(Collectors.toList());
	    
	    return ResponseEntity.ok(recetasDTO);
	}
	
	//No poseen un ingrediente
	@GetMapping("/getSinIngredienteReceta")
	public ResponseEntity<List<Map<String, Object>>> consultarRecetaSinIngrediente(@RequestParam String ingrediente, @RequestParam(required = false) String orden) {
	    List<Recetas> recetas;
	    if (orden != null && orden.equals("nueva")) {
	        recetas = recetasRepository.findBySinIngredientesOrderByFechaDesc(ingrediente);
	    } else if (orden != null && orden.equals("usuario")) {
	        recetas = recetasRepository.findBySinIngredientesOrderByUsuario(ingrediente);
	    } else {
	        recetas = recetasRepository.findBySinIngredientesOrderByIngredientes(ingrediente);
	    }
	    if (recetas.isEmpty()) {
	        return ResponseEntity.notFound().build();
	    }
	    
	   
	    List<Map<String, Object>> recetasDTO = recetas.stream()
	        .map(receta -> {
	            Map<String, Object> dto = new HashMap<>();
	            dto.put("idReceta", receta.getIdReceta());
	            dto.put("nombreReceta", receta.getNombreReceta());
	            dto.put("descripcionReceta", receta.getDescripcionReceta());
	            dto.put("fotoPrincipal", receta.getFotoPrincipal());
	            dto.put("porciones", receta.getPorciones());
	            dto.put("cantidadPersonas", receta.getCantidadPersonas());
	            dto.put("fecha", receta.getFecha());
	            dto.put("autorizada", receta.isAutorizada());
	            dto.put("instrucciones", receta.getInstrucciones());
	            
	            if (receta.getIdTipo() != null) {
	                Map<String, Object> tipoDTO = new HashMap<>();
	                tipoDTO.put("idTipo", receta.getIdTipo().getIdTipo());
	                tipoDTO.put("descripcion", receta.getIdTipo().getDescripcion());
	                dto.put("tipo", tipoDTO);
	            }
	            
	            if (receta.getUsuario() != null) {
	                Map<String, Object> usuarioDTO = new HashMap<>();
	                usuarioDTO.put("idUsuario", receta.getUsuario().getIdUsuario());
	                usuarioDTO.put("nombre", receta.getUsuario().getNombre());
	                usuarioDTO.put("mail", receta.getUsuario().getMail());
	                usuarioDTO.put("tipo", receta.getUsuario().getTipo());
	                dto.put("usuario", usuarioDTO);
	            }
	            
	            return dto;
	        })
	        .collect(Collectors.toList());
	    
	    return ResponseEntity.ok(recetasDTO);
	}

	//Usuario Particular 
	@GetMapping("/getUsuarioReceta")
	public ResponseEntity<List<Map<String, Object>>> consultarRecetaPorUsuario(@RequestParam String usuario, @RequestParam(required = false) String orden) {
	    List<Recetas> recetas;
	    if (orden != null && orden.equals("nueva")) {
	        recetas = recetasRepository.findByUsuarioNombreContainingIgnoreCaseOrderByFechaDesc(usuario);
	    } else if (orden != null && orden.equals("usuario")) {
	        recetas = recetasRepository.findByUsuarioNombreContainingIgnoreCaseOrderByUsuario(usuario);
	    } else {
	        recetas = recetasRepository.findByUsuarioNombreContainingIgnoreCaseOrderByNombreReceta(usuario);
	    }
	    if (recetas.isEmpty()) {
	        return ResponseEntity.notFound().build();
	    }
	    
	  
	    List<Map<String, Object>> recetasDTO = recetas.stream()
	        .map(receta -> {
	            Map<String, Object> dto = new HashMap<>();
	            dto.put("idReceta", receta.getIdReceta());
	            dto.put("nombreReceta", receta.getNombreReceta());
	            dto.put("descripcionReceta", receta.getDescripcionReceta());
	            dto.put("fotoPrincipal", receta.getFotoPrincipal());
	            dto.put("porciones", receta.getPorciones());
	            dto.put("cantidadPersonas", receta.getCantidadPersonas());
	            dto.put("fecha", receta.getFecha());
	            dto.put("autorizada", receta.isAutorizada());
	            dto.put("instrucciones", receta.getInstrucciones());
	            
	            if (receta.getIdTipo() != null) {
	                Map<String, Object> tipoDTO = new HashMap<>();
	                tipoDTO.put("idTipo", receta.getIdTipo().getIdTipo());
	                tipoDTO.put("descripcion", receta.getIdTipo().getDescripcion());
	                dto.put("tipo", tipoDTO);
	            }
	            
	            if (receta.getUsuario() != null) {
	                Map<String, Object> usuarioDTO = new HashMap<>();
	                usuarioDTO.put("idUsuario", receta.getUsuario().getIdUsuario());
	                usuarioDTO.put("nombre", receta.getUsuario().getNombre());
	                usuarioDTO.put("mail", receta.getUsuario().getMail());
	                usuarioDTO.put("tipo", receta.getUsuario().getTipo());
	                dto.put("usuario", usuarioDTO);
	            }
	            
	            return dto;
	        })
	        .collect(Collectors.toList());
	    
	    return ResponseEntity.ok(recetasDTO);
	}

	
	//Valorar una receta
	@PostMapping("/valorarReceta/{idReceta}")
	public ResponseEntity<String> valorarReceta(@PathVariable Integer idReceta, @RequestBody Calificaciones calificacion, @RequestParam(required = false) Integer idUsuario) {
	    
	    Optional<Recetas> recetaOpt = recetasRepository.findById(idReceta);
	    if (!recetaOpt.isPresent()) {
	        return ResponseEntity.badRequest().body("Receta no encontrada.");
	    }

	    Recetas receta = recetaOpt.get();

	    
	    Usuarios usuarioAutenticado = null;
	    
	    
	    try {
	        usuarioAutenticado = usuariosDAO.getUsuarioAutenticado();
	    } catch (Exception e) {
	        
	        if (idUsuario != null) {
	            usuarioAutenticado = usuariosDAO.findById(idUsuario);
	        }
	    }
	    
	    
	    if (usuarioAutenticado == null && calificacion.getIdusuario() != null) {
	        usuarioAutenticado = calificacion.getIdusuario();
	    }
	    
	    if (usuarioAutenticado == null) {
	        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Debe iniciar sesión para valorar la receta.");
	    }

	    
	    final Usuarios usuario = usuarioAutenticado;

	    
	    List<Calificaciones> valoracionesExistentes = calificacionesRepository.findByIdReceta(receta);
	    Optional<Calificaciones> valoracionExistente = valoracionesExistentes.stream()
	        .filter(v -> v.getIdusuario() != null && v.getIdusuario().getIdUsuario().equals(usuario.getIdUsuario()))
	        .findFirst();
	    
	    Calificaciones valoracionFinal;
	    
	    if (valoracionExistente.isPresent()) {
	      
	        valoracionFinal = valoracionExistente.get();
	        valoracionFinal.setCalificacion(calificacion.getCalificacion());
	        
	        
	        if (calificacion.getComentarios() != null && !calificacion.getComentarios().trim().isEmpty()) {
	            valoracionFinal.setComentarios(calificacion.getComentarios());
	            valoracionFinal.setAutorizado(false);
	        }
	        
	        System.out.println("Actualizando valoración existente para usuario " + usuario.getIdUsuario() + " en receta " + idReceta);
	    } else {
	       
	        valoracionFinal = new Calificaciones();
	        valoracionFinal.setIdReceta(receta);
	        valoracionFinal.setIdusuario(usuario);
	        valoracionFinal.setCalificacion(calificacion.getCalificacion());
	        valoracionFinal.setComentarios(calificacion.getComentarios());
	        valoracionFinal.setAutorizado(false); 
	        
	        System.out.println("Creando nueva valoración para usuario " + usuario.getIdUsuario() + " en receta " + idReceta);
	    }

	  
	    calificacionesRepository.save(valoracionFinal);

	    String mensaje = valoracionExistente.isPresent() ? 
	        "Valoración actualizada exitosamente." : 
	        "Valoración registrada exitosamente.";
	    
	    return ResponseEntity.ok(mensaje);
	}
	
	@PutMapping("/autorizarComentario/{idCalificacion}")
	public ResponseEntity<String> autorizarComentario(@PathVariable Integer idCalificacion) {
	    Optional<Calificaciones> calificacionOpt = calificacionesRepository.findById(idCalificacion);
	    if (!calificacionOpt.isPresent()) {
	        return ResponseEntity.badRequest().body("Calificación no encontrada.");
	    }

	    Calificaciones calificacion = calificacionOpt.get();
	    
	    calificacion.setAutorizado(true);
	    calificacionesRepository.save(calificacion);

	    return ResponseEntity.ok("Comentario autorizado.");
	}
	
	//Consultar valoraciones de una receta 
	@GetMapping("/getValoracionReceta/{idReceta}")
	public ResponseEntity<List<Calificaciones>> obtenerValoracionesReceta(@PathVariable Integer idReceta) {
	    Optional<Recetas> recetaOpt = recetasRepository.findById(idReceta);
	    if (!recetaOpt.isPresent()) {
	        return ResponseEntity.notFound().build();
	    }

	    Recetas receta = recetaOpt.get();
	    
	   
	    List<Calificaciones> todasLasValoraciones = calificacionesRepository.findByIdReceta(receta);
	    
	    if (todasLasValoraciones.isEmpty()) {
	        return ResponseEntity.noContent().build();
	    }

	    return ResponseEntity.ok(todasLasValoraciones);
	}
	
    @Value("${directorio.archivos.recetas}")
    private String directorioBase;

    //Cargar receta
    @PostMapping("/cargarReceta")
    public ResponseEntity<String> cargarReceta(@ModelAttribute Recetas receta, @RequestParam("archivos") MultipartFile[] archivos) {
        try {
            Usuarios usuario = usuariosDAO.getUsuarioAutenticado();
            
            if (usuario == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Debe iniciar sesión para cargar una receta.");
            }

            Optional<Recetas> recetaExistente = recetasDAO.buscarPorNombreYUsuario(receta.getNombreReceta(), usuario);
            if (recetaExistente.isPresent()) {
                boolean reemplazar = preguntarSiReemplazar(); 
                if (reemplazar) {
                    recetasDAO.eliminarReceta(recetaExistente.get());  
                } else {
                    // Permitir editar la receta existente
                    receta.setIdReceta(recetaExistente.get().getIdReceta());
                }
            }

            
            receta.setUsuario(usuario);
            receta.setAutorizada(false);
            
            
            if (receta.getFecha() == null) {
                receta.setFecha(java.time.LocalDate.now());
            }
            
            // Asegurar que cada ingrediente tenga la referencia a la receta
            if (receta.getIngredientes() != null && !receta.getIngredientes().isEmpty()) {
                receta.getIngredientes().forEach(ingrediente -> {
                    ingrediente.setReceta(receta);
                });
            }

            
            recetasDAO.save(receta);
            
           
            procesarPasosDeReceta(receta);
            
           
            guardarArchivosDeRecetaMejorado(archivos, receta);

            return ResponseEntity.ok("Receta cargada exitosamente y pendiente de autorización.");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error al cargar la receta: " + e.getMessage());
        }
    }

    // Método para procesar y guardar pasos individuales
    private void procesarPasosDeReceta(Recetas receta) {
        if (receta.getInstrucciones() != null && !receta.getInstrucciones().trim().isEmpty()) {
            // Dividir las instrucciones por saltos de línea
            String[] pasos = receta.getInstrucciones().split("\\n");
            
            for (int i = 0; i < pasos.length; i++) {
                String textoPaso = pasos[i].trim();
                if (!textoPaso.isEmpty()) {
                    Pasos paso = new Pasos();
                    paso.setIdReceta(receta);
                    paso.setNroPaso(i + 1);
                    paso.setTexto(textoPaso);
                    
                    
                    pasosDAO.save(paso);
                }
            }
        }
    }

  
    public void guardarArchivosDeRecetaMejorado(MultipartFile[] archivos, Recetas receta) {
        if (archivos != null && archivos.length > 0) {
            
            recetasDAO.guardarArchivos(archivos, receta);
            
            
        }
    }

    // Método para verificar la conexión y avisar sobre redes de pago
    private boolean verificarConexion() {
        boolean conectado = true; 
        if (!conectado) {
            // Si no está conectado, informar al usuario y esperar su decisión
            boolean deseaConectarRedConCargo = mostrarAdvertenciaRedConCargo();
            if (deseaConectarRedConCargo) {
                return true; 
            } else {
                return false; 
            }
        }
        return true;  
    }

    
    private boolean mostrarAdvertenciaRedConCargo() {
        
        boolean estaConectadoALaRed = verificarConexionRed();  

        if (!estaConectadoALaRed) {
           
            System.out.println("No estás conectado a una red gratuita.");
            System.out.println("¿Deseas usar una red con cargo? (S/N):");

            Scanner scanner = new Scanner(System.in);
            String respuesta = scanner.nextLine();

            if (respuesta.equalsIgnoreCase("S")) {
                
                System.out.println("Usando red con cargo...");
                return true;
            } else {
                
                System.out.println("Guardando la receta para cargarla más tarde...");
                return false;
            }
        } else {
            
            System.out.println("Conexión gratuita detectada, procediendo con la carga.");
            return true;
        }
    }

    private boolean verificarConexionRed() {
        return false;
    }

    // Lógica para preguntar si se desea reemplazar la receta existente
    private boolean preguntarSiReemplazar() {
        // Mostrar mensaje al usuario
        System.out.println("¡Atención! Ya existe una receta con este nombre.");
        System.out.println("¿Deseas reemplazarla? (S/N):");

        // Simular que el usuario ingresa una opción
        Scanner scanner = new Scanner(System.in);
        String respuesta = scanner.nextLine();

        if (respuesta.equalsIgnoreCase("S")) {
            // Si el usuario desea reemplazar la receta
            System.out.println("La receta será reemplazada.");
            return true;
        } else {
            // Si el usuario no desea reemplazar la receta
            System.out.println("La receta no será reemplazada.");
            return false;
        }
    }
    
    
    @GetMapping("/getAllRecetas")
    public ResponseEntity<List<Map<String, Object>>> obtenerRecetas() {
        try {
            List<Recetas> todasRecetas = recetasDAO.getAllRecetas();
            List<Map<String, Object>> recetasDTO = todasRecetas.stream()
                .map(receta -> {
                    Map<String, Object> dto = new HashMap<>();
                    dto.put("idReceta", receta.getIdReceta());
                    dto.put("nombreReceta", receta.getNombreReceta());
                    dto.put("descripcionReceta", receta.getDescripcionReceta());
                    dto.put("fotoPrincipal", receta.getFotoPrincipal());
                    dto.put("porciones", receta.getPorciones());
                    dto.put("cantidadPersonas", receta.getCantidadPersonas());
                    dto.put("fecha", receta.getFecha());
                    dto.put("autorizada", receta.isAutorizada());
                    dto.put("instrucciones", receta.getInstrucciones());
                    
                    // Calcular calificación promedio
                    List<Calificaciones> calificaciones = calificacionesRepository.findByIdReceta(receta);
                    if (!calificaciones.isEmpty()) {
                        double totalRating = calificaciones.stream()
                            .filter(cal -> cal != null && cal.getCalificacion() > 0) // Filtrar calificaciones válidas
                            .mapToDouble(cal -> cal.getCalificacion())
                            .sum();
                        long validRatingsCount = calificaciones.stream()
                            .filter(cal -> cal != null && cal.getCalificacion() > 0)
                            .count();
                        
                        if (validRatingsCount > 0) {
                            double averageRating = totalRating / validRatingsCount;
                            dto.put("calificacionPromedio", Math.round(averageRating * 10.0) / 10.0); // Redondear a 1 decimal
                        } else {
                            dto.put("calificacionPromedio", 0.0);
                        }
                        dto.put("totalCalificaciones", (int) validRatingsCount);
                    } else {
                        dto.put("calificacionPromedio", 0.0);
                        dto.put("totalCalificaciones", 0);
                    }
                    
                    if (receta.getIdTipo() != null) {
                        Map<String, Object> tipoDTO = new HashMap<>();
                        tipoDTO.put("idTipo", receta.getIdTipo().getIdTipo());
                        tipoDTO.put("descripcion", receta.getIdTipo().getDescripcion());
                        dto.put("tipo", tipoDTO);
                        dto.put("tipoReceta", tipoDTO); // Agregar también en formato tipoReceta para compatibilidad
                    }
                    
                    if (receta.getUsuario() != null) {
                        Map<String, Object> usuarioDTO = new HashMap<>();
                        usuarioDTO.put("idUsuario", receta.getUsuario().getIdUsuario());
                        usuarioDTO.put("nombre", receta.getUsuario().getNombre());
                        usuarioDTO.put("mail", receta.getUsuario().getMail());
                        usuarioDTO.put("tipo", receta.getUsuario().getTipo());
                        dto.put("usuario", usuarioDTO);
                    }
                    
                    if (receta.getIngredientes() != null && !receta.getIngredientes().isEmpty()) {
                        List<Map<String, Object>> ingredientesDTO = receta.getIngredientes().stream()
                            .map(ingrediente -> {
                                Map<String, Object> ingDTO = new HashMap<>();
                                ingDTO.put("idIngrediente", ingrediente.getIdIngrediente());
                                ingDTO.put("nombre", ingrediente.getNombre());
                                ingDTO.put("cantidad", ingrediente.getCantidad());
                                ingDTO.put("unidadMedida", ingrediente.getUnidadMedida());
                                return ingDTO;
                            })
                            .collect(Collectors.toList());
                        dto.put("ingredientes", ingredientesDTO);
                    }
                    
                    return dto;
                })
                .collect(Collectors.toList());
            return new ResponseEntity<>(recetasDTO, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    

    // Método para obtener la lista de recetas seleccionadas desde la sesión
    private List<Recetas> getListaRecetasSeleccionadas(WebRequest request) {
        List<Recetas> listaRecetasSeleccionadas = (List<Recetas>) request.getAttribute("listaRecetasSeleccionadas", WebRequest.SCOPE_SESSION);
        if (listaRecetasSeleccionadas == null) {
            listaRecetasSeleccionadas = new ArrayList<>();
            request.setAttribute("listaRecetasSeleccionadas", listaRecetasSeleccionadas, WebRequest.SCOPE_SESSION);
        }
        return listaRecetasSeleccionadas;
    }
    
    //---GET RECETAS BY ID
    @GetMapping("/getRecetaById/{id}")
    public ResponseEntity<Map<String, Object>> getRecetaById(@PathVariable("id") Long id) {
        Recetas receta = recetasDAO.findByIdOptional(id.intValue()).orElse(null);
        if (receta == null) {
            return ResponseEntity.notFound().build();
        }
        Map<String, Object> dto = new HashMap<>();
        dto.put("idReceta", receta.getIdReceta());
        dto.put("nombreReceta", receta.getNombreReceta());
        dto.put("descripcionReceta", receta.getDescripcionReceta());
        dto.put("fotoPrincipal", receta.getFotoPrincipal());
        dto.put("porciones", receta.getPorciones());
        dto.put("cantidadPersonas", receta.getCantidadPersonas());
        dto.put("fecha", receta.getFecha());
        // Usuario simple
        if (receta.getUsuario() != null) {
            Map<String, Object> usuarioDTO = new HashMap<>();
            usuarioDTO.put("idUsuario", receta.getUsuario().getIdUsuario());
            usuarioDTO.put("nombre", receta.getUsuario().getNombre());
            usuarioDTO.put("mail", receta.getUsuario().getMail());
            usuarioDTO.put("tipo", receta.getUsuario().getTipo());
            dto.put("usuario", usuarioDTO);
        }
        // Ingredientes planos (sin referencias circulares)
        List<Map<String, Object>> ingredientesDTO = new ArrayList<>();
        if (receta.getIngredientes() != null) {
            for (Ingredientes ingrediente : receta.getIngredientes()) {
                Map<String, Object> ingredienteMap = new HashMap<>();
                ingredienteMap.put("idIngrediente", ingrediente.getIdIngrediente());
                ingredienteMap.put("nombre", ingrediente.getNombre());
                ingredienteMap.put("cantidad", ingrediente.getCantidad());
                ingredienteMap.put("unidadMedida", ingrediente.getUnidadMedida());
                ingredientesDTO.add(ingredienteMap);
            }
        }
        dto.put("ingredientes", ingredientesDTO);
        
        // Obtener pasos ordenados por número con sus fotos
        List<Pasos> pasos = pasosRepository.findByIdRecetaOrderByNroPaso(receta);
        List<Map<String, Object>> pasosDTO = new ArrayList<>();
        for (Pasos paso : pasos) {
            Map<String, Object> pasoMap = new HashMap<>();
            pasoMap.put("idPaso", paso.getIdPaso());
            pasoMap.put("nroPaso", paso.getNroPaso());
            pasoMap.put("texto", paso.getTexto());
            
            // Buscar multimedia asociada a este paso
            List<Multimedia> multimediaPaso = multimediaRepository.findByReceta(receta);
            String imagenPaso = null;
            for (Multimedia media : multimediaPaso) {
                if (media.getIdPaso() != null && media.getIdPaso().getIdPaso().equals(paso.getIdPaso())) {
                    imagenPaso = media.getUrlContenido();
                    break;
                }
            }
            pasoMap.put("imagen", imagenPaso);
            
            pasosDTO.add(pasoMap);
        }
        dto.put("pasos", pasosDTO);
        
        // Obtener fotos adicionales de la receta (sin paso específico)
        List<Multimedia> fotosReceta = multimediaRepository.findByReceta(receta);
        List<Map<String, Object>> fotosDTO = new ArrayList<>();
        for (Multimedia media : fotosReceta) {
            if (media.getIdPaso() == null) { // Solo fotos de la receta, no de pasos
                Map<String, Object> fotoMap = new HashMap<>();
                fotoMap.put("idContenido", media.getIdContenido());
                fotoMap.put("url", media.getUrlContenido());
                fotoMap.put("tipo", media.getTipoContenido());
                fotoMap.put("extension", media.getExtension());
                fotosDTO.add(fotoMap);
            }
        }
        dto.put("multimedia", fotosDTO);
        
        // Mantener compatibilidad con instrucciones como string
        StringBuilder instruccionesStr = new StringBuilder();
        for (Pasos paso : pasos) {
            if (instruccionesStr.length() > 0) {
                instruccionesStr.append("\n");
            }
            instruccionesStr.append(paso.getTexto());
        }
        dto.put("instrucciones", instruccionesStr.toString());
        
        // Obtener multimedia asociada a la receta
        List<Multimedia> multimedia = multimediaRepository.findByReceta(receta);
        List<Map<String, Object>> multimediaDTO = new ArrayList<>();
        for (Multimedia media : multimedia) {
            Map<String, Object> mediaMap = new HashMap<>();
            mediaMap.put("idContenido", media.getIdContenido());
            mediaMap.put("tipoContenido", media.getTipoContenido());
            mediaMap.put("urlContenido", media.getUrlContenido());
            mediaMap.put("extension", media.getExtension());
            multimediaDTO.add(mediaMap);
        }
        dto.put("multimedia", multimediaDTO);
        
        return ResponseEntity.ok(dto);
    }

    // Agregar Receta a la lista de recetas a intentar (usando base de datos)
    @PostMapping("/agregarReceta/{idReceta}")
    public ResponseEntity<String> agregarReceta(@PathVariable Integer idReceta, @RequestParam(required = false) Integer idUsuario, WebRequest request) {
        try {
            // Verificar si el usuario es visitante
            if (isVisitanteWithRestrictedAccess(idUsuario)) {
                return createVisitorRestrictionResponse();
            }
            
            System.out.println(" Backend: Attempting to add recipe " + idReceta + " for user " + idUsuario);
            
            Optional<Recetas> recetaOptional = recetasRepository.findById(idReceta);
            if (!recetaOptional.isPresent()) {
                System.out.println(" Backend: Recipe " + idReceta + " not found");
                return new ResponseEntity<>("Receta no encontrada", HttpStatus.NOT_FOUND);
            }
            
            Recetas receta = recetaOptional.get();
            System.out.println(" Backend: Found recipe: " + receta.getNombreReceta());
            
            // Intentar obtener usuario autenticado o usar el parámetro
            Usuarios usuario = null;
            if (idUsuario != null) {
                usuario = usuariosDAO.findById(idUsuario);
            } else {
                usuario = usuariosDAO.getUsuarioAutenticado();
            }
            
            if (usuario == null) {
                
                List<Recetas> listaRecetasSeleccionadas = getListaRecetasSeleccionadas(request);
                if (!listaRecetasSeleccionadas.contains(receta)) {
                    listaRecetasSeleccionadas.add(receta);
                    return new ResponseEntity<>("Receta agregada a tu lista temporal", HttpStatus.OK);
                } else {
                    return new ResponseEntity<>("La receta ya está en tu lista", HttpStatus.BAD_REQUEST);
                }
            }
            
            // Usar la nueva tabla recetas_a_intentar
            boolean alreadyExists = recetasAIntentarRepository.existsByIdRecetaAndIdUsuario(idReceta, usuario.getIdUsuario());
            
            System.out.println(" Backend: User " + usuario.getIdUsuario() + " - Recipe " + idReceta + " already exists: " + alreadyExists);
            
            if (!alreadyExists) {
                try {
                    RecetasAIntentar nuevaRecetaIntentar = new RecetasAIntentar(idReceta, usuario.getIdUsuario());
                    System.out.println(" Backend: Creating new RecetasAIntentar - idReceta: " + nuevaRecetaIntentar.getIdReceta() + 
                                     ", idUsuario: " + nuevaRecetaIntentar.getIdUsuario() + 
                                     ", completada: " + nuevaRecetaIntentar.getCompletada() + 
                                     ", fechaAgregada: " + nuevaRecetaIntentar.getFechaAgregada());
                    
                    RecetasAIntentar savedReceta = recetasAIntentarRepository.save(nuevaRecetaIntentar);
                    
                    System.out.println(" Backend: Saved RecetasAIntentar - idReceta: " + savedReceta.getIdReceta() + 
                                     ", idUsuario: " + savedReceta.getIdUsuario() + 
                                     ", completada: " + savedReceta.getCompletada() + 
                                     ", fechaAgregada: " + savedReceta.getFechaAgregada());
                    
                    System.out.println(" Backend: Recipe " + idReceta + " successfully added to user " + usuario.getIdUsuario() + " list as PENDING");
                    return new ResponseEntity<>("Receta agregada a tu lista de pendientes", HttpStatus.OK);
                } catch (Exception e) {
                    // Si hay error de constraint (duplicado en BD), manejarlo
                    System.out.println(" Backend: Error adding recipe: " + e.getMessage());
                    if (e.getMessage().contains("constraint") || e.getMessage().contains("duplicate")) {
                        return new ResponseEntity<>("La receta ya está en tu lista de pendientes", HttpStatus.BAD_REQUEST);
                    }
                    throw e; // Re-lanzar si es otro tipo de error
                }
            } else {
                System.out.println(" Backend: Recipe " + idReceta + " already exists in user " + usuario.getIdUsuario() + " list");
                return new ResponseEntity<>("La receta ya está en tu lista de pendientes", HttpStatus.BAD_REQUEST);
            }
            
        } catch (Exception e) {
            return new ResponseEntity<>("Error al agregar receta: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Eliminar una receta completamente (solo el propietario)
    @DeleteMapping("/eliminarRecetaCompleta/{idReceta}")
    public ResponseEntity<String> eliminarRecetaCompleta(@PathVariable Integer idReceta, @RequestParam(required = false) Integer idUsuario) {
        try {
            Optional<Recetas> recetaOptional = recetasRepository.findById(idReceta);
            if (!recetaOptional.isPresent()) {
                return new ResponseEntity<>("Receta no encontrada", HttpStatus.NOT_FOUND);
            }
            
            Recetas receta = recetaOptional.get();
            
            // Verificar que el usuario que intenta eliminar es el propietario
            Usuarios usuario = null;
            if (idUsuario != null) {
                usuario = usuariosDAO.findById(idUsuario);
            } else {
                usuario = usuariosDAO.getUsuarioAutenticado();
            }
            
            if (usuario == null) {
                return new ResponseEntity<>("Usuario no identificado", HttpStatus.UNAUTHORIZED);
            }
            
            if (!receta.getUsuario().getIdUsuario().equals(usuario.getIdUsuario())) {
                return new ResponseEntity<>("No tienes permisos para eliminar esta receta", HttpStatus.FORBIDDEN);
            }
            
            // Eliminar la receta completamente (incluyendo ingredientes por CASCADE)
            recetasRepository.delete(receta);
            
            return new ResponseEntity<>("Receta eliminada completamente", HttpStatus.OK);
            
        } catch (Exception e) {
            return new ResponseEntity<>("Error al eliminar receta: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Eliminar una receta de la lista de recetas a intentar
    @DeleteMapping("/eliminarReceta/{idReceta}")
    public ResponseEntity<String> eliminarReceta(@PathVariable Integer idReceta, @RequestParam(required = false) Integer idUsuario, WebRequest request) {
        try {
            Optional<Recetas> recetaOptional = recetasRepository.findById(idReceta);
            if (!recetaOptional.isPresent()) {
                return new ResponseEntity<>("Receta no encontrada", HttpStatus.NOT_FOUND);
            }
            
            Recetas receta = recetaOptional.get();
            
            // Intentar obtener usuario autenticado o usar el parámetro
            Usuarios usuario = null;
            if (idUsuario != null) {
                usuario = usuariosDAO.findById(idUsuario);
            } else {
                usuario = usuariosDAO.getUsuarioAutenticado();
            }
            
            if (usuario == null) {
                List<Recetas> listaRecetasSeleccionadas = getListaRecetasSeleccionadas(request);
                if (listaRecetasSeleccionadas.remove(receta)) {
                    return new ResponseEntity<>("Receta eliminada de tu lista temporal", HttpStatus.OK);
                } else {
                    return new ResponseEntity<>("Receta no encontrada en tu lista", HttpStatus.NOT_FOUND);
                }
            }
            
            // Usar la nueva tabla recetas_a_intentar
            boolean exists = recetasAIntentarRepository.existsByIdRecetaAndIdUsuario(idReceta, usuario.getIdUsuario());
            if (exists) {
                recetasAIntentarRepository.deleteByIdRecetaAndIdUsuario(idReceta, usuario.getIdUsuario());
                return new ResponseEntity<>("Receta eliminada de tu lista de pendientes", HttpStatus.OK);
            } else {
                return new ResponseEntity<>("Receta no encontrada en tu lista de pendientes", HttpStatus.NOT_FOUND);
            }
            
        } catch (Exception e) {
            return new ResponseEntity<>("Error al eliminar receta: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Obtener la lista de recetas a intentar
    @GetMapping("/getMiListaRecetas")
    public ResponseEntity<List<Map<String, Object>>> obtenerMiListaDeRecetas(@RequestParam(required = false) Integer idUsuario, WebRequest request) {
        try {
            // Verificar si el usuario es visitante
            if (isVisitanteWithRestrictedAccess(idUsuario)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ArrayList<>());
            }
            
            // Intentar obtener usuario autenticado o usar el parámetro
            Usuarios usuario = null;
            if (idUsuario != null) {
                usuario = usuariosDAO.findById(idUsuario);
            } else {
                usuario = usuariosDAO.getUsuarioAutenticado();
            }
            
            List<Map<String, Object>> recetasDTO = new ArrayList<>();
            
            if (usuario == null) {
                // Fallback a la sesión si no hay usuario autenticado
                List<Recetas> recetas = getListaRecetasSeleccionadas(request);
                for (Recetas receta : recetas) {
                    Map<String, Object> dto = createRecipeDTO(receta, false, null, null);
                    recetasDTO.add(dto);
                }
            } else {
                // Usar la nueva tabla recetas_a_intentar
                List<RecetasAIntentar> recetasAIntentar = recetasAIntentarRepository.findByIdUsuarioWithRecetaDetails(usuario.getIdUsuario());
                
                for (RecetasAIntentar recetaIntentar : recetasAIntentar) {
                    Recetas receta = recetaIntentar.getReceta();
                    if (receta != null) {
                        Map<String, Object> dto = createRecipeDTO(receta, recetaIntentar.getCompletada(), 
                                                                  recetaIntentar.getFechaCompletada(), 
                                                                  recetaIntentar.getFechaAgregada());
                        recetasDTO.add(dto);
                    }
                }
            }
            
            return new ResponseEntity<>(recetasDTO, HttpStatus.OK);
            
        } catch (Exception e) {
            return new ResponseEntity<>(new ArrayList<>(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    private Map<String, Object> createRecipeDTO(Recetas receta, Boolean completada, java.util.Date fechaCompletada, java.util.Date fechaAgregada) {
        Map<String, Object> dto = new HashMap<>();
        dto.put("idReceta", receta.getIdReceta());
        dto.put("nombreReceta", receta.getNombreReceta());
        dto.put("descripcionReceta", receta.getDescripcionReceta());
        dto.put("fotoPrincipal", receta.getFotoPrincipal());
        dto.put("porciones", receta.getPorciones());
        dto.put("cantidadPersonas", receta.getCantidadPersonas());
        dto.put("fecha", receta.getFecha());
        dto.put("autorizada", receta.isAutorizada());
        
        // Agregar estados de la lista de recetas a intentar
        dto.put("completada", completada != null ? completada : false);
        dto.put("fechaCompletada", fechaCompletada);
        dto.put("fechaAgregada", fechaAgregada);
        
        // Agregar información del tipo
        if (receta.getIdTipo() != null) {
            Map<String, Object> tipo = new HashMap<>();
            tipo.put("idTipo", receta.getIdTipo().getIdTipo());
            tipo.put("descripcion", receta.getIdTipo().getDescripcion());
            dto.put("tipoReceta", tipo);
        }
        
        // Agregar información del usuario
        if (receta.getUsuario() != null) {
            Map<String, Object> usuarioMap = new HashMap<>();
            usuarioMap.put("idUsuario", receta.getUsuario().getIdUsuario());
            usuarioMap.put("nombre", receta.getUsuario().getNombre());
            usuarioMap.put("nickname", receta.getUsuario().getNickname());
            usuarioMap.put("mail", receta.getUsuario().getMail());
            dto.put("usuario", usuarioMap);
        }
        
        return dto;
    }
    
    // Marcar receta como completada/pendiente
    @PutMapping("/marcarRecetaCompletada/{idReceta}")
    public ResponseEntity<String> marcarRecetaCompletada(
        @PathVariable Integer idReceta, 
        @RequestParam boolean completada,
        @RequestParam(required = false) Integer idUsuario, 
        WebRequest request) {
        try {
            // Verificar si el usuario es visitante
            if (isVisitanteWithRestrictedAccess(idUsuario)) {
                return createVisitorRestrictionResponse();
            }
            
            System.out.println(" Backend: Marking recipe " + idReceta + " as " + (completada ? "completed" : "pending") + " for user " + idUsuario);
            
            // Intentar obtener usuario autenticado o usar el parámetro
            Usuarios usuario = null;
            if (idUsuario != null) {
                usuario = usuariosDAO.findById(idUsuario);
            } else {
                usuario = usuariosDAO.getUsuarioAutenticado();
            }
            
            if (usuario == null) {
                return new ResponseEntity<>("Usuario no encontrado", HttpStatus.NOT_FOUND);
            }
            
            // Buscar o crear el registro en recetas_a_intentar
            Optional<RecetasAIntentar> recetaIntentarOpt = recetasAIntentarRepository.findByIdRecetaAndIdUsuario(idReceta, usuario.getIdUsuario());
            
            if (recetaIntentarOpt.isPresent()) {
                // Actualizar el estado existente
                RecetasAIntentar recetaIntentar = recetaIntentarOpt.get();
                recetaIntentar.setCompletada(completada);
                recetasAIntentarRepository.save(recetaIntentar);
                
                System.out.println(" Backend: Recipe " + idReceta + " marked as " + (completada ? "completed" : "pending") + " for user " + usuario.getIdUsuario());
                return new ResponseEntity<>(completada ? "Receta marcada como completada" : "Receta marcada como pendiente", HttpStatus.OK);
            } else {
                // Si no existe, crear nuevo registro (solo si se está marcando como completada)
                if (completada) {
                    // Verificar que la receta existe
                    Optional<Recetas> recetaOpt = recetasRepository.findById(idReceta);
                    if (!recetaOpt.isPresent()) {
                        return new ResponseEntity<>("Receta no encontrada", HttpStatus.NOT_FOUND);
                    }
                    
                    RecetasAIntentar nuevaRecetaIntentar = new RecetasAIntentar(idReceta, usuario.getIdUsuario(), true, new java.util.Date());
                    recetasAIntentarRepository.save(nuevaRecetaIntentar);
                    
                    System.out.println(" Backend: New recipe " + idReceta + " added and marked as completed for user " + usuario.getIdUsuario());
                    return new ResponseEntity<>("Receta agregada y marcada como completada", HttpStatus.OK);
                } else {
                    return new ResponseEntity<>("No se puede marcar como pendiente una receta que no está en la lista", HttpStatus.BAD_REQUEST);
                }
            }
            
        } catch (Exception e) {
            
            return new ResponseEntity<>("Error al marcar receta: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    //Ajustar porciones de recetas
    private Map<Long, List<Recetas>> recetasPersonalizadasPorUsuario = new HashMap<>();
    @GetMapping("/ajustarPorciones/{idReceta}")
    public ResponseEntity<?> ajustarReceta(@PathVariable Integer idReceta,
            @RequestParam String tipo, 
            @RequestParam(required = false) Integer porciones, 
            @RequestParam(required = false) Integer idUsuario
        ) {
        // Verificar si el usuario es visitante
        if (isVisitanteWithRestrictedAccess(idUsuario)) {
            return createVisitorRestrictionResponse();
        }
        
        Optional<Recetas> recetaOpt = recetasDAO.findByIdOptional(idReceta);
        if (recetaOpt.isEmpty()) return ResponseEntity.notFound().build();

        Recetas receta = recetaOpt.get();
        Recetas ajustada = receta.clonar(); 
        ajustarIngredientes(ajustada, tipo, porciones);
        return ResponseEntity.ok(ajustada);
    }

    //Ajustar por ingredientes
    private void ajustarIngredientes(Recetas receta, String tipo, Integer porciones) {
        for (Ingredientes ingrediente : receta.getIngredientes()) {
            double cantidadOriginal = ingrediente.getCantidad();
            switch (tipo) {
                case "mitad":
                    ingrediente.setCantidad(cantidadOriginal / 2);
                    break;
                case "doble":
                    ingrediente.setCantidad(cantidadOriginal * 2);
                    break;
                case "porciones":
                    if (porciones != null && porciones > 0 && receta.getPorciones() > 0) {
                        double factor = (double) porciones / receta.getPorciones();
                        ingrediente.setCantidad(cantidadOriginal * factor);
                        receta.setPorciones(porciones);
                    }
                    break;
            }
        }
    }
    
    @PostMapping("/ajustarPorIngrediente/{idReceta}")
    public ResponseEntity<?> ajustarPorIngrediente(@PathVariable Integer idReceta, @RequestParam String nombreIngrediente, @RequestParam double nuevaCantidad, @RequestParam(required = false) Integer idUsuario) {
        // Verificar si el usuario es visitante
        if (isVisitanteWithRestrictedAccess(idUsuario)) {
            return createVisitorRestrictionResponse();
        }
        
        Optional<Recetas> recetaOpt = recetasDAO.findByIdOptional(idReceta);
        if (recetaOpt.isEmpty()) return ResponseEntity.notFound().build();

        Recetas receta = recetaOpt.get();
        Recetas ajustada = receta.clonar();

        Optional<Ingredientes> ingBaseOpt = ajustada.getIngredientes().stream()
                .filter(i -> i.getNombre().equalsIgnoreCase(nombreIngrediente))
                .findFirst();

        if (ingBaseOpt.isEmpty()) {
            return ResponseEntity.badRequest().body("Ingrediente no encontrado.");
        }

        double factor = nuevaCantidad / ingBaseOpt.get().getCantidad();

        for (Ingredientes ing : ajustada.getIngredientes()) {
            ing.setCantidad(ing.getCantidad() * factor);
        }

        int nuevasPorciones = (int) Math.round(receta.getPorciones() * factor);
        ajustada.setPorciones(nuevasPorciones);

        return ResponseEntity.ok(ajustada);
    }
    
    //Cursos disponibles
    @GetMapping("/getCursosDisponibles")
    public ResponseEntity<List<Map<String, Object>>> obtenerCursosDisponibles(@RequestParam Integer idUsuario) {
        try {
            Usuarios usuario = usuariosDAO.findById(idUsuario);
            if (usuario == null) {
                return ResponseEntity.badRequest().body(null);
            }

            List<CronogramaCursos> cronogramas = cronogramaCursosRepository.findAll();
            List<Map<String, Object>> resultado = new ArrayList<>();

            for (CronogramaCursos cronograma : cronogramas) {
                Cursos curso = cronograma.getIdCurso();
                Sedes sede = cronograma.getIdSede();
                
                Map<String, Object> dto = new HashMap<>();
                dto.put("id", curso.getIdCurso());
                dto.put("idCurso", curso.getIdCurso());
                dto.put("idCronograma", cronograma.getIdCronograma());
                dto.put("title", curso.getDescripcion());
                dto.put("descripcion", curso.getDescripcion());
                
                // Información disponible para todos los usuarios
                if ("alumno".equalsIgnoreCase(usuario.getTipo())) {
                    // Información completa para estudiantes
                    dto.put("contenidos", curso.getContenidos());
                    dto.put("requerimientos", curso.getRequerimientos());
                    dto.put("duracion", curso.getDuracion());
                    dto.put("precio", curso.getPrecio());
                    dto.put("modalidad", curso.getModalidad());
                    dto.put("fechaInicio", cronograma.getFechaInicio());
                    dto.put("fechaFin", cronograma.getFechaFin());
                    dto.put("vacantesDisponibles", cronograma.getVacantesDisponibles());
                    
                    // Información de la sede
                    if (sede != null) {
                        Map<String, Object> sedeDto = new HashMap<>();
                        sedeDto.put("id", sede.getIdSede());
                        sedeDto.put("nombre", sede.getNombreSede());
                        sedeDto.put("direccion", sede.getDireccionSede());
                        sedeDto.put("telefono", sede.getTelefonoSede());
                        sedeDto.put("email", sede.getMailSede());
                        sedeDto.put("whatsapp", sede.getWhatsApp());
                        sedeDto.put("tipoBonificacion", sede.getTipoBonificacion());
                        sedeDto.put("bonificacion", sede.getBonificacionCursos());
                        sedeDto.put("tipoPromocion", sede.getTipoPromocion());
                        sedeDto.put("promocion", sede.getPromocionCursos());
                        dto.put("sede", sedeDto);
                    }
                    
                    // Calcular precio final con descuentos
                    BigDecimal precioFinal = curso.getPrecio();
                    if (sede != null && sede.getBonificacionCursos() > 0) {
                        if ("descuento".equalsIgnoreCase(sede.getTipoBonificacion())) {
                            precioFinal = precioFinal.subtract(precioFinal.multiply(BigDecimal.valueOf(sede.getBonificacionCursos() / 100)));
                        }
                    }
                    dto.put("precioFinal", precioFinal);
                } else {
                    // Información limitada para no estudiantes
                    dto.put("contenidos", "Información completa disponible para estudiantes registrados");
                    dto.put("requerimientos", "Registrate como estudiante para ver los requisitos");
                    dto.put("duracion", "-");
                    dto.put("precio", "-");
                    dto.put("modalidad", curso.getModalidad());
                }

                resultado.add(dto);
            }

            return ResponseEntity.ok(resultado);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
    
    
    //Inscripcion
    @PostMapping("/inscribirseACurso")
    public ResponseEntity<String> inscribirseACurso(
        @RequestParam int idAlumno,
        @RequestParam int idCronograma) {

        
        System.out.println("Recibido idAlumno: " + idAlumno);
        System.out.println("Recibido idCronograma: " + idCronograma);

        try {
            
            Optional<Alumnos> alumnoOpt = alumnosRepository.findById(idAlumno);
            
            if (!alumnoOpt.isPresent()) {
                
                Optional<Usuarios> usuarioOpt = usuariosRepository.findById(idAlumno);
                if (!usuarioOpt.isPresent()) {
                    return ResponseEntity.badRequest().body("Usuario no encontrado");
                }
                
                Usuarios usuario = usuarioOpt.get();
                if (!"alumno".equals(usuario.getTipo())) {
                    return ResponseEntity.badRequest().body("El usuario no es de tipo alumno");
                }
                
                
                Alumnos alumno = new Alumnos();
                alumno.setIdAlumno(idAlumno);
                alumno.setNroTarjeta(usuario.getMedioPago()); 
                alumno.setCuentaCorriente(new java.math.BigDecimal("10000.00")); 
                alumno.setUsuario(usuario); 
                alumnosRepository.save(alumno);
                
                System.out.println("Alumno creado automáticamente para usuario ID: " + idAlumno);
            }

            
            cursosDAO.inscribirAlumnoACurso(idAlumno, idCronograma);
            
            System.out.println("Inscripción exitosa - Cuenta corriente actualizada automáticamente");
            return ResponseEntity.ok("Inscripción realizada con éxito. Se ha descontado el monto del curso de tu cuenta corriente.");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al procesar la inscripción: " + e.getMessage());
        }
    }
    
    
    //Obtener el curso del alumno
    @GetMapping("/alumno/{idAlumno}")
    public ResponseEntity<List<Map<String, Object>>> getCursosDelAlumno(@PathVariable int idAlumno) {
        try {
            // Obtener inscripciones activas del alumno
            List<Inscripcion> inscripciones = inscripcionRepository.findByIdAlumnoAndEstadoInscripcion(idAlumno, "inscrito");
            List<Map<String, Object>> resultado = new ArrayList<>();

            for (Inscripcion inscripcion : inscripciones) {
                CronogramaCursos cronograma = inscripcion.getCronograma();
                Cursos curso = cronograma.getIdCurso();
                Sedes sede = cronograma.getIdSede();
                
                Map<String, Object> dto = new HashMap<>();
                dto.put("id", curso.getIdCurso());
                dto.put("idCurso", curso.getIdCurso());
                dto.put("idCronograma", cronograma.getIdCronograma());
                dto.put("idInscripcion", inscripcion.getIdInscripcion()); 
                dto.put("title", curso.getDescripcion());
                dto.put("descripcion", curso.getDescripcion());
                dto.put("contenidos", curso.getContenidos());
                dto.put("requerimientos", curso.getRequerimientos());
                dto.put("duracion", curso.getDuracion());
                dto.put("precio", curso.getPrecio());
                dto.put("modalidad", curso.getModalidad());
                dto.put("fechaInicio", cronograma.getFechaInicio());
                dto.put("fechaFin", cronograma.getFechaFin());
                dto.put("vacantesDisponibles", cronograma.getVacantesDisponibles());
                dto.put("fechaInscripcion", inscripcion.getFechaInscripcion());
                dto.put("estadoInscripcion", inscripcion.getEstadoInscripcion());
                dto.put("estadoPago", inscripcion.getEstadoPago());
                dto.put("monto", inscripcion.getMonto());
                
                
                System.out.println("Curso ID: " + curso.getIdCurso());
                System.out.println("Cronograma ID: " + cronograma.getIdCronograma());
                System.out.println("Inscripcion ID: " + inscripcion.getIdInscripcion());
                System.out.println("Estado inscripcion: " + inscripcion.getEstadoInscripcion());
                System.out.println("Estado pago: " + inscripcion.getEstadoPago());
                
                
                if (sede != null) {
                    Map<String, Object> sedeDto = new HashMap<>();
                    sedeDto.put("id", sede.getIdSede());
                    sedeDto.put("nombre", sede.getNombreSede());
                    sedeDto.put("direccion", sede.getDireccionSede());
                    sedeDto.put("telefono", sede.getTelefonoSede());
                    sedeDto.put("email", sede.getMailSede());
                    sedeDto.put("whatsapp", sede.getWhatsApp());
                    dto.put("sede", sedeDto);
                }
                
                resultado.add(dto);
            }

            return ResponseEntity.ok(resultado);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
    
    //Obtener información del alumno incluyendo cuenta corriente
    @GetMapping("/alumnoInfo/{idAlumno}")
    public ResponseEntity<Map<String, Object>> getInfoAlumno(@PathVariable int idAlumno) {
        try {
            Optional<Alumnos> alumnoOpt = alumnosRepository.findById(idAlumno);
            
            if (!alumnoOpt.isPresent()) {
                return ResponseEntity.notFound().build();
            }
            
            Alumnos alumno = alumnoOpt.get();
            Map<String, Object> resultado = new HashMap<>();
            
            resultado.put("idAlumno", alumno.getIdAlumno());
            resultado.put("numeroTarjeta", alumno.getNroTarjeta());
            resultado.put("dniFrente", alumno.getDniFrente());
            resultado.put("dniFondo", alumno.getDniFondo());
            resultado.put("tramite", alumno.getTramite());
            resultado.put("cuentaCorriente", alumno.getCuentaCorriente());
            
            // Incluir información básica del usuario relacionado
            if (alumno.getUsuario() != null) {
                Usuarios usuario = alumno.getUsuario();
                resultado.put("mail", usuario.getMail());
                resultado.put("nombre", usuario.getNombre());
                resultado.put("nickname", usuario.getNickname());
                resultado.put("tipo", usuario.getTipo());
            }
            
            return ResponseEntity.ok(resultado);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
    
    //Darse de baja de un curso
    @PostMapping("/baja/{idInscripcion}")
    public ResponseEntity<String> bajaCurso(@PathVariable int idInscripcion, @RequestParam boolean reintegroEnTarjeta) {
        
        System.out.println("idInscripcion: " + idInscripcion);
        System.out.println("reintegroEnTarjeta: " + reintegroEnTarjeta);
        
        Optional<Inscripcion> inscripcion = inscripcionDAO.findById(idInscripcion);

        if (inscripcion.isEmpty()) {
            System.out.println("Inscripción no encontrada con ID: " + idInscripcion);
            return ResponseEntity.badRequest().body("Inscripción no encontrada.");
        }
        Inscripcion inscripciones = inscripcion.get();
        Cursos curso = inscripciones.getCronograma().getIdCurso();
        LocalDateTime fechaInicioCurso = inscripciones.getCronograma().getFechaInicio();
        LocalDateTime fechaBaja = LocalDateTime.now();

        long diasDeDiferencia = ChronoUnit.DAYS.between(fechaBaja.toLocalDate(), fechaInicioCurso.toLocalDate());

        BigDecimal reintegro = BigDecimal.ZERO;
        String mensajeReintegro = "";

        if (diasDeDiferencia > 10) {
            // Si la baja es más de 10 días antes, reintegro total
            reintegro = curso.getPrecio();
            mensajeReintegro = "Reintegro completo";
        } else if (diasDeDiferencia <= 10 && diasDeDiferencia > 1) {
            // Si la baja es entre 9 y 1 días antes, reintegro del 70%
            reintegro = curso.getPrecio().multiply(BigDecimal.valueOf(0.7));
            mensajeReintegro = "Reintegro del 70%";
        } else if (diasDeDiferencia == 1) {
            // Si la baja es el día antes del curso, reintegro del 50%
            reintegro = curso.getPrecio().multiply(BigDecimal.valueOf(0.5));
            mensajeReintegro = "Reintegro del 50%";
        } else if (diasDeDiferencia == 0) {
            // Si la baja es el mismo día del inicio, reintegro del 50%
            reintegro = curso.getPrecio().multiply(BigDecimal.valueOf(0.50));
            mensajeReintegro = "Reintegro del 50% (mismo día de inicio)";
        } else {
            // Si la baja es después del inicio del curso, no hay reintegro pero se permite
            reintegro = BigDecimal.ZERO;
            mensajeReintegro = "Sin reintegro (curso ya iniciado)";
            System.out.println("Cancelación después del inicio - Sin reintegro");
        }

        
        Alumnos alumno = inscripciones.getAlumno();
        if (reintegro.compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal cuentaAnterior = alumno.getCuentaCorriente();
            alumno.setCuentaCorriente(cuentaAnterior.add(reintegro));
            alumnosRepository.save(alumno);
            
        }

        inscripciones.setEstadoInscripcion("cancelado");
        inscripcionDAO.save(inscripciones);
        
        System.out.println("Inscripción cancelada exitosamente");
        System.out.println("Estado actualizado a: " + inscripciones.getEstadoInscripcion());
        System.out.println("Días de diferencia: " + diasDeDiferencia);
        System.out.println("Reintegro calculado: " + reintegro);
        System.out.println("Mensaje: " + mensajeReintegro);

        String mensajeFinal = "Baja procesada correctamente. " + mensajeReintegro + ". Monto: $" + reintegro;
        if (reintegro.compareTo(BigDecimal.ZERO) > 0) {
            mensajeFinal += ". El reintegro ha sido acreditado en tu cuenta corriente.";
        }
        
        return ResponseEntity.ok(mensajeFinal);
    }


    
    @GetMapping("/auth/check-username")
    public ResponseEntity<Map<String, Object>> checkUsernameAvailability(@RequestParam String username) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            
            boolean available = usuariosDAO.isUsernameAvailable(username);
            result.put("available", available);
            
            if (!available) {
               
                List<String> suggestions = new ArrayList<>();
                String base = username.replaceAll("\\d+$", "");
                for (int i = 0; i < 3; i++) {
                    suggestions.add(base + (int)(Math.random() * 1000));
                }
                suggestions.add(base + LocalDate.now().getYear());
                suggestions.add(base + "_chef");
                suggestions.add("chef_" + base);
                result.put("suggestions", suggestions);
            } else {
                result.put("suggestions", new ArrayList<>());
            }
        } catch (Exception e) {
            result.put("available", true); 
            result.put("suggestions", new ArrayList<>());
            result.put("error", e.getMessage());
        }
        
        return ResponseEntity.ok(result);
    }

    // Endpoint de verificación de email
    @PostMapping("/auth/verify-email")
    public ResponseEntity<Map<String, Object>> verifyEmail(@RequestBody Map<String, String> payload) {
        String email = payload.get("email");
        Map<String, Object> result = new HashMap<>();
        Optional<Usuarios> userOpt = usuariosRepository.findByMail(email);
        if (userOpt.isPresent()) {
            Usuarios user = userOpt.get();
            user.setHabilitado("Si");
            usuariosRepository.save(user);
            result.put("success", true);
        } else {
            result.put("success", false);
            result.put("message", "Email not found");
        }
        return ResponseEntity.ok(result);
    }

    // Endpoint para completar perfil
    @PutMapping("/usuarios/perfil")
    public ResponseEntity<Map<String, Object>> completeProfile(@RequestBody Map<String, Object> profileData) {
        String email = (String) profileData.get("email");
        Map<String, Object> result = new HashMap<>();
        Optional<Usuarios> userOpt = usuariosRepository.findByMail(email);
        if (userOpt.isPresent()) {
            Usuarios user = userOpt.get();
            if (profileData.containsKey("nombre")) user.setNombre((String) profileData.get("nombre"));
            if (profileData.containsKey("direccion")) user.setDireccion((String) profileData.get("direccion"));
            if (profileData.containsKey("avatar")) user.setAvatar((String) profileData.get("avatar"));
            if (profileData.containsKey("medioPago")) user.setMedioPago((String) profileData.get("medioPago"));
            usuariosRepository.save(user);
            result.put("success", true);
        } else {
            result.put("success", false);
            result.put("message", "User not found");
        }
        return ResponseEntity.ok(result);
    }

    // Endpoint para obtener ID de usuario por email
    @GetMapping("/getUsuarioByEmail")
    public ResponseEntity<Map<String, Object>> getUsuarioByEmail(@RequestParam String mail) {
        Optional<Usuarios> userOpt = usuariosRepository.findByMail(mail);
        Map<String, Object> result = new HashMap<>();
        if (userOpt.isPresent()) {
            result.put("idUsuario", userOpt.get().getIdUsuario());
            return ResponseEntity.ok(result);
        } else {
            result.put("error", "Usuario no encontrado");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(result);
        }
    }

    
    @PutMapping("/aprobarReceta/{idReceta}")
    public ResponseEntity<String> aprobarReceta(@PathVariable Integer idReceta, @RequestParam boolean aprobar) {
        try {
            Optional<Recetas> recetaOpt = recetasDAO.findByIdOptional(idReceta);
            if (!recetaOpt.isPresent()) {
                return ResponseEntity.notFound().build();
            }
            
            Recetas receta = recetaOpt.get();
            receta.setAutorizada(aprobar);
            recetasDAO.save(receta);
            
            String mensaje = aprobar ? "Receta aprobada exitosamente" : "Receta rechazada";
            return ResponseEntity.ok(mensaje);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error al procesar la aprobación de la receta");
        }
    }

    
    @PostMapping("/crearUsuarioEmpresa")
    public ResponseEntity<String> crearUsuarioEmpresa(@RequestBody Usuarios usuario) {
        try {
            if (usuariosRepository.existsByMail(usuario.getMail())) {
                return ResponseEntity.badRequest().body("Ya existe un usuario con ese email");
            }
            
            usuario.setTipo("empresa"); 
            usuario.setHabilitado("Si"); 
            usuariosRepository.save(usuario);
            
            return ResponseEntity.ok("Usuario empresa creado exitosamente");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error al crear usuario empresa: " + e.getMessage());
        }
    }

    
    @PostMapping("/crearUsuarioAdmin")
    public ResponseEntity<String> crearUsuarioAdmin(@RequestBody Usuarios usuario) {
        try {
            if (usuariosRepository.existsByMail(usuario.getMail())) {
                return ResponseEntity.badRequest().body("Ya existe un usuario con ese email");
            }
            
            usuario.setTipo("admin"); 
            usuario.setRol("admin"); 
            usuario.setHabilitado("Si"); 
            usuariosRepository.save(usuario);
            
            return ResponseEntity.ok("Usuario admin creado exitosamente");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error al crear usuario admin: " + e.getMessage());
        }
    }

    @PutMapping("/recetas/{idReceta}")
    public ResponseEntity<String> actualizarReceta(@PathVariable Integer idReceta, @RequestBody Recetas recetaActualizada) {
        try {
            
            System.out.println("ID Receta: " + idReceta);
            System.out.println("Usuario en request: " + (recetaActualizada.getUsuario() != null ? recetaActualizada.getUsuario().getIdUsuario() : "NULL"));
            
            Optional<Recetas> recetaExistente = recetasRepository.findById(idReceta);
            
            if (!recetaExistente.isPresent()) {
                System.out.println("ERROR: Receta no encontrada con ID: " + idReceta);
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Receta no encontrada");
            }
            
            Recetas receta = recetaExistente.get();
            System.out.println("Receta encontrada: " + receta.getNombreReceta());
            System.out.println("Propietario actual: " + receta.getUsuario().getIdUsuario());
            
            // Verificar que el usuario que intenta editar es el propietario de la receta
            if (recetaActualizada.getUsuario() == null || recetaActualizada.getUsuario().getIdUsuario() == null) {
                System.out.println("ERROR: Usuario no proporcionado en el request");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Usuario no proporcionado");
            }
            
            if (!receta.getUsuario().getIdUsuario().equals(recetaActualizada.getUsuario().getIdUsuario())) {
                System.out.println("ERROR: Usuario no autorizado. Propietario: " + receta.getUsuario().getIdUsuario() + ", Usuario request: " + recetaActualizada.getUsuario().getIdUsuario());
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("No tienes permisos para editar esta receta");
            }
            
            System.out.println("Verificación de permisos exitosa");
            
            // Actualizar campos de la receta
            if (recetaActualizada.getNombreReceta() != null && !recetaActualizada.getNombreReceta().trim().isEmpty()) {
                System.out.println("Actualizando nombre: " + recetaActualizada.getNombreReceta());
                receta.setNombreReceta(recetaActualizada.getNombreReceta());
            }
            if (recetaActualizada.getDescripcionReceta() != null && !recetaActualizada.getDescripcionReceta().trim().isEmpty()) {
                System.out.println("Actualizando descripción");
                receta.setDescripcionReceta(recetaActualizada.getDescripcionReceta());
            }
            if (recetaActualizada.getFotoPrincipal() != null && !recetaActualizada.getFotoPrincipal().trim().isEmpty()) {
                System.out.println("Actualizando foto");
                receta.setFotoPrincipal(recetaActualizada.getFotoPrincipal());
            }
            if (recetaActualizada.getPorciones() > 0) {
                System.out.println("Actualizando porciones: " + recetaActualizada.getPorciones());
                receta.setPorciones(recetaActualizada.getPorciones());
            }
            if (recetaActualizada.getCantidadPersonas() > 0) {
                System.out.println("Actualizando cantidad personas: " + recetaActualizada.getCantidadPersonas());
                receta.setCantidadPersonas(recetaActualizada.getCantidadPersonas());
            }
            if (recetaActualizada.getInstrucciones() != null && !recetaActualizada.getInstrucciones().trim().isEmpty()) {
                System.out.println("Actualizando instrucciones");
                receta.setInstrucciones(recetaActualizada.getInstrucciones());
            }
            if (recetaActualizada.getIdTipo() != null) {
                System.out.println("Actualizando tipo de receta - ID recibido: " + recetaActualizada.getIdTipo().getIdTipo());
                
                // Validar que el tipo de receta existe en la base de datos
                Optional<TiposReceta> tipoRecetaExistente = tiposRecetaRepository.findById(recetaActualizada.getIdTipo().getIdTipo());
                if (!tipoRecetaExistente.isPresent()) {
                    System.out.println("ERROR: Tipo de receta no válido - ID: " + recetaActualizada.getIdTipo().getIdTipo());
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("El tipo de receta especificado no existe. Use el endpoint /getTiposReceta para obtener los tipos válidos.");
                }
                
                receta.setIdTipo(tipoRecetaExistente.get());
                System.out.println("Tipo de receta actualizado a: " + tipoRecetaExistente.get().getDescripcion());
            }
            
            // Actualizar ingredientes si se proporcionan
            if (recetaActualizada.getIngredientes() != null && !recetaActualizada.getIngredientes().isEmpty()) {
                System.out.println("Actualizando ingredientes: " + recetaActualizada.getIngredientes().size() + " ingredientes recibidos");
                
                // Eliminar ingredientes existentes
                List<Ingredientes> ingredientesExistentes = receta.getIngredientes();
                if (ingredientesExistentes != null && !ingredientesExistentes.isEmpty()) {
                    for (Ingredientes ing : ingredientesExistentes) {
                        ingredientesRepository.delete(ing);
                    }
                    receta.setIngredientes(new ArrayList<>());
                }
                
                // Añadir nuevos ingredientes
                for (Ingredientes ingrediente : recetaActualizada.getIngredientes()) {
                    ingrediente.setReceta(receta);
                    ingredientesRepository.save(ingrediente);
                    receta.getIngredientes().add(ingrediente);
                }
                
                System.out.println("Ingredientes actualizados correctamente");
            }
            
            // Actualizar pasos si se proporcionan
            if (recetaActualizada.getPasos() != null && !recetaActualizada.getPasos().isEmpty()) {
                System.out.println("Actualizando pasos: " + recetaActualizada.getPasos().size() + " pasos recibidos");
                
                // Eliminar pasos existentes
                List<Pasos> pasosExistentes = pasosRepository.findByIdRecetaOrderByNroPaso(receta);
                if (pasosExistentes != null && !pasosExistentes.isEmpty()) {
                    for (Pasos paso : pasosExistentes) {
                        pasosRepository.delete(paso);
                    }
                }
                
                // Añadir nuevos pasos
                for (int i = 0; i < recetaActualizada.getPasos().size(); i++) {
                    Pasos paso = recetaActualizada.getPasos().get(i);
                    paso.setIdReceta(receta);
                    paso.setNroPaso(i + 1); 
                    pasosRepository.save(paso);
                }
                
                System.out.println("Pasos actualizados correctamente");
            }
            
            // Marcar como no autorizada para revisión después de la edición
            receta.setAutorizada(false);
            
            // Actualizar fecha de modificación
            receta.setFecha(java.time.LocalDate.now());
            
            System.out.println("Guardando receta actualizada...");
            recetasRepository.save(receta);
            System.out.println("Receta guardada exitosamente");
            
            return ResponseEntity.ok("Receta actualizada exitosamente. Pendiente de aprobación.");
            
        } catch (Exception e) {
            System.out.println("ERROR GENERAL: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error al actualizar la receta: " + e.getMessage());
        }
    }

    
    @GetMapping("/getTiposReceta")
    public ResponseEntity<List<Map<String, Object>>> obtenerTiposReceta() {
        try {
            List<TiposReceta> tipos = tiposRecetaRepository.findAll();
            
            List<Map<String, Object>> tiposDTO = tipos.stream()
                .map(tipo -> {
                    Map<String, Object> dto = new HashMap<>();
                    dto.put("idTipo", tipo.getIdTipo());
                    dto.put("descripcion", tipo.getDescripcion());
                    return dto;
                })
                .collect(Collectors.toList());
            
            return ResponseEntity.ok(tiposDTO);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(null);
        }
    }

    
    @GetMapping("/getRecetasPendientesAprobacion")
    public ResponseEntity<List<Map<String, Object>>> obtenerRecetasPendientesAprobacion() {
        try {
            List<Recetas> recetasPendientes = recetasRepository.findByAutorizadaFalse();
            
            List<Map<String, Object>> recetasDTO = recetasPendientes.stream()
                .map(receta -> {
                    Map<String, Object> dto = new HashMap<>();
                    dto.put("idReceta", receta.getIdReceta());
                    dto.put("nombreReceta", receta.getNombreReceta());
                    dto.put("descripcionReceta", receta.getDescripcionReceta());
                    dto.put("fotoPrincipal", receta.getFotoPrincipal());
                    dto.put("porciones", receta.getPorciones());
                    dto.put("cantidadPersonas", receta.getCantidadPersonas());
                    dto.put("fecha", receta.getFecha());
                    dto.put("autorizada", receta.isAutorizada());
                    dto.put("instrucciones", receta.getInstrucciones());
                    
                    
                    List<Calificaciones> calificaciones = calificacionesRepository.findByIdReceta(receta);
                    if (!calificaciones.isEmpty()) {
                        double totalRating = calificaciones.stream()
                            .filter(cal -> cal != null && cal.getCalificacion() > 0) 
                            .mapToDouble(cal -> cal.getCalificacion())
                            .sum();
                        long validRatingsCount = calificaciones.stream()
                            .filter(cal -> cal != null && cal.getCalificacion() > 0)
                            .count();
                        
                        if (validRatingsCount > 0) {
                            double averageRating = totalRating / validRatingsCount;
                            dto.put("calificacionPromedio", Math.round(averageRating * 10.0) / 10.0); 
                        } else {
                            dto.put("calificacionPromedio", 0.0);
                        }
                        dto.put("totalCalificaciones", (int) validRatingsCount);
                    } else {
                        dto.put("calificacionPromedio", 0.0);
                        dto.put("totalCalificaciones", 0);
                    }
                    
                    if (receta.getIdTipo() != null) {
                        Map<String, Object> tipoDTO = new HashMap<>();
                        tipoDTO.put("idTipo", receta.getIdTipo().getIdTipo());
                        tipoDTO.put("descripcion", receta.getIdTipo().getDescripcion());
                        dto.put("tipo", tipoDTO);
                        dto.put("tipoReceta", tipoDTO); 
                    }
                    
                    if (receta.getUsuario() != null) {
                        Map<String, Object> usuarioDTO = new HashMap<>();
                        usuarioDTO.put("idUsuario", receta.getUsuario().getIdUsuario());
                        usuarioDTO.put("nombre", receta.getUsuario().getNombre());
                        usuarioDTO.put("mail", receta.getUsuario().getMail());
                        usuarioDTO.put("tipo", receta.getUsuario().getTipo());
                        dto.put("usuario", usuarioDTO);
                    }
                    
                    if (receta.getIngredientes() != null && !receta.getIngredientes().isEmpty()) {
                        List<Map<String, Object>> ingredientesDTO = receta.getIngredientes().stream()
                            .map(ingrediente -> {
                                Map<String, Object> ingDTO = new HashMap<>();
                                ingDTO.put("idIngrediente", ingrediente.getIdIngrediente());
                                ingDTO.put("nombre", ingrediente.getNombre());
                                ingDTO.put("cantidad", ingrediente.getCantidad());
                                ingDTO.put("unidadMedida", ingrediente.getUnidadMedida());
                                return ingDTO;
                            })
                            .collect(Collectors.toList());
                        dto.put("ingredientes", ingredientesDTO);
                    }
                    
                    return dto;
                })
                .collect(Collectors.toList());
            return new ResponseEntity<>(recetasDTO, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

   
    @GetMapping("/getRecetasPendientes")
    public ResponseEntity<List<Map<String, Object>>> obtenerRecetasPendientes() {
        return obtenerRecetasPendientesAprobacion();
    }

    
    @PostMapping("/recetas/{idReceta}/ingredientes")
    public ResponseEntity<?> agregarIngredientesAReceta(
            @PathVariable Integer idReceta,
            @RequestBody List<Ingredientes> ingredientes) {
        try {
            Optional<Recetas> recetaOpt = recetasRepository.findById(idReceta);
            if (!recetaOpt.isPresent()) {
                return ResponseEntity.notFound().build();
            }
            
            Recetas receta = recetaOpt.get();
            
            
            Usuarios usuarioActual = usuariosDAO.getUsuarioAutenticado();
            if (usuarioActual == null || !receta.getUsuario().getIdUsuario().equals(usuarioActual.getIdUsuario())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body("No tienes permiso para modificar esta receta");
            }
            
           
            for (Ingredientes ingrediente : ingredientes) {
                ingrediente.setReceta(receta);
                ingredientesRepository.save(ingrediente);
            }
            
            
            List<Ingredientes> ingredientesActuales = receta.getIngredientes();
            if (ingredientesActuales == null) {
                ingredientesActuales = new ArrayList<>();
            }
            ingredientesActuales.addAll(ingredientes);
            receta.setIngredientes(ingredientesActuales);
            
         
            recetasRepository.save(receta);
            
            return ResponseEntity.ok().body("Ingredientes agregados correctamente");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al agregar ingredientes: " + e.getMessage());
        }
    }

  
    @GetMapping("/ingredientes")
    public ResponseEntity<List<Map<String, Object>>> obtenerTodosLosIngredientes() {
        try {
            List<Ingredientes> ingredientes = ingredientesRepository.findAll();
            
            
            List<Map<String, Object>> ingredientesDTO = ingredientes.stream()
                .map(ingrediente -> {
                    Map<String, Object> dto = new HashMap<>();
                    dto.put("idIngrediente", ingrediente.getIdIngrediente());
                    dto.put("nombre", ingrediente.getNombre());
                    dto.put("cantidad", ingrediente.getCantidad());
                    dto.put("unidadMedida", ingrediente.getUnidadMedida());
                    
                    
                    if (ingrediente.getReceta() != null) {
                        Map<String, Object> recetaDTO = new HashMap<>();
                        recetaDTO.put("idReceta", ingrediente.getReceta().getIdReceta());
                        recetaDTO.put("nombreReceta", ingrediente.getReceta().getNombreReceta());
                        dto.put("receta", recetaDTO);
                    }
                    
                    return dto;
                })
                .collect(Collectors.toList());
            
            return ResponseEntity.ok(ingredientesDTO);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

   
    @PostMapping("/crearRecetaConIngredientes")
    public ResponseEntity<?> crearRecetaConIngredientes(@RequestBody Map<String, Object> recetaData) {
        try {
            System.out.println("Datos recibidos para crear receta: " + recetaData);
            
           
            Usuarios usuarioActual = usuariosDAO.getUsuarioAutenticado();
            
            if (usuarioActual == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("success", false, "message", "Debe iniciar sesión para crear una receta."));
            }

            // Crear nueva receta
            Recetas nuevaReceta = new Recetas();
            
            // Datos básicos de la receta
            String nombreReceta = (String) recetaData.get("nombreReceta");
            String descripcionReceta = (String) recetaData.get("descripcionReceta");
            String fotoPrincipal = (String) recetaData.get("fotoPrincipal");
            Integer porciones = (Integer) recetaData.get("porciones");
            Integer cantidadPersonas = (Integer) recetaData.get("cantidadPersonas");
            String instrucciones = (String) recetaData.get("instrucciones");
            
            nuevaReceta.setNombreReceta(nombreReceta);
            nuevaReceta.setDescripcionReceta(descripcionReceta);
            nuevaReceta.setFotoPrincipal(fotoPrincipal);
            nuevaReceta.setPorciones(porciones != null ? porciones : 1);
            nuevaReceta.setCantidadPersonas(cantidadPersonas != null ? cantidadPersonas : 1);
            nuevaReceta.setInstrucciones(instrucciones);
            nuevaReceta.setUsuario(usuarioActual);
            nuevaReceta.setAutorizada(false); 
            nuevaReceta.setFecha(java.time.LocalDate.now());
            
            
            if (recetaData.get("idTipo") != null) {
                Map<String, Object> tipoData = (Map<String, Object>) recetaData.get("idTipo");
                Integer idTipo = (Integer) tipoData.get("idTipo");
                if (idTipo != null) {
                    Optional<TiposReceta> tipoReceta = tiposRecetaRepository.findById(idTipo);
                    if (tipoReceta.isPresent()) {
                        nuevaReceta.setIdTipo(tipoReceta.get());
                    }
                }
            }
            
            
            Recetas recetaGuardada = recetasRepository.save(nuevaReceta);
            
            
            if (recetaData.get("ingredientes") != null && recetaData.get("ingredientes") instanceof List) {
                List<Map<String, Object>> ingredientesData = (List<Map<String, Object>>) recetaData.get("ingredientes");
                
                for (Map<String, Object> ingredienteData : ingredientesData) {
                    String nombre = (String) ingredienteData.get("nombre");
                    Number cantidadNum = (Number) ingredienteData.get("cantidad");
                    String unidadMedida = (String) ingredienteData.get("unidadMedida");
                    
                    if (nombre != null && !nombre.trim().isEmpty()) {
                        Ingredientes ingrediente = new Ingredientes();
                        ingrediente.setNombre(nombre.trim());
                        ingrediente.setCantidad(cantidadNum != null ? cantidadNum.doubleValue() : 1.0);
                        ingrediente.setUnidadMedida(unidadMedida != null ? unidadMedida : "unidad");
                        ingrediente.setReceta(recetaGuardada);
                        
                        ingredientesRepository.save(ingrediente);
                    }
                }
            }
            
            
            if (recetaData.get("fotos") != null && recetaData.get("fotos") instanceof List) {
                List<Map<String, Object>> fotosData = (List<Map<String, Object>>) recetaData.get("fotos");
                
                for (Map<String, Object> fotoData : fotosData) {
                    String urlFoto = (String) fotoData.get("url");
                    String tipoContenido = (String) fotoData.get("tipo");
                    String extension = (String) fotoData.get("extension");
                    
                    if (urlFoto != null && !urlFoto.trim().isEmpty()) {
                        Multimedia multimedia = new Multimedia();
                        multimedia.setReceta(recetaGuardada);
                        multimedia.setTipoContenido(tipoContenido != null ? tipoContenido : "foto");
                        multimedia.setExtension(extension != null ? extension : ".jpg");
                        multimedia.setUrlContenido(urlFoto);
                        
                        
                        multimediaRepository.save(multimedia);
                    }
                }
            }
            
           
            if (recetaData.get("fotosInstrucciones") != null && recetaData.get("fotosInstrucciones") instanceof List) {
                List<Map<String, Object>> fotosInstruccionesData = (List<Map<String, Object>>) recetaData.get("fotosInstrucciones");
                
                for (Map<String, Object> fotoInstruccionData : fotosInstruccionesData) {
                    String urlFoto = (String) fotoInstruccionData.get("url");
                    String tipoContenido = (String) fotoInstruccionData.get("tipo");
                    String extension = (String) fotoInstruccionData.get("extension");
                    Integer nroPaso = (Integer) fotoInstruccionData.get("nroPaso");
                    
                    if (urlFoto != null && !urlFoto.trim().isEmpty() && nroPaso != null) {
                        // Buscar el paso correspondiente
                        List<Pasos> pasosReceta = pasosRepository.findByIdRecetaOrderByNroPaso(recetaGuardada);
                        Pasos pasoCorrespondiente = null;
                        
                        for (Pasos paso : pasosReceta) {
                            if (paso.getNroPaso() == nroPaso) {
                                pasoCorrespondiente = paso;
                                break;
                            }
                        }
                        
                        Multimedia multimedia = new Multimedia();
                        multimedia.setReceta(recetaGuardada);
                        multimedia.setTipoContenido(tipoContenido != null ? tipoContenido : "foto_paso");
                        multimedia.setExtension(extension != null ? extension : ".jpg");
                        multimedia.setUrlContenido(urlFoto);
                        
                        if (pasoCorrespondiente != null) {
                            multimedia.setIdPaso(pasoCorrespondiente);
                        }
                        
                        multimediaRepository.save(multimedia);
                    }
                }
            }
            
            System.out.println("Receta creada exitosamente con ID: " + recetaGuardada.getIdReceta());
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Receta creada exitosamente",
                "idReceta", recetaGuardada.getIdReceta(),
                "data", recetaGuardada
            ));
            
        } catch (Exception e) {
            System.err.println("Error creando receta: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of(
                    "success", false,
                    "message", "Error interno del servidor: " + e.getMessage()
                ));
        }
    }

 
    @PostMapping("/crearRecetaSimple")
    public ResponseEntity<?> crearRecetaSimple(@RequestBody Map<String, Object> recetaData) {
        try {
            System.out.println("Datos recibidos para crear receta simple: " + recetaData);
            
        
            Integer idUsuario = null;
            
            if (recetaData.get("usuario") != null && recetaData.get("usuario") instanceof Map) {
                Map<String, Object> usuarioData = (Map<String, Object>) recetaData.get("usuario");
                idUsuario = (Integer) usuarioData.get("idUsuario");
            }
            
            if (idUsuario == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("success", false, "message", "ID de usuario requerido."));
            }
            
         
            Optional<Usuarios> usuarioOpt = usuariosRepository.findById(idUsuario);
            if (!usuarioOpt.isPresent()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("success", false, "message", "Usuario no encontrado."));
            }
            
            Usuarios usuario = usuarioOpt.get();

           
            Recetas nuevaReceta = new Recetas();
            
          
            String nombreReceta = (String) recetaData.get("nombreReceta");
            String descripcionReceta = (String) recetaData.get("descripcionReceta");
            String fotoPrincipal = (String) recetaData.get("fotoPrincipal");
            Integer porciones = (Integer) recetaData.get("porciones");
            Integer cantidadPersonas = (Integer) recetaData.get("cantidadPersonas");
            String instrucciones = (String) recetaData.get("instrucciones");
            
            nuevaReceta.setNombreReceta(nombreReceta);
            nuevaReceta.setDescripcionReceta(descripcionReceta);
            nuevaReceta.setFotoPrincipal(fotoPrincipal);
            nuevaReceta.setPorciones(porciones != null ? porciones : 1);
            nuevaReceta.setCantidadPersonas(cantidadPersonas != null ? cantidadPersonas : 1);
            nuevaReceta.setInstrucciones(instrucciones);
            nuevaReceta.setUsuario(usuario);
            nuevaReceta.setAutorizada(false); 
            nuevaReceta.setFecha(java.time.LocalDate.now());
            
           
            if (recetaData.get("idTipo") != null) {
                if (recetaData.get("idTipo") instanceof Map) {
                    Map<String, Object> tipoData = (Map<String, Object>) recetaData.get("idTipo");
                    Integer idTipo = (Integer) tipoData.get("idTipo");
                    if (idTipo != null) {
                        Optional<TiposReceta> tipoReceta = tiposRecetaRepository.findById(idTipo);
                        if (tipoReceta.isPresent()) {
                            nuevaReceta.setIdTipo(tipoReceta.get());
                        }
                    }
                } else if (recetaData.get("idTipo") instanceof Integer) {
                    Integer idTipo = (Integer) recetaData.get("idTipo");
                    Optional<TiposReceta> tipoReceta = tiposRecetaRepository.findById(idTipo);
                    if (tipoReceta.isPresent()) {
                        nuevaReceta.setIdTipo(tipoReceta.get());
                    }
                }
            }
            
            
            Recetas recetaGuardada = recetasRepository.save(nuevaReceta);
            
       
            if (recetaData.get("ingredientes") != null && recetaData.get("ingredientes") instanceof List) {
                List<Map<String, Object>> ingredientesData = (List<Map<String, Object>>) recetaData.get("ingredientes");
                
                for (Map<String, Object> ingredienteData : ingredientesData) {
                    String nombre = (String) ingredienteData.get("nombre");
                    Number cantidadNum = (Number) ingredienteData.get("cantidad");
                    String unidadMedida = (String) ingredienteData.get("unidadMedida");
                    
                    if (nombre != null && !nombre.trim().isEmpty()) {
                        Ingredientes ingrediente = new Ingredientes();
                        ingrediente.setNombre(nombre.trim());
                        ingrediente.setCantidad(cantidadNum != null ? cantidadNum.doubleValue() : 1.0);
                        ingrediente.setUnidadMedida(unidadMedida != null ? unidadMedida : "unidad");
                        ingrediente.setReceta(recetaGuardada);
                        
                        ingredientesRepository.save(ingrediente);
                    }
                }
            }
            
      
            if (instrucciones != null && !instrucciones.trim().isEmpty()) {
                String[] pasosTexto = instrucciones.split("\\n");
                for (int i = 0; i < pasosTexto.length; i++) {
                    String textoPaso = pasosTexto[i].trim();
                    if (!textoPaso.isEmpty()) {
                        Pasos paso = new Pasos();
                        paso.setIdReceta(recetaGuardada);
                        paso.setNroPaso(i + 1);
                        paso.setTexto(textoPaso);
                        
                        pasosRepository.save(paso);
                    }
                }
            }
            
            
            if (recetaData.get("fotos") != null && recetaData.get("fotos") instanceof List) {
                List<Map<String, Object>> fotosData = (List<Map<String, Object>>) recetaData.get("fotos");
                for (Map<String, Object> fotoData : fotosData) {
                    String urlFoto = (String) fotoData.get("url");
                    String tipoContenido = (String) fotoData.get("tipo");
                    String extension = (String) fotoData.get("extension");
                    Integer idPaso = (Integer) fotoData.get("idPaso"); 
                    
                    if (urlFoto != null && !urlFoto.trim().isEmpty()) {
                        Multimedia multimedia = new Multimedia();
                        multimedia.setReceta(recetaGuardada);
                        multimedia.setUrlContenido(urlFoto);
                        multimedia.setTipoContenido(tipoContenido != null ? tipoContenido : "foto");
                        multimedia.setExtension(extension != null ? extension : "jpg");
                        
                        
                        if (idPaso != null) {
                            List<Pasos> pasosReceta = pasosRepository.findByIdRecetaOrderByNroPaso(recetaGuardada);
                            if (idPaso > 0 && idPaso <= pasosReceta.size()) {
                                multimedia.setIdPaso(pasosReceta.get(idPaso - 1));
                            }
                        }
                        
                        multimediaRepository.save(multimedia);
                    }
                }
            }
            
            
            if (recetaData.get("fotosInstrucciones") != null && recetaData.get("fotosInstrucciones") instanceof List) {
                List<Map<String, Object>> fotosInstruccionesData = (List<Map<String, Object>>) recetaData.get("fotosInstrucciones");
                List<Pasos> pasosReceta = pasosRepository.findByIdRecetaOrderByNroPaso(recetaGuardada);
                
                for (Map<String, Object> fotoInstruccion : fotosInstruccionesData) {
                    String urlFoto = (String) fotoInstruccion.get("url");
                    Integer numeroPaso = (Integer) fotoInstruccion.get("paso");
                    String tipoContenido = (String) fotoInstruccion.get("tipo");
                    String extension = (String) fotoInstruccion.get("extension");
                    
                    if (urlFoto != null && !urlFoto.trim().isEmpty() && numeroPaso != null) {
                      
                        Pasos pasoCorrespondiente = null;
                        for (Pasos paso : pasosReceta) {
                            if (paso.getNroPaso() == numeroPaso) {
                                pasoCorrespondiente = paso;
                                break;
                            }
                        }
                        
                        if (pasoCorrespondiente != null) {
                            Multimedia multimedia = new Multimedia();
                            multimedia.setReceta(recetaGuardada);
                            multimedia.setIdPaso(pasoCorrespondiente);
                            multimedia.setUrlContenido(urlFoto);
                            multimedia.setTipoContenido(tipoContenido != null ? tipoContenido : "foto");
                            multimedia.setExtension(extension != null ? extension : "jpg");
                            
                            multimediaRepository.save(multimedia);
                        }
                    }
                }
            }
            
            System.out.println("Receta creada exitosamente con ID: " + recetaGuardada.getIdReceta());
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Receta creada exitosamente",
                "idReceta", recetaGuardada.getIdReceta(),
                "data", recetaGuardada
            ));
            
        } catch (Exception e) {
            System.err.println("Error creando receta simple: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of(
                    "success", false,
                    "message", "Error interno del servidor: " + e.getMessage()
                ));
        }
    }

   
    @PutMapping("/actualizarRecetaConPasos/{idReceta}")
    public ResponseEntity<?> actualizarRecetaConPasos(@PathVariable Integer idReceta, @RequestBody Map<String, Object> recetaData) {
        try {
           
            System.out.println("ID Receta: " + idReceta);
            System.out.println("Datos recibidos: " + recetaData);
            
            Optional<Recetas> recetaExistente = recetasRepository.findById(idReceta);
            
            if (!recetaExistente.isPresent()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Receta no encontrada");
            }
            
            Recetas receta = recetaExistente.get();
            
           
            Integer idUsuario = null;
            if (recetaData.get("idUsuario") != null) {
                idUsuario = (Integer) recetaData.get("idUsuario");
            } else if (recetaData.get("usuario") != null && ((Map<String, Object>)recetaData.get("usuario")).get("idUsuario") != null) {
                idUsuario = (Integer) ((Map<String, Object>)recetaData.get("usuario")).get("idUsuario");
            }
            
            if (idUsuario == null || !receta.getUsuario().getIdUsuario().equals(idUsuario)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("No tienes permisos para editar esta receta");
            }
            
            // Actualizar campos básicos
            if (recetaData.get("nombreReceta") != null) {
                receta.setNombreReceta((String) recetaData.get("nombreReceta"));
            }
            if (recetaData.get("descripcionReceta") != null) {
                receta.setDescripcionReceta((String) recetaData.get("descripcionReceta"));
            }
            if (recetaData.get("fotoPrincipal") != null) {
                receta.setFotoPrincipal((String) recetaData.get("fotoPrincipal"));
            }
            if (recetaData.get("porciones") != null) {
                receta.setPorciones((Integer) recetaData.get("porciones"));
            }
            if (recetaData.get("cantidadPersonas") != null) {
                receta.setCantidadPersonas((Integer) recetaData.get("cantidadPersonas"));
            }
            
            // Actualizar tipo de receta si se proporciona
            if (recetaData.get("idTipo") != null) {
                Map<String, Object> tipoMap = (Map<String, Object>) recetaData.get("idTipo");
                Integer idTipo = (Integer) tipoMap.get("idTipo");
                Optional<TiposReceta> tipoReceta = tiposRecetaRepository.findById(idTipo);
                if (tipoReceta.isPresent()) {
                    receta.setIdTipo(tipoReceta.get());
                }
            }
            
            // Eliminar y actualizar pasos
            if (recetaData.get("pasos") != null && recetaData.get("pasos") instanceof List) {
                // Eliminar pasos existentes
                List<Pasos> pasosExistentes = pasosRepository.findByIdRecetaOrderByNroPaso(receta);
                for (Pasos paso : pasosExistentes) {
                    pasosRepository.delete(paso);
                }
                
                // Agregar nuevos pasos
                List<Map<String, Object>> pasosData = (List<Map<String, Object>>) recetaData.get("pasos");
                for (int i = 0; i < pasosData.size(); i++) {
                    Map<String, Object> pasoData = pasosData.get(i);
                    String textoPaso = (String) pasoData.get("texto");
                    
                    if (textoPaso != null && !textoPaso.trim().isEmpty()) {
                        Pasos paso = new Pasos();
                        paso.setIdReceta(receta);
                        paso.setNroPaso(i + 1);
                        paso.setTexto(textoPaso.trim());
                        pasosRepository.save(paso);
                    }
                }
            }
            
            // Eliminar y actualizar ingredientes
            if (recetaData.get("ingredientes") != null && recetaData.get("ingredientes") instanceof List) {
                // Eliminar ingredientes existentes
                List<Ingredientes> ingredientesExistentes = receta.getIngredientes();
                for (Ingredientes ing : ingredientesExistentes) {
                    ingredientesRepository.delete(ing);
                }
                receta.setIngredientes(new ArrayList<>());
                
                // Agregar nuevos ingredientes
                List<Map<String, Object>> ingredientesData = (List<Map<String, Object>>) recetaData.get("ingredientes");
                for (Map<String, Object> ingredienteData : ingredientesData) {
                    String nombre = (String) ingredienteData.get("nombre");
                    Double cantidad = null;
                    String unidadMedida = "unidad";
                    
                    if (ingredienteData.get("cantidad") != null) {
                        if (ingredienteData.get("cantidad") instanceof Double) {
                            cantidad = (Double) ingredienteData.get("cantidad");
                        } else if (ingredienteData.get("cantidad") instanceof Integer) {
                            cantidad = ((Integer) ingredienteData.get("cantidad")).doubleValue();
                        } else if (ingredienteData.get("cantidad") instanceof String) {
                            try {
                                cantidad = Double.parseDouble((String) ingredienteData.get("cantidad"));
                            } catch (NumberFormatException e) {
                                cantidad = 1.0;
                            }
                        }
                    }
                    
                    if (ingredienteData.get("unidadMedida") != null) {
                        unidadMedida = (String) ingredienteData.get("unidadMedida");
                    }
                    
                    if (nombre != null && !nombre.trim().isEmpty()) {
                        Ingredientes ingrediente = new Ingredientes();
                        ingrediente.setNombre(nombre);
                        ingrediente.setCantidad(cantidad != null ? cantidad : 1.0);
                        ingrediente.setUnidadMedida(unidadMedida);
                        ingrediente.setReceta(receta);
                        ingredientesRepository.save(ingrediente);
                        receta.getIngredientes().add(ingrediente);
                    }
                }
            }
            
            // Actualizar multimedia/fotos
            if (recetaData.get("fotos") != null && recetaData.get("fotos") instanceof List) {
                // Eliminar multimedia existente
                List<Multimedia> multimediaExistente = multimediaRepository.findByReceta(receta);
                for (Multimedia media : multimediaExistente) {
                    multimediaRepository.delete(media);
                }
                
                // Agregar nuevas fotos
                List<Map<String, Object>> fotosData = (List<Map<String, Object>>) recetaData.get("fotos");
                for (Map<String, Object> fotoData : fotosData) {
                    String urlFoto = (String) fotoData.get("url");
                    String tipoContenido = (String) fotoData.get("tipo");
                    String extension = (String) fotoData.get("extension");
                    
                    if (urlFoto != null && !urlFoto.trim().isEmpty()) {
                        Multimedia multimedia = new Multimedia();
                        multimedia.setReceta(receta);
                        multimedia.setUrlContenido(urlFoto);
                        multimedia.setTipoContenido(tipoContenido != null ? tipoContenido : "foto");
                        multimedia.setExtension(extension != null ? extension : ".jpg");
                        multimediaRepository.save(multimedia);
                    }
                }
            }
            
            // Marcar como no autorizada para nueva revisión
            receta.setAutorizada(false);
            
            // Actualizar fecha
            receta.setFecha(java.time.LocalDate.now());
            
            // Guardar receta
            recetasRepository.save(receta);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Receta actualizada exitosamente");
            response.put("idReceta", receta.getIdReceta());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error al actualizar la receta: " + e.getMessage());
        }
    }

    @PostMapping("/guardarReceta/{idReceta}")
    public ResponseEntity<?> guardarReceta(@PathVariable Integer idReceta, @RequestParam(required = false) Integer idUsuario) {
        try {
            // Verificar si el usuario es visitante
            if (isVisitanteWithRestrictedAccess(idUsuario)) {
                return createVisitorRestrictionResponse();
            }
            
            // Obtener el usuario autenticado o usar el ID proporcionado
            Usuarios usuario;
            if (idUsuario != null) {
                usuario = usuariosRepository.findById(idUsuario).orElse(null);
            } else {
                usuario = usuariosDAO.getUsuarioAutenticado();
            }
            
            if (usuario == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Debe iniciar sesión para guardar una receta.");
            }
            
            // Verificar si la receta existe
            Optional<Recetas> recetaOpt = recetasRepository.findById(idReceta);
            if (!recetaOpt.isPresent()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("La receta no existe.");
            }
            
            Recetas receta = recetaOpt.get();
            
            // Verificar si ya está guardada
            if (recetasGuardadasRepository.existsByIdRecetaAndIdUsuario(idReceta, usuario.getIdUsuario())) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body("Esta receta ya está guardada.");
            }
            
            // Guardar la receta
            RecetasGuardadas recetaGuardada = new RecetasGuardadas(idReceta, usuario.getIdUsuario());
            recetaGuardada.setFechaGuardada(new java.util.Date());
            recetasGuardadasRepository.save(recetaGuardada);
            
            return ResponseEntity.ok("Receta guardada exitosamente.");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error al guardar la receta: " + e.getMessage());
        }
    }
    
    // Obtener todas las recetas guardadas por un usuario
    @GetMapping("/recetasGuardadas")
    public ResponseEntity<?> getRecetasGuardadas(@RequestParam(required = false) Integer idUsuario) {
        try {
            // Verificar si el usuario es visitante
            if (isVisitanteWithRestrictedAccess(idUsuario)) {
                return createVisitorRestrictionResponse();
            }
            
            // Obtener el usuario autenticado o usar el ID proporcionado
            Usuarios usuario;
            if (idUsuario != null) {
                usuario = usuariosRepository.findById(idUsuario).orElse(null);
            } else {
                usuario = usuariosDAO.getUsuarioAutenticado();
            }
            
            if (usuario == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Debe iniciar sesión para ver sus recetas guardadas.");
            }
            
            // Obtener las recetas guardadas con detalles
            List<RecetasGuardadas> recetasGuardadas = recetasGuardadasRepository.findByIdUsuarioWithRecetaDetails(usuario.getIdUsuario());
            
            // Transformar a DTOs para la respuesta
            List<Map<String, Object>> recetasDTO = recetasGuardadas.stream()
                .map(rg -> {
                    Recetas receta = rg.getReceta();
                    return createRecipeDTO(receta, null, null, rg.getFechaGuardada());
                })
                .collect(Collectors.toList());
                
            return new ResponseEntity<>(recetasDTO, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error al obtener las recetas guardadas: " + e.getMessage());
        }
    }
    
    // Eliminar una receta guardada
    @DeleteMapping("/eliminarRecetaGuardada/{idReceta}")
    public ResponseEntity<?> eliminarRecetaGuardada(@PathVariable Integer idReceta, @RequestParam(required = false) Integer idUsuario) {
        try {
            // Verificar si el usuario es visitante
            if (isVisitanteWithRestrictedAccess(idUsuario)) {
                return createVisitorRestrictionResponse();
            }
            
            // Obtener el usuario autenticado o usar el ID proporcionado
            Usuarios usuario;
            if (idUsuario != null) {
                usuario = usuariosRepository.findById(idUsuario).orElse(null);
            } else {
                usuario = usuariosDAO.getUsuarioAutenticado();
            }
            
            if (usuario == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Debe iniciar sesión para eliminar una receta guardada.");
            }
            
            // Verificar si la receta está guardada
            if (!recetasGuardadasRepository.existsByIdRecetaAndIdUsuario(idReceta, usuario.getIdUsuario())) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Esta receta no está en tu lista de guardadas.");
            }
            
            // Eliminar la receta guardada
            recetasGuardadasRepository.deleteByIdRecetaAndIdUsuario(idReceta, usuario.getIdUsuario());
            
            return ResponseEntity.ok("Receta eliminada de tus guardadas.");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al eliminar la receta guardada: " + e.getMessage());
        }
    }

    // Obtener multimedia de una receta específica
    @GetMapping("/getMultimediaReceta/{idReceta}")
    public ResponseEntity<?> getMultimediaReceta(@PathVariable Integer idReceta) {
        try {
            Optional<Recetas> recetaOpt = recetasRepository.findById(idReceta);
            if (!recetaOpt.isPresent()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Receta no encontrada");
            }
            
            Recetas receta = recetaOpt.get();
            List<Multimedia> multimedia = multimediaRepository.findByReceta(receta);
            
            // Formatear multimedia para respuesta
            List<Map<String, Object>> multimediaResponse = new ArrayList<>();
            for (Multimedia media : multimedia) {
                Map<String, Object> mediaMap = new HashMap<>();
                mediaMap.put("idContenido", media.getIdContenido());
                mediaMap.put("urlContenido", media.getUrlContenido());
                mediaMap.put("tipoContenido", media.getTipoContenido());
                mediaMap.put("extension", media.getExtension());
                mediaMap.put("idPaso", media.getIdPaso() != null ? media.getIdPaso().getNroPaso() : null);
                multimediaResponse.add(mediaMap);
            }
            
            return ResponseEntity.ok(multimediaResponse);
            
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error al obtener multimedia: " + e.getMessage());
        }
    }
}


