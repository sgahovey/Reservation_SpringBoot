package com.dreams.hellowordspring.reservation.Controller;

import com.dreams.hellowordspring.reservation.Model.Creneau;
import com.dreams.hellowordspring.reservation.Model.Utilisateur;
import com.dreams.hellowordspring.reservation.Service.CreneauService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/creneaux")
public class CreneauController {

    @Autowired
    private CreneauService creneauService;

    // Afficher les créneaux disponibles
    @GetMapping
    public String afficherCreneauxDisponibles(Model model) {
        model.addAttribute("creneaux", creneauService.getCreneauxDisponibles());
        return "creneaux/index"; // page Thymeleaf : /templates/creneaux/index.html
    }

    // Réserver un créneau
    @PostMapping("/reserver/{id}")
    public String reserverCreneau(@PathVariable Long id, @SessionAttribute("utilisateur") Utilisateur utilisateur, Model model) {
        boolean success = creneauService.reserverCreneau(id, utilisateur);
        model.addAttribute("success", success);
        return "redirect:/creneaux"; // Redirection après action
    }

    // Annuler un créneau réservé
    @PostMapping("/annuler/{id}")
    public String annulerCreneau(@PathVariable Long id, @SessionAttribute("utilisateur") Utilisateur utilisateur, Model model) {
        boolean success = creneauService.annulerCreneau(id, utilisateur);
        model.addAttribute("success", success);
        return "redirect:/creneaux";
    }

    // (Optionnel) Page de création de créneau (admin)
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


}
