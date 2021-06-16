package com.example.callapp;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


public class CreateConferenceRoom extends AppCompatActivity {

    private EditText edName;
    DatabaseReference databaseReference;
    FirebaseAuth auth;
    FirebaseUser user;
    private ArrayList<String> arrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_conference_room);

        edName = (EditText) findViewById(R.id.conferenceId);

        auth = FirebaseAuth.getInstance();

        databaseReference = FirebaseDatabase.getInstance().getReference().child("ConferenceRoom");

        user = auth.getCurrentUser();

        arrayList = new ArrayList<String>();


    }

    public void create(View view) {

        final String id = edName.getText().toString();
        arrayList.clear();

        if (!id.equals("")) {
            databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {

                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        for (DataSnapshot snapshot1 : dataSnapshot.getChildren()) {
                            for (DataSnapshot snapshot2 : snapshot1.getChildren()) {
                                if (snapshot2.getValue().equals(id)) {
                                    arrayList.add(id);
                                    System.out.println(arrayList);
                                }
                            }
                        }
                    }

                    checkArrayList(arrayList, id);
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }

            });
        }else {
            Toast.makeText(getApplicationContext(), "You can't make Conference with empty ID",
                    Toast.LENGTH_LONG).show();
            finish();
        }

    }

    public void checkArrayList(ArrayList<String> arrayList, String id){
        if (arrayList.isEmpty()) {
            System.out.println("ArrayIsEmpty");
            databaseReference.child("ConferenceId").child(id).child("conferenceRoomId").setValue(id);
            databaseReference.child("ConferenceId").child(id).child("User").child(auth.getCurrentUser().getUid())
                    .child("userId").setValue(auth.getCurrentUser().getUid());
            Intent i = new Intent(CreateConferenceRoom.this, ConferenceActivity.class);
            startActivity(i);
            finish();
        } else{
            Toast.makeText(getApplicationContext(), "Conference with such ID already exist",
                    Toast.LENGTH_LONG).show();
            arrayList.clear();
        }
    }

    public void back(View view){
        Intent i = new Intent(this, ConferenceActivity.class);
        startActivity(i);
        finish();
    }
}
