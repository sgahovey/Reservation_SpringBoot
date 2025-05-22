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

    public Utilisateur verifierConnexion(String pseudo, String password) {
        return utilisateurRepository.findByPseudoAndPassword(pseudo, password).orElse(null);
    }

    public List<Utilisateur> getAllUtilisateurs() {
        return utilisateurRepository.findAll();
    }

    public void save(Utilisateur utilisateur) {
        utilisateurRepository.save(utilisateur);
    }

    public Utilisateur getById(Long id) {
        return utilisateurRepository.findById(id).orElse(null);
    }

    public void delete(Long id) {
        utilisateurRepository.deleteById(id);
    }


}