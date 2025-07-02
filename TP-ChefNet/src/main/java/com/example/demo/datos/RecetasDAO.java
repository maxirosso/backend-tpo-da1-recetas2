package com.example.demo.datos;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import org.springframework.web.multipart.MultipartFile;

import com.example.demo.modelo.Multimedia;
import com.example.demo.modelo.Pasos;
import com.example.demo.modelo.Recetas;
import com.example.demo.modelo.TiposReceta;
import com.example.demo.modelo.Usuarios;

import jakarta.persistence.criteria.Path;

@Repository
public class RecetasDAO {
	@Autowired
	RecetasRepository recetasRepository;
	
	@Autowired
	MultimediaRepository multimediaRepository;
	
	public List<Recetas> getAllRecetas(){
		return recetasRepository.findAll();
	}
	
    public void save(Recetas recetas) {
    	recetasRepository.save(recetas);
    }
	
	public void delete(Recetas recetas) {
		recetasRepository.delete(recetas);;
	}
	
	public List<Recetas> obtenerSugerencias(TiposReceta tipo) {
	    if (tipo == null || tipo.getDescripcion() == null || tipo.getDescripcion().isEmpty()) {
	        return recetasRepository.findAll(); 
	    }
	    return recetasRepository.findByIdTipo(tipo);
	}
	
	public List<Recetas> buscarPorNombre(String nombre) {
	    
	    return recetasRepository.findByNombreRecetaContainingIgnoreCase(nombre);
	}

	
    @Value("${directorio.archivos.recetas}")
    private String directorioBase;

    public void guardarArchivos(MultipartFile[] archivos, Recetas receta) {
        if (archivos == null || archivos.length == 0 || receta == null) {
            return; 
        }

        
        String carpetaReceta = directorioBase + File.separator + "receta_" + receta.getIdReceta();
        File directorio = new File(carpetaReceta);
        if (!directorio.exists()) {
            directorio.mkdirs();
        }

        int fotoPrincipalIndex = 0; 
        boolean fotoPrincipalAsignada = false;

        for (int i = 0; i < archivos.length; i++) {
            MultipartFile archivo = archivos[i];
            if (!archivo.isEmpty()) {
                try {
                    String nombreArchivo = archivo.getOriginalFilename();
                    String nombreArchivoUnico = System.currentTimeMillis() + "_" + nombreArchivo;
                    Path rutaArchivo = (Path) Paths.get(carpetaReceta, nombreArchivoUnico);
                    File archivoDestino = new File(carpetaReceta + File.separator + nombreArchivoUnico);

                    
                    try (FileOutputStream fos = new FileOutputStream(archivoDestino)) {
                        fos.write(archivo.getBytes());
                    }

                    
                    String tipoContenido = determinarTipoContenido(nombreArchivo, i);
                    
                    
                    if (!fotoPrincipalAsignada && i == fotoPrincipalIndex) {
                       
                        receta.setFotoPrincipal(rutaArchivo.toString());
                        save(receta);
                        fotoPrincipalAsignada = true;
                    }

                    
                    Multimedia multimedia = new Multimedia();
                    multimedia.setReceta(receta); 
                    multimedia.setTipoContenido(tipoContenido);
                    multimedia.setExtension(getExtension(nombreArchivo));
                    multimedia.setUrlContenido(rutaArchivo.toString());

                    
                    Integer numeroPaso = extraerNumeroPasoDeNombreArchivo(nombreArchivo);
                    if (numeroPaso != null) {
                       
                        List<Pasos> pasosReceta = recetasRepository.findById(receta.getIdReceta())
                                .map(Recetas::getPasos)
                                .orElse(new ArrayList<>());
                        
                        for (Pasos paso : pasosReceta) {
                            if (paso.getNroPaso() == numeroPaso) {
                                multimedia.setIdPaso(paso);
                                break;
                            }
                        }
                    }

                   
                    multimediaRepository.save(multimedia);
                } catch (IOException e) {
                    e.printStackTrace(); 
                }
            }
        }
    }

    
    private String determinarTipoContenido(String nombreArchivo, int posicion) {
        if (nombreArchivo.toLowerCase().contains("principal")) {
            return "foto_principal";
        } else if (nombreArchivo.toLowerCase().contains("paso") || nombreArchivo.toLowerCase().contains("step")) {
            return "foto_paso";
        } else if (nombreArchivo.toLowerCase().contains("adicional")) {
            return "foto_adicional";
        } else if (posicion == 0) {
            return "foto_principal";
        } else {
            return "foto_receta";
        }
    }

    
    private Integer extraerNumeroPasoDeNombreArchivo(String nombreArchivo) {
        try {
            
            if (nombreArchivo.toLowerCase().contains("paso_")) {
                String[] partes = nombreArchivo.toLowerCase().split("paso_");
                if (partes.length > 1) {
                    String numeroParte = partes[1].split("[^0-9]")[0];
                    return Integer.parseInt(numeroParte);
                }
            }
            
            if (nombreArchivo.toLowerCase().contains("step_")) {
                String[] partes = nombreArchivo.toLowerCase().split("step_");
                if (partes.length > 1) {
                    String numeroParte = partes[1].split("[^0-9]")[0];
                    return Integer.parseInt(numeroParte);
                }
            }
        } catch (NumberFormatException e) {
            
        }
        return null;
    }

    private String getExtension(String nombreArchivo) {
        int index = nombreArchivo.lastIndexOf('.');
        return (index > 0) ? nombreArchivo.substring(index + 1) : "";
    }
    
    public Optional<Recetas> buscarPorNombreYUsuario(String nombre, Usuarios usuario) {
        return recetasRepository.findByNombreRecetaAndUsuario(nombre, usuario);
    }
    
    public void eliminarReceta(Recetas receta) {
        recetasRepository.delete(receta);
    }

	public Optional<Recetas> findByIdOptional(Integer idReceta) {
		
		return recetasRepository.findById(idReceta);
	}
}
