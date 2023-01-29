package com.app.elgoumri.bean;

public class UserFactory {

    public static Utilisateur utilisateur;

    public static Utilisateur getUtilisateur() {
        return utilisateur;
    }

    public static void setUtilisateur(Utilisateur utilisateur) {
        UserFactory.utilisateur = utilisateur;
    }
}
