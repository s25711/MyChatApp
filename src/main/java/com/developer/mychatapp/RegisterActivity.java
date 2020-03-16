package com.developer.mychatapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import de.hdodenhof.circleimageview.CircleImageView;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.developer.mychatapp.Models.UserModel;
import com.developer.mychatapp.Utils.GlobalUtills;
import com.developer.mychatapp.Utils.SharedPreference;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {
    public static final int PICK_IMAGE = 1;
    TextView back_txt, login_txt;
    EditText email_edt, password_edt, user_edt;
    Button register_btn;
    ProgressBar progressbar;
    CircleImageView profile_image;
    FloatingActionButton edit_profile_fab;
    FirebaseAuth auth;
    DatabaseReference mDatabase;

    FirebaseStorage storage;
    StorageReference storageRef;
    Uri selectedImage;
    String picture_url;
    EditText phone_number;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        getSupportActionBar().hide();

        auth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference().child("images");

        back_txt = findViewById(R.id.back_txt);
        login_txt = findViewById(R.id.login_txt);
        email_edt = findViewById(R.id.email_edt);
        password_edt = findViewById(R.id.password_edt);
        register_btn = findViewById(R.id.register_btn);
        user_edt = findViewById(R.id.user_edt);
        progressbar = findViewById(R.id.progressbar);
        edit_profile_fab = findViewById(R.id.edit_profile_fab);
        profile_image = findViewById(R.id.profile_image);
        phone_number = findViewById(R.id.phone_number);



        listner();
    }

    private void listner() {
        back_txt.setOnClickListener(this);
        register_btn.setOnClickListener(this);
        login_txt.setOnClickListener(this);
        edit_profile_fab.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.back_txt:
                onBackPressed();
                break;

            case R.id.register_btn:

                boolean isNetworkAvailable = GlobalUtills.getInstance().isNetworkConnected(RegisterActivity.this);
                if (isNetworkAvailable){
                    registerUser();
                }else {
                    GlobalUtills.getInstance().interntDialog(RegisterActivity.this);
                }
                break;

            case R.id.edit_profile_fab:
                openGallary();
                break;

        }
    }

    private void openGallary() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data != null) {
            if (requestCode == PICK_IMAGE) {
                selectedImage = data.getData();
                Bitmap bitmap = null;
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImage);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                profile_image.setImageBitmap(bitmap);
            }
        }
    }
    private void registerUser() {
        final String email = email_edt.getText().toString().trim();
        String password = password_edt.getText().toString().trim();
        final String username = user_edt.getText().toString().trim();
        final String number = phone_number.getText().toString().trim();

        if (selectedImage == null) {
            Toast.makeText(this, "Please choose profile picture!", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(username)) {
            Toast.makeText(getApplicationContext(), "Enter username!", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(email)) {
            Toast.makeText(getApplicationContext(), "Enter email address!", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(password)) {
            Toast.makeText(getApplicationContext(), "Enter password!", Toast.LENGTH_SHORT).show();
        } else if (password.length() < 6) {
            Toast.makeText(getApplicationContext(), "Password too short, enter minimum 6 characters!", Toast.LENGTH_SHORT).show();
        }else if (phone_number.getText().toString().isEmpty()){
            Toast.makeText(getApplicationContext(), "Enter valid phone number!", Toast.LENGTH_SHORT).show();
        }else {
            //create user
            progressbar.setVisibility(View.VISIBLE);
            auth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {

                            if (!task.isSuccessful()) {

                                Toast.makeText(RegisterActivity.this, "Authentication failed." + task.getException(),
                                        Toast.LENGTH_SHORT).show();
                                progressbar.setVisibility(View.GONE);
                            } else {


                                final String id = task.getResult().getUser().getUid();
                                storageRef.child("Profile Images").child("IMG" + System.currentTimeMillis()).putFile(selectedImage).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.e("picture", "==" + e.getLocalizedMessage());

                                    }
                                }).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                                        progressbar.setVisibility(View.GONE);

                                        if (task.isSuccessful()) {
                                            task.getResult().getStorage().getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Uri> task) {
                                                    progressbar.setVisibility(View.GONE);
                                                    picture_url = task.getResult().toString();


                                                    if (!picture_url.isEmpty()) {
                                                        UserModel userModel = new UserModel(id, username, email, picture_url, "online",number);
                                                        mDatabase.child("users").child(id).setValue(userModel);

                                                        SharedPreference.getInstance().saveString(getApplicationContext(), "login_user", id);

                                                        startActivity(new Intent(RegisterActivity.this, HomeActivity.class));
                                                        finishAffinity();
                                                    }

                                                }
                                            });
                                        }
                                    }
                                });

                            }
                        }
                    });
        }
    }
}
