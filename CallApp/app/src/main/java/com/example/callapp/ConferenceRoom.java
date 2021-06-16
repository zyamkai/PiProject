package com.example.callapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.callapp.Models.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.sinch.android.rtc.PushPair;
import com.sinch.android.rtc.Sinch;
import com.sinch.android.rtc.SinchClient;
import com.sinch.android.rtc.calling.Call;
import com.sinch.android.rtc.calling.CallClient;
import com.sinch.android.rtc.calling.CallClientListener;
import com.sinch.android.rtc.calling.CallListener;

import java.util.List;

public class ConferenceRoom extends AppCompatActivity {

    private RecyclerView recyclerView;
    private FirebaseAuth auth;
    private FirebaseUser firebaseUser;
    private SinchClient sinchClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conference_room);

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

        sinchClient.getCallClient().addCallClientListener(new CallClientListener() {

            @Override
            public void onIncomingCall(CallClient callClient,Call call) {
                //open dialog for new incoming call
                AlertDialog alertDialog = new AlertDialog.Builder(ConferenceRoom.this).create();
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
                        call.answer();
                        openCallerDialog(call);
                        call.addCallListener(new CallListener() {
                            @Override
                            public void onCallProgressing(Call call) {
                                Toast.makeText(getApplicationContext(), "Ringing...", Toast.LENGTH_LONG).show();
                            }

                            @Override
                            public void onCallEstablished(Call call) {
                                Toast.makeText(getApplicationContext(), "Call is established", Toast.LENGTH_LONG).show();
                            }

                            @Override
                            public void onCallEnded(Call call) {
                                Toast.makeText(getApplicationContext(), "Call ended", Toast.LENGTH_LONG).show();
                                call = null;
                                call.hangup();
                            }

                            @Override
                            public void onShouldSendPushNotification(Call call, List<PushPair> list) {

                            }
                        });
                        Toast.makeText(getApplicationContext(), "Call is started",Toast.LENGTH_LONG).show();
                    }
                });

                alertDialog.show();
            }
        });

        sinchClient.start();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.conference_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == R.id.conference_menu_logout){
            if(firebaseUser != null){
                auth.signOut();
                finish();
                Intent i = new Intent(ConferenceRoom.this, LoginActivity.class);
                startActivity(i);
            }
        }
        if(item.getItemId() == R.id.conference_menu_Back){
            if(firebaseUser != null){
                finish();
                Intent i = new Intent(ConferenceRoom.this, CallActivity.class);
                startActivity(i);
            }
        }
        return super.onOptionsItemSelected(item);
    }

        private void openCallerDialog(final Call call) {
            AlertDialog alertDialogCall = new AlertDialog.Builder(ConferenceRoom.this).create();
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


    public void callConference(ConferenceRoom conferenceRoom) {
    }


}