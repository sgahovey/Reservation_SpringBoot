package com.dreams.hellowordspring.reservation.Config;

import com.dreams.hellowordspring.reservation.Service.UtilisateurDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public UserDetailsService userDetailsService() {
        return new UtilisateurDetailsService();
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider(UserDetailsService userDetailsService,
                                                            BCryptPasswordEncoder passwordEncoder) {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder);
        return provider;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf().disable() // désactiver CSRF pour les tests (à activer en prod)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/login",
                                "/register",
                                "/css/**",
                                "/images/**",
                                "/js/**"
                        ).permitAll() // ✅ accessible sans connexion

                        .requestMatchers(
                                "/creneaux/**",
                                "/utilisateurs/reglages"
                        ).hasAnyRole("USER", "ADMIN") // accessibles à tous les connectés

                        .requestMatchers(
                                "/admin/**",
                                "/Creneaux_Admin/**",
                                "/Statistiques/**",
                                "/utilisateurs/**" // sauf /utilisateurs/reglages
                        ).hasRole("ADMIN")

                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/login")
                        .defaultSuccessUrl("/creneaux", true)
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/login?logout")
                        .permitAll()
                );

        // 🔒 Redirection en cas de 403
        http.exceptionHandling(exception -> exception
                .accessDeniedPage("/access-denied")
        );

        return http.build();
    }

}
