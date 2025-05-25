package com.example.demo.datos;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
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
	    return recetasRepository.findByNombreReceta(nombre);
	}

	
    @Value("${directorio.archivos.recetas}")
    private String directorioBase;

    public void guardarArchivos(MultipartFile[] archivos, Recetas receta) {
        if (archivos == null || archivos.length == 0 || receta == null) {
            return; // Si no hay archivos o receta, no hacer nada
        }

        // Directorio base donde se almacenarán los archivos
        String carpetaReceta = directorioBase + File.separator + "receta_" + receta.getIdReceta();
        File directorio = new File(carpetaReceta);
        if (!directorio.exists()) {
            directorio.mkdirs();
        }

        for (MultipartFile archivo : archivos) {
            if (!archivo.isEmpty()) {
                try {
                    String nombreArchivo = archivo.getOriginalFilename();
                    Path rutaArchivo = (Path) Paths.get(carpetaReceta, nombreArchivo);
                    File archivoDestino = new File(carpetaReceta + File.separator + nombreArchivo);

                    // Escribir el archivo en el disco
                    try (FileOutputStream fos = new FileOutputStream(archivoDestino)) {
                        fos.write(archivo.getBytes());
                    }

                    // Crear objeto Multimedia para asociar el archivo con la receta
                    Multimedia multimedia = new Multimedia();
                    multimedia.setReceta(receta); 
                    multimedia.setTipoContenido(archivo.getContentType());
                    multimedia.setExtension(getExtension(nombreArchivo));
                    multimedia.setUrlContenido(rutaArchivo.toString());

                    // Guardar la información del archivo en la base de datos
                    multimediaRepository.save(multimedia);
                } catch (IOException e) {
                    e.printStackTrace(); 
                }
            }
        }
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
		// TODO Auto-generated method stub
		return recetasRepository.findById(idReceta);
	}
}
