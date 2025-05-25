package com.example.demo.datos;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.example.demo.modelo.Alumnos;
import com.example.demo.modelo.AsistenciaCursos;
import com.example.demo.modelo.CronogramaCursos;

@Repository
public class AsistenciaCursosDAO {
    @Autowired
	AsistenciaCursosRepository asistenciaCursosRepository;
	
    @Autowired
    private AlumnosRepository alumnosRepository;

    @Autowired
    private CronogramaCursosRepository cronogramaRepository;
	
	public List<AsistenciaCursos> getAllAsistenciaCursos(AsistenciaCursosRepository asistenciaCursosRepository){
		return asistenciaCursosRepository.findAll();
	}
	
    public void save(AsistenciaCursos asistenciaCursos) {
    	asistenciaCursosRepository.save(asistenciaCursos);
    }
	
	public void delete(AsistenciaCursos asistenciaCursos) {
		asistenciaCursosRepository.delete(asistenciaCursos);;
	}
	
    public void inscribirse(int idAlumno, int idCronograma) {
        Alumnos alumno = alumnosRepository.findById(idAlumno)
                .orElseThrow(() -> new RuntimeException("Alumno no encontrado"));

        CronogramaCursos cronograma = cronogramaRepository.findById(idCronograma)
                .orElseThrow(() -> new RuntimeException("Cronograma no encontrado"));

        AsistenciaCursos asistencia = new AsistenciaCursos();
        asistencia.setIdAlumno(alumno);
        asistencia.setIdCronograma(cronograma);
        //asistencia.setPresente(false); --agregar en la bd lo de presente en la tabla AsistenciaCursos

        asistenciaCursosRepository.save(asistencia);
    }
    
    public void cancelarInscripcion(int idAlumno, int idCronograma) {
        AsistenciaCursos asistencia = asistenciaCursosRepository
            .findByIdAlumno_IdAlumnoAndIdCronograma_IdCronograma(idAlumno, idCronograma)
            .orElseThrow(() -> new RuntimeException("Inscripción no encontrada"));
        
        asistenciaCursosRepository.delete(asistencia);
    }

}
