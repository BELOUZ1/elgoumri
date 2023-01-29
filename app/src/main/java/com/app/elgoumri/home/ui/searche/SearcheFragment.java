package com.app.elgoumri.home.ui.searche;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;

import com.app.elgoumri.R;
import com.app.elgoumri.adapter.AnnonceAdapter;
import com.app.elgoumri.annonce.AjouterAnnonceActivity;
import com.app.elgoumri.annonce.ViewAnnonceActivity;
import com.app.elgoumri.bean.Annonce;
import com.app.elgoumri.bean.Constants;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mapbox.api.geocoding.v5.models.CarmenFeature;
import com.mapbox.mapboxsdk.plugins.places.autocomplete.PlaceAutocomplete;
import com.mapbox.mapboxsdk.plugins.places.autocomplete.model.PlaceOptions;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SearcheFragment extends Fragment implements View.OnClickListener {

    private static final int ADRESSE_DEP = 1;
    private static final int ADRESSE_ARV = 2;
    private DatePickerDialog datePickerDialog;
    private DatabaseReference databaseAnnonces;
    private EditText dateDepart, dateArrive, adresseDepart, adresseArrive;
    private Button rechercher;
    private Intent placeIntent;
    private List<Annonce> annonces = new ArrayList<>();
    private List<Annonce> annoncesFiltred = new ArrayList<>();
    private AnnonceAdapter adapter;
    private RecyclerView recyclerView;
    private Intent viewAnnonceIntent;
    private int year;
    private int month;
    private int day;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_searche, container, false);
        dateArrive = v.findViewById(R.id.searche_datearv_et);
        dateDepart = v.findViewById(R.id.searche_datedep_et);
        adresseArrive = v.findViewById(R.id.searche_villearv_et);
        adresseDepart = v.findViewById(R.id.searche_villedep_et);
        rechercher = v.findViewById(R.id.searche_searche_btn);
        viewAnnonceIntent = new Intent(getActivity(), ViewAnnonceActivity.class);

        recyclerView = v.findViewById(R.id.searche_listann_rv);


        databaseAnnonces = FirebaseDatabase.getInstance().getReference(Annonce.class.getSimpleName().toLowerCase());


        dateDepart.setOnClickListener(this);
        dateArrive.setOnClickListener(this);
        adresseDepart.setOnClickListener(this);
        adresseArrive.setOnClickListener(this);
        rechercher.setOnClickListener(this);

        setCalendar();
        setMapBox();

        return v;
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(resultCode == getActivity().RESULT_OK){
            CarmenFeature feature = PlaceAutocomplete.getPlace(data);
            if(requestCode == ADRESSE_DEP){
                adresseDepart.setText(feature.text());
            }

            if(requestCode == ADRESSE_ARV){
                adresseArrive.setText(feature.text());
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();

        switch (id){
            case R.id.searche_datedep_et:
                doGetCalendar(dateDepart);
                break;
            case R.id.searche_datearv_et:
                doGetCalendar(dateArrive);
                break;
            case R.id.searche_villedep_et:
                doGetMapBox(ADRESSE_DEP);
                break;
            case R.id.searche_villearv_et:
                doGetMapBox(ADRESSE_ARV);
                break;
            case R.id.searche_searche_btn:
                getAnnonces(this);
                break;
        }

    }


    private void setCalendar(){
        final Calendar calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);
    }

    private void setMapBox(){
        placeIntent = new PlaceAutocomplete.IntentBuilder()
                .accessToken(getString(R.string.mapbox_access_token))
                .placeOptions(PlaceOptions.builder()
                        .backgroundColor(Color.parseColor("#EEEEEE"))
                        .language("fr")
                        .build())
                .build(getActivity());
    }

    private void doGetMapBox(int requestCode){
        startActivityForResult(placeIntent, requestCode);
    }

    private void doGetCalendar(EditText date){
        datePickerDialog = new DatePickerDialog(getActivity(),
                (view, year, monthOfYear, dayOfMonth) -> {
                    date.setText(dayOfMonth + "-"
                            + (monthOfYear + 1) + "-" + year);

                }, year, month, day);
        datePickerDialog.show();
    }

    private void getAnnonces(SearcheFragment fragment) {
        databaseAnnonces.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                annoncesFiltred.clear();
                annonces.clear();
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    Annonce annonce = postSnapshot.getValue(Annonce.class);
                    annonces.add(annonce);
                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    filterList();
                }
                RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
                recyclerView.setLayoutManager(mLayoutManager);
                recyclerView.setItemAnimator(new DefaultItemAnimator());
                adapter = new AnnonceAdapter(annoncesFiltred, fragment, false);
                recyclerView.setAdapter(adapter);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void filterList(){
        String sAdressArv = adresseArrive.getText().toString();
        String sAdressDpr = adresseDepart.getText().toString();
        String sDateArv = dateArrive.getText().toString();
        String sDateDpr = dateDepart.getText().toString();

        SimpleDateFormat sdf =new SimpleDateFormat("dd-MM-yyyy");
        Predicate<Annonce> f1 = a -> sAdressDpr.isEmpty()? true:a.getAdresseDepart().contains(sAdressDpr);
        Predicate<Annonce> f2 = a -> sAdressArv.isEmpty()? true:a.getAdressArrive().contains(sAdressArv);
        Predicate<Annonce> f3 = a -> {
            try {
                return sDateArv.isEmpty()? true : sdf.parse(a.getDateArrive()).compareTo(sdf.parse(sDateArv)) <= 0;
            } catch (ParseException e) {
                e.printStackTrace();
            }
            return true;
        };
        Predicate<Annonce> f4 = a -> {
            try {
                return sDateDpr.isEmpty()? true : sdf.parse(a.getDateDepart()).compareTo(sdf.parse(sDateDpr)) >= 0;
            } catch (ParseException e) {
                e.printStackTrace();
            }
            return true;
        };

        annoncesFiltred = annonces.stream()
                .filter(f1)
                .filter(f2)
                .filter(f3)
                .filter(f4)
                .collect(Collectors.toList());
        annoncesFiltred.size();
    }

    public void viewAnnonce(int position){
        Annonce annonce = annoncesFiltred.get(position);
        viewAnnonceIntent.putExtra(Constants.ANNONCE_KEY, annonce);
        viewAnnonceIntent.putExtra(Constants.EST_MOI_KEY, false);
        getActivity().startActivity(viewAnnonceIntent);
    }
}