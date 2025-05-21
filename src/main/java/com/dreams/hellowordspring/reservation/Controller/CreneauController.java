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

@Controller
@RequestMapping("/creneaux")
public class CreneauController {

    @Autowired
    private CreneauService creneauService;

    @GetMapping
    public String afficherCreneauxDisponibles(Model model) {
        Utilisateur utilisateur = getUtilisateurConnecte();
        model.addAttribute("creneaux", creneauService.getCreneauxDisponibles());
        model.addAttribute("utilisateur", utilisateur);
        return "creneaux/index";
    }

    @PostMapping("/reserver/{id}")
    public String reserverCreneau(@PathVariable Long id, Model model) {
        Utilisateur utilisateur = getUtilisateurConnecte();
        boolean success = creneauService.reserverCreneau(id, utilisateur);
        model.addAttribute("success", success);
        return "redirect:/creneaux";
    }

    @PostMapping("/annuler/{id}")
    public String annulerCreneau(@PathVariable Long id, Model model) {
        Utilisateur utilisateur = getUtilisateurConnecte();
        boolean success = creneauService.annulerCreneau(id, utilisateur);
        model.addAttribute("success", success);
        return "redirect:/creneaux";
    }

    @GetMapping("/ajouter")
    public String formAjouter(Model model) {
        model.addAttribute("creneau", new Creneau());
        return "creneaux/ajouter";
    }

    @PostMapping("/ajouter")
    public String ajouterCreneau(@ModelAttribute Creneau creneau) {
        creneauService.save(creneau);
        return "redirect:/creneaux";
    }

    private Utilisateur getUtilisateurConnecte() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof Utilisateur) {
            return (Utilisateur) auth.getPrincipal();
        }
        return null;
    }

    @GetMapping("/demander")
    public String formDemandeCreneau(Model model) {
        model.addAttribute("creneau", new Creneau());
        return "creneaux/demande";
    }

    @PostMapping("/demander")
    public String demanderCreneau(@ModelAttribute Creneau creneau) {
        // On fixe automatiquement l'état à EN_ATTENTE
        creneau.setEtat(Creneau.EtatCreneau.EN_ATTENTE);
        creneauService.save(creneau);
        return "redirect:/creneaux?demandeSuccess";
    }

    @GetMapping("/demandes")
    public String afficherDemandes(Model model) {
        model.addAttribute("demandes", creneauService.getCreneauxEnAttente());
        return "creneaux/demandes";
    }

    @PostMapping("/valider/{id}")
    public String validerCreneau(@PathVariable Long id) {
        creneauService.validerCreneau(id);
        return "redirect:/creneaux/demandes";
    }

    @PostMapping("/refuser/{id}")
    public String refuserCreneau(@PathVariable Long id) {
        creneauService.refuserCreneau(id);
        return "redirect:/creneaux/demandes";
    }

    @GetMapping("/mes_demandes")
    public String afficherMesDemandes(Model model) {
        Utilisateur utilisateur = getUtilisateurConnecte();
        model.addAttribute("mesCreneaux", creneauService.getCreneauxParUtilisateur(utilisateur));
        return "creneaux/mes_demandes";
    }

    @PostMapping("/annuler-demande/{id}")
    public String annulerDemande(@PathVariable Long id) {
        Utilisateur utilisateur = getUtilisateurConnecte();
        creneauService.supprimerDemandeEnAttente(id, utilisateur);
        return "redirect:/creneaux/mes_demandes";
    }


}