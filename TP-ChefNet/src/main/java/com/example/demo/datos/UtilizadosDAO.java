package com.example.demo.datos;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.example.demo.modelo.Utilizados;

@Repository
public class UtilizadosDAO {
	@Autowired
	UtilizadosRepository utilizadosRepository;
	
	public List<Utilizados> getAllUtilizados(UtilizadosRepository utilizadosRepository){
		return utilizadosRepository.findAll();
	}
	
    public void save(Utilizados utilizados) {
    	utilizadosRepository.save(utilizados);
    }
	
	public void delete(Utilizados utilizados) {
		utilizadosRepository.delete(utilizados);;
	}
}
