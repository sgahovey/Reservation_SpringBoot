package com.dreams.hellowordspring.reservation.Controller;

import com.dreams.hellowordspring.reservation.Model.Creneau;
import com.dreams.hellowordspring.reservation.Repository.CreneauRepository;
import com.dreams.hellowordspring.reservation.Repository.UtilisateurRepository;
import com.dreams.hellowordspring.reservation.Service.CreneauService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import com.dreams.hellowordspring.reservation.Model.Creneau.EtatCreneau;

import java.time.LocalDate;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@Controller
@RequestMapping("/admin")

// Injection des dépendances
public class AdminController {
    @Autowired
private UtilisateurRepository utilisateurRepository;

    @Autowired
    private CreneauRepository creneauRepository;

    @Autowired
    private CreneauService creneauService;

    /**
     * Affiche la liste des demandes de créneaux en attente avec option de tri
     */
    @GetMapping("/demandes")
    public String afficherDemandes(
            @RequestParam(required = false) String sort,
            @RequestParam(required = false, defaultValue = "asc") String direction,
            Model model
    ) {
        List<Creneau> demandes = creneauService.getCreneauxEnAttente();

        // Tri dynamique selon la colonne demandée
        Comparator<Creneau> comparator = null;
        if ("utilisateur".equals(sort)) {
            comparator = Comparator.comparing(c -> c.getReservePar() != null ? c.getReservePar().getNom() : "");
        } else if ("date".equals(sort)) {
            comparator = Comparator.comparing(Creneau::getDate);
        }
        // Application du tri
        if (comparator != null) {
            demandes.sort(comparator);
            if ("desc".equals(direction)) {
                Collections.reverse(demandes);
            }
        }
        // Ajout des données au modèle pour la vue
        model.addAttribute("demandes", demandes);
        model.addAttribute("sort", sort);
        model.addAttribute("direction", direction);
        return "creneaux_Admin/demandes";
    }

    @GetMapping("/ajouter")
    public String formAjouter(Model model) {
        model.addAttribute("creneau", new Creneau());
        return "admin/ajouter";
    }

    /**
     * Affiche un formulaire d’ajout pré-rempli avec une date (dans une modale)
     */
    @GetMapping("/formulaire-ajout")
    public String afficherFormulaireAjout(@RequestParam String date, Model model) {
        Creneau creneau = new Creneau();
        creneau.setDate(LocalDate.parse(date));
        model.addAttribute("creneau", creneau);
        return "creneaux_Admin/ajouter :: modalAjout";
    }

    /**
     * Enregistre un créneau validé depuis le formulaire
     */
    @PostMapping("/ajouter")
    public String ajouterCreneau(@ModelAttribute Creneau creneau) {
        creneau.setEtat(EtatCreneau.VALIDE);
        creneauService.save(creneau);
        return "redirect:/creneaux";
    }

    /**
     * Supprime un créneau existant par son ID
     */
    @PostMapping("/delete/{id}")
    public String supprimerCreneau(@PathVariable Long id) {
        creneauService.deleteById(id); // ou une méthode sécurisée
        return "redirect:/creneaux?deleteSuccess";
    }

    /**
     * Valide une demande de créneau (modifie son état)
     */
    @PostMapping("/valider/{id}")
    public String validerCreneau(@PathVariable Long id) {
        creneauService.validerCreneau(id);
        return "redirect:/admin/demandes";
    }

    /**
     * Refuse une demande de créneau
     */
    @PostMapping("/refuser/{id}")
    public String refuserCreneau(@PathVariable Long id) {
        creneauService.refuserCreneau(id);
        return "redirect:/admin/demandes";
    }

    /**
     * Affiche l’historique filtrable des demandes de créneaux (par date ou état)
     */
    @GetMapping("/historique")
    public String afficherHistorique(
            @RequestParam(required = false) String etat,
            @RequestParam(required = false) String date,
            @RequestParam(required = false) String sort,
            @RequestParam(required = false, defaultValue = "asc") String direction,
            Model model
    ) {
        List<Creneau> demandes = creneauService.getHistoriqueDemandes(etat, date);

        // Appliquer le tri
        if ("date".equals(sort)) {
            demandes.sort(Comparator.comparing(Creneau::getDate));
        } else if ("etat".equals(sort)) {
            demandes.sort(Comparator.comparing(Creneau::getEtat));
        }

        if ("desc".equals(direction)) {
            Collections.reverse(demandes);
        }

        // Envoi des données à la vue
        model.addAttribute("demandes", demandes);
        model.addAttribute("etatFiltre", etat);
        model.addAttribute("dateFiltre", date);
        model.addAttribute("sort", sort);
        model.addAttribute("direction", direction);

        return "creneaux_Admin/historique";
    }

    /**
     * Affiche les statistiques globales des utilisateurs et des créneaux
     */
    @GetMapping("/stats")
    public String afficherStatistiques(Model model) {
        // Utilisateurs
        long total = utilisateurRepository.count();
        long admins = utilisateurRepository.countByAdminTrue();
        long simples = total - admins;

        // Créneaux
        long totalDemandes = creneauRepository.count();
        long demandesValidees = creneauRepository.countByEtat(EtatCreneau.VALIDE);
        long demandesEnAttente = creneauRepository.countByEtat(EtatCreneau.EN_ATTENTE);
        long demandesRefusees = creneauRepository.countByEtat(EtatCreneau.REFUSE);

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
