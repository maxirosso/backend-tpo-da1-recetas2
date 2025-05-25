package com.example.demo.datos;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.example.demo.modelo.Conversiones;

@Repository
public class ConversionesDAO {
	@Autowired
	ConversionesRepository conversionesRepository;
	
	public List<Conversiones> getAllConversiones(ConversionesRepository conversionesRepository){
		return conversionesRepository.findAll();
	}
	
    public void save(Conversiones conversiones) {
    	conversionesRepository.save(conversiones);
    }
	
	public void delete(Conversiones conversiones) {
		conversionesRepository.delete(conversiones);;
	}

}
