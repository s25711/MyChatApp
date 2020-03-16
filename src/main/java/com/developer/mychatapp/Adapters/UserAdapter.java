package com.developer.mychatapp.Adapters;

import android.content.Context;
import android.content.Intent;
import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.developer.mychatapp.ChatActivity;
import com.developer.mychatapp.Models.SingleUserModel;
import com.developer.mychatapp.Models.UserMessage;
import com.developer.mychatapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import de.hdodenhof.circleimageview.CircleImageView;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.MyViewHolder> {
    String lastMsg;
    long lastMsgTime;
    List<SingleUserModel> list;
    Context context;

    public UserAdapter(Context context, List<SingleUserModel> list) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.inflater_users, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        holder.user.setText(list.get(position).getUsername());

        lastMessage(list.get(position).getId(),holder.lastMessage_txt,holder.time);

        Glide.with(context)
                .load(list.get(position).getImage())
                .centerCrop()
                .placeholder(R.drawable.user_placegolder)
                .into(holder.profile_image);

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView user,lastMessage_txt,time;
        CircleImageView profile_image;
        LinearLayout mainLayout;


        public MyViewHolder(@NonNull final View itemView) {
            super(itemView);

            lastMessage_txt = itemView.findViewById(R.id.lastMessage_txt);
            user = itemView.findViewById(R.id.user);
            profile_image = itemView.findViewById(R.id.profile_image);
            mainLayout = itemView.findViewById(R.id.main_layout);
            time = itemView.findViewById(R.id.time);


            mainLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // Toast.makeText(context, "click", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(context, ChatActivity.class);
                    intent.putExtra("user_id", list.get(getAdapterPosition()).getId());
                    intent.putExtra("user_name", list.get(getAdapterPosition()).getUsername());
                    intent.putExtra("reciver_image",list.get(getAdapterPosition()).getImage());
                    context.startActivity(intent);
                }
            });


        }
    }
    private void lastMessage(final String userId,final TextView msg,final TextView time) {
        lastMsg = "Default";
        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("messages").child(firebaseUser.getUid() + "_" + userId);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    UserMessage userMessage = snapshot.getValue(UserMessage.class);
                    if (userMessage.getReciver_id().equals(firebaseUser.getUid()) && userMessage.getSender_id().equals(userId) || userMessage.getReciver_id().equals(userId) && userMessage.getSender_id().equals(firebaseUser.getUid())) {

                        lastMsg = userMessage.getMessage();
                        lastMsgTime = userMessage.getTime();

                      time.setText(convertMilisToTime(lastMsgTime));
                    }
                }

                switch (lastMsg) {
                    case "Default":
                       msg.setText("No Message");
                        break;
                    default:
                        if (lastMsg.isEmpty()|| lastMsg == null){
                            msg.setText("No Message");
                        }else {

                            Log.d("TEST","==="+lastMsg);
                            if (lastMsg.equals("default")){
                                msg.setText("chat not initiated.");
                            }else {
                                String finalMessage = decryptMessage(lastMsg);
                                msg.setText(finalMessage);
                            }
                        }
                        break;
                }

                lastMsg = "default";

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
    //decrypt message
    public String decryptMessage(String encryptString) {
        final int shift_key = 4;
        char character;
        char ch[] = new char[encryptString.length()];//for storing encrypt char
        for (int i = 0; i < encryptString.length(); i++) {
            character = encryptString.charAt(i);
            character = (char) (character - shift_key); //perform shift
            ch[i] = character;
        }
        String decryptstr = String.valueOf(ch);

        return decryptstr;
    }

    private String convertMilisToTime(long milis) {
        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm", Locale.US);
        Date date = new Date(milis);
        String result = formatter.format(date);
        return ""+result;
    }


}
