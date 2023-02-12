package com.app.elgoumri.bean;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.app.elgoumri.user.ConnexionActivity;
import com.app.elgoumri.user.ProfileActivity;
import com.google.firebase.auth.FirebaseAuth;

import java.util.HashMap;

public class SessionManager {

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private Context context;
    private int PRIVATE_MODE = 0;

    private static final String PREF_NAME = "LOGIN";
    private static final String LOGIN = "IS_LOGIN";
    public static final String NOM = "NOM";
    public static final String PRENOM = "PRENOM";
    public static final String EMAIL = "EMAIL";
    public static final String MOT_DE_PASSE = "motDePasse";
    public static final String TELEPHONE = "TELEPHONE";
    public static final String IDUTILISATEUR = "IDUTILISATEUR";

    public SessionManager(Context context) {
        this.context = context;
        sharedPreferences = context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = sharedPreferences.edit();
    }

    public void createSession(Utilisateur utilisateur){

        editor.putBoolean(LOGIN, true);
        editor.putString(NOM, utilisateur.getNom());
        editor.putString(PRENOM, utilisateur.getPrenom());
        editor.putString(EMAIL, utilisateur.getEmail());
        editor.putString(MOT_DE_PASSE, utilisateur.getPasswor());
        editor.putString(TELEPHONE, utilisateur.getTelephone());
        editor.putString(IDUTILISATEUR, utilisateur.getId());
        editor.apply();
    }

    public Utilisateur getUserFromSession(){
        Utilisateur utilisateur = new Utilisateur();
        utilisateur.setId(sharedPreferences.getString(IDUTILISATEUR, null))
                .setNom(sharedPreferences.getString(NOM, null))
                .setPrenom(sharedPreferences.getString(PRENOM, null))
                .setTelephone(sharedPreferences.getString(TELEPHONE, null))
                .setEmail(sharedPreferences.getString(EMAIL, null))
                .setPasswor(sharedPreferences.getString(MOT_DE_PASSE, null));

        return utilisateur;
    }

    public boolean isLoggin(){
        return sharedPreferences.getBoolean(LOGIN, false);
    }

    public void checkLogin(){
        if (!this.isLoggin()){
            Intent i = new Intent(context, ConnexionActivity.class);
            context.startActivity(i);
            ((ProfileActivity) context).finish();
        }
    }

    public HashMap<String, String> getUserDetail(){
        HashMap<String, String> user = new HashMap<>();
        user.put(NOM, sharedPreferences.getString(NOM, null));
        user.put(PRENOM, sharedPreferences.getString(PRENOM, null));
        user.put(EMAIL, sharedPreferences.getString(EMAIL, null));
        user.put(IDUTILISATEUR, sharedPreferences.getString(IDUTILISATEUR, null));
        user.put(TELEPHONE, sharedPreferences.getString(TELEPHONE, null));
        user.put(MOT_DE_PASSE, sharedPreferences.getString(MOT_DE_PASSE, null));
        return user;
    }

    public void logout(){
        editor.clear();
        editor.commit();
        FirebaseAuth.getInstance().signOut();
        ((Activity) context).finish();
    }

}
