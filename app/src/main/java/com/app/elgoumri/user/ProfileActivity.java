package com.app.elgoumri.user;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;

import com.app.elgoumri.R;
import com.app.elgoumri.annonce.ListAnnonceActivity;
import com.app.elgoumri.bean.Constants;
import com.app.elgoumri.bean.SessionManager;
import com.app.elgoumri.chat.UsersActivity;

public class ProfileActivity extends AppCompatActivity implements View.OnClickListener {

    private CardView listAnnonceCV;
    private CardView updateProfileCV;
    private CardView messagesCV;
    private CardView deconnecterCV;
    private Intent listAnnonceIntent;
    private Intent updateProfileIntent;
    private Intent messagesIntent;
    private TextView userTV;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        Toolbar toolbar = findViewById(R.id.profile_tb);
        setSupportActionBar(toolbar);

        sessionManager = new SessionManager(this);

        listAnnonceCV = findViewById(R.id.list_annonce_cv);
        userTV = findViewById(R.id.user_tv);
        userTV.setText(sessionManager.getUserFromSession().getPrenom());
        listAnnonceIntent = new Intent(this, ListAnnonceActivity.class);
        updateProfileCV = findViewById(R.id.update_profile_cv);
        updateProfileIntent = new Intent(this, CompteActivity.class);

        messagesCV = findViewById(R.id.messages_cv);
        messagesIntent = new Intent(this, UsersActivity.class);

        deconnecterCV = findViewById(R.id.deconnecter_cv);

        listAnnonceCV.setOnClickListener(this);
        updateProfileCV.setOnClickListener(this);
        messagesCV.setOnClickListener(this);
        deconnecterCV.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();

        switch (id){
            case R.id.list_annonce_cv:
                listAnnonceIntent.putExtra(Constants.EST_MOI_KEY, true);
                startActivity(listAnnonceIntent);
                break;

            case R.id.update_profile_cv:
                updateProfileIntent.putExtra(Constants.EST_UPDATE_KEY, true);
                startActivity(updateProfileIntent);
                break;
            case R.id.messages_cv:
                startActivity(messagesIntent);
                break;
            case R.id.deconnecter_cv:
                sessionManager.logout();
                break;
            default:
                break;
        }

    }
}