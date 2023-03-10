package com.app.elgoumri.user;

import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.app.elgoumri.R;
import com.app.elgoumri.bean.Constants;
import com.app.elgoumri.bean.SessionManager;
import com.app.elgoumri.bean.Utilisateur;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;

import org.apache.commons.lang3.StringUtils;

import java.util.Locale;
import java.util.Random;

public class CompteActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText nomET;
    private EditText prenomET;
    private EditText telephoneET;
    private EditText emailET;
    private EditText motDePasseET;
    private Button validerBTN;

    private FirebaseAuth mAuth;
    private DatabaseReference firebaseUser;

    private ProgressDialog progressDoalog ;

    private PhoneNumberUtil phoneNumberUtil;
    private SessionManager sessionManager;

    private String sNom;
    private String sPrenom;
    private String sTelephone;
    private String sEmail;
    private String sMotDePasse;
    private boolean estUpdate;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compte);
        estUpdate = getIntent().getBooleanExtra(Constants.EST_UPDATE_KEY, false);
        init(estUpdate);
    }

    private void init(boolean update){
        nomET = findViewById(R.id.nom_et);
        prenomET = findViewById(R.id.prenom_et);
        telephoneET = findViewById(R.id.tel_et);
        emailET = findViewById(R.id.email_et);
        motDePasseET = findViewById(R.id.mdp_et);
        validerBTN = findViewById(R.id.valider_btn);

        progressDoalog = new ProgressDialog(CompteActivity.this);
        progressDoalog.setTitle("Cr??ation du compte");
        progressDoalog.setMessage("Cr??ation du compte en cours...");

        mAuth = FirebaseAuth.getInstance();
        firebaseUser =  FirebaseDatabase.getInstance().getReference(Utilisateur.class.getSimpleName().toLowerCase());
        sessionManager = new SessionManager(this);
        phoneNumberUtil = PhoneNumberUtil.getInstance();

        if(update){
            Utilisateur utilisateur = sessionManager.getUserFromSession();
            nomET.setText(utilisateur.getNom());
            prenomET.setText(utilisateur.getPrenom());
            telephoneET.setText(utilisateur.getTelephone());
            emailET.setText(utilisateur.getEmail());
            motDePasseET.setText(utilisateur.getPasswor());
            emailET.setEnabled(false);
            motDePasseET.setEnabled(false);
        }

        validerBTN.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {

        int id = view.getId();

        if(id == R.id.valider_btn && estUpdate){
            if(valideFields()){
                updateUser();
            }

        }

        if(id == R.id.valider_btn && !estUpdate){
            if(valideFields()){
                createUser();
            }

        }

    }

    private boolean valideFields(){

        sNom = nomET.getText().toString().trim();
        sPrenom = prenomET.getText().toString().trim();
        sTelephone = telephoneET.getText().toString();
        sEmail = emailET.getText().toString().trim();
        sMotDePasse = motDePasseET.getText().toString();

        if(StringUtils.isBlank(sNom)){
            Toast.makeText(getApplicationContext(), "Le nom est obligatoire", Toast.LENGTH_LONG).show();
            return false;
        }

        if(StringUtils.isBlank(sPrenom)){
            Toast.makeText(getApplicationContext(), "Le pr??nom est obligatoire", Toast.LENGTH_LONG).show();
            return false;
        }

        if(StringUtils.isBlank(sTelephone)){
            Toast.makeText(getApplicationContext(), "Le t??l??phone est obligatoire", Toast.LENGTH_LONG).show();
            return false;
        }

        if(!checkPhoneNumber()){
            Toast.makeText(getApplicationContext(), "Num??ro de t??l??phone n'est pas valide", Toast.LENGTH_LONG).show();
            return false;
        }

        if(!estUpdate){
            if(StringUtils.isBlank(sEmail)){
                Toast.makeText(getApplicationContext(), "L'email est obligatoire", Toast.LENGTH_LONG).show();
                return false;
            }

            if(!checkEmail()){
                Toast.makeText(getApplicationContext(), "Email n'est pas valide", Toast.LENGTH_LONG).show();
                return false;
            }

            if(StringUtils.isBlank(sMotDePasse)){
                Toast.makeText(getApplicationContext(), "Le mot de passe est obligatoire", Toast.LENGTH_LONG).show();
                return false;
            }
        }

        return true;
    }

    private void createUser(){
        Utilisateur utilisateur = setUtilisateur();
        progressDoalog.show();
        mAuth.createUserWithEmailAndPassword(sEmail,sMotDePasse)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        String idUser = mAuth.getUid();
                        utilisateur.setId(idUser);
                        firebaseUser.child(idUser).setValue(utilisateur);
                        progressDoalog.dismiss();
                        finish();
                    } else {
                        progressDoalog.dismiss();
                        Toast.makeText(getApplicationContext(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void updateUser(){
        progressDoalog.show();
        Utilisateur utilisateur = setUtilisateur();
        firebaseUser.child(utilisateur.getId()).setValue(utilisateur);
        progressDoalog.dismiss();
        finish();
    }

    private Utilisateur setUtilisateur(){

        Phonenumber.PhoneNumber phone;
        String internationalFormat;
        try {
            phone = phoneNumberUtil.parse(sTelephone, Locale.getDefault().getCountry());
            internationalFormat = phoneNumberUtil.format(phone, PhoneNumberUtil.PhoneNumberFormat.INTERNATIONAL);
        } catch (NumberParseException e) {
            internationalFormat = sTelephone;
        }

        Utilisateur utilisateur = new Utilisateur();
        utilisateur
                .setNom(sNom)
                .setPrenom(sPrenom)
                .setEmail(sEmail)
                .setTelephone(internationalFormat)
                .setPasswor(sMotDePasse)
                .setColorUser(getRandomColor());

        return utilisateur;
    }

    private boolean checkPhoneNumber(){
        try {
            Phonenumber.PhoneNumber phone = phoneNumberUtil.parse(sTelephone, Locale.getDefault().getCountry());
            return phoneNumberUtil.isValidNumber(phone);
        } catch (NumberParseException e) {
            return false;
        }

    }

    private boolean checkEmail(){
        if(Patterns.EMAIL_ADDRESS.matcher(sEmail).matches()){
            return true;
        }
        return false;
    }

    public int getRandomColor(){
        Random rnd = new Random();
        return Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));
    }


}