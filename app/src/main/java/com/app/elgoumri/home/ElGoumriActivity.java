package com.app.elgoumri.home;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.app.elgoumri.R;
import com.app.elgoumri.bean.SessionManager;
import com.app.elgoumri.databinding.ActivityElGoumriBinding;
import com.app.elgoumri.user.ConnexionActivity;
import com.app.elgoumri.user.ProfileActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class ElGoumriActivity extends AppCompatActivity implements MenuItem.OnMenuItemClickListener {

    private ActivityElGoumriBinding binding;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityElGoumriBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(binding.toolbar);

        sessionManager = new SessionManager(this);

        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_searche)
                .build();

        MenuItem item = binding.navView.getMenu().findItem(R.id.navigation_compte);
        item.setOnMenuItemClickListener(this);


        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_el_goumri);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(binding.navView, navController);

    }

    @Override
    public boolean onMenuItemClick(MenuItem menuItem) {
        int id = menuItem.getItemId();
        if(id == R.id.navigation_compte){
            goToActivity();
        }
        return true;
    }

    private void goToActivity(){

        Intent intent;

        if(sessionManager.isLoggin()){
            intent = new Intent(this, ProfileActivity.class);
        }else{
            intent = new Intent(this, ConnexionActivity.class);
        }

        startActivity(intent);
    }
}