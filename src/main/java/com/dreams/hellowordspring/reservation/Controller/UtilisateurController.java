package com.dreams.hellowordspring.reservation.Controller;

import com.dreams.hellowordspring.reservation.Model.Utilisateur;
import com.dreams.hellowordspring.reservation.Repository.UtilisateurRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.Optional;

@Controller
public class UtilisateurController {

    @Autowired
    private UtilisateurRepository utilisateurRepository;

    // Affiche le formulaire de connexion
    @GetMapping("/login")
    public String afficherLogin(Model model) {
        model.addAttribute("utilisateur", new Utilisateur());
        return "utilisateur/login"; // /templates/utilisateur/login.html
    }

    // Traite la soumission du formulaire
    @PostMapping("/login")
    public String connexion(@ModelAttribute Utilisateur utilisateur, HttpSession session, Model model) {
        Optional<Utilisateur> user = utilisateurRepository.findByPseudoAndPassword(utilisateur.getPseudo(), utilisateur.getPassword());

        if (user.isPresent()) {
            session.setAttribute("utilisateur", user.get());
            return "redirect:/creneaux";
        } else {
            model.addAttribute("erreur", "Identifiants incorrects");
            return "utilisateur/login";
        }
    }

    // Déconnexion
    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }
}
