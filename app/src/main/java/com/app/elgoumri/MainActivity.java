package com.app.elgoumri;

import android.content.Intent;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.app.elgoumri.home.ElGoumriActivity;

public class MainActivity extends AppCompatActivity {

    private Intent homeIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TextView elgoumri = findViewById(R.id.elgoumri_tv);
        ImageView logo = findViewById(R.id.elgoumri_img);
        homeIntent = new Intent(this, ElGoumriActivity.class);

        Animation animation = AnimationUtils.loadAnimation(this,R.anim.animation);

        elgoumri.startAnimation(animation);
        logo.startAnimation(animation);

        Thread timer = new Thread(){
            public void run(){
                try {
                    sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }finally {
                    startActivity(homeIntent);
                    finish();
                }
            }
        };

        timer.start();
    }
}