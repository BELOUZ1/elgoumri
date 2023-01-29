package com.app.elgoumri.user;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.telephony.PhoneNumberUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.app.elgoumri.R;
import com.app.elgoumri.bean.Constants;
import com.app.elgoumri.bean.UserFactory;
import com.app.elgoumri.bean.Utilisateur;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;
import com.google.i18n.phonenumbers.Phonenumber.PhoneNumber.CountryCodeSource;

import org.apache.commons.lang3.StringUtils;

import java.util.Locale;

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
        progressDoalog.setTitle("Création du compte");
        progressDoalog.setMessage("Création du compte en cours...");

        mAuth = FirebaseAuth.getInstance();
        firebaseUser =  FirebaseDatabase.getInstance().getReference(Utilisateur.class.getSimpleName().toLowerCase());

        phoneNumberUtil = PhoneNumberUtil.getInstance();

        if(update){
            nomET.setText(UserFactory.getUtilisateur().getNom());
            prenomET.setText(UserFactory.getUtilisateur().getPrenom());
            telephoneET.setText(UserFactory.getUtilisateur().getTelephone());
            emailET.setText(UserFactory.getUtilisateur().getEmail());
            motDePasseET.setText(UserFactory.getUtilisateur().getPasswor());
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
            Toast.makeText(getApplicationContext(), "Le prénom est obligatoire", Toast.LENGTH_LONG).show();
            return false;
        }

        if(StringUtils.isBlank(sTelephone)){
            Toast.makeText(getApplicationContext(), "Le téléphone est obligatoire", Toast.LENGTH_LONG).show();
            return false;
        }

        if(!checkPhoneNumber()){
            Toast.makeText(getApplicationContext(), "Numéro de téléphone n'est pas valide", Toast.LENGTH_LONG).show();
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
                .setPasswor(sMotDePasse);

        return utilisateur;
    }

    private boolean checkPhoneNumber(){
        try {
            Phonenumber.PhoneNumber phone = phoneNumberUtil.parse(sTelephone, Locale.getDefault().getCountry());
            String internationalFormat = phoneNumberUtil.format(phone, PhoneNumberUtil.PhoneNumberFormat.INTERNATIONAL);
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

}