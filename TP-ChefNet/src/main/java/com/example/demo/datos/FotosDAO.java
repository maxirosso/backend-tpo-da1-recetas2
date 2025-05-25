package com.example.demo.datos;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.example.demo.modelo.Fotos;

@Repository
public class FotosDAO {
	@Autowired
	FotosRepository fotosRepository;
	
	public List<Fotos> getAllFotos(FotosRepository fotosRepository){
		return fotosRepository.findAll();
	}
	
    public void save(Fotos fotos) {
    	fotosRepository.save(fotos);
    }
	
	public void delete(Fotos fotos) {
		fotosRepository.delete(fotos);;
	}

}
