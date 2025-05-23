package com.dreams.hellowordspring.reservation.Service;

import com.dreams.hellowordspring.reservation.Model.Utilisateur;
import com.dreams.hellowordspring.reservation.Repository.UtilisateurRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UtilisateurService {

    @Autowired
    private UtilisateurRepository utilisateurRepository;

    /**
     * Vérifie les identifiants d’un utilisateur.
     */
    public Utilisateur verifierConnexion(String pseudo, String password) {
        return utilisateurRepository.findByPseudoAndPassword(pseudo, password).orElse(null);
    }

    /**
     * Retourne la liste complète des utilisateurs.
     */
    public List<Utilisateur> getAllUtilisateurs() {
        return utilisateurRepository.findAll();
    }

    /**
     * Sauvegarde ou met à jour un utilisateur.
     */
    public void save(Utilisateur utilisateur) {
        utilisateurRepository.save(utilisateur);
    }

    /**
     * Récupère un utilisateur par son identifiant.
     */
    public Utilisateur getById(Long id) {
        return utilisateurRepository.findById(id).orElse(null);
    }

    /**
     * Supprime un utilisateur selon son identifiant.
     */
    public void delete(Long id) {
        utilisateurRepository.deleteById(id);
    }


}