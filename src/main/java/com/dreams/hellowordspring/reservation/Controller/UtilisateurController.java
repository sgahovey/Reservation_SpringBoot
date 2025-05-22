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

    @GetMapping("/utilisateurs/ajouter")
    public String formAjouter(Model model) {
        model.addAttribute("utilisateur", new Utilisateur());
        return "utilisateurs/ajouter"; // <- important
    }


    @PostMapping("/utilisateurs")
    public String ajouterUtilisateur(@ModelAttribute Utilisateur utilisateur) {
        utilisateurService.save(utilisateur);
        return "redirect:/utilisateurs";
    }

    @GetMapping("/utilisateurs/modifier/{id}")
    public String formulaireModif(@PathVariable Long id, Model model) {
        Utilisateur utilisateur = utilisateurService.getById(id);
        model.addAttribute("utilisateur", utilisateur);
        return "utilisateurs/modifier"; // ou "utilisateurs/form_utilisateur" si tu préfères
    }


    @GetMapping("/supprimer/{id}")
    public String supprimer(@PathVariable Long id) {
        utilisateurService.delete(id);
        return "redirect:/utilisateurs";
    }

    @GetMapping("/utilisateurs/reglages")
    public String afficherReglages(Model model, HttpSession session) {
        Utilisateur utilisateur = (Utilisateur) session.getAttribute("utilisateur");
        model.addAttribute("utilisateur", utilisateur);
        return "utilisateurs/reglages";
    }

    @PostMapping("/utilisateurs/reglages")
    public String modifierInfos(@ModelAttribute Utilisateur utilisateurModifie, HttpSession session) {
        Utilisateur original = (Utilisateur) session.getAttribute("utilisateur");
        if (original != null) {
            original.setNom(utilisateurModifie.getNom());
            original.setPrenom(utilisateurModifie.getPrenom());
            original.setPseudo(utilisateurModifie.getPseudo());
            original.setEmail(utilisateurModifie.getEmail());

            if (utilisateurModifie.getPassword() != null && !utilisateurModifie.getPassword().isEmpty()) {
                BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
                original.setPassword(encoder.encode(utilisateurModifie.getPassword()));
            }

            utilisateurService.save(original);
            session.setAttribute("utilisateur", original);
        }

        return "redirect:/creneaux";
    }

}
