package com.dreams.hellowordspring.reservation.Config;

import com.dreams.hellowordspring.reservation.Model.Utilisateur;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

/**
 * Intercepteur qui permet d'injecter l'utilisateur connecté dans le modèle
 * après le traitement de la requête, avant l'affichage de la vue.
 */
public class UtilisateurInterceptor implements HandlerInterceptor {

/**
 * Méthode appelée automatiquement après l'exécution du contrôleur,
 * mais avant le rendu de la vue.
 */
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response,
                           Object handler, ModelAndView modelAndView) {
        // Vérifie que le modèle et la vue ne sont pas null
        if (modelAndView != null) {

            // Récupère l'authentification de l'utilisateur en cours
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();

            // Si l'utilisateur est authentifié et que le principal est une instance de notre classe Utilisateur
            if (auth != null && auth.getPrincipal() instanceof Utilisateur utilisateur) {
                // Ajoute l'utilisateur au modèle pour qu'il soit accessible dans les vues (Thymeleaf par exemple)
                modelAndView.addObject("utilisateur", utilisateur);
            }
        }
    }

}
