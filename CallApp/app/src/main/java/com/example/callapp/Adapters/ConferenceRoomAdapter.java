package com.example.callapp.Adapters;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.callapp.CallActivity;
import com.example.callapp.ConferenceRoom;
import com.example.callapp.Models.Conference;
import com.example.callapp.Models.User;
import com.example.callapp.R;

import java.util.ArrayList;

public class ConferenceRoomAdapter extends RecyclerView.Adapter<ConferenceRoomAdapter.ConferenceRoomViewHolder> {

    Activity context;
    ArrayList<Conference> conferenceArrayList;

    public ConferenceRoomAdapter(Activity context, ArrayList<Conference> conferenceArrayList){
        this.context = context;
        this.conferenceArrayList = conferenceArrayList;
    }

    @NonNull
    @Override
    public ConferenceRoomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_conference_adapter,parent,false);
        ConferenceRoomViewHolder conferenceRoomAdapter = new ConferenceRoomViewHolder(view);



        return conferenceRoomAdapter;
    }

    @Override
    public void onBindViewHolder(@NonNull ConferenceRoomAdapter.ConferenceRoomViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public class ConferenceRoomViewHolder extends RecyclerView.ViewHolder{

        TextView textViewName;

        public ConferenceRoomViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewName = (TextView)itemView.findViewById(R.id.conference_adapter_itemName);

        }
    }
}