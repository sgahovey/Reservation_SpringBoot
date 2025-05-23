package com.dreams.hellowordspring.reservation.Controller;

import com.dreams.hellowordspring.reservation.Model.Creneau;
import com.dreams.hellowordspring.reservation.Model.Utilisateur;
import com.dreams.hellowordspring.reservation.Service.CreneauService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
import java.time.LocalDate;
import java.util.*;

@Controller
@RequestMapping("/creneaux")
public class CreneauController {

    @Autowired
    private CreneauService creneauService;

    /**
     * Affiche la page des créneaux disponibles pour réservation
     */
    @GetMapping
    public String afficherCreneauxDisponibles(Model model) {
        Utilisateur utilisateur = getUtilisateurConnecte();
        model.addAttribute("creneaux", creneauService.getCreneauxDisponibles());
        model.addAttribute("utilisateur", utilisateur);
        return "creneaux/index";
    }

    /**
     * Permet à un utilisateur de réserver un créneau donné
     */
    @PostMapping("/reserver/{id}")
    public String reserverCreneau(@PathVariable Long id, Model model) {
        Utilisateur utilisateur = getUtilisateurConnecte();
        boolean success = creneauService.reserverCreneau(id, utilisateur);
        model.addAttribute("success", success);
        return "redirect:/creneaux";
    }

    /**
     * Permet à un utilisateur d'annuler une réservation
     */
    @PostMapping("/annuler/{id}")
    public String annulerCreneau(@PathVariable Long id, Model model) {
        Utilisateur utilisateur = getUtilisateurConnecte();
        boolean success = creneauService.annulerCreneau(id, utilisateur);
        model.addAttribute("success", success);
        return "redirect:/creneaux";
    }

    /**
     * Méthode privée pour récupérer l’utilisateur actuellement connecté
     */
    private Utilisateur getUtilisateurConnecte() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof Utilisateur) {
            return (Utilisateur) auth.getPrincipal();
        }
        return null;
    }

    /**
     * Affiche les détails d’un créneau spécifique avec calcul de la durée
     */
    @GetMapping("/details/{id}")
    public String afficherDetailsCreneau(@PathVariable Long id, Model model) {
        Optional<Creneau> opt = creneauService.findById(id);
        if (opt.isPresent()) {
            Creneau creneau = opt.get();

            // Calcul de la durée entre l’heure de début et de fin
            Duration duree = Duration.between(creneau.getHeureDebut(), creneau.getHeureFin());
            long heures = duree.toHours();
            long minutes = duree.toMinutes() % 60;
            String dureeFormatee = heures + "h" + (minutes > 0 ? minutes : "");

            // Utilisateur connecté (si tu utilises Spring Security)
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            Utilisateur utilisateur = (auth.getPrincipal() instanceof Utilisateur) ? (Utilisateur) auth.getPrincipal() : null;

            model.addAttribute("creneau", creneau);
            model.addAttribute("duree", dureeFormatee);
            model.addAttribute("utilisateur", utilisateur); // ✅ Ajout pour vérifier admin

            return "creneaux/details";
        } else {
            return "redirect:/creneaux";
        }
    }

    /**
     * Affiche un formulaire de demande de créneau pour une date donnée
     */
    @GetMapping("/formulaire-demande")
    public String afficherFormulaireDemande(@RequestParam String date, Model model) {
        Creneau creneau = new Creneau();
        creneau.setDate(LocalDate.parse(date));
        model.addAttribute("creneau", creneau);
        return "creneaux/demander :: modalForm";
    }
}