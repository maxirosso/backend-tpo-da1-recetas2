package com.example.demo.datos;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.example.demo.modelo.Unidades;

@Repository
public class UnidadesDAO {
	@Autowired
	UnidadesRepository unidadesRepository;
	
	public List<Unidades> getAllUnidades(UnidadesRepository unidadesRepository){
		return unidadesRepository.findAll();
	}
	
    public void save(Unidades unidades) {
    	unidadesRepository.save(unidades);
    }
	
	public void delete(Unidades unidades) {
		unidadesRepository.delete(unidades);;
	}

}
