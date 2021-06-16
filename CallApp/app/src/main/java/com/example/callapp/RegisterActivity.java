package com.example.callapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.callapp.Models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterActivity extends AppCompatActivity {

    private EditText edName, edEmail, edPassword;
    DatabaseReference databaseReference;

    private FirebaseAuth auth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        edName = (EditText)findViewById(R.id.Name2);
        edEmail = (EditText)findViewById(R.id.Email2);
        edPassword = (EditText)findViewById(R.id.Password2);

        auth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Users");

    }

    public void register(View view){

        final String name = edName.getText().toString();
        final String email = edEmail.getText().toString();
        final String pass = edPassword.getText().toString();

        auth.createUserWithEmailAndPassword(email, pass)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if(task.isSuccessful()){

                            FirebaseUser firebaseUser = auth.getCurrentUser();
                            User user = new User(name, email, pass, firebaseUser.getUid());

                            databaseReference.child(firebaseUser.getUid()).setValue(user)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {

                                            if(task.isSuccessful()){
                                                Toast.makeText(getApplicationContext(), "User created successfully",
                                                        Toast.LENGTH_LONG).show();
                                                Intent i = new Intent(RegisterActivity.this, CallActivity.class);
                                                startActivity(i);
                                                finish();
                                            }else{
                                                Toast.makeText(getApplicationContext(), "User couldn't be registered",
                                                        Toast.LENGTH_LONG).show();
                                            }

                                        }
                                    });

                        }

                    }
                });
    }

    public void goToLogin(View view){
        Intent i = new Intent(RegisterActivity.this, LoginActivity.class);
        startActivity(i);
    }

}