package com.dreams.hellowordspring.reservation.Repository;

import com.dreams.hellowordspring.reservation.Model.Utilisateur;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface UtilisateurRepository extends CrudRepository<Utilisateur, Long> {

    /**
     * Recherche d'un utilisateur par pseudo (utilisé pour la connexion ou la validation d'unicité)
     */
    Optional<Utilisateur> findByPseudo(String pseudo);

    /**
     * Recherche par pseudo + mot de passe
     */
    Optional<Utilisateur> findByPseudoAndPassword(String pseudo, String password);

    /**
     * Retourne tous les utilisateurs sous forme de liste (utile pour l'affichage avec Thymeleaf)
     */
    List<Utilisateur> findAll(); // méthode pour obtenir une liste utilisable avec Thymeleaf

    /**
     * Compte les utilisateurs qui sont administrateurs (admin = true)
     */
    long countByAdminTrue();

}
