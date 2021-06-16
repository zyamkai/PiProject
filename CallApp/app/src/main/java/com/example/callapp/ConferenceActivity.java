package com.example.callapp;


import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.example.callapp.Adapters.AllUsersAdapterConference;
import com.example.callapp.Adapters.ConferenceRoomAdapter;
import com.example.callapp.Models.ConferenceRoom;
import com.example.callapp.Models.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sinch.android.rtc.PushPair;
import com.sinch.android.rtc.Sinch;
import com.sinch.android.rtc.SinchClient;
import com.sinch.android.rtc.calling.Call;
import com.sinch.android.rtc.calling.CallClient;
import com.sinch.android.rtc.calling.CallClientListener;
import com.sinch.android.rtc.calling.CallListener;

import java.util.ArrayList;
import java.util.List;

public class ConferenceActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private FirebaseAuth auth;
    private FirebaseUser firebaseUser;
    private SinchClient sinchClient;
    private Call call;
    private ArrayList<User> userArrayList;
    private ArrayList<ConferenceRoom> connferenceArrayList;
    private DatabaseReference databaseReference;
    private DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_call);

        recyclerView = (RecyclerView)findViewById(R.id.recyclerview);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        databaseReference = FirebaseDatabase.getInstance().getReference().child("ConferenceRoom").child("ConferenceId");
        connferenceArrayList = new ArrayList<>();

        userArrayList = new ArrayList<>();
        reference = FirebaseDatabase.getInstance().getReference().child("Users");
        auth = FirebaseAuth.getInstance();
        firebaseUser = auth.getCurrentUser();

        sinchClient = Sinch.getSinchClientBuilder()
                .context(this)
                .userId(firebaseUser.getUid())
                .applicationKey("YourKey")
                .applicationSecret("YourSecret")
                .environmentHost("clientapi.sinch.com")
                .build();

        sinchClient.setSupportCalling(true);
        sinchClient.startListeningOnActiveConnection();

        sinchClient.getCallClient().addCallClientListener(new SinchCallClientListener(){
        });


        sinchClient.start();
        fetchAllConferences(firebaseUser);
    }

    private void fetchAllConferences(FirebaseUser user) {
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                connferenceArrayList.clear();

                for(DataSnapshot dataSnapshot:snapshot.getChildren()){
                    for(DataSnapshot snapshot1:dataSnapshot.getChildren()) {
                        for(DataSnapshot snapshot2:snapshot1.getChildren()) {
                            for(DataSnapshot snapshot3:snapshot2.getChildren()){
                                if (snapshot3.getValue().equals(user.getUid())) {
                                    ConferenceRoom conferenceRoom = dataSnapshot.getValue(ConferenceRoom.class);
                                    connferenceArrayList.add(conferenceRoom);
                                }
                            }
                        }
                    }
                }

                ConferenceRoomAdapter adapter = new ConferenceRoomAdapter(ConferenceActivity.this, connferenceArrayList);
                recyclerView.setAdapter(adapter);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getApplicationContext(),"Error"+error.getMessage(),Toast.LENGTH_LONG).show();
            }
        });
    }

    private void fetchAllUsers(ConferenceRoom conferenceRoom) {

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                userArrayList.clear();

                for(DataSnapshot dataSnapshot:snapshot.getChildren()){
                    User user = dataSnapshot.getValue(User.class);
                    userArrayList.add(user);
                }


                AllUsersAdapterConference adapter = new AllUsersAdapterConference(ConferenceActivity.this, userArrayList, conferenceRoom);
                recyclerView.setAdapter(adapter);
                adapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getApplicationContext(),"Error"+error.getMessage(),Toast.LENGTH_LONG).show();
            }
        });

    }

    public void addUserToConference(User user, ConferenceRoom conferenceRoom) {


        databaseReference.child(conferenceRoom.getConferenceRoomId()).child("User").child(user.getUserId()).child("userId").setValue(user.getUserId());

    }

    public void addUserToConferenceRoom(ConferenceRoom conferenceRoom) {
        fetchAllUsers(conferenceRoom);
    }


    private class SinchCallListener implements CallListener {

        @Override
        public void onCallProgressing(Call call) {
            Toast.makeText(getApplicationContext(), "Ringing...", Toast.LENGTH_LONG).show();
        }

        @Override
        public void onCallEstablished(Call call) {
            Toast.makeText(getApplicationContext(), "Call is established", Toast.LENGTH_LONG).show();
        }

        @Override
        public void onCallEnded(Call endedCall) {
            Toast.makeText(getApplicationContext(), "Call ended", Toast.LENGTH_LONG).show();
            call = null;
            endedCall.hangup();
        }

        @Override
        public void onShouldSendPushNotification(Call call, List<PushPair> list) {

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.conference_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == R.id.menu_logout){
            if(firebaseUser != null){
                auth.signOut();
                finish();
                Intent i = new Intent(ConferenceActivity.this, LoginActivity.class);
                startActivity(i);
            }
        }
        if(item.getItemId() == R.id.menu_create_conference){
            finish();
            Intent i = new Intent(ConferenceActivity.this, CreateConferenceRoom.class);
            startActivity(i);

        }
        if(item.getItemId() == R.id.conference_menu_back){
            finish();
            Intent i = new Intent(ConferenceActivity.this, CallActivity.class);
            startActivity(i);

        }

        return super.onOptionsItemSelected(item);
    }

    private class SinchCallClientListener implements CallClientListener {

        @Override
        public void onIncomingCall(CallClient callClient, Call incomingCall) {
            //open dialog for new incoming call
            AlertDialog alertDialog = new AlertDialog.Builder(ConferenceActivity.this).create();
            alertDialog.setTitle("Calling");
            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "Reject", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    call.hangup();
                    dialog.dismiss();
                }
            });

            alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Pick", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    call = incomingCall;
                    call.answer();
                    openCallerDialog(call);
                    call.addCallListener(new SinchCallListener());
                    Toast.makeText(getApplicationContext(), "Call is started",Toast.LENGTH_LONG).show();
                }
            });

            alertDialog.show();
        }
    }

    public void callConference(ConferenceRoom conferenceRoom){
        if(call == null){
            call = sinchClient.getCallClient().callConference(conferenceRoom.getConferenceRoomId());
            call.addCallListener(new SinchCallListener());
            openCallerDialog(call);
        }
    }

    private void openCallerDialog(final Call call) {
        AlertDialog alertDialogCall = new AlertDialog.Builder(ConferenceActivity.this).create();
        alertDialogCall.setTitle("Alert");
        alertDialogCall.setMessage("Calling");
        alertDialogCall.setButton(AlertDialog.BUTTON_NEUTRAL, "Hang Up", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                call.hangup();
            }
        });

        alertDialogCall.show();
    }

}