package com.app.elgoumri.bean;

import java.io.Serializable;

public class Utilisateur implements Serializable {

    private String id;

    private String nom;

    private String prenom;

    private String email;

    private String telephone;

    private String passwor;

    public String getId() {
        return id;
    }

    public Utilisateur setId(String id) {
        this.id = id;
        return this;
    }

    public String getNom() {
        return nom;
    }

    public Utilisateur setNom(String nom) {
        this.nom = nom;
        return this;
    }

    public String getPrenom() {
        return prenom;
    }

    public Utilisateur setPrenom(String prenom) {
        this.prenom = prenom;
        return this;
    }

    public String getEmail() {
        return email;
    }

    public Utilisateur setEmail(String email) {
        this.email = email;
        return this;
    }

    public String getTelephone() {
        return telephone;
    }

    public Utilisateur setTelephone(String telephone) {
        this.telephone = telephone;
        return this;
    }

    public String getPasswor() {
        return passwor;
    }

    public Utilisateur setPasswor(String passwor) {
        this.passwor = passwor;
        return this;
    }
}
