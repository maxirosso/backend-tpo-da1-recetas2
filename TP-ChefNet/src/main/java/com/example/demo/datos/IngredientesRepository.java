package com.example.demo.datos;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.modelo.Ingredientes;

public interface IngredientesRepository extends JpaRepository<Ingredientes, Integer>{

}
