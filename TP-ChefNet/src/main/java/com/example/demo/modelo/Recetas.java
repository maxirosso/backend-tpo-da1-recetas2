package com.example.demo.modelo;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

@Entity
@Table(name = "recetas")
public class Recetas {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "idReceta")
	private Integer idReceta;
	
    @ManyToOne
    @JoinColumn(name = "idUsuario")
	private Usuarios usuario;
	
	@Column(name = "nombreReceta")
	private String nombreReceta;
	
	@Column(name = "descripcionReceta")
	private String descripcionReceta;
	
	@Column(name = "fotoPrincipal")
	private String fotoPrincipal; 
	
	@Column(name = "porciones")
	private int porciones;
	
	@Column(name = "cantidadPersonas")
	private int cantidadPersonas;
	
    @ManyToOne
    @JoinColumn(name = "idTipo")
	private TiposReceta idTipo;
	
	@OneToMany(mappedBy = "idReceta", cascade = CascadeType.ALL)
	private List<Fotos> fotos;
	
	@ManyToMany
    @JoinTable(name = "recetas_a_intentar", joinColumns = @JoinColumn(name = "idReceta"), inverseJoinColumns = @JoinColumn(name = "idUsuario"))
    private List<Usuarios> usuariosAIntentar;
	
	@Column(name = "autorizada")
	private boolean autorizada = false;
	
	private String instrucciones;
	
	@OneToMany(mappedBy = "receta", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<Ingredientes> ingredientes = new ArrayList<>();
	
    @Column(name = "fecha")
    private LocalDate fecha;
    
    @OneToMany(mappedBy = "receta")
    private List<Utilizados> utilizados;
	
	@Transient
	private MultipartFile[] archivos;
	
	public Recetas() {}
	
	public Recetas(Integer idReceta, Usuarios usuario, String nombreReceta, String descripcionReceta, String fotoPrincipal, int porciones, int cantidadPersonas, TiposReceta idTipo) {
		this.idReceta = idReceta;
		this.usuario = usuario;
		this.nombreReceta = nombreReceta;
		this.descripcionReceta = descripcionReceta;
		this.fotoPrincipal = fotoPrincipal;
		this.porciones = porciones;
		this.cantidadPersonas = cantidadPersonas;
		this.idTipo = idTipo;
	}
	
	
	public Integer getIdReceta() {
		return idReceta;
	}
	
	public void setIdReceta(Integer idReceta) {
		this.idReceta = idReceta;
	}
	
	public Usuarios getUsuario() {
		return usuario;
	}
	
	public void setUsuario(Usuarios usuario) {
		this.usuario = usuario;
	}
	
	public String getNombreReceta() {
		return nombreReceta;
	}
	
	public void setNombreReceta(String nombreReceta) {
		this.nombreReceta = nombreReceta;
	}
	
	public String getDescripcionReceta() {
		return descripcionReceta;
	}
	
	public void setDescripcionReceta(String descripcionReceta) {
		this.descripcionReceta = descripcionReceta;
	}
	
	public String getFotoPrincipal() {
		return fotoPrincipal;
	}
	
	public void setFotoPrincipal(String fotoPrincipal) {
		this.fotoPrincipal = fotoPrincipal;
	}
	
	public int getPorciones() {
		return porciones;
	}
	
	public void setPorciones(int porciones) {
		this.porciones = porciones;
	}
	
	public int getCantidadPersonas() {
		return cantidadPersonas;
	}
	
	public void setCantidadPersonas(int cantidadPersonas) {
		this.cantidadPersonas = cantidadPersonas;
	}
	
	public TiposReceta getIdTipo() {
		return idTipo;
	}
	
	public void setIdTipo(TiposReceta idTipo) {
		this.idTipo = idTipo;
	}

	public List<Fotos> getFotos() {
		return fotos;
	}

	public void setFotos(List<Fotos> fotos) {
		this.fotos = fotos;
	}

	public List<Usuarios> getUsuariosAIntentar() {
		return usuariosAIntentar;
	}

	public void setUsuariosAIntentar(List<Usuarios> usuariosAIntentar) {
		this.usuariosAIntentar = usuariosAIntentar;
	}
	
	public boolean isAutorizada() {
	    return autorizada;
	}

	public void setAutorizada(boolean autorizada) {
	    this.autorizada = autorizada;
	}

	public MultipartFile[] getArchivos() {
	    return archivos;
	}

	public void setArchivos(MultipartFile[] archivos) {
	    this.archivos = archivos;
	}

	public List<Ingredientes> getIngredientes() {
	    return ingredientes;
	}

	public void setIngredientes(List<Ingredientes> ingredientes) {
	    this.ingredientes = ingredientes;
	}
	
	public String getInstrucciones() {
	    return instrucciones;
	}

	public void setInstrucciones(String instrucciones) {
	    this.instrucciones = instrucciones;
	}
	
	public Recetas clonar() {
	    Recetas clon = new Recetas();

	    clon.setUsuario(this.usuario);
	    clon.setNombreReceta(this.nombreReceta);
	    clon.setDescripcionReceta(this.descripcionReceta);
	    clon.setFotoPrincipal(this.fotoPrincipal);
	    clon.setPorciones(this.porciones);
	    clon.setCantidadPersonas(this.cantidadPersonas);
	    clon.setIdTipo(this.idTipo);
	    clon.setInstrucciones(this.instrucciones);
	    clon.setAutorizada(this.autorizada);
	    
	    // Clonar ingredientes
	    List<Ingredientes> ingredientesClonados = new ArrayList<>();
	    for (Ingredientes ing : this.ingredientes) {
	        Ingredientes ingClon = ing.clonar();
	        ingClon.setReceta(clon); // importante establecer la relación inversa
	        ingredientesClonados.add(ingClon);
	    }
	    clon.setIngredientes(ingredientesClonados);

	    return clon;
	}
	
	public LocalDate getFecha() {
	    return fecha;
	}

	public void setFecha(LocalDate fecha) {
	    this.fecha = fecha;
	}


	@Override
	public String toString() {
		return "Recetas [idReceta=" + idReceta + ", idUsuario=" + usuario + ", nombreReceta=" + nombreReceta
				+ ", descripcionReceta=" + descripcionReceta + ", fotoPrincipal=" + fotoPrincipal + ", porciones="
				+ porciones + ", cantidadPersonas=" + cantidadPersonas + ", idTipo=" + idTipo + "]";
	}
	
	

}
