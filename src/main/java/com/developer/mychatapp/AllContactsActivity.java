package com.developer.mychatapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.database.Cursor;
import android.media.Image;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.developer.mychatapp.Adapters.ContactsAdapter;
import com.developer.mychatapp.Models.SingleUserModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class AllContactsActivity extends AppCompatActivity implements View.OnClickListener{

    DatabaseReference databaseReference;

    RecyclerView contactsRecycerView;
    ImageView back_img;
    SwipeRefreshLayout swipeToRefresh;
    List<SingleUserModel> contactVOList = new ArrayList();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_contacts);
        getSupportActionBar().hide();
        getViewIds();
        listners();

        databaseReference = FirebaseDatabase.getInstance().getReference("users");

        getUserList();


    }

    private void getViewIds(){
        contactsRecycerView = findViewById(R.id.contactsRecycerView);
        back_img = findViewById(R.id.back_img);
        swipeToRefresh =findViewById(R.id.swipeToRefresh);
    }

    private void listners(){
        back_img.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.back_img:
                onBackPressed();
                break;
        }
    }

    // to get firebase users list
    private void getUserList() {
        swipeToRefresh.setRefreshing(true);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                swipeToRefresh.setRefreshing(false);
                for (final DataSnapshot childDataSnapshot : dataSnapshot.getChildren()) {
                    if (!FirebaseAuth.getInstance().getCurrentUser().getUid().equals(childDataSnapshot.getKey())) {

                        Log.d("STATUS", "=====================" + childDataSnapshot.child("status").getValue().toString());

                        final SingleUserModel model = new SingleUserModel(childDataSnapshot.child("name").getValue().toString()
                                , childDataSnapshot.child("id").getValue().toString()
                                , childDataSnapshot.child("url").getValue().toString()
                                , childDataSnapshot.child("status").getValue().toString()
                        ,childDataSnapshot.child("number").getValue().toString());

                        contactVOList.add(model);

                        ContactsAdapter contactAdapter = new ContactsAdapter(contactVOList, getApplicationContext());
                        contactsRecycerView.setLayoutManager(new LinearLayoutManager(AllContactsActivity.this));
                        contactsRecycerView.setAdapter(contactAdapter);
                    }
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                swipeToRefresh.setRefreshing(false);
            }
        });
    }


    private void phoneContacts()
    {
         /*  String[] projection = new String[]{ContactsContract.Contacts._ID, ContactsContract.Data.DISPLAY_NAME, ContactsContract.CommonDataKinds.Phone.NUMBER, ContactsContract.CommonDataKinds.Phone.PHOTO_URI};
        Cursor phones = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, projection, null, null,
                ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC");
        List<ContactVO> userList = new ArrayList<>();

        String lastPhoneName = " ";
        if (phones.getCount() > 0) {
            while (phones.moveToNext()) {
                String name = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                String phoneNumber = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                String contactId = phones.getString(phones.getColumnIndex(ContactsContract.Contacts._ID));
                String photoUri = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.PHOTO_URI));
                if (!name.equalsIgnoreCase(lastPhoneName)) {
                    lastPhoneName = name;
                    ContactVO user = new ContactVO();
                    user.setContactName(phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)));
                    user.setContactNumber(phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)));
                    userList.add(user);
                    Log.d("getContactsList", name + "---" + phoneNumber + " -- " + contactId + " -- " + photoUri);
                }
            }
        }
        phones.close();*/
    }

}
