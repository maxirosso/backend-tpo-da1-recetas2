package com.example.demo.datos;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.example.demo.modelo.Multimedia;
import com.example.demo.modelo.TiposReceta;

@Repository
public class TiposRecetaDAO {
	@Autowired
	TiposRecetaRepository tiposRecetaRepository;
	
	public List<TiposReceta> getAllTiposReceta(TiposRecetaRepository tiposRecetaRepository){
		return tiposRecetaRepository.findAll();
	}
	
    public void save(TiposReceta tiposReceta) {
    	tiposRecetaRepository.save(tiposReceta);
    }
	
	public void delete(TiposReceta tiposReceta) {
		tiposRecetaRepository.delete(tiposReceta);;
	}

}
