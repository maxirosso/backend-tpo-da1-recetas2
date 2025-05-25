package com.example.demo.datos;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.example.demo.modelo.Pasos;

@Repository
public class PasosDAO {
	@Autowired
	PasosRepository pasosRepository;
	
	public List<Pasos> getAllPasos(PasosRepository pasosRepository){
		return pasosRepository.findAll();
	}
	
    public void save(Pasos pasos) {
    	pasosRepository.save(pasos);
    }
	
	public void delete(Pasos pasos) {
		pasosRepository.delete(pasos);;
	}

	
}
