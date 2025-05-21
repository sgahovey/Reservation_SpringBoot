package com.dreams.hellowordspring.reservation.Model;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "creneau")
public class Creneau {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    private Long id;

    private LocalDate date;
    private LocalDateTime heureDebut;
    private LocalDateTime heureFin;

    private String lieu;

    @ManyToOne
    private Utilisateur reservePar; // Null si non réservé

    @Enumerated(EnumType.STRING)
    private EtatCreneau etat = EtatCreneau.EN_ATTENTE; //Par défaut en attente

    public enum EtatCreneau {
        EN_ATTENTE,
        VALIDE
    }





    public Creneau() {}

    public Creneau(Long id, LocalDate date, LocalDateTime heureDebut, LocalDateTime heureFin, String lieu, Utilisateur reservePar, EtatCreneau etat) {
        this.id = id;
        this.date = date;
        this.heureDebut = heureDebut;
        this.heureFin = heureFin;
        this.lieu = lieu;
        this.reservePar = reservePar;
        this.etat = etat;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public LocalDateTime getHeureDebut() {
        return heureDebut;
    }

    public void setHeureDebut(LocalDateTime heureDebut) {
        this.heureDebut = heureDebut;
    }

    public LocalDateTime getHeureFin() {
        return heureFin;
    }

    public void setHeureFin(LocalDateTime heureFin) {
        this.heureFin = heureFin;
    }

    public String getLieu() {
        return lieu;
    }

    public void setLieu(String lieu) {
        this.lieu = lieu;
    }

    public Utilisateur getReservePar() {
        return reservePar;
    }

    public void setReservePar(Utilisateur reservePar) {
        this.reservePar = reservePar;
    }

    public EtatCreneau getEtat() {
        return etat;
    }

    public void setEtat(EtatCreneau etat) {
        this.etat = etat;
    }
}