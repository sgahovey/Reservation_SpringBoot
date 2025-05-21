package com.dreams.hellowordspring.reservation.Model;


import jakarta.persistence.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

@Entity
@Table(name = "utilisateur")
public class Utilisateur implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    private Long id;
    private String nom;
    private String prenom;
    private String pseudo;
    private String email;
    private String password;

    private boolean admin;

    @OneToMany(mappedBy = "reservePar")
    private List<Creneau> creneauxReserves;

    public Utilisateur() {
    }

    public Utilisateur(Long id, String nom, String prenom, String pseudo, String email, String password, boolean admin, List<Creneau> creneauxReserves) {
        this.id = id;
        this.nom = nom;
        this.prenom = prenom;
        this.pseudo = pseudo;
        this.email = email;
        this.password = password;
        this.admin = admin;
        this.creneauxReserves = creneauxReserves;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getPrenom() {
        return prenom;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    public String getPseudo() {
        return pseudo;
    }

    public void setPseudo(String pseudo) {
        this.pseudo = pseudo;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }


    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isAdmin() {
        return admin;
    }

    public void setAdmin(boolean admin) {
        this.admin = admin;
    }

    public List<Creneau> getCreneauxReserves() {
        return creneauxReserves;
    }

    public void setCreneauxReserves(List<Creneau> creneauxReserves) {
        this.creneauxReserves = creneauxReserves;
    }

    // 🛡️ Implémentation de UserDetails :

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        if (this.admin) {
            return List.of(new SimpleGrantedAuthority("ROLE_ADMIN")); // ← ici
        }
        return List.of();
    }


    @Override
    public String getUsername() {
        return pseudo; // ⚠️ utilisé par Spring Security
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }


}