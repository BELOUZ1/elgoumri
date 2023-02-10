package com.app.elgoumri.user;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.app.elgoumri.R;
import com.app.elgoumri.bean.SessionManager;
import com.app.elgoumri.bean.Utilisateur;
import com.app.elgoumri.utils.AlertDialogUtils;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseNetworkException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.apache.commons.lang3.StringUtils;

public class ConnexionActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView nvCompteTV;
    private Intent nvCompteIntent;
    private Intent profileIntent;
    private Button connectionBTN;
    private EditText emailET;
    private EditText motDePasseET;
    private TextView motDePasseOublie;
    private String sEmail;
    private String sMotDePasse;

    private FirebaseAuth mAuth;
    private DatabaseReference firebaseUser;
    private SessionManager sessionManager;

    private ProgressDialog progressDoalog ;

    private AlertDialogUtils alertDialogUtils;

    private AlertDialogUtils confirmationDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connexion);

        firebaseUser = FirebaseDatabase.getInstance().getReference(Utilisateur.class.getSimpleName().toLowerCase());
        mAuth = FirebaseAuth.getInstance();

        progressDoalog = new ProgressDialog(ConnexionActivity.this);
        progressDoalog.setTitle("Connexion");
        progressDoalog.setMessage("Connexion en cours...");

        alertDialogUtils = new AlertDialogUtils(this);
        confirmationDialog = new AlertDialogUtils(this);
        sessionManager = new SessionManager(this);

        nvCompteIntent = new Intent(this, CompteActivity.class);
        profileIntent = new Intent(this, ProfileActivity.class);

        nvCompteTV = findViewById(R.id.cnx_nvcompte_tv);
        connectionBTN = findViewById(R.id.cnx_cnx_btn);
        emailET = findViewById(R.id.cnx_email_et);
        motDePasseET = findViewById(R.id.cnx_mdp_et);
        motDePasseOublie = findViewById(R.id.cnx_mdp_forg_tv);

        connectionBTN.setOnClickListener(this);
        nvCompteTV.setOnClickListener(this);
        motDePasseOublie.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();

        if(id == R.id.cnx_nvcompte_tv){
            startActivity(nvCompteIntent);
        }

        if(id == R.id.cnx_cnx_btn){
            login();
        }

        if(id == R.id.cnx_mdp_forg_tv){
            resetPassword();
        }
    }

    private void beginRecovery(String email){
        progressDoalog.show();
        mAuth.sendPasswordResetEmail(email).addOnCompleteListener(task -> {
            progressDoalog.dismiss();
            if(task.isSuccessful())
            {
                alertDialogUtils.showAlert("Info !!", "Email envoyé à l'adresse " + email);
            } else {
                if(task.getException() instanceof FirebaseAuthInvalidUserException){
                    alertDialogUtils.showAlert("Erreur !!", "Email " + email + " n'existe pas");
                }else if(task.getException() instanceof FirebaseNetworkException){
                    alertDialogUtils.showAlert("Oops !!", "Veuillez vérifier votre connexion internet");
                }else{
                    alertDialogUtils.showAlert("Erreur !!", task.getException().getMessage());
                }

            }
        });
    }

    private void resetPassword(){
        AlertDialog.Builder builder = confirmationDialog.getBuilder();
        LinearLayout linearLayout=new LinearLayout(this);
        final EditText emailet= new EditText(this);
        emailet.setHint("Email");
        emailet.setMinEms(16);
        emailet.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
        linearLayout.addView(emailet);
        linearLayout.setPadding(10,10,10,10);
        builder.setView(linearLayout);

        builder.setPositiveButton("OK", (dialog, which) -> {
            String email=emailet.getText().toString().trim();
            beginRecovery(email);
            dialog.dismiss();
        });

        builder.setNegativeButton("Annuler", (dialog, which) -> dialog.dismiss());

        confirmationDialog.showAlert("Info", "Réinitialiser mot de passe");
    }

    private boolean validateEmailPassword(){

        sEmail = emailET.getText().toString().trim();
        sMotDePasse = motDePasseET.getText().toString().trim();

        if(StringUtils.isBlank(sEmail)){
            Toast.makeText(getApplicationContext(),"L'email est obligatoire", Toast.LENGTH_LONG).show();
            return false;
        }

        if(StringUtils.isBlank(sMotDePasse)){
            Toast.makeText(getApplicationContext(),"Le mot de passe est obligatoire",Toast.LENGTH_LONG).show();
            return false;
        }

        return true;
    }

    private void login(){

        if(validateEmailPassword()){
            progressDoalog.show();
            mAuth.signInWithEmailAndPassword(sEmail, sMotDePasse)
                    .addOnCompleteListener(this, task -> {
                        if (task.isSuccessful()) {
                            getUser();
                        } else {
                            progressDoalog.dismiss();
                            showException(task);
                        }
                    });
        }
    }

    private void getUser(){
        String idUser = mAuth.getCurrentUser().getUid();
        firebaseUser.child(idUser).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Utilisateur utilisateur = dataSnapshot.getValue(Utilisateur.class);
                sessionManager.createSession(utilisateur);
                progressDoalog.dismiss();
                startActivity(profileIntent);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                progressDoalog.dismiss();
                alertDialogUtils.showAlert("Erreur connexion !!", databaseError.getMessage());
            }
        });
    }

    private void showException(Task<AuthResult> task){
        Exception e = task.getException();
        if(e instanceof FirebaseAuthInvalidCredentialsException || e instanceof FirebaseAuthInvalidUserException){
            alertDialogUtils.showAlert("Oops !!", "Email ou mot de passe incorrecte");
        }else if(e instanceof FirebaseNetworkException){
            alertDialogUtils.showAlert("Oops !!", "Veuillez vérifier votre connexion internet");
        }else{
            alertDialogUtils.showAlert("Oops !!", task.getException().getMessage());
        }
    }
}