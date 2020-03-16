package com.developer.mychatapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;

import com.developer.mychatapp.Utils.SharedPreference;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                String login = SharedPreference.getInstance().getString(getApplicationContext(),"login_user");

                if (!login.isEmpty()){
                    startActivity(new Intent(getApplicationContext(),HomeActivity.class));
                    finish();
                }else{
                    startActivity(new Intent(getApplicationContext(),LoginActivity.class));
                    finish();
                }



            }
        },4000);
    }
}
