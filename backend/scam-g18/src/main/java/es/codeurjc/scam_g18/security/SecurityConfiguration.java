package es.codeurjc.scam_g18.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import es.codeurjc.scam_g18.security.jwt.JwtRequestFilter;
import es.codeurjc.scam_g18.security.jwt.UnauthorizedHandlerJwt;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration {

        @Autowired
        public UserDetailsService userDetailsService;
        @Autowired
        private CustomOAuth2UserService customOAuth2UserService;
        @Autowired
        private ActiveUserSessionFilter activeUserSessionFilter;
        @Autowired
        private JwtRequestFilter jwtRequestFilter;
        @Autowired
        private UnauthorizedHandlerJwt unauthorizedHandlerJwt;

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
        public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration)
                        throws Exception {
                return authenticationConfiguration.getAuthenticationManager();
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
        @Order(1) // API filter chain. crsf and oauth2 disabled, JWT-based authentication, only
                  // for /api/v1/**
        public SecurityFilterChain apiFilterChain(HttpSecurity http) throws Exception {

                http.securityMatcher("/api/v1/**");

                http.authenticationProvider(authenticationProvider());

                http
                                .csrf(AbstractHttpConfigurer::disable)
                                .sessionManagement(session -> session
                                                .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                                .exceptionHandling(exceptionHandling -> exceptionHandling
                                                .authenticationEntryPoint(unauthorizedHandlerJwt))
                                .addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class)
                                .authorizeHttpRequests(authorize -> authorize
                                                .requestMatchers("/api/v1/auth/login", "/api/v1/auth/refresh",
                                                                "/api/v1/auth/logout", "/api/v1/auth/register",
                                                                "/api/v1/auth/register/check-availability")
                                                .permitAll()
                                                .requestMatchers("/api/v1/images/**", "/images/**")
                                                .permitAll()
                                                .requestMatchers("/api/v1/global")
                                                .permitAll()
                                                .requestMatchers("/api/v1/courses", "/api/v1/courses/",
                                                                "/api/v1/courses/{id}")
                                                .permitAll()
                                                .requestMatchers("/api/v1/events", "/api/v1/events/",
                                                                "/api/v1/events/{id}", "/api/v1/events/locations")
                                                .permitAll()
                                                .requestMatchers(
                                                                "/api/v1/statistics/course-ages",
                                                                "/api/v1/statistics/course-genders",
                                                                "/api/v1/statistics/course-tags")
                                                .permitAll()
                                                .requestMatchers("/api/v1/admin/**").hasRole("ADMIN")
                                                .anyRequest().authenticated())
                                .formLogin(AbstractHttpConfigurer::disable)
                                .oauth2Login(AbstractHttpConfigurer::disable)
                                .logout(AbstractHttpConfigurer::disable);

                return http.build();
        }

        @Bean
        @Order(2) // Web filter chain. For the rest of the endpoints, with form login and oauth2
                  // login
        public SecurityFilterChain webFilterChain(HttpSecurity http) throws Exception {

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
                                                .requestMatchers("/new", "/new/**")
                                                .permitAll()
                                                .requestMatchers("/v3/api-docs/**", "/swagger-ui/**",
                                                                "/swagger-ui.html")
                                                .permitAll()
                                                .requestMatchers("/", "/courses", "/events").permitAll()
                                                .requestMatchers("/api/courses", "/api/events", "/api/v1/events/locations")
                                                .permitAll()
                                                .requestMatchers("/login", "/register", "/register/check-availability",
                                                                "/error")
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