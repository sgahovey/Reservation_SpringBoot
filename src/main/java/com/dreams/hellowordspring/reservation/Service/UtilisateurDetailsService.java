package com.dreams.hellowordspring.reservation.Service;

import com.dreams.hellowordspring.reservation.Repository.UtilisateurRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UtilisateurDetailsService implements UserDetailsService {

    @Autowired
    private UtilisateurRepository utilisateurRepository;

    /**
     * Cette méthode est appelée automatiquement par Spring Security lors de la tentative de connexion.
     * Elle charge l’utilisateur correspondant au pseudo fourni.
     *
     * @param pseudo Le nom d’utilisateur (pseudo) fourni lors de la connexion
     * @return Un objet UserDetails contenant les informations nécessaires pour l’authentification
     * @throws UsernameNotFoundException si l’utilisateur n’est pas trouvé en base
     */
    @Override
    public UserDetails loadUserByUsername(String pseudo) throws UsernameNotFoundException {
        return utilisateurRepository.findByPseudo(pseudo)
                .orElseThrow(() -> new UsernameNotFoundException("Utilisateur non trouvé : " + pseudo));
    }

}
