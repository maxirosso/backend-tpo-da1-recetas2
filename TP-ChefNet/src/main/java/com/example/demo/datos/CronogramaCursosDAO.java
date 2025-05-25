package com.example.demo.datos;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.example.demo.modelo.CronogramaCursos;

@Repository
public class CronogramaCursosDAO {
	@Autowired
	CronogramaCursosRepository cronogramaCursosRepository;
	
	public List<CronogramaCursos> getAllCronogramaCursos(){
		return cronogramaCursosRepository.findAll();
	}
	
    public void save(CronogramaCursos cronogramaCursos) {
    	cronogramaCursosRepository.save(cronogramaCursos);
    }
	
	public void delete(CronogramaCursos cronogramaCursos) {
		cronogramaCursosRepository.delete(cronogramaCursos);;
	}

}
