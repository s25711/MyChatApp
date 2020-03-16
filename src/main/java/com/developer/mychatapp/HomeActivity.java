package com.developer.mychatapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import com.developer.mychatapp.Adapters.UserAdapter;
import com.developer.mychatapp.Models.SingleUserModel;
import com.developer.mychatapp.Utils.GlobalUtills;
import com.developer.mychatapp.Utils.SharedPreference;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends AppCompatActivity {

    FirebaseAuth mAuth;
    FirebaseDatabase mFirebaseDatabase;
    DatabaseReference databaseReference;
    DatabaseReference mDatabaseMessage;
    DatabaseReference messagesDatabaseReference;
    List<SingleUserModel> list = new ArrayList<>();
    boolean doubleBackToExitPressedOnce = false;
    RecyclerView user_list;
    UserAdapter mAdapter;
    TextView no_user_txt, logout_txt;
    SwipeRefreshLayout swipeToRefresh;
    FloatingActionButton contacts_fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        getSupportActionBar().hide();
        user_list = findViewById(R.id.user_list);
        no_user_txt = findViewById(R.id.no_user_txt);
        logout_txt = findViewById(R.id.logout_txt);
        swipeToRefresh = findViewById(R.id.swipeToRefresh);
        contacts_fab = findViewById(R.id.contacts_fab);

        mAuth = FirebaseAuth.getInstance();
        mDatabaseMessage = FirebaseDatabase.getInstance().getReference();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = mFirebaseDatabase.getReference().child("users");
        messagesDatabaseReference = mFirebaseDatabase.getReference().child("messages");

        // refresh the users list
        swipeToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

              boolean isNetworkAvailable = GlobalUtills.getInstance().isNetworkConnected(getApplicationContext());

              if (isNetworkAvailable){
                  getUserList();
              }else{
                  GlobalUtills.getInstance().interntDialog(HomeActivity.this);
                  swipeToRefresh.setRefreshing(false);
              }


            }
        });

        // logout user
        logout_txt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean isNetworkAvailable = GlobalUtills.getInstance().isNetworkConnected(getApplicationContext());
                if (isNetworkAvailable) {
                    SharedPreference.getInstance().clearData(HomeActivity.this);
                    startActivity(new Intent(HomeActivity.this, LoginActivity.class));
                    finish();

                }else{
                    GlobalUtills.getInstance().interntDialog(HomeActivity.this);
                    swipeToRefresh.setRefreshing(false);
                }
            }
        });


        contacts_fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startActivity(new Intent(HomeActivity.this,AllContactsActivity.class));

            }
        });

    }
    @Override
    protected void onResume() {
        super.onResume();
        Log.d("myStatus : onR ", "online");

        boolean isNetworkAvailable = GlobalUtills.getInstance().isNetworkConnected(getApplicationContext());

        if (isNetworkAvailable){
            getUserList();
            statusUpdate(FirebaseAuth.getInstance().getCurrentUser().getUid(), "online");
            getFirebaseToken();

        }else{
            GlobalUtills.getInstance().interntDialog(HomeActivity.this);
        }

    }
    // firebase token method
    private void getFirebaseToken() {
        FirebaseInstanceId.getInstance().getInstanceId().addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
            @Override
            public void onComplete(@NonNull Task<InstanceIdResult> task) {
                if (!task.isSuccessful()) {
                    Log.w("getInstanceId failed", task.getException());
                    return;
                }
                String token = task.getResult().getToken();
                databaseReference.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("Token").setValue(token);
                SharedPreference.getInstance().saveString(HomeActivity.this, "Token", token);
                Log.d("Token", token);
            }
        });
    }
    @Override
    protected void onDestroy() {
        statusUpdate(FirebaseAuth.getInstance().getCurrentUser().getUid(), System.currentTimeMillis() + "");
        super.onDestroy();
        Log.d("myStatus : ondes ", "offline");
    }
    @Override
    protected void onPause() {
        statusUpdate(FirebaseAuth.getInstance().getCurrentUser().getUid(), System.currentTimeMillis() + "");
        super.onPause();
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
                doubleBackToExitPressedOnce = false;
            }
        }, 2000);
    }
    // to get firebase users list
    private void getUserList() {
       swipeToRefresh.setRefreshing(true);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                list.clear();
                swipeToRefresh.setRefreshing(false);
                for (final DataSnapshot childDataSnapshot : dataSnapshot.getChildren()) {
                    if (!FirebaseAuth.getInstance().getCurrentUser().getUid().equals(childDataSnapshot.getKey())) {

                       final SingleUserModel model = new SingleUserModel(childDataSnapshot.child("name").getValue().toString()
                                , childDataSnapshot.child("id").getValue().toString()
                                , childDataSnapshot.child("url").getValue().toString()
                                , childDataSnapshot.child("status").getValue().toString()
                               ,childDataSnapshot.child("number").getValue().toString());
                        list.add(model);
                    }
                }
                if (list.isEmpty() || list == null) {
                    no_user_txt.setVisibility(View.VISIBLE);
                    user_list.setVisibility(View.GONE);
                } else {
                    user_list.setVisibility(View.VISIBLE);
                    no_user_txt.setVisibility(View.GONE);
                    mAdapter = new UserAdapter(HomeActivity.this, list);
                    RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
                    user_list.setLayoutManager(mLayoutManager);
                    user_list.setItemAnimator(new DefaultItemAnimator());
                    user_list.setAdapter(mAdapter);
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
               swipeToRefresh.setRefreshing(false);
            }
        });
    }



    // update the status of user
    private void statusUpdate(String userId, String status) {
        databaseReference.child(userId).child("status").setValue(status);
    }
}
