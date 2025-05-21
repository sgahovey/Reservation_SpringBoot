package com.dreams.hellowordspring.reservation.Service;

import com.dreams.hellowordspring.reservation.Model.Utilisateur;
import com.dreams.hellowordspring.reservation.Repository.UtilisateurRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UtilisateurService {

    @Autowired
    private UtilisateurRepository utilisateurRepository;

    public Utilisateur verifierConnexion(String pseudo, String password) {
        return utilisateurRepository.findByPseudoAndPassword(pseudo, password).orElse(null);
    }

}