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

import java.util.*;

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
        Utilisateur utilisateur = getUtilisateurConnecte();

        creneau.setEtat(Creneau.EtatCreneau.EN_ATTENTE);
        creneau.setReservePar(utilisateur); // 🔥 attache l'utilisateur connecté à la demande

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

    @GetMapping("/calendrier")
    public String afficherCalendrier() {
        return "creneaux/calendrier";
    }


    //API
    @GetMapping("/api/creneaux-valides")
    @ResponseBody
    public List<Map<String, Object>> getCreneauxValides() {
        List<Creneau> liste = creneauService.getCreneauxValides();
        List<Map<String, Object>> resultats = new ArrayList<>();

        for (Creneau c : liste) {
            Map<String, Object> evt = new HashMap<>();
            evt.put("title", c.getLieu());

            // ✅ Combine date et heure début et fin au format ISO
            evt.put("start", c.getDate().toString() + "T" + c.getHeureDebut().toString());
            evt.put("end", c.getDate().toString() + "T" + c.getHeureFin().toString());

            // Props supplémentaires
            evt.put("extendedProps", Map.of(
                    "lieu", c.getLieu(),
                    "date", c.getDate().toString(),
                    "heureDebut", c.getHeureDebut().toString(),
                    "heureFin", c.getHeureFin().toString()
            ));
            resultats.add(evt);
        }
        return resultats;
    }


    @GetMapping("/details/{id}")
    public String afficherDetailsCreneau(@PathVariable Long id, Model model) {
        Optional<Creneau> opt = creneauService.findById(id);
        if (opt.isPresent()) {
            model.addAttribute("creneau", opt.get());
            return "creneaux/details";
        } else {
            return "redirect:/creneaux";
        }
    }


}