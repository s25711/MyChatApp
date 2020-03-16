package com.developer.mychatapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.developer.mychatapp.Utils.GlobalUtills;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ResetPasswordActivity extends AppCompatActivity implements View.OnClickListener {


    Button btn_reset_password, btn_back;
    EditText email_edt;
    private FirebaseAuth auth;
    ProgressBar progressbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);
        getSupportActionBar().hide();
        auth = FirebaseAuth.getInstance();

        email_edt = findViewById(R.id.email_edt);
        btn_reset_password = findViewById(R.id.btn_reset_password);
        btn_back = findViewById(R.id.btn_back);
        progressbar = findViewById(R.id.progressbar);

        listner();
    }

    private void listner() {
        btn_reset_password.setOnClickListener(this);
        btn_back.setOnClickListener(this);
    }

    private void resetPasseord() {

        String email = email_edt.getText().toString().trim();

        if (TextUtils.isEmpty(email)) {
            Toast.makeText(getApplication(), "Enter your registered email id", Toast.LENGTH_SHORT).show();
        } else {
            progressbar.setVisibility(View.VISIBLE);
            auth.sendPasswordResetEmail(email)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            progressbar.setVisibility(View.GONE);
                            if (task.isSuccessful()) {
                                Toast.makeText(ResetPasswordActivity.this, "We have sent you instructions to reset your password!", Toast.LENGTH_SHORT).show();

                                startActivity(new Intent(ResetPasswordActivity.this, LoginActivity.class));
                                finish();
                            } else {
                                Toast.makeText(ResetPasswordActivity.this, "Failed to send reset email!", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_reset_password:
                boolean isNetworkAvailable = GlobalUtills.getInstance().isNetworkConnected(ResetPasswordActivity.this);
                if (isNetworkAvailable) {
                    resetPasseord();
                } else {
                    GlobalUtills.getInstance().interntDialog(ResetPasswordActivity.this);
                }
                break;

            case R.id.btn_back:
                onBackPressed();
                break;
        }
    }
}
