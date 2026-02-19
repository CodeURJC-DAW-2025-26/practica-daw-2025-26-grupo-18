package es.codeurjc.scam_g18.security;

import org.springframework.security.core.userdetails.UserDetailsService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration {

    @Autowired
    public UserDetailsService userDetailsService;
    @Autowired
    private CustomOAuth2UserService customOAuth2UserService;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http.authenticationProvider(authenticationProvider()); 

        http
            .authorizeHttpRequests(authorize -> authorize
                // NIVEL 0: ANONYMOUS (Público)
                .requestMatchers("/css/**", "/js/**", "/img/**", "/images/**", "/*.css", "/*.js", "/webjars/**").permitAll()
                .requestMatchers("/", "/courses", "/events").permitAll()
                .requestMatchers("/login", "/register", "/error").permitAll()
                
                // NIVEL 1: REGISTERED (Ver detalles y comprar)
                .requestMatchers("/course/{id}", "/events/{id}").hasAnyRole("USER", "SUBSCRIBED", "ADMIN")
                .requestMatchers("/course/{id}/enroll", "/events/{id}/register").hasAnyRole("USER", "SUBSCRIBED", "ADMIN")
                
                // NIVEL 2: SUBSCRIBED (Crear contenido)
                .requestMatchers("/courses/new", "/events/new").hasAnyRole("SUBSCRIBED", "ADMIN")
                
                // Editar y borrar (la comprobación de "dueño" va en el controlador, pero aquí filtramos el rol mínimo)
                .requestMatchers("/course/{id}/edit", "/course/{id}/delete").hasAnyRole("SUBSCRIBED", "ADMIN")
                .requestMatchers("/events/*/edit", "/events/*/delete").hasAnyRole("SUBSCRIBED", "ADMIN")
                
                // NIVEL 3: ADMIN (Todo)
                .requestMatchers("/admin/**").hasRole("ADMIN")

                // Cualquier otra cosa requiere login
                .anyRequest().authenticated()
            )
            
            // 1. SISTEMA DE LOGIN TRADICIONAL (Usuario y Contraseña)
            .formLogin(formLogin -> formLogin
                .loginPage("/login")
                .failureUrl("/loginerror")
                .defaultSuccessUrl("/")
                .permitAll()
            )
            
            // 2. NUEVO: SISTEMA DE LOGIN SOCIAL (OAuth2 con Google)
            .oauth2Login(oauth2 -> oauth2
                .loginPage("/login")
                .defaultSuccessUrl("/")
                .failureUrl("/login?error=userNotFound") // Redirige aquí si el usuario no existe en tu BD
                .userInfoEndpoint(userInfo -> userInfo
                    .userService(customOAuth2UserService) // Usa tu servicio personalizado
                )
            )
            
            // 3. SISTEMA DE LOGOUT
            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/")
                .permitAll()
            );

        return http.build();
    }
}