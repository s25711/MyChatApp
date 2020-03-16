package com.developer.mychatapp.Adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.developer.mychatapp.Models.UserMessage;
import com.developer.mychatapp.R;
import com.developer.mychatapp.Utils.SharedPreference;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import androidx.recyclerview.widget.RecyclerView;
import de.hdodenhof.circleimageview.CircleImageView;

import static android.content.DialogInterface.OnClickListener;

public class MessageListAdapter  extends RecyclerView.Adapter {
    private static final int VIEW_TYPE_MESSAGE_SENT = 1;
    private static final int VIEW_TYPE_MESSAGE_RECEIVED = 2;

    private Context mContext;
    private List<UserMessage> mMessageList;
    String recieverImageUrl;

    public MessageListAdapter(Context context, List<UserMessage> messageList,String recieverImageUrl) {
        mContext = context;
        mMessageList = messageList;
        this.recieverImageUrl=recieverImageUrl;
    }

    @Override
    public int getItemCount() {
        return mMessageList.size();
    }

    // Determines the appropriate ViewType according to the sender of the message.
    @Override
    public int getItemViewType(int position) {
        UserMessage message = mMessageList.get(position);

        String login_user_id = SharedPreference.getInstance().getString(mContext,"login_user");

        if (message.getSender_id().equals(login_user_id)) {
            // If the current user is the sender of the message
            return VIEW_TYPE_MESSAGE_SENT;
        } else {
            // If some other user sent the message
            return VIEW_TYPE_MESSAGE_RECEIVED;
        }
    }

    // Inflates the appropriate layout according to the ViewType.
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;

        if (viewType == VIEW_TYPE_MESSAGE_SENT) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.inflater_message_sent, parent, false);
            return new SentMessageHolder(view);
        } else if (viewType == VIEW_TYPE_MESSAGE_RECEIVED) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.inflater_message_recive, parent, false);
            return new ReceivedMessageHolder(view);
        }

        return null;
    }

    // Passes the message object to a ViewHolder so that the contents can be bound to UI.
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        UserMessage message =  mMessageList.get(position);

        switch (holder.getItemViewType()) {
            case VIEW_TYPE_MESSAGE_SENT:
                ((SentMessageHolder) holder).bind(message);
                break;
            case VIEW_TYPE_MESSAGE_RECEIVED:
                ((ReceivedMessageHolder) holder).bind(message);
        }
    }
    private class SentMessageHolder extends RecyclerView.ViewHolder {
        TextView messageText, nameText,timetxt;
        SentMessageHolder(View itemView) {
            super(itemView);
            messageText = itemView.findViewById(R.id.text_message_body);
            nameText = itemView.findViewById(R.id.sender_name);
            timetxt = itemView.findViewById(R.id.time);

        }
        void bind(final UserMessage message) {
            String msg = decryptMessage(message.getMessage());
            String time = convertMilisToTime(message.getTime());
            messageText.setText(msg);
            timetxt.setText(time);
            if (getAdapterPosition()==mMessageList.size()-1) {
                if (message.isIsseen()) {
                    nameText.setText("Seen");
                } else {
                    nameText.setText("Delivered");
                }
            }else{
                nameText.setVisibility(View.GONE);
            }
        }
    }
    private class ReceivedMessageHolder extends RecyclerView.ViewHolder {
        TextView messageText, nameText,timetxt;
        CircleImageView small_user_image;
        ReceivedMessageHolder(View itemView) {
            super(itemView);
            small_user_image = itemView.findViewById(R.id.small_user_image);
            messageText =  itemView.findViewById(R.id.text_message_body);
            nameText =  itemView.findViewById(R.id.reciver_name);
            timetxt = itemView.findViewById(R.id.timetxt);
        }
        void bind(UserMessage message) {
            String msg = decryptMessage(message.getMessage());
            messageText.setText(msg);
            nameText.setText(message.getSender());
            timetxt.setText(convertMilisToTime(message.getTime()));
            Glide.with(mContext).load(recieverImageUrl).placeholder(R.drawable.user_placegolder).centerCrop().into(small_user_image);

        }
    }

    // message decryption
    public String decryptMessage(String encryptString){
        final int shift_key = 4;
        char character;
        char ch[]=new char[encryptString.length()];//for storing encrypt char
        for(int i=0;i<encryptString.length();i++)
        {
            character=encryptString.charAt(i);
            character = (char) (character -shift_key); //perform shift
            ch[i]=character;
        }
        String decryptstr = String.valueOf(ch);

        return decryptstr;
    }

    //time conversion
    private String convertMilisToTime(long milis) {
        SimpleDateFormat formatter = new SimpleDateFormat("hh:mm a", Locale.US);
        Date date = new Date(milis);
        String result = formatter.format(date);
        return ""+result;
    }


    public void showDialog(Context activity, String title, CharSequence message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);

        if (title != null) builder.setTitle(title);

        builder.setMessage(message);
        builder.setPositiveButton("YES", YESclickListener);
        builder.setNegativeButton("Cancel", NoclickListener);
        builder.show();
    }

    private OnClickListener YESclickListener = new OnClickListener(){
        @Override
        public void onClick(DialogInterface dialogInterface, int i) {

            dialogInterface.dismiss();
        }
    };

    private OnClickListener NoclickListener = new OnClickListener(){
        @Override
        public void onClick(DialogInterface dialogInterface, int i) {
            dialogInterface.dismiss();
        }
    };


}
