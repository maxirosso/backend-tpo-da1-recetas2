package com.example.demo.controlador;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
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
import com.example.demo.modelo.Calificaciones;
import com.example.demo.modelo.CronogramaCursos;
import com.example.demo.modelo.Cursos;
import com.example.demo.modelo.Ingredientes;
import com.example.demo.modelo.Inscripcion;
import com.example.demo.modelo.Recetas;
import com.example.demo.modelo.Sedes;
import com.example.demo.modelo.TiposReceta;
import com.example.demo.modelo.Usuarios;

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
	
	
	@GetMapping("/")
	public String mensaje() {
		return ("Comenzamos a ejecutar");
	}
	
	//login usuario
	@PostMapping("/login")
	public ResponseEntity<?> login(@RequestParam String mail, @RequestParam String password) {
	    Usuarios user = usuariosRepository.findByMailAndPassword(mail, password);
	    if (user != null) {
	        return ResponseEntity.ok(user); 
	    }
	    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Credenciales inválidas");
	}
	
	//si tiene conexión se le mostrarán las 3 últimas recetas cargadas por los usuarios
	@GetMapping("/ultimasRecetas")
	public ResponseEntity<List<Recetas>> getUltimasRecetas() {
	    List<Recetas> recetas = recetasRepository.findTop3ByOrderByFechaDesc();
	    return ResponseEntity.ok(recetas);
	}
	
	
	//recuperar clave
	@PostMapping("/recuperarClave")
	public ResponseEntity<String> recuperarClave(@RequestParam String mail) {
	    Optional<Usuarios> usuario = usuariosRepository.findByMail(mail);

	    if (usuario.isPresent()) {
	        Usuarios usuarios = usuario.get();
	        String codigo = String.valueOf((int)(Math.random()*900000 + 100000)); // Código de 6 dígitos
	        usuarios.setCodigoRecuperacion(codigo);
	        usuariosRepository.save(usuarios);

	        usuariosDAO.enviarCorreoDeConfirmacion(mail);

	        return ResponseEntity.ok("Código enviado al correo.");
	    }

	    return ResponseEntity.badRequest().body("Correo no encontrado.");
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
	    usuario.setTipo("comun"); // por defecto, o "alumno" si corresponde
	    usuario.setHabilitado("no"); // podría habilitarse luego de verificar el email, etc.
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
            
            // Convert to DTOs to avoid circular references and include all user recipes (including pending)
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
                
            return new ResponseEntity<>(recetasDTO, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

 
    //Cargar nuevas recetas
    @PostMapping("/CargarNuevasRecetas")
    public ResponseEntity<Recetas> cargarNuevasRecetas(@RequestBody Recetas receta) {
        Recetas recetaGuardada = usuariosDAO.cargarReceta(receta);
        return new ResponseEntity<>(recetaGuardada, HttpStatus.CREATED);
    }
    
    
    //Lista de recetas a intentar (recordatorio de recetas a preparar)
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
    
   
    //Registro de Visitantes
    @PostMapping("/registrarVisitante")
    public ResponseEntity<String> registrarVisitante(@RequestParam String mail, @RequestParam Integer idUsuario) {
        boolean registrado = usuariosDAO.registrarVisitante(mail, idUsuario);
        if (registrado) {
            return new ResponseEntity<>("Registro exitoso, se ha enviado un correo para completar el proceso.", HttpStatus.OK);
        }
        return new ResponseEntity<>("Alias ya registrado, por favor elija otro.", HttpStatus.BAD_REQUEST);
    }
  
    //Registro de Alumnos
    @PostMapping("/registrarAlumno")
    public ResponseEntity<String> registrarAlumno(@RequestParam String mail, @RequestParam Integer idUsuario, 
                                                  @RequestParam String medioPago, @RequestParam String dniFrente, 
                                                  @RequestParam String dniFondo, @RequestParam String tramite) {
        boolean registrado = alumnosDAO.registrarAlumno(mail, idUsuario, medioPago, dniFrente, dniFondo, tramite);
        if (registrado) {
            return new ResponseEntity<>("Registro como alumno exitoso, se ha enviado un correo para completar el proceso.", HttpStatus.OK);
        }
        return new ResponseEntity<>("Alias ya registrado o correo ya asociado a otro usuario.", HttpStatus.BAD_REQUEST);
    }
   
    //Cambiar registro a Alumno
    @PutMapping("/cambiarAAlumno/{idUsuario}")
    public ResponseEntity<String> cambiarAAlumno(@PathVariable int idUsuario, @RequestBody Alumnos alumnos) {
        boolean cambioExitoso = usuariosDAO.cambiarAAlumno(idUsuario, alumnos);
        if (cambioExitoso) {
            return new ResponseEntity<>("Usuario convertido a alumno exitosamente.", HttpStatus.OK);
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

	
	//publicar/cargar recetas
	@PostMapping("/publicarRecetas")
	public ResponseEntity<String> publicarReceta(@RequestBody Recetas recetas) {
        try {
            recetasDAO.save(recetas);
            return ResponseEntity.ok("Receta publicada exitosamente");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error al publicar la receta: " + e.getMessage());
        }
	}
	
	//crear curso
	@PostMapping("/crearCurso")
	public ResponseEntity<String> crearCurso(@RequestBody Cursos cursos) {
        try {
            cursosDAO.save(cursos);
            return ResponseEntity.ok("Curso creado exitosamente");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error al crear el curso: " + e.getMessage());
        }
	}
	
	//crear cronograma
	@PostMapping("/crearCronograma")
	public ResponseEntity<String> crearCronograma(@RequestBody CronogramaCursos cronogramaCursos) {
        try {
        	cronogramaCursoDAO.save(cronogramaCursos);   
            return ResponseEntity.ok("Cronograma creado exitosamente");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error al crear el cronograma: " + e.getMessage());
        }
	}
	
	//realizar sugerencias 
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
	
	// Debug endpoint to list all recipe names
	@GetMapping("/debug/listRecipeNames")
	public ResponseEntity<List<String>> listAllRecipeNames() {
	    List<Recetas> allRecetas = recetasRepository.findAll();
	    List<String> names = allRecetas.stream()
	        .map(Recetas::getNombreReceta)
	        .collect(Collectors.toList());
	    System.out.println("All recipe names in database: " + names);
	    return ResponseEntity.ok(names);
	}
	
	// Debug endpoint to test search directly
	@GetMapping("/debug/testSearch")
	public ResponseEntity<Map<String, Object>> testSearch(@RequestParam String searchTerm) {
	    Map<String, Object> result = new HashMap<>();
	    
	    // Test exact match
	    List<Recetas> exactMatch = recetasRepository.findByNombreReceta(searchTerm);
	    result.put("exactMatch", exactMatch.size());
	    
	    // Test partial match
	    List<Recetas> partialMatch = recetasRepository.findByNombreRecetaContainingIgnoreCase(searchTerm);
	    result.put("partialMatch", partialMatch.size());
	    
	    if (!partialMatch.isEmpty()) {
	        result.put("foundRecipes", partialMatch.stream()
	            .map(Recetas::getNombreReceta)
	            .collect(Collectors.toList()));
	    }
	    
	    System.out.println("Search test for '" + searchTerm + "': " + result);
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
	
	//------CONSULTA DE RECETAS----
	
	//Nombre (supports partial search as per task requirements)
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
	    
	    // Convert to DTOs to avoid circular references
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

	    // Convert to DTOs to avoid circular references
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
	    
	    // Convert to DTOs to avoid circular references
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
	    
	    // Convert to DTOs to avoid circular references
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

	//Usuario Particular (supports partial user name search as per task requirements)
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
	    
	    // Convert to DTOs to avoid circular references
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
	public ResponseEntity<String> valorarReceta(@PathVariable Integer idReceta, @RequestBody Calificaciones calificacion) {
	    // Verificar si la receta existe
	    Optional<Recetas> recetaOpt = recetasRepository.findById(idReceta);
	    if (!recetaOpt.isPresent()) {
	        return ResponseEntity.badRequest().body("Receta no encontrada.");
	    }

	    Recetas receta = recetaOpt.get();

	    // Verificar si el usuario está autenticado
	    Usuarios usuario = usuariosDAO.getUsuarioAutenticado();
	    if (usuario == null) {
	        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Debe iniciar sesión para valorar la receta.");
	    }

	    // Crear la valoración
	    Calificaciones nuevaCalificacion = new Calificaciones();
	    nuevaCalificacion.setIdReceta(receta);
	    nuevaCalificacion.setIdusuario(usuario);
	    nuevaCalificacion.setCalificacion(calificacion.getCalificacion());
	    nuevaCalificacion.setComentarios(calificacion.getComentarios());

	    // Guardar la valoración
	    calificacionesRepository.save(nuevaCalificacion);

	    // Enviar confirmación
	    return ResponseEntity.ok("Valoración registrada exitosamente.");
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
	    
	    List<Calificaciones> valoracionesAutorizadas = calificacionesRepository.findByIdRecetaAndAutorizadoTrue(receta);
	    
	    if (valoracionesAutorizadas.isEmpty()) {
	        return ResponseEntity.noContent().build();
	    }

	    return ResponseEntity.ok(valoracionesAutorizadas);
	}
	
    @Value("${directorio.archivos.recetas}")
    private String directorioBase;

    //Cargar receta
    @PostMapping("/cargarReceta")
    public ResponseEntity<String> cargarReceta(@ModelAttribute Recetas receta, @RequestParam("archivos") MultipartFile[] archivos) {
        Usuarios usuario = usuariosDAO.getUsuarioAutenticado();
        
        if (usuario == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Debe iniciar sesión para cargar una receta.");
        }

        Optional<Recetas> recetaExistente = recetasDAO.buscarPorNombreYUsuario(receta.getNombreReceta(), usuario);
        if (recetaExistente.isPresent()) {
            boolean reemplazar = preguntarSiReemplazar(); 
            if (reemplazar) {
                recetasDAO.eliminarReceta(recetaExistente.get());  // Reemplazar la receta
            } else {
                // Permitir editar la receta existente
                receta.setIdReceta(recetaExistente.get().getIdReceta());
            }
        }

        // Configurar la receta, siempre con autorización pendiente
        receta.setUsuario(usuario);
        receta.setAutorizada(false);

        recetasDAO.save(receta);
        guardarArchivosDeReceta(archivos, receta);

        return ResponseEntity.ok("Receta cargada exitosamente y pendiente de autorización.");
    }

    // Método que guarda los archivos asociados a la receta
    public void guardarArchivosDeReceta(MultipartFile[] archivos, Recetas receta) {
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
        return true;  // Si está conectado a una red sin cargo
    }

    // Método para mostrar advertencia al usuario y pedirle si desea usar una red con cargo
    private boolean mostrarAdvertenciaRedConCargo() {
        // Lógica para verificar el estado de la conexión
        boolean estaConectadoALaRed = verificarConexionRed();  // Método hipotético que verifica si hay conexión

        if (!estaConectadoALaRed) {
            // Si no está conectado a una red sin cargo
            // Mostrar una advertencia (en este caso simula la interacción)
            System.out.println("No estás conectado a una red gratuita.");
            System.out.println("¿Deseas usar una red con cargo? (S/N):");

            // Simular que el usuario ingresa una opción
            // En un entorno web, esto podría ser un cuadro de diálogo
            Scanner scanner = new Scanner(System.in);
            String respuesta = scanner.nextLine();

            if (respuesta.equalsIgnoreCase("S")) {
                // Si el usuario decide usar la red con cargo
                System.out.println("Usando red con cargo...");
                return true;
            } else {
                // Si decide no usar la red con cargo
                System.out.println("Guardando la receta para cargarla más tarde...");
                return false;
            }
        } else {
            // Si está conectado a una red gratuita, no es necesario preguntar
            System.out.println("Conexión gratuita detectada, procediendo con la carga.");
            return true;
        }
    }

    // Método hipotético para verificar si hay una conexión gratuita
    private boolean verificarConexionRed() {
        // Aquí debería ir la lógica real para verificar la conexión.
        // Para este ejemplo, vamos a simular que no está conectado a una red gratuita.
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
    
    //---GET ALL RECETAS
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
                    
                    if (receta.getIngredientes() != null && !receta.getIngredientes().isEmpty()) {
                        List<Map<String, Object>> ingredientesDTO = receta.getIngredientes().stream()
                            .map(ingrediente -> {
                                Map<String, Object> ingDTO = new HashMap<>();
                                ingDTO.put("idIngrediente", ingrediente.getIdIngrediente());
                                ingDTO.put("nombre", ingrediente.getNombre());
                                ingDTO.put("cantidad", ingrediente.getCantidad());
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
        // Ingredientes planos
        dto.put("ingredientes", receta.getIngredientes());
        // Instrucciones planas
        dto.put("instrucciones", receta.getInstrucciones());
        // Otros campos simples si existen
        return ResponseEntity.ok(dto);
    }

    // Agregar Receta a la lista de recetas seleccionadas
    @PostMapping("/agregarReceta/{idReceta}")
    public ResponseEntity<String> agregarReceta(@PathVariable Integer idReceta, WebRequest request) {
        Optional<Recetas> recetaOptional = recetasRepository.findById(idReceta);
        if (recetaOptional.isPresent()) {
            Recetas receta = recetaOptional.get();
            List<Recetas> listaRecetasSeleccionadas = getListaRecetasSeleccionadas(request);
            listaRecetasSeleccionadas.add(receta);
            return new ResponseEntity<>("Receta agregada a tu lista", HttpStatus.OK);
        } else {
            return new ResponseEntity<>("Receta no encontrada", HttpStatus.NOT_FOUND);
        }
    }

    // Eliminar una receta de la lista de recetas seleccionadas
    @DeleteMapping("/eliminarReceta/{idReceta}")
    public ResponseEntity<String> eliminarReceta(@PathVariable Integer idReceta, WebRequest request) {
        Optional<Recetas> recetaOptional = recetasRepository.findById(idReceta);
        if (recetaOptional.isPresent()) {
            Recetas receta = recetaOptional.get();
            List<Recetas> listaRecetasSeleccionadas = getListaRecetasSeleccionadas(request);
            if (listaRecetasSeleccionadas.remove(receta)) {
                return new ResponseEntity<>("Receta eliminada de tu lista", HttpStatus.OK);
            } else {
                return new ResponseEntity<>("Receta no encontrada en tu lista", HttpStatus.NOT_FOUND);
            }
        } else {
            return new ResponseEntity<>("Receta no encontrada", HttpStatus.NOT_FOUND);
        }
    }

    // Obtener la lista de recetas seleccionadas
    @GetMapping("/getMiListaRecetas")
    public ResponseEntity<List<Recetas>> obtenerMiListaDeRecetas(WebRequest request) {
        List<Recetas> listaRecetasSeleccionadas = getListaRecetasSeleccionadas(request);
        return new ResponseEntity<>(listaRecetasSeleccionadas, HttpStatus.OK);
    }
    
    //Ajustar porciones de recetas
    private Map<Long, List<Recetas>> recetasPersonalizadasPorUsuario = new HashMap<>();
    @GetMapping("/ajustarPorciones/{id}")
    public ResponseEntity<Recetas> ajustarReceta(@PathVariable Integer idReceta,
            @RequestParam String tipo, // "mitad", "doble" o "porciones"
            @RequestParam(required = false) Integer porciones // si es tipo = "porciones"
        ) {
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
    
    @PostMapping("/ajustarPorIngrediente/{id}")
    public ResponseEntity<?> ajustarPorIngrediente(@PathVariable Integer idReceta, @RequestParam String nombreIngrediente, @RequestParam double nuevaCantidad) {
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
    public ResponseEntity<List<Cursos>> obtenerCursosDisponibles(@RequestParam Integer idUsuario) {
        Usuarios usuario = usuariosDAO.findById(idUsuario);
        List<Cursos> cursos = cursosDAO.getAllCursos(cursosRepository);
        List<Cursos> resultado = new ArrayList<>();

        for (Cursos curso : cursos) {
            Cursos dto = new Cursos();
            dto.setIdCurso(curso.getIdCurso());
            dto.setDescripcion(curso.getDescripcion());

            if ("alumno".equalsIgnoreCase(usuario.getTipo())) {
                dto.setContenidos(curso.getContenidos());
                dto.setRequerimientos(curso.getRequerimientos());
                dto.setDuracion(curso.getDuracion());
                dto.setPrecio(curso.getPrecio());
                dto.setModalidad(curso.getModalidad());
            }

            resultado.add(dto);
        }

        return ResponseEntity.ok(resultado);
    }

    
    //Inscripcion
    @PostMapping("/inscribirseACurso")
    public ResponseEntity<String> inscribirseACurso(
        @RequestParam int idAlumno,
        @RequestParam int idCronograma) {

        try {
            cursosDAO.inscribirAlumnoACurso(idAlumno, idCronograma);
            return ResponseEntity.ok("Inscripción realizada con éxito. Se ha cargado el pago y enviado confirmación.");
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    
    //Obtener el curso del alumno
    @GetMapping("/alumno/{idAlumno}")
    public ResponseEntity<List<Cursos>> getCursosDelAlumno(@PathVariable int idAlumno) {
        List<Cursos> cursos = cursosDAO.obtenerCursosPorAlumno(idAlumno);
        return ResponseEntity.ok(cursos);
    }
    
    //Darse de baja de un curso
    @PostMapping("/baja/{idInscripcion}")
    public ResponseEntity<String> bajaCurso(@PathVariable int idInscripcion, @RequestParam boolean reintegroEnTarjeta) {
        Optional<Inscripcion> inscripcion = inscripcionDAO.findById(idInscripcion);

        if (inscripcion.isEmpty()) {
            return ResponseEntity.badRequest().body("Inscripción no encontrada.");
        }
        Inscripcion inscripciones = inscripcion.get();
        Cursos curso = inscripciones.getCronograma().getIdCurso();
        LocalDateTime fechaInicioCurso = inscripciones.getCronograma().getFechaInicio();
        LocalDateTime fechaBaja = LocalDateTime.now();

        long diasDeDiferencia = ChronoUnit.DAYS.between(fechaBaja.toLocalDate(), fechaInicioCurso.toLocalDate());

        BigDecimal reintegro = BigDecimal.ZERO;

        if (diasDeDiferencia > 10) {
            // Si la baja es más de 10 días antes, reintegro total
            reintegro = curso.getPrecio();
        } else if (diasDeDiferencia <= 10 && diasDeDiferencia > 1) {
            // Si la baja es entre 9 y 1 días antes, reintegro del 70%
            reintegro = curso.getPrecio().multiply(BigDecimal.valueOf(0.7));
        } else if (diasDeDiferencia == 1) {
            // Si la baja es el día antes del curso, reintegro del 50%
            reintegro = curso.getPrecio().multiply(BigDecimal.valueOf(0.5));
        } else {
            // Si la baja es después del inicio del curso, no hay reintegro
            return ResponseEntity.status(400).body("No se puede dar de baja después del inicio del curso.");
        }

        inscripciones.setEstadoInscripcion("Baja");
        inscripcionDAO.save(inscripciones);

        /*
        // Procesar el reintegro: puede ir a la tarjeta de crédito o a la cuenta corriente
        if (reintegroEnTarjeta) {
            procesarReintegroEnTarjeta(inscripcion, reintegro);
        } else {
            procesarReintegroEnCuentaCorriente(inscripcion, reintegro);
        }
        */

        return ResponseEntity.ok("Baja procesada correctamente. Reintegro: " + reintegro);
    }

    /*
    //Método para procesar reintegro a tarjeta
    private void procesarReintegroEnTarjeta(Inscripcion inscripcion, BigDecimal reintegro) {
        // Lógica para procesar el reintegro en tarjeta de crédito
        // Aquí se debería llamar al servicio de pagos para hacer el reintegro
    }

    //Método para procesar reintegro a cuenta corriente
    private void procesarReintegroEnCuentaCorriente(Inscripcion inscripcion, BigDecimal reintegro) {
        // Lógica para reintegrar a la cuenta corriente del alumno
        // Aquí se debería actualizar la cuenta corriente del alumno
    }
    */

    // Username availability check
    @GetMapping("/auth/check-username")
    public ResponseEntity<Map<String, Object>> checkUsernameAvailability(@RequestParam String username) {
        Map<String, Object> result = new HashMap<>();
        boolean exists = usuariosRepository.findAll().stream().anyMatch(u -> username.equalsIgnoreCase(u.getNombre()));
        result.put("available", !exists);
        if (exists) {
            List<String> suggestions = new ArrayList<>();
            String base = username.replaceAll("\\d+$", "");
            for (int i = 0; i < 3; i++) {
                suggestions.add(base + (int)(Math.random() * 1000));
            }
            suggestions.add(base + LocalDate.now().getYear());
            result.put("suggestions", suggestions);
        } else {
            result.put("suggestions", new ArrayList<>());
        }
        return ResponseEntity.ok(result);
    }

    // Email verification endpoint
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

    // Complete profile endpoint
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

    // Endpoint to get user ID by email
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

    //---APROBAR RECETA (Solo para administradores/empresa)
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

    //---CREAR USUARIO EMPRESA (Solo para desarrollo/setup inicial)
    @PostMapping("/crearUsuarioEmpresa")
    public ResponseEntity<String> crearUsuarioEmpresa(@RequestBody Usuarios usuario) {
        try {
            if (usuariosRepository.existsByMail(usuario.getMail())) {
                return ResponseEntity.badRequest().body("Ya existe un usuario con ese email");
            }
            
            usuario.setTipo("empresa"); // Set user type as company representative
            usuario.setHabilitado("Si"); // Enable immediately
            usuariosRepository.save(usuario);
            
            return ResponseEntity.ok("Usuario empresa creado exitosamente");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error al crear usuario empresa: " + e.getMessage());
        }
    }

}

