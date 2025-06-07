package com.example.demo.modelo;

import java.io.Serializable;
import java.util.Objects;

public class RecetasAIntentarId implements Serializable {
    
    private Integer idReceta;
    private Integer idUsuario;

    public RecetasAIntentarId() {}

    public RecetasAIntentarId(Integer idReceta, Integer idUsuario) {
        this.idReceta = idReceta;
        this.idUsuario = idUsuario;
    }

    public Integer getIdReceta() {
        return idReceta;
    }

    public void setIdReceta(Integer idReceta) {
        this.idReceta = idReceta;
    }

    public Integer getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(Integer idUsuario) {
        this.idUsuario = idUsuario;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RecetasAIntentarId that = (RecetasAIntentarId) o;
        return Objects.equals(idReceta, that.idReceta) && 
               Objects.equals(idUsuario, that.idUsuario);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idReceta, idUsuario);
    }
} 