package com.dreams.hellowordspring.reservation.Repository;

import com.dreams.hellowordspring.reservation.Model.Creneau;
import com.dreams.hellowordspring.reservation.Model.Utilisateur;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalTime;
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

    @Query("""
    SELECT c FROM Creneau c
    WHERE c.lieu = :lieu
    AND c.date = :date
    AND (
        (:heureDebut < c.heureFin AND :heureFin > c.heureDebut)
    )
""")
    List<Creneau> findChevauchements(
            @Param("lieu") String lieu,
            @Param("date") LocalDate date,
            @Param("heureDebut") LocalTime heureDebut,
            @Param("heureFin") LocalTime heureFin
    );


}
