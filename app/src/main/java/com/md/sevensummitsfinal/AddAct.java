package com.md.sevensummitsfinal;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

import static android.content.ContentValues.TAG;

public class AddAct extends AppCompatActivity {

    EditText titel, beschreibung;
    ImageView foto;
    Button addAct, addFoto;
    FirebaseFirestore db;
    FirebaseAuth currentUser;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);
        titel = findViewById(R.id.editTextTitel);
        beschreibung = findViewById(R.id.editTextBeschreibung);
        foto = findViewById(R.id.imageViewAct);
        addAct = findViewById(R.id.btnAddAct);
        addFoto = findViewById(R.id.btnAddImage);

        currentUser = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        titel.setText("");
        titel.setFocusableInTouchMode(true);

        beschreibung.setText("");
        beschreibung.setFocusableInTouchMode(true);



        addFoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        addAct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DocumentReference doc = db.collection("activities").document();
                String rTitel = titel.getText().toString().trim();
                String rBeschreibung = beschreibung.getText().toString().trim();
                String uID = currentUser.getUid();
                Map<String, Object> myAct = new HashMap<>();
                myAct.put("Titel", rTitel);
                myAct.put("Beschreibung", rBeschreibung);
                myAct.put("userID", uID);

                db.collection("activities")
                        .add(myAct)
                        .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                            @Override
                            public void onSuccess(DocumentReference documentReference) {
                                Toast.makeText(AddAct.this, "Aktivität erfolgreich hinzugefügt", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(AddAct.this, MenuActivity.class));
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull @NotNull Exception e) {
                                Toast.makeText(AddAct.this, "Aktivität konnte nicht erfolgreich hinzugefügt werden: " + e.toString(), Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(AddAct.this, MenuActivity.class));
                            }
                        });

            }
        });
    }
}