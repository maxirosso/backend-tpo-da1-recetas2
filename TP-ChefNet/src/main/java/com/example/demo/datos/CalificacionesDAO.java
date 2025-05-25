package com.example.demo.datos;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.example.demo.modelo.Calificaciones;

@Repository
public class CalificacionesDAO {
	@Autowired
	CalificacionesRepository calificacionesRepository;
	
	public List<Calificaciones> getAllCalificaciones(CalificacionesRepository calificacionesRepository){
		return calificacionesRepository.findAll();
	}
	
    public void save(Calificaciones calificaciones) {
    	calificacionesRepository.save(calificaciones);
    }
	
	public void delete(Calificaciones calificaciones) {
		calificacionesRepository.delete(calificaciones);;
	}

}
