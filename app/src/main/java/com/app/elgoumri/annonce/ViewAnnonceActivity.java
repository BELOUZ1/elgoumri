package com.app.elgoumri.annonce;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.app.elgoumri.R;
import com.app.elgoumri.bean.Annonce;
import com.app.elgoumri.bean.Constants;

public class ViewAnnonceActivity extends AppCompatActivity {

    private TextView titre, prix, description, depart, arrive, user, telephone;
    private Button contacter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_annonce);

        Intent intent = getIntent();
        Annonce annonce = (Annonce) intent.getSerializableExtra(Constants.ANNONCE_KEY);
        boolean estMoi = intent.getBooleanExtra(Constants.EST_MOI_KEY, false);
        titre = findViewById(R.id.titre_view_tv);
        prix = findViewById(R.id.prix_view_tv);
        description = findViewById(R.id.desc_view_tv);
        depart = findViewById(R.id.depart_view_tv);
        arrive = findViewById(R.id.arrive_view_tv);
        user = findViewById(R.id.user_view_tv);
        telephone = findViewById(R.id.tel_view_tv);
        contacter = findViewById(R.id.contacter_view_btn);

        titre.setText(annonce.getTitre() + " - " + annonce.getCategorie());
        if(annonce.isGratuit()){
            prix.setText("Gratuit");
        }else{
            prix.setText(annonce.getPrix() + " " + annonce.getDevise());
        }
        description.setText(annonce.getDescription());
        String sDepart = "Départ de " + annonce.getAdresseDepart() + " le " + annonce.getDateDepart();
        depart.setText(sDepart);
        String sArrive = "Arrivée à " + annonce.getAdressArrive() + " le " + annonce.getDateArrive();
        arrive.setText(sArrive);
        user.setText(annonce.getUtilisateur().getPrenom() + " " + annonce.getUtilisateur().getNom());
        telephone.setText(annonce.getUtilisateur().getTelephone());
        if(estMoi){
            contacter.setVisibility(View.GONE);
        }else{
            contacter.setVisibility(View.VISIBLE);
        }

    }
}