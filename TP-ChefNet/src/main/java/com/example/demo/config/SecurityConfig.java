package com.example.demo.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .cors(cors -> cors.configure(http))
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(authz -> authz
                // Endpoints que no requieren autenticación publicos
                .requestMatchers(new AntPathRequestMatcher("/")).permitAll()
                .requestMatchers(new AntPathRequestMatcher("/login")).permitAll()
                .requestMatchers(new AntPathRequestMatcher("/registrarUsuario")).permitAll()
                .requestMatchers(new AntPathRequestMatcher("/registrarAlumno")).permitAll()
                .requestMatchers(new AntPathRequestMatcher("/registrarVisitante")).permitAll()
                .requestMatchers(new AntPathRequestMatcher("/registrarVisitanteEtapa1")).permitAll()
                .requestMatchers(new AntPathRequestMatcher("/verificarCodigoVisitante")).permitAll()
                .requestMatchers(new AntPathRequestMatcher("/reenviarCodigoVisitante")).permitAll()
                .requestMatchers(new AntPathRequestMatcher("/registrarUsuarioEtapa1")).permitAll()
                .requestMatchers(new AntPathRequestMatcher("/verificarCodigoUsuario")).permitAll()
                .requestMatchers(new AntPathRequestMatcher("/reenviarCodigoUsuario")).permitAll()
                .requestMatchers(new AntPathRequestMatcher("/completarRegistroUsuario")).permitAll()
                .requestMatchers(new AntPathRequestMatcher("/sugerenciasAlias")).permitAll()
                .requestMatchers(new AntPathRequestMatcher("/cambiarAAlumno/**")).permitAll()
                .requestMatchers(new AntPathRequestMatcher("/test-email")).permitAll()
                .requestMatchers(new AntPathRequestMatcher("/auth/check-username")).permitAll()
                .requestMatchers(new AntPathRequestMatcher("/auth/**")).permitAll()
                .requestMatchers(new AntPathRequestMatcher("/usuarios/perfil")).permitAll()
                .requestMatchers(new AntPathRequestMatcher("/recuperarClave")).permitAll()
                .requestMatchers(new AntPathRequestMatcher("/recuperarContrasena")).permitAll()
                .requestMatchers(new AntPathRequestMatcher("/verificarCodigoRecuperacion")).permitAll()
                .requestMatchers(new AntPathRequestMatcher("/cambiarContrasenaConCodigo")).permitAll()
                .requestMatchers(new AntPathRequestMatcher("/ultimasRecetas")).permitAll()
                .requestMatchers(new AntPathRequestMatcher("/recetas/buscar/**")).permitAll()
                .requestMatchers(new AntPathRequestMatcher("/recetas/publicas")).permitAll()
                .requestMatchers(new AntPathRequestMatcher("/cursos/publicos")).permitAll()
                // Recipe-related endpoints that visitors should access
                .requestMatchers(new AntPathRequestMatcher("/getValoracionReceta/**")).permitAll()
                .requestMatchers(new AntPathRequestMatcher("/getRecetaById/**")).permitAll()
                .requestMatchers(new AntPathRequestMatcher("/getNombrereceta")).permitAll()
                .requestMatchers(new AntPathRequestMatcher("/getTiporeceta")).permitAll()
                .requestMatchers(new AntPathRequestMatcher("/getIngredienteReceta")).permitAll()
                .requestMatchers(new AntPathRequestMatcher("/getSinIngredienteReceta")).permitAll()
                .requestMatchers(new AntPathRequestMatcher("/getUsuarioReceta")).permitAll()
                .requestMatchers(new AntPathRequestMatcher("/getAllRecetas")).permitAll()
                .requestMatchers(new AntPathRequestMatcher("/sugerenciasRecetas")).permitAll()
                .requestMatchers(new AntPathRequestMatcher("/buscarRecetas")).permitAll()
                .requestMatchers(new AntPathRequestMatcher("/getTiposReceta")).permitAll()
                .requestMatchers(new AntPathRequestMatcher("/ingredientes")).permitAll()
                .requestMatchers(new AntPathRequestMatcher("/buscarRecetasSinIngredientes")).permitAll()
                // Temporarily allow enrollment endpoint for debugging
                .requestMatchers(new AntPathRequestMatcher("/inscribirseACurso")).permitAll()
                .requestMatchers(new AntPathRequestMatcher("/getCursosDisponibles")).permitAll()
                // Course management endpoints
                .requestMatchers(new AntPathRequestMatcher("/baja/**")).permitAll()
                .requestMatchers(new AntPathRequestMatcher("/alumno/**")).permitAll()
                // All other endpoints require authentication
                .anyRequest().authenticated()
            )
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
            
        return http.build();
    }
} 