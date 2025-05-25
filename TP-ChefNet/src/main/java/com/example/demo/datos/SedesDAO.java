package com.example.demo.datos;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.example.demo.modelo.Sedes;

@Repository
public class SedesDAO {
	@Autowired
	SedesRepository sedesRepository;
	
	public List<Sedes> getAllSedes(SedesRepository sedesRepository){
		return sedesRepository.findAll();
	}
	
    public void save(Sedes sedes) {
    	sedesRepository.save(sedes);
    }
	
	public void delete(Sedes sedes) {
		sedesRepository.delete(sedes);;
	}

}
