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
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration {

        @Autowired
        public UserDetailsService userDetailsService;
        @Autowired
        private CustomOAuth2UserService customOAuth2UserService;
        @Autowired
        private ActiveUserSessionFilter activeUserSessionFilter;

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
                                .addFilterBefore(activeUserSessionFilter, UsernamePasswordAuthenticationFilter.class)
                                .headers(headers -> headers
                                                .frameOptions(frameOptions -> frameOptions.sameOrigin()))
                                .authorizeHttpRequests(authorize -> authorize
                                                // LEVEL 0: ANONYMOUS (Public)
                                                .requestMatchers("/css/**", "/js/**", "/img/**", "/images/**", "/*.css",
                                                                "/*.js", "/webjars/**")
                                                .permitAll()
                                                .requestMatchers("/", "/courses", "/events").permitAll()
                                                .requestMatchers("/login", "/register", "/register/check-availability", "/error")
                                                .permitAll()
                                                .requestMatchers("/course/{id}", "/event/{id}").permitAll()
                                                .requestMatchers("/statistics/course-ages",
                                                                "/statistics/course-genders", "/statistics/course-tags")
                                                .permitAll()

                                                // Complete registration data via Google (PENDING users only)
                                                .requestMatchers("/register/google", "/register/google/cancel")
                                                .hasAnyRole("PENDING")

                                                // LEVEL 1: REGISTERED (Purchase)
                                                .requestMatchers("/course/{id}/enroll", "/events/{id}/register")
                                                .hasAnyRole("USER", "SUBSCRIBED", "ADMIN")

                                                // LEVEL 2: SUBSCRIBED (Create content)
                                                .requestMatchers("/courses/new", "/events/new")
                                                .hasAnyRole("SUBSCRIBED", "ADMIN")

                                                .requestMatchers("/course/new", "/event/new")
                                                .hasAnyRole("SUBSCRIBED", "ADMIN")

                                                // Edit and delete ("owner" validation is done in the controller,
                                                // but here we enforce the minimum role)
                                                .requestMatchers("/course/{id}/edit", "/course/{id}/delete")
                                                .hasAnyRole("SUBSCRIBED", "ADMIN")
                                                .requestMatchers("/event/{id}/edit", "/event/{id}/delete")
                                                .hasAnyRole("SUBSCRIBED", "ADMIN")

                                                // LEVEL 3: ADMIN (Everything)
                                                .requestMatchers("/admin/**").hasRole("ADMIN")

                                                // Any other request requires login
                                                .anyRequest().authenticated())

                                // 1. TRADITIONAL LOGIN SYSTEM (Username and Password)
                                .formLogin(formLogin -> formLogin
                                                .loginPage("/login")
                                                .failureUrl("/loginerror")
                                                .defaultSuccessUrl("/", true)
                                                .permitAll())

                                // 2. SOCIAL LOGIN SYSTEM (OAuth2 with Google)
                                .oauth2Login(oauth2 -> oauth2
                                                .loginPage("/login")
                                                .successHandler(oauth2SuccessHandler())
                                                .failureUrl("/login?error=oauth2error")
                                                .userInfoEndpoint(userInfo -> userInfo
                                                                .userService(customOAuth2UserService)))

                                // 2.1 ACCESS DENIED (Logged user without required role)
                                .exceptionHandling(exceptionHandling -> exceptionHandling
                                                .accessDeniedHandler((request, response, accessDeniedException) -> {
                                                        response.sendRedirect("/error/forbidden");
                                                }))

                                // 3. LOGOUT SYSTEM
                                .logout(logout -> logout
                                                .logoutUrl("/logout")
                                                .logoutSuccessUrl("/")
                                                .permitAll());

                return http.build();
        }
}