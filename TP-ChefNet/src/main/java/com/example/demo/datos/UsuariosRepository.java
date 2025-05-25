package com.example.demo.datos;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.modelo.Usuarios;

public interface UsuariosRepository extends JpaRepository<Usuarios, Integer>{
    Optional<Usuarios> findByMail(String mail);
    Usuarios findByMailAndPassword(String mail, String password);
    boolean existsByMail(String mail);


}
