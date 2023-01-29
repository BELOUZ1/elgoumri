package com.app.elgoumri.annonce;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.app.elgoumri.R;
import com.app.elgoumri.adapter.AnnonceAdapter;
import com.app.elgoumri.bean.Annonce;
import com.app.elgoumri.bean.Constants;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ListAnnonceActivity extends AppCompatActivity implements View.OnClickListener {

    private Intent ajouterAnnonceIntent;
    private FloatingActionButton ajouterAnnonceFAB;
    private DatabaseReference databaseAnnonces;
    private List<Annonce> annonces;
    private AnnonceAdapter adapter;
    private RecyclerView recyclerView;
    private Intent viewAnnonceIntent;
    private boolean estMoi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_annonce);

        estMoi = getIntent().getBooleanExtra(Constants.EST_MOI_KEY,false);

        annonces = new ArrayList();
        ajouterAnnonceFAB = findViewById(R.id.add_annonce_fb);
        ajouterAnnonceIntent = new Intent(this, AjouterAnnonceActivity.class);
        viewAnnonceIntent = new Intent(this, ViewAnnonceActivity.class);
        databaseAnnonces = FirebaseDatabase.getInstance().getReference(Annonce.class.getSimpleName().toLowerCase());
        recyclerView = findViewById(R.id.list_annonce_rv);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        adapter = new AnnonceAdapter(annonces, this, estMoi);
        recyclerView.setAdapter(adapter);
        getAnnonces();
        ajouterAnnonceFAB.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if(id == R.id.add_annonce_fb){
            startActivity(ajouterAnnonceIntent);
        }
    }

    public void deleteAnnonce(int position){
        DatabaseReference databaseReference = databaseAnnonces.child(annonces.get(position).getId());
        databaseReference.removeValue();
        annonces.remove(position);
        adapter.notifyDataSetChanged();
    }

    public void viewAnnonce(int position){
        Annonce annonce = annonces.get(position);
        viewAnnonceIntent.putExtra(Constants.ANNONCE_KEY, annonce);
        viewAnnonceIntent.putExtra(Constants.EST_MOI_KEY, estMoi);
        startActivity(viewAnnonceIntent);
    }

    private void getAnnonces() {
        databaseAnnonces.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                annonces.clear();
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    Annonce annonce = postSnapshot.getValue(Annonce.class);
                    annonces.add(annonce);
                }
                adapter.notifyDataSetChanged();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

}