package com.app.elgoumri.user;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.app.elgoumri.R;
import com.app.elgoumri.annonce.ListAnnonceActivity;
import com.app.elgoumri.bean.Constants;
import com.app.elgoumri.bean.UserFactory;

public class ProfileActivity extends AppCompatActivity implements View.OnClickListener {

    private CardView listAnnonceCV;
    private CardView updateProfileCV;
    private Intent listAnnonceIntent;
    private Intent updateProfileIntent;
    private TextView userTV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        Toolbar toolbar = findViewById(R.id.profile_tb);
        setSupportActionBar(toolbar);

        listAnnonceCV = findViewById(R.id.list_annonce_cv);
        userTV = findViewById(R.id.user_tv);
        userTV.setText(UserFactory.getUtilisateur().getPrenom());
        listAnnonceIntent = new Intent(this, ListAnnonceActivity.class);
        updateProfileCV = findViewById(R.id.update_profile_cv);
        updateProfileIntent = new Intent(this, CompteActivity.class);

        listAnnonceCV.setOnClickListener(this);
        updateProfileCV.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();

        if(id == R.id.list_annonce_cv){
            listAnnonceIntent.putExtra(Constants.EST_MOI_KEY, true);
            startActivity(listAnnonceIntent);
        }

        if(id == R.id.update_profile_cv){
            updateProfileIntent.putExtra(Constants.EST_UPDATE_KEY, true);
            startActivity(updateProfileIntent);
        }
    }
}