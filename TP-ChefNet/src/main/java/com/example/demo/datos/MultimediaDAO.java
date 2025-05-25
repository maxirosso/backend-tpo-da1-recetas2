package com.example.demo.datos;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.example.demo.modelo.Multimedia;

@Repository
public class MultimediaDAO {
	@Autowired
	MultimediaRepository multimediaRepository;
	
	public List<Multimedia> getAllMultimedia(MultimediaRepository multimediaRepository){
		return multimediaRepository.findAll();
	}
	
    public void save(Multimedia multimedia) {
    	multimediaRepository.save(multimedia);
    }
	
	public void delete(Multimedia multimedia) {
		multimediaRepository.delete(multimedia);;
	}

}
