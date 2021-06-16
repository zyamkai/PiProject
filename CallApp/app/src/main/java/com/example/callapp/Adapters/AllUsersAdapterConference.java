package com.example.callapp.Adapters;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.callapp.ConferenceActivity;
import com.example.callapp.Models.ConferenceRoom;
import com.example.callapp.Models.User;
import com.example.callapp.R;

import java.util.ArrayList;

public class AllUsersAdapterConference extends RecyclerView.Adapter<AllUsersAdapterConference.AllUsersViewHolder> {

    Activity context;
    ArrayList<User> userArrayList;
    ConferenceRoom conferenceRoom;

    public AllUsersAdapterConference(Activity context, ArrayList<User> userArrayList, ConferenceRoom conferenceRoomId){
        this.context = context;
        this.userArrayList = userArrayList;
        this.conferenceRoom = conferenceRoomId;
    }


    @NonNull
    @Override
    public AllUsersViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.all_users_for_conference,parent,false);
        AllUsersViewHolder allUsersAdapter = new AllUsersViewHolder(view);



        return allUsersAdapter;
    }

    @Override
    public void onBindViewHolder(@NonNull AllUsersViewHolder holder, int position) {
        User user = userArrayList.get(position);
        holder.textViewName.setText(user.getName());

    }



    @Override
    public int getItemCount() {
        return userArrayList.size();
    }

    public class AllUsersViewHolder extends RecyclerView.ViewHolder{

        TextView textViewName;
        Button button, button1;

        public AllUsersViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewName = (TextView)itemView.findViewById(R.id.itemName);
            //button = itemView.findViewById(R.id.CallButton);
            button1 = itemView.findViewById(R.id.AddButton);
/*
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    User user = userArrayList.get(getAdapterPosition());
                    ((ConferenceActivity)context).callUser(user);
                }
            });


 */
            button1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    User user = userArrayList.get(getAdapterPosition());
                    ((ConferenceActivity)context).addUserToConference(user, conferenceRoom);
                }
            });
        }
    }
}
