package com.developer.mychatapp;

import android.Manifest;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.developer.mychatapp.Utils.GlobalUtills;
import com.developer.mychatapp.Utils.SharedPreference;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GetTokenResult;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.DialogOnAnyDeniedMultiplePermissionsListener;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    boolean doubleBackToExitPressedOnce = false;
    private FirebaseAuth auth;

    EditText email_edt,password_edt;
    Button login_btn;
    TextView register_txt,reset_txt;
    ProgressBar progressbar;

    DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getSupportActionBar().hide();


        //Dexter runtime permisions
        Dexter.withActivity(this)
                .withPermissions(
                        Manifest.permission.CAMERA,
                        Manifest.permission.READ_CONTACTS
                        /*Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.ACCESS_FINE_LOCATION*/
                ).withListener(new MultiplePermissionsListener() {

            @Override public void onPermissionsChecked(MultiplePermissionsReport report) {

            }
            @Override public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {

                token.continuePermissionRequest();

               }
        }).check();


        auth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();


        email_edt = findViewById(R.id.email_edt);
        password_edt = findViewById(R.id.password_edt);
        login_btn = findViewById(R.id.login_btn);
        register_txt = findViewById(R.id.register_txt);
        reset_txt =findViewById(R.id.reset_txt);
        progressbar = findViewById(R.id.progressbar);



        listner();

    }

    private void listner(){
        login_btn.setOnClickListener(this);
        register_txt.setOnClickListener(this);
        reset_txt.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.login_btn:
                boolean isNetworkAvailable = GlobalUtills.getInstance().isNetworkConnected(LoginActivity.this);
                if (isNetworkAvailable){
                    loginUser();
                }else {
                        GlobalUtills.getInstance().interntDialog(LoginActivity.this);
                }

                break;

            case R.id.register_txt:
                startActivity(new Intent(this,RegisterActivity.class));
                break;

            case R.id.reset_txt:
                resetUserPasswordActivity();
                break;
        }
    }

    private void resetUserPasswordActivity(){
        startActivity(new Intent(LoginActivity.this,ResetPasswordActivity.class));
    }
    private void loginUser() {

        String email = email_edt.getText().toString();
        final String password = password_edt.getText().toString();


        if (TextUtils.isEmpty(email)) {
            Toast.makeText(getApplicationContext(), "Enter email address!", Toast.LENGTH_SHORT).show();

        } else if (TextUtils.isEmpty(password)) {
            Toast.makeText(getApplicationContext(), "Enter password!", Toast.LENGTH_SHORT).show();

        } else {

            //authenticate user
            progressbar.setVisibility(View.VISIBLE);
            auth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull final Task<AuthResult> task) {

                            progressbar.setVisibility(View.GONE);
                            if (!task.isSuccessful()) {
                                // there was an error
                                if (password.length() < 6) {
                                    password_edt.setError("Password too short, enter minimum 6 characters");
                                } else {
                                    Toast.makeText(LoginActivity.this, "Authentication failed, check your email and password or sign up", Toast.LENGTH_LONG).show();
                                }
                            } else {

                                final String id =  task.getResult().getUser().getUid();
                                FirebaseAuth mAuth = FirebaseAuth.getInstance();
                                mAuth.getCurrentUser().getIdToken(true).addOnSuccessListener(new OnSuccessListener<GetTokenResult>() {
                                    @Override
                                    public void onSuccess(GetTokenResult getTokenResult) {

                                        mDatabase.child("users").child(id).child("Token").setValue(getTokenResult.getToken());
                                    }
                                });

                                SharedPreference.getInstance().saveString(getApplicationContext(), "login_user", task.getResult().getUser().getUid());

                                Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                                startActivity(intent);
                                finish();
                            }
                        }
                    });

        }
    }

    @Override
    public void onBackPressed() {

        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }

        this.doubleBackToExitPressedOnce = true;

        Toast.makeText(this, "Please click back again to exit", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {

                doubleBackToExitPressedOnce=false;

            }
        }, 2000);
    }



}
