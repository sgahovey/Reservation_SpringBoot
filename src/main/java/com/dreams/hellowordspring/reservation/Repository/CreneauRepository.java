package com.dreams.hellowordspring.reservation.Repository;

import com.dreams.hellowordspring.reservation.Model.Creneau;
import org.springframework.data.repository.CrudRepository;

import java.time.LocalDate;
import java.util.List;

public interface CreneauRepository extends CrudRepository<Creneau, Long> {

    // Créneaux disponibles (non réservés)
    List<Creneau> findByReserveParIsNull();

    // Filtrage par date
    List<Creneau> findByDate(LocalDate date);
}
