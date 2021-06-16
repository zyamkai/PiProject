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
import com.example.callapp.R;

import java.util.ArrayList;

public class ConferenceRoomAdapter extends RecyclerView.Adapter<ConferenceRoomAdapter.ConferenceRoomViewHolder> {

    Activity context;
    ArrayList<ConferenceRoom> conferenceRoomArrayList;

    public ConferenceRoomAdapter(Activity context, ArrayList<ConferenceRoom> conferenceRoomArrayList){
        this.context = context;
        this.conferenceRoomArrayList = conferenceRoomArrayList;
    }


    @NonNull
    @Override
    public ConferenceRoomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.conference_room,parent,false);
        ConferenceRoomViewHolder conferenceRoomAdapter = new ConferenceRoomViewHolder(view);



        return conferenceRoomAdapter;
    }

    @Override
    public void onBindViewHolder(@NonNull ConferenceRoomViewHolder holder, int position) {
        ConferenceRoom conferenceRoom = conferenceRoomArrayList.get(position);
        holder.textViewName.setText(conferenceRoom.getConferenceRoomId());
    }

    @Override
    public int getItemCount() {
        return conferenceRoomArrayList.size();
    }

    public class ConferenceRoomViewHolder extends RecyclerView.ViewHolder{

        TextView textViewName;
        Button button, button2;

        public ConferenceRoomViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewName = (TextView)itemView.findViewById(R.id.ConferenceItemName);
            button = itemView.findViewById(R.id.ConferenceCallButton);
            button2 = itemView.findViewById(R.id.ConferenceAddUser);

            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ConferenceRoom conferenceRoom = conferenceRoomArrayList.get(getAdapterPosition());
                    ((ConferenceActivity)context).callConference(conferenceRoom);
                }
            });

            button2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ConferenceRoom conferenceRoom = conferenceRoomArrayList.get(getAdapterPosition());
                    ((ConferenceActivity)context).addUserToConferenceRoom(conferenceRoom);
                }
            });
        }
    }
}
