package com.app.elgoumri.annonce;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import com.app.elgoumri.R;
import com.app.elgoumri.bean.Annonce;
import com.app.elgoumri.bean.UserFactory;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.mapbox.api.geocoding.v5.models.CarmenFeature;
import com.mapbox.mapboxsdk.plugins.places.autocomplete.PlaceAutocomplete;
import com.mapbox.mapboxsdk.plugins.places.autocomplete.model.PlaceOptions;

import org.apache.commons.lang3.StringUtils;

import java.util.Calendar;
import java.util.Locale;

public class AjouterAnnonceActivity extends AppCompatActivity implements RadioGroup.OnCheckedChangeListener, View.OnClickListener {

    private EditText titre;
    private EditText description;
    private EditText adresseD;
    private EditText adresseA;
    private String adresseDID;
    private String adresseAID;
    private EditText prix;
    private EditText dateD;
    private EditText dateA;
    private RadioGroup estGratuit;
    private Button valider;
    private Spinner spinner;
    private Spinner deviseSP;
    private LinearLayout layout;
    private boolean gratuit = true;
    private DatePickerDialog datePickerDialog;
    private int year;
    private int month;
    private int day;
    private Annonce mAnnonce;
    private Intent placeIntent;
    private DatabaseReference refAnnonce;
    private static final int ADRESSE_DEP = 1;
    private static final int ADRESSE_ARV = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ajouter_annonce);

        titre = findViewById(R.id.titre_et);
        description = findViewById(R.id.description_et);
        adresseA = findViewById(R.id.adresse_a_et);
        adresseD = findViewById(R.id.adresse_d_et);
        prix = findViewById(R.id.prix_et);
        estGratuit = findViewById(R.id.gratuit_rg);
        valider = findViewById(R.id.valider_annonce_btn);
        dateA = findViewById(R.id.date_a_et);
        dateD = findViewById(R.id.date_d_et);
        layout = findViewById(R.id.prix_ll);

        setMapBox();
        setCategories();
        setDevise();
        setCalendar();

        refAnnonce = FirebaseDatabase.getInstance().getReference(Annonce.class.getSimpleName().toLowerCase());

        layout.setVisibility(View.GONE);

        setEvents();

    }

    @Override
    public void onCheckedChanged(RadioGroup radioGroup, int i) {
        int id = radioGroup.getCheckedRadioButtonId();

        if(id == R.id.gratuit_g_rb){
            gratuit = true;
            layout.setVisibility(View.GONE);
        }else{
            gratuit = false;
            layout.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();

        if(id == R.id.date_d_et){
            doGetCalendar(dateD);
        }

        if(id == R.id.date_a_et){
            doGetCalendar(dateA);
        }

        if(id == R.id.valider_annonce_btn && validerAnnonce()){
            String idAnnonce = refAnnonce.push().getKey();
            mAnnonce.setId(idAnnonce);
            refAnnonce.child(idAnnonce).setValue(mAnnonce)
                    .addOnFailureListener(e -> Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show()
                    ).addOnSuccessListener(unused -> {
                        finish();
                    });

        }

        if(id == R.id.adresse_d_et){
            doGetMapBox(ADRESSE_DEP);
        }

        if(id == R.id.adresse_a_et){
            doGetMapBox(ADRESSE_ARV);
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        if(resultCode == RESULT_OK){
            CarmenFeature feature = PlaceAutocomplete.getPlace(data);
            if(requestCode == ADRESSE_DEP){
                adresseD.setText(feature.text());
                adresseDID = feature.id();
            }

            if(requestCode == ADRESSE_ARV){
                adresseA.setText(feature.text());
                adresseAID = feature.id();
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }


    private void setEvents(){
        estGratuit.setOnCheckedChangeListener(this);
        valider.setOnClickListener(this);
        dateD.setOnClickListener(this);
        dateA.setOnClickListener(this);
        adresseA.setOnClickListener(this);
        adresseD.setOnClickListener(this);
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
                .build(this);
    }
    private void setCategories(){
        spinner = findViewById(R.id.categories_sp);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.categories, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
    }

    private void setDevise(){
        deviseSP = findViewById(R.id.devise_sp);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.devise_items, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        deviseSP.setAdapter(adapter);
    }

    private void doGetMapBox(int requestCode){
        startActivityForResult(placeIntent, requestCode);
    }

    private boolean validerAnnonce(){

        mAnnonce = new Annonce();
        if(StringUtils.isBlank(titre.getText())){
            Toast.makeText(getApplicationContext(), "Le titre est obligatoir", Toast.LENGTH_LONG).show();
            return false;
        }
        mAnnonce.setTitre(titre.getText().toString().trim());

        if(StringUtils.isBlank(description.getText())){
            Toast.makeText(getApplicationContext(), "La description est obligatoire", Toast.LENGTH_LONG).show();
            return false;
        }
        mAnnonce.setDescription(description.getText().toString().trim());

        if(StringUtils.isBlank(adresseD.getText())){
            Toast.makeText(getApplicationContext(), "L'adresse de départ est obligatoire", Toast.LENGTH_LONG).show();
            return false;
        }
        mAnnonce.setAdresseDepart(adresseD.getText().toString().trim());

        if(StringUtils.isBlank(adresseA.getText())){
            Toast.makeText(getApplicationContext(), "L'adresse d'arrivée est obligatoire", Toast.LENGTH_LONG).show();
            return false;
        }
        mAnnonce.setAdressArrive(adresseA.getText().toString().trim());

        if(StringUtils.isBlank(dateD.getText())){
            Toast.makeText(getApplicationContext(), "La date de départ est obligatoire", Toast.LENGTH_LONG).show();
            return false;
        }
        mAnnonce.setDateDepart(dateD.getText().toString().trim());

        if(StringUtils.isBlank(dateA.getText())){
            Toast.makeText(getApplicationContext(), "La date d'arrivée est obligatoire", Toast.LENGTH_LONG).show();
            return false;
        }
        mAnnonce.setDateArrive(dateA.getText().toString().trim());

        mAnnonce.setGratuit(gratuit);

        if(!gratuit){
            if(StringUtils.isBlank(prix.getText())){
                Toast.makeText(getApplicationContext(), "Le prix est obligatoire", Toast.LENGTH_LONG).show();
                return false;
            }

            float fPrix = Float.parseFloat(prix.getText().toString());

            if(fPrix <= 0){
                Toast.makeText(getApplicationContext(), "Le prix n'est pas valide", Toast.LENGTH_LONG).show();
                return false;
            }
            mAnnonce.setPrix(fPrix);
        }

        mAnnonce.setCategorie(spinner.getSelectedItem().toString());
        mAnnonce.setUtilisateur(UserFactory.getUtilisateur());
        mAnnonce.setDevise(deviseSP.getSelectedItem().toString());
        mAnnonce.setAdressArriveID(adresseAID);
        mAnnonce.setAdresseDepartID(adresseDID);
        
        return true;
    }

    private void doGetCalendar(EditText date){
        datePickerDialog = new DatePickerDialog(AjouterAnnonceActivity.this,
                (view, year, monthOfYear, dayOfMonth) -> {
                    date.setText(dayOfMonth + "-"
                            + (monthOfYear + 1) + "-" + year);

                }, year, month, day);
        datePickerDialog.show();
    }
}