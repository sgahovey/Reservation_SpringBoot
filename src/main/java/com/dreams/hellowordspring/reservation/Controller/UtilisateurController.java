package com.dreams.hellowordspring.reservation.Controller;

import com.dreams.hellowordspring.reservation.Model.Utilisateur;
import com.dreams.hellowordspring.reservation.Repository.UtilisateurRepository;
import com.dreams.hellowordspring.reservation.Service.UtilisateurService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Controller
public class UtilisateurController {

    @Autowired
    private UtilisateurRepository utilisateurRepository;

    @Autowired
    private UtilisateurService utilisateurService;
    @GetMapping("/login")
    public String afficherLogin(Model model) {
        model.addAttribute("utilisateur", new Utilisateur());
        return "Utilisateurs/login";
    }
    @PostMapping("/login")
    public String traiterLogin(@ModelAttribute Utilisateur utilisateurFormulaire,
                               HttpSession session,
                               Model model) {
        Utilisateur utilisateur = utilisateurService.verifierConnexion(utilisateurFormulaire.getPseudo(), utilisateurFormulaire.getPassword());

        if (utilisateur != null) {
            session.setAttribute("utilisateur", utilisateur); // 🔐 Stocke dans la session
            return "redirect:/accueil"; // ou la page d'accueil/admin
        } else {
            model.addAttribute("erreur", "Identifiants invalides");
            return "Utilisateurs/login";
        }
    }


    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }

    @GetMapping("/register")
    public String afficherFormulaireInscription(Model model) {
        Utilisateur utilisateurConnecte = getUtilisateurConnecte();
        model.addAttribute("utilisateur", new Utilisateur());
        model.addAttribute("utilisateurSession", utilisateurConnecte);
        return "Utilisateurs/register";
    }

    @PostMapping("/register")
    public String enregistrerUtilisateur(@ModelAttribute Utilisateur utilisateur, Model model) {
        Utilisateur utilisateurConnecte = getUtilisateurConnecte();

        Optional<Utilisateur> existingUser = utilisateurRepository.findByPseudo(utilisateur.getPseudo());
        if (existingUser.isPresent()) {
            model.addAttribute("erreur", "Ce pseudo est déjà utilisé.");
            model.addAttribute("utilisateurSession", utilisateurConnecte);
            return "Utilisateurs/register";
        }

        if (utilisateurConnecte == null || !utilisateurConnecte.isAdmin()) {
            utilisateur.setAdmin(false);
        }

        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        utilisateur.setPassword(encoder.encode(utilisateur.getPassword()));
        utilisateurRepository.save(utilisateur);

        return "redirect:/login?registerSuccess";
    }



    private Utilisateur getUtilisateurConnecte() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof Utilisateur) {
            return (Utilisateur) auth.getPrincipal();
        }
        return null;
    }

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
