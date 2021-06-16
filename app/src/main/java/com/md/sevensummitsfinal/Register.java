package com.md.sevensummitsfinal;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.md.sevensummitsfinal.R;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

import static android.content.ContentValues.TAG;

public class Register extends AppCompatActivity {


    EditText rEmail;
    EditText rPasswort;
    EditText rPasswortBestaetigt;
    Button rButton;
    TextView liLogin;

    private FirebaseAuth mAuth;
    FirebaseFirestore db;
    String userID;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        db = FirebaseFirestore.getInstance();
        rEmail = findViewById(R.id.RegisterEmail);
        rPasswort = findViewById(R.id.RegisterPasswort);
        rPasswortBestaetigt = findViewById(R.id.RegisterPasswortBestaetigen);
        rButton = findViewById(R.id.RegisterButton);
        liLogin = findViewById(R.id.LinkLogin);
        mAuth = FirebaseAuth.getInstance();
        Log.d(TAG, "onCreate: ");

        rButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createUser();
            }
        });

        liLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Register.this, Login.class));
            }
        });
    }

    private void createUser() {
        String email = rEmail.getText().toString();
        String passwort = rPasswort.getText().toString().trim();
        String passwortb = rPasswortBestaetigt.getText().toString().trim();

        if (TextUtils.isEmpty(email)) {
            rEmail.setError("Bitte eine Email eintragen");
            rEmail.requestFocus();
        } else if (TextUtils.isEmpty(passwort)) {
            rPasswort.setError("Bitte ein Passwort eingeben");
            rPasswort.requestFocus();
        } else {
            mAuth.createUserWithEmailAndPassword(email, passwort).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull @NotNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        Toast.makeText(Register.this, "User wurde registriert", Toast.LENGTH_SHORT).show();
                        userID = mAuth.getCurrentUser().getUid();
                        DocumentReference doc = db.collection("users").document(userID);
                        Map<String, Object> user = new HashMap<>();
                        user.put("email", email);
                        doc.set(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull @NotNull Task<Void> task2) {
                                if (task2.isSuccessful()) {
                                    Log.d(TAG, "User erfolgreich registriert");
                                } else {
                                    Log.d(TAG, "Fehler bei Datenbank: " + task2.getException().getMessage());
                                }

                            }
                        });
                        startActivity(new Intent(Register.this, Login.class));

                    } else {
                        Toast.makeText(Register.this, "Leider ist ein Fehler aufgetreten: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }
}