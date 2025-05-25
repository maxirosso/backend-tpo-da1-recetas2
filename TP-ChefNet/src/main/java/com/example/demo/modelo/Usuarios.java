package com.example.demo.modelo;

import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "usuarios")
public class Usuarios {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "idUsuario")
	private Integer idUsuario;
	@Column(name = "mail")
	private String mail;
	@Column(name = "password")
	private String password;
	@Column(name = "habilitado")
	private String habilitado;
	@Column(name = "nombre")
	private String nombre;
	@Column(name = "direccion")
	private String direccion;
	@Column(name = "avatar")
	private String avatar;
	@OneToOne(mappedBy = "usuario")
	private Alumnos alumno;
    @OneToMany(mappedBy = "usuario")
    private List<Recetas> recetas;

	@Column(name = "codigo_recuperacion")
	private String codigoRecuperacion;
	
	@Column(name = "tipo")
	private String tipo;

	@Column(name = "medio_pago")
	private String medioPago;
	
    @ManyToMany(mappedBy = "usuariosAIntentar")
    private List<Recetas> recetasAIntentar;

	public Usuarios() {}
	
	public Usuarios(Integer idUsuario, String mail, String password, String habilitado, String nombre, String direccion, String avatar){
		this.idUsuario = idUsuario;
		this.mail = mail;
		this.password = password;
		this.habilitado = habilitado;
		this.nombre = nombre;
		this.direccion = direccion;
		this.avatar = avatar;
	}

	public Integer getIdUsuario() {
		return idUsuario;
	}

	public void setIdUsuario(Integer idUsuario) {
		this.idUsuario = idUsuario;
	}

	public String getMail() {
		return mail;
	}

	public void setMail(String mail) {
		this.mail = mail;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getHabilitado() {
		return habilitado;
	}

	public void setHabilitado(String habilitado) {
		this.habilitado = habilitado;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public String getDireccion() {
		return direccion;
	}

	public void setDireccion(String direccion) {
		this.direccion = direccion;
	}

	public String getAvatar() {
		return avatar;
	}

	public void setAvatar(String avatar) {
		this.avatar = avatar;
	}
	
	public String getTipo() {
	    return tipo;
	}

	public void setTipo(String tipo) {
	    this.tipo = tipo;
	}

	public String getMedioPago() {
	    return medioPago;
	}

	public void setMedioPago(String medioPago) {
	    this.medioPago = medioPago;
	}
	
	
	public Alumnos getAlumno() {
		return alumno;
	}

	public void setAlumno(Alumnos alumno) {
		this.alumno = alumno;
	}

	public List<Recetas> getRecetas() {
		return recetas;
	}

	public void setRecetas(List<Recetas> recetas) {
		this.recetas = recetas;
	}
	
	public String getCodigoRecuperacion() {
	    return codigoRecuperacion;
	}

	public void setCodigoRecuperacion(String codigoRecuperacion) {
	    this.codigoRecuperacion = codigoRecuperacion;
	}
	

	public List<Recetas> getRecetasAIntentar() {
		return recetasAIntentar;
	}

	public void setRecetasAIntentar(List<Recetas> recetasAIntentar) {
		this.recetasAIntentar = recetasAIntentar;
	}

	@Override
	public String toString() {
		return "Usuarios [idUsuario=" + idUsuario + ", mail=" + mail + ", password=" + password + ", habilitado="
				+ habilitado + ", nombre=" + nombre + ", direccion=" + direccion + ", avatar=" + avatar + "]";
	}
	

}
