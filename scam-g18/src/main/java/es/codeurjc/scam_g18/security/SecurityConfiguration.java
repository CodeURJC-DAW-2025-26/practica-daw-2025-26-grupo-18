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
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

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

        /**
         * Si el usuario logueado con Google aún no tiene cuenta (ROLE_PENDING),
         * le lleva al formulario de completar datos. Si ya tiene cuenta, va al inicio.
         */
        @Bean
        public AuthenticationSuccessHandler oauth2SuccessHandler() {
                return (request, response, authentication) -> {
                        boolean isPending = authentication.getAuthorities().stream()
                                        .anyMatch(a -> a.getAuthority().equals("ROLE_PENDING"));
                        if (isPending) {
                                response.sendRedirect("/register/google");
                        } else {
                                response.sendRedirect("/");
                        }
                };
        }

        @Bean
        public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

                http.authenticationProvider(authenticationProvider());

                http
                                .authorizeHttpRequests(authorize -> authorize
                                                // NIVEL 0: ANONYMOUS (Público)
                                                .requestMatchers("/css/**", "/js/**", "/img/**", "/images/**", "/*.css",
                                                                "/*.js", "/webjars/**")
                                                .permitAll()
                                                .requestMatchers("/", "/courses", "/events").permitAll()
                                                .requestMatchers("/login", "/register", "/error").permitAll()
                                                .requestMatchers("/course/{id}", "/event/{id}").permitAll()

                                                // Registro completar datos via Google (solo usuarios PENDING)
                                                .requestMatchers("/register/google", "/register/google/cancel")
                                                .hasAnyRole("PENDING")

                                                // NIVEL 1: REGISTERED (Comprar)
                                                .requestMatchers("/course/{id}/enroll", "/events/{id}/register")
                                                .hasAnyRole("USER", "SUBSCRIBED", "ADMIN")

                                                // NIVEL 2: SUBSCRIBED (Crear contenido)
                                                .requestMatchers("/courses/new", "/events/new")
                                                .hasAnyRole("SUBSCRIBED", "ADMIN")

                                                // Editar y borrar (la comprobación de "dueño" va en el controlador,
                                                // pero aquí filtramos el rol mínimo)
                                                .requestMatchers("/course/{id}/edit", "/course/{id}/delete")
                                                .hasAnyRole("SUBSCRIBED", "ADMIN")
                                                .requestMatchers("/events/*/edit", "/events/*/delete")
                                                .hasAnyRole("SUBSCRIBED", "ADMIN")

                                                // NIVEL 3: ADMIN (Todo)
                                                .requestMatchers("/admin/**").hasRole("ADMIN")

                                                // Cualquier otra cosa requiere login
                                                .anyRequest().authenticated())

                                // 1. SISTEMA DE LOGIN TRADICIONAL (Usuario y Contraseña)
                                .formLogin(formLogin -> formLogin
                                                .loginPage("/login")
                                                .failureUrl("/loginerror")
                                                .defaultSuccessUrl("/")
                                                .permitAll())

                                // 2. SISTEMA DE LOGIN SOCIAL (OAuth2 con Google)
                                .oauth2Login(oauth2 -> oauth2
                                                .loginPage("/login")
                                                .successHandler(oauth2SuccessHandler())
                                                .failureUrl("/login?error=oauth2error")
                                                .userInfoEndpoint(userInfo -> userInfo
                                                                .userService(customOAuth2UserService)))

                                // 3. SISTEMA DE LOGOUT
                                .logout(logout -> logout
                                                .logoutUrl("/logout")
                                                .logoutSuccessUrl("/")
                                                .permitAll());

                return http.build();
        }
}