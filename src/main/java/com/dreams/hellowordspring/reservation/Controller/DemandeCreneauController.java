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

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@Controller
@RequestMapping("/creneaux")
public class DemandeCreneauController {
    @Autowired
    private CreneauService creneauService;

    /**
     * Méthode utilitaire : récupère l'utilisateur actuellement connecté
     */
    private Utilisateur getUtilisateurConnecte() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof Utilisateur) {
            return (Utilisateur) auth.getPrincipal();
        }
        return null;
    }

    /**
     * Affiche le formulaire de demande de créneau
     */
    @GetMapping("/demander")
    public String formDemandeCreneau(Model model) {
        model.addAttribute("creneau", new Creneau());
        return "creneaux/demande";
    }

    /**
     * Enregistre une demande de créneau en attente
     */
    @PostMapping("/demander")
    public String demanderCreneau(@ModelAttribute Creneau creneau) {
        Utilisateur utilisateur = getUtilisateurConnecte();

        creneau.setEtat(Creneau.EtatCreneau.EN_ATTENTE);
        creneau.setReservePar(utilisateur); // 🔥 attache l'utilisateur connecté à la demande

        creneauService.save(creneau);
        return "redirect:/creneaux?demandeSuccess";
    }

    /**
     * Affiche les demandes de l'utilisateur connecté (triables)
     */
    @GetMapping("/mes_demandes")
    public String afficherMesDemandes(
            @RequestParam(required = false) String sort,
            @RequestParam(required = false, defaultValue = "asc") String direction,
            Model model
    ) {
        Utilisateur utilisateur = getUtilisateurConnecte();
        List<Creneau> mesCreneaux = creneauService.getCreneauxParUtilisateur(utilisateur);

        if ("date".equals(sort)) {
            mesCreneaux.sort(Comparator.comparing(Creneau::getDate));
        } else if ("etat".equals(sort)) {
            mesCreneaux.sort(Comparator.comparing(Creneau::getEtat));
        }

        if ("desc".equals(direction)) {
            Collections.reverse(mesCreneaux);
        }

        model.addAttribute("mesCreneaux", mesCreneaux);
        model.addAttribute("sort", sort);
        model.addAttribute("direction", direction);
        return "creneaux/mes_demandes";
    }

    /**
     * Permet à l'utilisateur d'annuler une demande en attente
     */
    @PostMapping("/annuler-demande/{id}")
    public String annulerDemande(@PathVariable Long id) {
        Utilisateur utilisateur = getUtilisateurConnecte();
        creneauService.supprimerDemandeEnAttente(id, utilisateur);
        return "redirect:/creneaux/mes_demandes";
    }

}
