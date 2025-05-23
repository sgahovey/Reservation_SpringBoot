package com.dreams.hellowordspring.reservation.Config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    /**
     * Méthode permettant d'enregistrer les intercepteurs personnalisés dans le cycle de vie des requêtes HTTP.
     * Ici, on enregistre l'intercepteur UtilisateurInterceptor.
     *
     * @param registry registre des intercepteurs où l'on peut ajouter le nôtre
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // Ajout de l'intercepteur qui injecte l'utilisateur connecté dans le modèle
        registry.addInterceptor(new UtilisateurInterceptor());
    }
}
