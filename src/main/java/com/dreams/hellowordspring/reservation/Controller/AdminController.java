package com.dreams.hellowordspring.reservation.Controller;

import com.dreams.hellowordspring.reservation.Repository.CreneauRepository;
import com.dreams.hellowordspring.reservation.Repository.UtilisateurRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import com.dreams.hellowordspring.reservation.Model.Creneau.EtatCreneau;

@Controller
public class AdminController {
    @Autowired
private UtilisateurRepository utilisateurRepository;

    @Autowired
    private CreneauRepository creneauRepository;

    @GetMapping("/admin/stats")
    public String afficherStatistiques(Model model) {
        // Utilisateurs
        long total = utilisateurRepository.count();
        long admins = utilisateurRepository.countByAdminTrue();
        long simples = total - admins;

        // Créneaux
        long totalDemandes = creneauRepository.count(); // ✅ corrigé
        long demandesValidees = creneauRepository.countByEtat(EtatCreneau.VALIDE); // ✅ corrigé
        long demandesEnAttente = creneauRepository.countByEtat(EtatCreneau.EN_ATTENTE); // ✅ corrigé
        long demandesRefusees = creneauRepository.countByEtat(EtatCreneau.REFUSE); // ✅ corrigé

        model.addAttribute("totalUtilisateurs", total);
        model.addAttribute("totalAdmins", admins);
        model.addAttribute("totalSimples", simples);

        model.addAttribute("totalDemandes", totalDemandes);
        model.addAttribute("demandesValidees", demandesValidees);
        model.addAttribute("demandesEnAttente", demandesEnAttente);
        model.addAttribute("demandesRefusees", demandesRefusees);


        return "Statistiques/index";
    }

}
