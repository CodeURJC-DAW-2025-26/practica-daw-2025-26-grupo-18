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

        http.authenticationProvider(authenticationProvider()); // Esto lo haremos luego, no te preocupes si da rojo
                                                               // ahora

        http
                .authorizeHttpRequests(authorize -> authorize
                        // NIVEL 0: ANONYMOUS (Público)
                        .requestMatchers("/css/**", "/js/**", "/images/**").permitAll()
                        .requestMatchers("/", "/courses", "/events").permitAll()
                        .requestMatchers("/login", "/register", "/error").permitAll()
                        // NIVEL 1: REGISTERED (Ver detalles y comprar)
                        .requestMatchers("/courses/{id}", "/events/{id}").hasAnyRole("USER", "SUBSCRIBED", "ADMIN")
                        .requestMatchers("/courses/{id}/enroll", "/events/{id}/register")
                        .hasAnyRole("USER", "SUBSCRIBED", "ADMIN")
                        // NIVEL 2: SUBSCRIBED (Crear contenido)
                        .requestMatchers("/courses/new", "/events/new").hasAnyRole("SUBSCRIBED", "ADMIN")
                        // Editar y borrar (la comprobación de "dueño" va en el controlador, pero aquí
                        // filtramos el rol mínimo)
                        .requestMatchers("/courses/*/edit", "/courses/*/delete").hasAnyRole("SUBSCRIBED", "ADMIN")
                        .requestMatchers("/events/*/edit", "/events/*/delete").hasAnyRole("SUBSCRIBED", "ADMIN")
                        // NIVEL 3: ADMIN (Todo)
                        .requestMatchers("/admin/**").hasRole("ADMIN")

                        // Cualquier otra cosa requiere login
                        .anyRequest().authenticated())
                .formLogin(formLogin -> formLogin
                        .loginPage("/login")
                        .failureUrl("/loginerror")
                        .defaultSuccessUrl("/")
                        .permitAll())
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/")
                        .permitAll());
        return http.build();
    }
}