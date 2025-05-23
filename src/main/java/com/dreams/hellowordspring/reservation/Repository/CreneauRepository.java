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

    /**
     * Retourne les créneaux disponibles (aucun utilisateur n’a réservé)
     */
    List<Creneau> findByReserveParIsNull();

    /**
     * Retourne les créneaux pour une date précise
     */
    List<Creneau> findByDate(LocalDate date);

    /**
     * Retourne les créneaux selon leur état (VALIDE, EN_ATTENTE, REFUSE)
     */
    List<Creneau> findByEtat(Creneau.EtatCreneau etat);

    /**
     * Retourne tous les créneaux réservés par un utilisateur donné
     */
    List<Creneau> findByReservePar(Utilisateur utilisateur);

    /**
     * Retourne tous les créneaux déjà réservés (par n’importe qui)
     */
    List<Creneau> findByReserveParIsNotNull();

    /**
     * Retourne les créneaux selon état et date (utile pour les historiques filtrés)
     */
    List<Creneau> findByEtatAndDate(Creneau.EtatCreneau etat, LocalDate date);

    /**
     * Requête personnalisée JPQL pour détecter les conflits d'horaires.
     * On vérifie si un créneau existe au même lieu et à la même date avec un chevauchement d'heure.
     */
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

    /**
     * Compte total des créneaux
     */
    long count();

    /**
     * Compte les créneaux selon leur état (ex : combien en attente, combien validés)
     */
    long countByEtat(Creneau.EtatCreneau etat); // ✅ Correct type
}
