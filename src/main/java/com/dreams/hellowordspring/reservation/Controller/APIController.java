package com.dreams.hellowordspring.reservation.Controller;

import com.dreams.hellowordspring.reservation.Model.Creneau;
import com.dreams.hellowordspring.reservation.Model.Utilisateur;
import com.dreams.hellowordspring.reservation.Service.CreneauService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// Contrôleur REST pour exposer des données au format JSON
@RestController
@RequestMapping("/api")
public class APIController {

    @Autowired
    private CreneauService creneauService;

    /**
     * Méthode utilitaire pour récupérer l'utilisateur actuellement connecté
     */
    private Utilisateur getUtilisateurConnecte() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof Utilisateur) {
            return (Utilisateur) auth.getPrincipal();
        }
        return null;
    }

    /**
     * API pour obtenir la liste des créneaux validés (utilisée par un calendrier par ex.)
     *
     */
    @GetMapping("/creneaux-valides")
    @ResponseBody
    public List<Map<String, Object>> getCreneauxValides() {
        List<Creneau> liste = creneauService.getCreneauxValides();
        List<Map<String, Object>> resultats = new ArrayList<>();

        for (Creneau c : liste) {
            Map<String, Object> evt = new HashMap<>();
            evt.put("id", c.getId());
            evt.put("title", c.getLieu());
            evt.put("start", c.getDate().toString() + "T" + c.getHeureDebut().toString());
            evt.put("end", c.getDate().toString() + "T" + c.getHeureFin().toString());

            // Couleur en fonction de l’état
            switch (c.getEtat()) {
                case VALIDE -> evt.put("color", "#28a745");       // Vert
                case EN_ATTENTE -> evt.put("color", "#ffc107");   // Jaune
                case REFUSE -> evt.put("color", "#dc3545");       // Rouge
            }

            evt.put("extendedProps", Map.of(
                    "lieu", c.getLieu(),
                    "date", c.getDate().toString(),
                    "heureDebut", c.getHeureDebut().toString(),
                    "heureFin", c.getHeureFin().toString()
            ));

            resultats.add(evt);
        }
        return resultats; // Résultat JSON envoyé au client (ex. FullCalendar)
    }

    /**
     * API pour récupérer uniquement les créneaux en attente de l'utilisateur connecté
     */
    @GetMapping("/mes-creneaux-en-attente")
    @ResponseBody
    public List<Map<String, Object>> getMesCreneauxEnAttente() {
        Utilisateur utilisateur = getUtilisateurConnecte();
        List<Creneau> liste = creneauService.getCreneauxParUtilisateur(utilisateur)
                .stream()
                .filter(c -> c.getEtat() == Creneau.EtatCreneau.EN_ATTENTE)
                .toList();

        List<Map<String, Object>> resultats = new ArrayList<>();

        for (Creneau c : liste) {
            Map<String, Object> evt = new HashMap<>();
            evt.put("id", c.getId());
            evt.put("title", c.getLieu() + " (En attente)");
            evt.put("start", c.getDate() + "T" + c.getHeureDebut());
            evt.put("end", c.getDate() + "T" + c.getHeureFin());
            evt.put("color", "#ffc107"); // jaune
            resultats.add(evt);
        }
        return resultats;
    }
}
