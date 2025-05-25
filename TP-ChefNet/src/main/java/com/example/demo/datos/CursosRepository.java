package com.example.demo.datos;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.modelo.Cursos;

public interface CursosRepository extends JpaRepository<Cursos, Integer>{
}
