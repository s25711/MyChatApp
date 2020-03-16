package com.developer.mychatapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import de.hdodenhof.circleimageview.CircleImageView;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.android.volley.AuthFailureError;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.developer.mychatapp.Adapters.MessageListAdapter;
import com.developer.mychatapp.Models.UserMessage;
import com.developer.mychatapp.Utils.SharedPreference;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import org.json.JSONException;
import org.json.JSONObject;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class ChatActivity extends AppCompatActivity {
    String token;
    String NOTIFICATION_TITLE;
    String NOTIFICATION_MESSAGE;
    DatabaseReference mReference;
    DatabaseReference mDatabase;
    DatabaseReference mDatabase1;
    DatabaseReference mDatabase2;
    DatabaseReference refre;
    ImageView back_img;
    TextView name_txt, txt_status;
    RecyclerView reyclerview_message_list;
    MessageListAdapter mMessageAdapter;
    EditText edittext_chatbox;
    CircleImageView reciver_profile;
    Button button_chatbox_send;
    List<UserMessage> messageList = new ArrayList<>();
    String reciver_name,reciver_id,sender_name,sender_id,status;
    ValueEventListener seenListner;
    String url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        getSupportActionBar().hide();

        getViewIds();

        mDatabase = FirebaseDatabase.getInstance().getReference();
        refre = FirebaseDatabase.getInstance().getReference();
        mDatabase1 = FirebaseDatabase.getInstance().getReference();
        mDatabase2 = FirebaseDatabase.getInstance().getReference();

        //get Intent data
        Intent intent = getIntent();
        reciver_name = intent.getStringExtra("user_name");
        reciver_id = intent.getStringExtra("user_id");

        // reciever profile image url
        url = intent.getStringExtra("reciver_image");
        Glide.with(this).load(url).centerCrop().placeholder(R.drawable.user_placegolder).into(reciver_profile);

        sender_id = SharedPreference.getInstance().getString(this, "login_user");
        edittext_chatbox.addTextChangedListener(textWatcher);


        //to getting user status and sender name
        mDatabase1.child("users").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                    if (snapshot.child("id").getValue().equals(sender_id)) {

                        sender_name = snapshot.child("name").getValue().toString();
                        Log.e("VALUE", ":" + status);

                    } else if (snapshot.child("id").getValue().equals(reciver_id)) {

                        status = snapshot.child("status").getValue().toString();

                        if (status.equals("online")) {

                            txt_status.setText(status);

                        } else if (status.equals("is typing")) {

                            txt_status.setText(status);

                        } else {

                            txt_status.setText("last seen at " + convertMilisToTime(status));
                            txt_status.setSelected(true);

                        }

                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        // get user token
        mDatabase1.child("users").child(reciver_id).child("Token").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.d("NEW_TOKEN","====="+dataSnapshot.getValue());

                token = dataSnapshot.getValue().toString();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        //set chat user name
        name_txt.setText(reciver_name);

        // back icon action
        back_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
                finish();
            }
        });

        //create chat rooms
        String chat_room = sender_id + "_" + reciver_id;
        String chat_room1 = reciver_id + "_" + sender_id;
        mDatabase1 = mDatabase1.child("messages").child(chat_room);
        mDatabase2 = mDatabase2.child("messages").child(chat_room1);
        //send message action
        button_chatbox_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                messageList.clear();

                if (edittext_chatbox.getText().toString().isEmpty()) {

                    Toast.makeText(ChatActivity.this, "Please type something", Toast.LENGTH_SHORT).show();

                } else {
                    //send message
                    sendMessage(edittext_chatbox.getText().toString(), System.currentTimeMillis());
                    edittext_chatbox.setText("");
                }
            }
        });


        //get all the messages
        mDatabase.child("messages").child(sender_id + "_" + reciver_id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                messageList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                    UserMessage userMessage = snapshot.getValue(UserMessage.class);
                    messageList.add(userMessage);
                }

                setUpAdapter(messageList,url);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        //subscribe topic for notifications
           FirebaseMessaging.getInstance().subscribeToTopic("news");


           // seen unread messages
            seenMessage(sender_id);


    }

    // get all the view id's
    private void getViewIds() {
        txt_status = findViewById(R.id.txt_status);
        back_img = findViewById(R.id.back_img);
        name_txt = findViewById(R.id.name_txt);
        button_chatbox_send = findViewById(R.id.button_chatbox_send);
        edittext_chatbox = findViewById(R.id.edittext_chatbox);
        reyclerview_message_list = findViewById(R.id.reyclerview_message_list);
        reciver_profile = findViewById(R.id.reciver_profile);
    }

    // setting up the adapter
    private void setUpAdapter(List<UserMessage> messageList,String mURL) {
        mMessageAdapter = new MessageListAdapter(this, messageList,mURL);
        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setOrientation(RecyclerView.VERTICAL);
        manager.setStackFromEnd(true);
        manager.setSmoothScrollbarEnabled(true);
        manager.setReverseLayout(false);
        reyclerview_message_list.setLayoutManager(manager);
        reyclerview_message_list.setAdapter(mMessageAdapter);

    }

    //send message into chat
    private void sendMessage(String message, final long milis) {

        String encryptedMSG = encryptedMessage(message);
        UserMessage userMessage = new UserMessage(mDatabase1.push().getKey(),sender_id, reciver_id, sender_name, reciver_name, encryptedMSG, milis,false);
        mDatabase1.push().setValue(userMessage);
        mDatabase2.push().setValue(userMessage);
        send();

    }

    //message encryption
    private String encryptedMessage(String text) {

        final int shift_key = 4; //it is the shift key to move charcter, like if i have 'a' then a=97+4=101 which =e and thus it changes
        char character;
        char ch[] = new char[text.length()];//for storing encrypt char
        for (int iteration = 0; iteration < text.length(); iteration++) {
            character = text.charAt(iteration); //get characters
            character = (char) (character + shift_key); //perform shift
            ch[iteration] = character;//assign char to char array
        }
        String encryptstr = String.valueOf(ch);//converting char array to string

        return encryptstr;
    }

    //update the status of user's states
    private void statusUpdate(String userId, String status) {

        refre.child("users").child(userId).child("status").setValue(status);

    }

    @Override
    protected void onPause() {
        super.onPause();
        statusUpdate(sender_id, System.currentTimeMillis() + "");
        mReference.removeEventListener(seenListner);
        Log.d("myStatus: onStop", "offline");
    }

    @Override
    protected void onResume() {
        super.onResume();
        statusUpdate(sender_id, "online");
    }

    //set the time of message
    private String convertMilisToTime(String milis) {
        SimpleDateFormat formatter = new SimpleDateFormat("hh:mm a", Locale.US);
        Date date = new Date(Long.parseLong(milis));
        String finalDate = DateFormat.getDateInstance(DateFormat.MEDIUM).format(date);
        String result = formatter.format(date);
        return finalDate + " , " + result;
    }

    // read the user's message tying state
    TextWatcher textWatcher = new TextWatcher() {
        public void afterTextChanged(Editable s) {

            if (s.toString().isEmpty()) {
                statusUpdate(sender_id, "online");
            }
        }

        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        public void onTextChanged(CharSequence s, int start, final int before, final int count) {

            Log.d("test", "===" + s);

            if (!s.equals("")) {
                statusUpdate(sender_id, "is typing");
            } else {
                statusUpdate(sender_id, "online");
            }
        }
    };
    private void seenMessage(final String userId){
        mReference = FirebaseDatabase.getInstance().getReference("messages").child(reciver_id+"_"+sender_id);
        seenListner = mReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot:dataSnapshot.getChildren())
                {
                    UserMessage userMessage = snapshot.getValue(UserMessage.class);
                    if (userMessage.getReciver_id().equals(userId)&& userMessage.getSender_id().equals(reciver_id)){

                        HashMap<String,Object> map = new HashMap<>();
                        map.put("isseen",true);
                        snapshot.getRef().updateChildren(map);
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }
    //-------------------------------------notifications----------------------------------------
    private void send(){
        String TOPICS="/topics/"+"news";
        TOPICS = TOPICS.replace("\\","");
        NOTIFICATION_TITLE = reciver_name;
        NOTIFICATION_MESSAGE = edittext_chatbox.getText().toString();
        JSONObject notification = new JSONObject();
        JSONObject notifcationBody = new JSONObject();
        try {
            notifcationBody.put("title", NOTIFICATION_TITLE);
            notifcationBody.put("body", NOTIFICATION_MESSAGE);
            notification.put("to",TOPICS);
            notification.put("notification", notifcationBody);
            Log.d("TEST","==="+notification);
            sendNotification1(notification);
        } catch (JSONException e) {
            Log.e("TEST JSON ERROR" , "onCreate: " + e.getMessage() );
        }
    }
    private void sendNotification1(JSONObject notification) throws JSONException {
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        String notification_url= "https://fcm.googleapis.com/fcm/send";
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(notification_url, notification,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.i("TEST RESPONSE", "onResponse: " + response.toString());
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(ChatActivity.this, "Request error", Toast.LENGTH_LONG).show();
                        Log.i("TEST NETWORK ERROR", "onErrorResponse: "+error.networkResponse.statusCode);
                    }
                }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("Authorization", "key=AIzaSyDCnEijhKxDHHTR9PN2sYY1hBFd1kIk5zw");
                params.put("Content-Type", "application/json");
                return params;
            }
        };
        requestQueue.add(jsonObjectRequest);
    }

}
