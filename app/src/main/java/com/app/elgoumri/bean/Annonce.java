package com.app.elgoumri.bean;


import java.io.Serializable;

public class Annonce implements Serializable {

    private String id;

    private String titre;

    private String description;

    private String categorie;

    private String adresseDepart;

    private String adresseDepartID;

    private String adressArrive;

    private String adressArriveID;

    private String dateDepart;

    private String dateArrive;

    private boolean gratuit;

    private float prix;

    private String devise;

    private Utilisateur utilisateur;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitre() {
        return titre;
    }

    public void setTitre(String titre) {
        this.titre = titre;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getAdresseDepart() {
        return adresseDepart;
    }

    public void setAdresseDepart(String adresseDepart) {
        this.adresseDepart = adresseDepart;
    }

    public String getAdressArrive() {
        return adressArrive;
    }

    public void setAdressArrive(String adressArrive) {
        this.adressArrive = adressArrive;
    }

    public boolean isGratuit() {
        return gratuit;
    }

    public void setGratuit(boolean gratuit) {
        this.gratuit = gratuit;
    }

    public float getPrix() {
        return prix;
    }

    public void setPrix(float prix) {
        this.prix = prix;
    }

    public Utilisateur getUtilisateur() {
        return utilisateur;
    }

    public void setUtilisateur(Utilisateur utilisateur) {
        this.utilisateur = utilisateur;
    }

    public String getDateDepart() {
        return dateDepart;
    }

    public void setDateDepart(String dateDepart) {
        this.dateDepart = dateDepart;
    }

    public String getDateArrive() {
        return dateArrive;
    }

    public void setDateArrive(String dateArrive) {
        this.dateArrive = dateArrive;
    }

    public String getCategorie() {
        return categorie;
    }

    public void setCategorie(String categorie) {
        this.categorie = categorie;
    }

    public String getAdresseDepartID() {
        return adresseDepartID;
    }

    public void setAdresseDepartID(String adresseDepartID) {
        this.adresseDepartID = adresseDepartID;
    }

    public String getAdressArriveID() {
        return adressArriveID;
    }

    public void setAdressArriveID(String adressArriveID) {
        this.adressArriveID = adressArriveID;
    }

    public String getDevise() {
        return devise;
    }

    public void setDevise(String devise) {
        this.devise = devise;
    }
}
