package com.example.callapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.callapp.Adapters.AllUsersAdapter;
import com.example.callapp.Models.Conference;
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

public class CallActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private FirebaseAuth auth;
    private FirebaseUser firebaseUser;
    private SinchClient sinchClient;
    private Call call;
    private ArrayList<User> userArrayList;
    private DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_call);


        recyclerView = (RecyclerView)findViewById(R.id.recyclerview);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        userArrayList = new ArrayList<>();
        reference = FirebaseDatabase.getInstance().getReference().child("Users");
        auth = FirebaseAuth.getInstance();
        firebaseUser = auth.getCurrentUser();

        sinchClient = Sinch.getSinchClientBuilder()
                .context(this)
                .userId(firebaseUser.getUid())
                .applicationKey("f7fc6a8a-da42-419c-9d21-6fbe71860bec")
                .applicationSecret("4w8/yj77BEOYHvSK3mGlFg==")
                .environmentHost("clientapi.sinch.com")
                .build();

        sinchClient.setSupportCalling(true);
        sinchClient.startListeningOnActiveConnection();

        sinchClient.getCallClient().addCallClientListener(new SinchCallClientListener(){
        });


        sinchClient.start();
        fetchAllUsers();
    }

    private void fetchAllUsers() {

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                userArrayList.clear();

                for(DataSnapshot dataSnapshot:snapshot.getChildren()){
                    User user = dataSnapshot.getValue(User.class);
                    userArrayList.add(user);
                }


                AllUsersAdapter adapter = new AllUsersAdapter(CallActivity.this, userArrayList);
                recyclerView.setAdapter(adapter);
                adapter.notifyDataSetChanged();


            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getApplicationContext(),"Error"+error.getMessage(),Toast.LENGTH_LONG).show();
            }
        });

    }


    private class SinchCallListener implements CallListener{

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
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == R.id.menu_logout){
            if(firebaseUser != null){
                auth.signOut();
                finish();
                Intent i = new Intent(CallActivity.this, LoginActivity.class);
                startActivity(i);
            }
        }
        if(item.getItemId() == R.id.menu_Back){
            if(firebaseUser != null){
                finish();
                Intent i = new Intent(CallActivity.this, MainActivity.class);
                startActivity(i);
            }
        }
        if(item.getItemId() == R.id.menu_go_to_conference){
            if(firebaseUser != null){
                finish();
                Intent i = new Intent(CallActivity.this, ConferenceRoom.class);
                startActivity(i);
            }
        }
        return super.onOptionsItemSelected(item);
    }

    private class SinchCallClientListener implements CallClientListener{

        @Override
        public void onIncomingCall(CallClient callClient,Call incomingCall) {
            //open dialog for new incoming call
            AlertDialog alertDialog = new AlertDialog.Builder(CallActivity.this).create();
            alertDialog.setTitle("Calling");
            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "Reject", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    call.hangup();
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

    public void callUser(User user){
        if(call == null){
            call = sinchClient.getCallClient().callUser(user.getUserId());
            call.addCallListener(new SinchCallListener());
            openCallerDialog(call);
        }
    }

    private void openCallerDialog(final Call call) {
        AlertDialog alertDialogCall = new AlertDialog.Builder(CallActivity.this).create();
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