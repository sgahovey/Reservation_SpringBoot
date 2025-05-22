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

import java.time.Duration;
import java.time.LocalDate;
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
    public String ajouterCreneau(@ModelAttribute Creneau creneau, Model model) {
        if (!creneauService.estDisponible(creneau)) {
            model.addAttribute("erreur", "Créneau déjà occupé à cet horaire et lieu !");
            return "creneaux/ajouter"; // retourne le formulaire avec un message
        }

        creneau.setEtat(Creneau.EtatCreneau.VALIDE);
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
            evt.put("id", c.getId()); // ✅ ICI pour permettre le clic vers /details/{id}
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
            Creneau creneau = opt.get();

            // Durée calculée
            Duration duree = Duration.between(creneau.getHeureDebut(), creneau.getHeureFin());
            long heures = duree.toHours();
            long minutes = duree.toMinutes() % 60;
            String dureeFormatee = heures + "h" + (minutes > 0 ? minutes : "");

            // Utilisateur connecté (si tu utilises Spring Security)
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            Utilisateur utilisateur = (auth.getPrincipal() instanceof Utilisateur) ? (Utilisateur) auth.getPrincipal() : null;

            model.addAttribute("creneau", creneau);
            model.addAttribute("duree", dureeFormatee);
            model.addAttribute("utilisateur", utilisateur); // ✅ Ajout pour vérifier admin

            return "creneaux/details";
        } else {
            return "redirect:/creneaux";
        }
    }

    @PostMapping("/delete/{id}")
    public String supprimerCreneau(@PathVariable Long id) {
        creneauService.deleteById(id); // ou une méthode sécurisée
        return "redirect:/creneaux?deleteSuccess";
    }

    @GetMapping("/formulaire-demande")
    public String afficherFormulaireDemande(@RequestParam String date, Model model) {
        Creneau creneau = new Creneau();
        creneau.setDate(LocalDate.parse(date));
        model.addAttribute("creneau", creneau);
        return "creneaux/demander :: modalForm";
    }

    @GetMapping("/formulaire-ajout")
    public String afficherFormulaireAjout(@RequestParam String date, Model model) {
        Creneau creneau = new Creneau();
        creneau.setDate(LocalDate.parse(date));
        model.addAttribute("creneau", creneau);
        return "creneaux/ajouter :: modalAjout";
    }

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

        model.addAttribute("demandes", demandes);
        model.addAttribute("etatFiltre", etat);
        model.addAttribute("dateFiltre", date);
        model.addAttribute("sort", sort);
        model.addAttribute("direction", direction);

        return "creneaux/historique";
    }




}