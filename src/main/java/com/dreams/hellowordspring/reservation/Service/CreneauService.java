package com.dreams.hellowordspring.reservation.Service;

import com.dreams.hellowordspring.reservation.Model.Creneau;
import com.dreams.hellowordspring.reservation.Model.Utilisateur;
import com.dreams.hellowordspring.reservation.Repository.CreneauRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class CreneauService {
    @Autowired
    private CreneauRepository creneauRepository;

    /**
     * Retourne tous les créneaux disponibles (non réservés)
     */
    public List<Creneau> getCreneauxDisponibles() {
        return creneauRepository.findByReserveParIsNull();
    }

    /**
     * Permet à un utilisateur de réserver un créneau libre et non passé
     */
    @Transactional
    public boolean reserverCreneau(Long idCreneau, Utilisateur utilisateur) {
        Optional<Creneau> optionalCreneau = creneauRepository.findById(idCreneau);

        if (optionalCreneau.isPresent()) {
            Creneau creneau = optionalCreneau.get();

            // Vérifie que le créneau est libre et dans le futur
            if (creneau.getReservePar() == null && !creneau.getDate().isBefore(LocalDate.now())) {
                creneau.setReservePar(utilisateur);
                creneauRepository.save(creneau);
                return true;
            }
        }
        return false;
    }

    /**
     * Annule la réservation d’un créneau si c’est l’utilisateur qui l’a réservé
     */
    @Transactional
    public boolean annulerCreneau(Long idCreneau, Utilisateur utilisateur) {
        Optional<Creneau> optionalCreneau = creneauRepository.findById(idCreneau);

        if (optionalCreneau.isPresent()) {
            Creneau creneau = optionalCreneau.get();

            // Vérifie que l'utilisateur est bien celui qui a réservé
            if (creneau.getReservePar() != null && creneau.getReservePar().getId().equals(utilisateur.getId())) {
                creneau.setReservePar(null);
                creneauRepository.save(creneau);
                return true;
            }
        }
        return false;
    }

    // CRUD basiques
    public Iterable<Creneau> findAll() {
        return creneauRepository.findAll();
    }

    public Optional<Creneau> findById(Long id) {
        return creneauRepository.findById(id);
    }

    public Creneau save(Creneau creneau) {
        return creneauRepository.save(creneau);
    }

    public void deleteById(Long id) {
        creneauRepository.deleteById(id);
    }

    /**
     * Retourne tous les créneaux en attente (à valider par un admin)
     */
    public List<Creneau> getCreneauxEnAttente() {
        return creneauRepository.findByEtat(Creneau.EtatCreneau.EN_ATTENTE);
    }

    /**
     * Change l’état d’un créneau en VALIDÉ
     */
    public void validerCreneau(Long id) {
        Optional<Creneau> creneau = creneauRepository.findById(id);
        creneau.ifPresent(c -> {
            c.setEtat(Creneau.EtatCreneau.VALIDE);
            creneauRepository.save(c);
        });
    }

    /**
     * Change l’état d’un créneau en REFUSÉ
     */
    public void refuserCreneau(Long id) {
        Optional<Creneau> opt = creneauRepository.findById(id);
        if (opt.isPresent()) {
            Creneau creneau = opt.get();
            creneau.setEtat(Creneau.EtatCreneau.REFUSE); // 👈 ici
            creneauRepository.save(creneau);
        }
    }

    /**
     * Retourne les créneaux réservés par un utilisateur donné
     */
    public List<Creneau> getCreneauxParUtilisateur(Utilisateur utilisateur) {
        return creneauRepository.findByReservePar(utilisateur);
    }

    /**
     * Supprime une demande de créneau si elle est en attente et que c’est bien l'utilisateur qui l’a faite
     */
    public boolean supprimerDemandeEnAttente(Long id, Utilisateur utilisateur) {
        Optional<Creneau> creneauOpt = creneauRepository.findById(id);
        if (creneauOpt.isPresent()) {
            Creneau c = creneauOpt.get();
            if (c.getReservePar() != null && c.getReservePar().getId().equals(utilisateur.getId())
                    && c.getEtat() == Creneau.EtatCreneau.EN_ATTENTE) {
                creneauRepository.deleteById(id);
                return true;
            }
        }
        return false;
    }

    /**
     * Retourne tous les créneaux validés
     */
    public List<Creneau> getCreneauxValides() {
        return creneauRepository.findByEtat(Creneau.EtatCreneau.VALIDE);
    }

    /**
     * Filtrage historique : par état, par date ou les deux
     */
    public List<Creneau> getHistoriqueDemandes(String etat, String dateStr) {
        try {
            if (etat != null && !etat.isEmpty() && dateStr != null && !dateStr.isEmpty()) {
                return creneauRepository.findByEtatAndDate(Creneau.EtatCreneau.valueOf(etat.toUpperCase()), LocalDate.parse(dateStr));
            } else if (etat != null && !etat.isEmpty()) {
                return creneauRepository.findByEtat(Creneau.EtatCreneau.valueOf(etat.toUpperCase()));
            } else if (dateStr != null && !dateStr.isEmpty()) {
                return creneauRepository.findByDate(LocalDate.parse(dateStr));
            } else {
                List<Creneau> result = new ArrayList<>();
                creneauRepository.findAll().forEach(result::add);
                return result;
            }
        } catch (IllegalArgumentException e) {
            return new ArrayList<>();
        }
    }

    /**
     * Vérifie s'il y a des conflits horaires avec d'autres créneaux au même lieu
     */
    public boolean estDisponible(Creneau creneau) {
        List<Creneau> chevauchements = creneauRepository.findChevauchements(
                creneau.getLieu(),
                creneau.getDate(),
                creneau.getHeureDebut(),
                creneau.getHeureFin()
        );
        return chevauchements.isEmpty();
    }




}

