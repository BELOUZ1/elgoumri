package com.app.elgoumri.home.ui.home;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.elgoumri.R;
import com.app.elgoumri.adapter.AnnonceAdapter;
import com.app.elgoumri.annonce.ViewAnnonceActivity;
import com.app.elgoumri.bean.Annonce;
import com.app.elgoumri.bean.Constants;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    private RecyclerView recyclerView;
    private DatabaseReference databaseAnnonces;
    private List<Annonce> annonces = new ArrayList<>();
    private AnnonceAdapter adapter;
    private Intent viewAnnonceIntent;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_home, container, false);
        databaseAnnonces = FirebaseDatabase.getInstance().getReference(Annonce.class.getSimpleName().toLowerCase());
        viewAnnonceIntent = new Intent(getActivity(), ViewAnnonceActivity.class);
        adapter = new AnnonceAdapter(annonces, this, false);
        recyclerView = v.findViewById(R.id.home_annonces_rv);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);
        getAnnonces();

        return v;
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

    public void viewAnnonce(int position){
        Annonce annonce = annonces.get(position);
        viewAnnonceIntent.putExtra(Constants.ANNONCE_KEY, annonce);
        viewAnnonceIntent.putExtra(Constants.EST_MOI_KEY, false);
        getActivity().startActivity(viewAnnonceIntent);
    }
}