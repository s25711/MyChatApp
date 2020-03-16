package com.developer.mychatapp.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.developer.mychatapp.Models.SingleUserModel;
import com.developer.mychatapp.R;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import de.hdodenhof.circleimageview.CircleImageView;

public class ContactsAdapter  extends RecyclerView.Adapter<ContactsAdapter.MyViewHolder> {
    List<SingleUserModel> list;
    Context context;
    public ContactsAdapter(List<SingleUserModel> list, Context context) {
        this.list = list;
        this.context = context;
    }
    @NonNull
    @Override
    public ContactsAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.inflater_contacts, parent, false);
        return new ContactsAdapter.MyViewHolder(itemView);
    }
    @Override
    public void onBindViewHolder(@NonNull final ContactsAdapter.MyViewHolder holder, final int position) {
        SingleUserModel contactVO = list.get(position);
        holder.textViewName.setText(contactVO.getUsername());
        holder.number.setText(contactVO.getNumber());
        Glide.with(context).load(contactVO.getImage()).placeholder(R.drawable.user_placegolder).into(holder.imageView);
        holder.textViewName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }
    @Override
    public int getItemCount() {
        return list.size();
    }
    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView textViewName,number;
        CircleImageView imageView;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewName = itemView.findViewById(R.id.textViewName);
            number = itemView.findViewById(R.id.number);
            imageView = itemView.findViewById(R.id.profileImg);
        }
    }
}
