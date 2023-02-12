package com.app.elgoumri.bean;

import java.io.Serializable;

public class Utilisateur implements Serializable {

    private String id;

    private String nom;

    private String prenom;

    private String email;

    private boolean emailVerif;

    private String telephone;

    private boolean telephoneVerif;

    private String passwor;

    private int colorUser;

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

    public boolean isEmailVerif() {
        return emailVerif;
    }

    public Utilisateur setEmailVerif(boolean emailVerif) {
        this.emailVerif = emailVerif;
        return this;
    }

    public boolean isTelephoneVerif() {
        return telephoneVerif;
    }

    public Utilisateur setTelephoneVerif(boolean telephoneVerif) {
        this.telephoneVerif = telephoneVerif;
        return this;
    }

    public int getColorUser() {
        return colorUser;
    }

    public Utilisateur setColorUser(int colorUser) {
        this.colorUser = colorUser;
        return this;
    }
}
