package com.example.demo;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.example.demo.datos.AlumnosDAO;
import com.example.demo.datos.AlumnosRepository;
import com.example.demo.datos.AsistenciaCursosDAO;
import com.example.demo.datos.AsistenciaCursosRepository;
import com.example.demo.datos.CalificacionesDAO;
import com.example.demo.datos.CalificacionesRepository;
import com.example.demo.datos.ConversionesDAO;
import com.example.demo.datos.ConversionesRepository;
import com.example.demo.datos.CronogramaCursosDAO;
import com.example.demo.datos.CronogramaCursosRepository;
import com.example.demo.datos.CursosDAO;
import com.example.demo.datos.CursosRepository;
import com.example.demo.datos.FotosDAO;
import com.example.demo.datos.FotosRepository;
import com.example.demo.datos.IngredientesDAO;
import com.example.demo.datos.IngredientesRepository;
import com.example.demo.datos.InscripcionDAO;
import com.example.demo.datos.InscripcionRepository;
import com.example.demo.datos.MultimediaDAO;
import com.example.demo.datos.MultimediaRepository;
import com.example.demo.datos.PasosDAO;
import com.example.demo.datos.PasosRepository;
import com.example.demo.datos.RecetasDAO;
import com.example.demo.datos.RecetasRepository;
import com.example.demo.datos.SedesDAO;
import com.example.demo.datos.SedesRepository;
import com.example.demo.datos.TiposRecetaDAO;
import com.example.demo.datos.TiposRecetaRepository;
import com.example.demo.datos.UnidadesDAO;
import com.example.demo.datos.UnidadesRepository;
import com.example.demo.datos.UsuariosDAO;
import com.example.demo.datos.UsuariosRepository;
import com.example.demo.datos.UtilizadosDAO;
import com.example.demo.datos.UtilizadosRepository;
import com.example.demo.modelo.Alumnos;

@SpringBootApplication
public class TpChefNetApplication implements CommandLineRunner{
	
	@Autowired
	private AlumnosRepository alumnosRepository;
	@Autowired
	AlumnosDAO alumnosDAO;

	@Autowired
	AsistenciaCursosRepository asistenciaCursosRepository;
	@Autowired
	AsistenciaCursosDAO asistenciaCursosDAO;
	
	@Autowired
	CalificacionesRepository calificacionesRepository;
	@Autowired
	CalificacionesDAO calificacionesDAO;
	
	@Autowired
	ConversionesRepository conversionesRepository;
	@Autowired
	ConversionesDAO conversionesDAO;
	
	@Autowired
	CronogramaCursosRepository cronogramaCursosRepository;
	@Autowired
	CronogramaCursosDAO cronogramaCursoDAO;
	
	@Autowired
	CursosRepository cursosRepository;
	@Autowired
	CursosDAO cursosDAO;
	
	@Autowired
	FotosRepository fotosRepository;
	@Autowired
	FotosDAO fotosDAO;
	
	@Autowired
	IngredientesRepository ingredientesRepository;
	@Autowired
	IngredientesDAO ingredientesDAO;
	
	@Autowired
	MultimediaRepository multimediaRepository;
	@Autowired
	MultimediaDAO multimediaDAO;
	
	@Autowired
	PasosRepository pasosRepository;
	@Autowired
	PasosDAO pasosDAO;
	
	@Autowired
	RecetasRepository recetasRepository;
	@Autowired
	RecetasDAO recetasDAO;
	
	@Autowired
	SedesRepository sedesRepository;
	@Autowired
	SedesDAO sedesDAO;
	
	@Autowired
	TiposRecetaRepository tiposRecetaRepository;
	@Autowired
	TiposRecetaDAO tiposRecetaDAO;
	
	@Autowired
	UnidadesRepository unidadesRepository;
	@Autowired
	UnidadesDAO unidadesDAO;
	
	@Autowired
	UsuariosRepository usuariosRepository;
	@Autowired
	UsuariosDAO usuariosDAO;
	
	@Autowired
	UtilizadosRepository utilizadosRepository;
	@Autowired
	UtilizadosDAO utilizadosDAO;
	
	@Autowired
	InscripcionRepository inscripcionRepository;
	@Autowired
	InscripcionDAO inscripcionDAO;
	
	public static void main(String[] args) {
		SpringApplication.run(TpChefNetApplication.class, args);

	}

	@Override
	public void run(String... args) throws Exception {
		// TODO Auto-generated method stub
		System.out.println("Empezamos a ejecutar");
		List<Alumnos> alumnos = alumnosRepository.findAll();
		for(Alumnos alumno : alumnos)
			System.out.println(alumno.toString());
		
	}
	

}
