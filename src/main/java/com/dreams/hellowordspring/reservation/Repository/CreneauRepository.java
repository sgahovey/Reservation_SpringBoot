package com.dreams.hellowordspring.reservation.Repository;

import com.dreams.hellowordspring.reservation.Model.Creneau;
import com.dreams.hellowordspring.reservation.Model.Utilisateur;
import org.springframework.data.repository.CrudRepository;

import java.time.LocalDate;
import java.util.List;

public interface CreneauRepository extends CrudRepository<Creneau, Long> {

    // Créneaux disponibles (non réservés)
    List<Creneau> findByReserveParIsNull();

    // Filtrage par date
    List<Creneau> findByDate(LocalDate date);

    List<Creneau> findByEtat(Creneau.EtatCreneau etat);

    List<Creneau> findByReservePar(Utilisateur utilisateur);

    List<Creneau> findByReserveParIsNotNull();

    List<Creneau> findByEtatAndDate(Creneau.EtatCreneau etat, LocalDate date);



}
