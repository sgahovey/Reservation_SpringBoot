package com.dreams.hellowordspring.reservation.Repository;

import com.dreams.hellowordspring.reservation.Model.Utilisateur;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface UtilisateurRepository extends CrudRepository<Utilisateur, Long> {

    // Authentification par pseudo
    Optional<Utilisateur> findByPseudo(String pseudo);
    // Authentification par pseudo et mot de passe

    Optional<Utilisateur> findByPseudoAndPassword(String pseudo, String password);

        List<Utilisateur> findAll(); // méthode pour obtenir une liste utilisable avec Thymeleaf


}
