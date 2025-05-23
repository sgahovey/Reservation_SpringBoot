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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.Optional;

@Controller
public class AuthentificationController {
    @Autowired
    private UtilisateurRepository utilisateurRepository;

    @Autowired
    private UtilisateurService utilisateurService;

    /**
     * Affiche la page de connexion
     */
    @GetMapping("/login")
    public String afficherLogin(Model model) {
        model.addAttribute("utilisateur", new Utilisateur());
        return "Authentification/login";
    }

    /**
     * Traite la tentative de connexion manuelle (non utilisée si Spring Security est actif)
     */
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
            return "Authentification/login";
        }
    }

    /**
     * Affiche le formulaire d'inscription
     */
    @GetMapping("/register")
    public String afficherFormulaireInscription(Model model) {
        Utilisateur utilisateurConnecte = getUtilisateurConnecte();
        model.addAttribute("utilisateur", new Utilisateur());
        model.addAttribute("utilisateurSession", utilisateurConnecte);
        return "Authentification/register";
    }

    /**
     * Traite le formulaire d'inscription
     */
    @PostMapping("/register")
    public String enregistrerUtilisateur(@ModelAttribute Utilisateur utilisateur, Model model) {
        Utilisateur utilisateurConnecte = getUtilisateurConnecte();
        // Vérifie les identifiants via le service
        Optional<Utilisateur> existingUser = utilisateurRepository.findByPseudo(utilisateur.getPseudo());
        if (existingUser.isPresent()) {
            model.addAttribute("erreur", "Ce pseudo est déjà utilisé.");
            model.addAttribute("utilisateurSession", utilisateurConnecte);
            return "Authentification/register";
        }

        if (utilisateurConnecte == null || !utilisateurConnecte.isAdmin()) {
            utilisateur.setAdmin(false);
        }

        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        utilisateur.setPassword(encoder.encode(utilisateur.getPassword()));
        utilisateurRepository.save(utilisateur);

        return "redirect:/login?registerSuccess";
    }

    /**
     * Récupère l’utilisateur connecté à partir du contexte de sécurité
     */
    private Utilisateur getUtilisateurConnecte() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof Utilisateur) {
            return (Utilisateur) auth.getPrincipal();
        }
        return null;
    }


}
