package com.dreams.hellowordspring.reservation.Controller;

import com.dreams.hellowordspring.reservation.Model.Utilisateur;
import com.dreams.hellowordspring.reservation.Service.UtilisateurService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@Controller
public class UtilisateurController {


    @Autowired
    private UtilisateurService utilisateurService;

    /**
     * Affiche la liste des utilisateurs avec tri optionnel
     */
    @GetMapping("/utilisateurs")
    public String listeUtilisateurs(
            @RequestParam(required = false) String sort,
            @RequestParam(required = false, defaultValue = "asc") String direction,
            Model model
    ) {
        List<Utilisateur> utilisateurs = utilisateurService.getAllUtilisateurs();

        if ("id".equals(sort)) {
            utilisateurs.sort(Comparator.comparing(Utilisateur::getId));
        } else if ("admin".equals(sort)) {
            utilisateurs.sort(Comparator.comparing(Utilisateur::isAdmin));
        }

        if ("desc".equals(direction)) {
            Collections.reverse(utilisateurs);
        }

        model.addAttribute("utilisateurs", utilisateurs);
        model.addAttribute("sort", sort);
        model.addAttribute("direction", direction);
        return "utilisateurs/index";
    }

    /**
     * Affiche le formulaire pour ajouter un nouvel utilisateur
     */
    @GetMapping("/utilisateurs/ajouter")
    public String formAjouter(Model model) {
        model.addAttribute("utilisateur", new Utilisateur());
        return "utilisateurs/ajouter"; // <- important
    }

    /**
     * Enregistre un nouvel utilisateur en base
     */
    @PostMapping("/utilisateurs")
    public String ajouterUtilisateur(@ModelAttribute Utilisateur utilisateur) {
        if (utilisateur.getPassword() != null && !utilisateur.getPassword().isBlank()) {
            BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
            utilisateur.setPassword(encoder.encode(utilisateur.getPassword()));
        }
        utilisateurService.save(utilisateur);
        return "redirect:/utilisateurs";
    }

    /**
     * Affiche le formulaire de modification d’un utilisateur existant
     */
    @GetMapping("/utilisateurs/modifier/{id}")
    public String formulaireModif(@PathVariable Long id, Model model) {
        Utilisateur utilisateur = utilisateurService.getById(id);
        model.addAttribute("utilisateur", utilisateur);
        return "utilisateurs/modifier"; // ou "utilisateurs/form_utilisateur" si tu préfères
    }

    /**
     * Enregistre les modifications d’un utilisateur existant
     */
    @PostMapping("/utilisateurs/modifier")
    public String modifierUtilisateur(@ModelAttribute Utilisateur utilisateur) {
        Utilisateur utilisateurExistant = utilisateurService.getById(utilisateur.getId());

        utilisateurExistant.setNom(utilisateur.getNom());
        utilisateurExistant.setPrenom(utilisateur.getPrenom());
        utilisateurExistant.setPseudo(utilisateur.getPseudo());
        utilisateurExistant.setEmail(utilisateur.getEmail());
        utilisateurExistant.setAdmin(utilisateur.isAdmin());

        // ✅ encodage du nouveau mot de passe si fourni
        if (utilisateur.getPassword() != null && !utilisateur.getPassword().isBlank()) {
            BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
            utilisateurExistant.setPassword(encoder.encode(utilisateur.getPassword()));
        }

        utilisateurService.save(utilisateurExistant);
        return "redirect:/utilisateurs";
    }

    /**
     * Supprime un utilisateur par son ID
     */
    @GetMapping("/supprimer/{id}")
    public String supprimer(@PathVariable Long id) {
        utilisateurService.delete(id);
        return "redirect:/utilisateurs";
    }

    /**
     * Affiche la page des réglages du profil utilisateur connecté
     */
    @GetMapping("/utilisateurs/reglages")
    public String afficherReglages(Model model, HttpSession session) {
        Utilisateur utilisateur = (Utilisateur) session.getAttribute("utilisateur");
        model.addAttribute("utilisateur", utilisateur);
        return "utilisateurs/reglages";
    }

    /**
     * Permet à l’utilisateur connecté de modifier ses infos personnelles
     */
    @PostMapping("/utilisateurs/reglages")
    public String modifierInfos(@ModelAttribute Utilisateur utilisateurModifie, HttpSession session) {
        Utilisateur utilisateurEnBase = utilisateurService.getById(utilisateurModifie.getId());

        if (utilisateurEnBase != null) {
            utilisateurEnBase.setNom(utilisateurModifie.getNom());
            utilisateurEnBase.setPrenom(utilisateurModifie.getPrenom());
            utilisateurEnBase.setPseudo(utilisateurModifie.getPseudo());
            utilisateurEnBase.setEmail(utilisateurModifie.getEmail());

            if (utilisateurModifie.getPassword() != null && !utilisateurModifie.getPassword().isBlank()) {
                BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
                utilisateurEnBase.setPassword(encoder.encode(utilisateurModifie.getPassword()));
            }

            utilisateurService.save(utilisateurEnBase);
            session.setAttribute("utilisateur", utilisateurEnBase);
        }
        return "redirect:/creneaux?modif=ok";
    }
}
