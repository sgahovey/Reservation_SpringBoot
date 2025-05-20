package com.dreams.hellowordspring.reservation.Service;

import com.dreams.hellowordspring.reservation.Model.Creneau;
import com.dreams.hellowordspring.reservation.Model.Utilisateur;
import com.dreams.hellowordspring.reservation.Repository.CreneauRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class CreneauService {
    @Autowired
    private CreneauRepository creneauRepository;

    // Liste des créneaux disponibles
    public List<Creneau> getCreneauxDisponibles() {
        return creneauRepository.findByReserveParIsNull();
    }
    // Réserver un créneau
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

    // Annuler une réservation
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
}

