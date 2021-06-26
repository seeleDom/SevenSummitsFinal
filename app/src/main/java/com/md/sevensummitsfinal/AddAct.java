package com.md.sevensummitsfinal;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.internal.Storage;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.content.ContentValues.TAG;

public class AddAct extends AppCompatActivity {
    private static final int PICK_IMAGE_REQUEST = 1;
    EditText titel, beschreibung, fileName;
    ImageView foto;
    Button addAct, addFoto;
    FirebaseFirestore db;
    FirebaseAuth currentUser;
    StorageReference stRef;
    DocumentReference docRef;
    String upload;
    Uri image;
    StorageReference fileRef;
    Spinner chooseBerg;
    String userID;
    String ActiveChallenge;
    ArrayList<String> berge;
    ArrayList<String> user;
    int countBerge;
    Boolean hasWon;
    String uID;
    GeoPoint activeBerg;
    ArrayList<GeoPoint> geoBerge;
    //Die Google API für die Location Services. Der Hauptteil der App wird diese Klasse benutzen
    FusedLocationProviderClient fusedLocationProviderClient;
    private static final int PERMISSIONS_FINE_LOCATION = 99;
    Location currentLocation;
    boolean isClose;
    String wait;
    String resultCheck;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);
        titel = findViewById(R.id.editTextTitel);
        beschreibung = findViewById(R.id.editTextBeschreibung);
        foto = findViewById(R.id.imageViewAct);
        addAct = findViewById(R.id.btnAddAct);
        addFoto = findViewById(R.id.btnAddImage);
        fileName = findViewById(R.id.editTextFileName);
        stRef = FirebaseStorage.getInstance().getReference("uploads");
        hasWon = false;
        Context context = getApplicationContext();
        currentUser = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        titel.setText("");
        titel.setFocusableInTouchMode(true);
        uID = currentUser.getUid();
        beschreibung.setText("");
        beschreibung.setFocusableInTouchMode(true);
        chooseBerg = findViewById(R.id.spinnerBerg);
        userID = currentUser.getUid();
        updateGPS();
        db.collection("users").document(uID)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull @NotNull Task<DocumentSnapshot> task) {
                        if(task.isSuccessful()){
                            DocumentSnapshot doc = task.getResult();
                            if(doc.getString("ActiveChallenge") != null || !(doc.getString("ActiveChallenge").equals(""))){
                                ActiveChallenge = doc.getString("ActiveChallenge");
                                db.collection("challenge").document(ActiveChallenge)
                                        .get()
                                        .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull @NotNull Task<DocumentSnapshot> task) {
                                                DocumentSnapshot docBerge = task.getResult();
                                                List<String> spinnerArray = (ArrayList<String>) docBerge.get("bergTitel");
                                                ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_spinner_dropdown_item, spinnerArray);
                                                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                                chooseBerg.setAdapter(adapter);
                                            }
                                        });
                            }
                        }
                    }
                });



        addFoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFileChooser();
            }
        });

        addAct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DocumentReference doc = db.collection("activities").document();
                String rTitel = titel.getText().toString().trim();
                String rBeschreibung = beschreibung.getText().toString().trim();
                resultCheck = checkRadius();
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    public void run() {
                        Log.d(TAG, "IsCloseCheck: " + isClose);
                        if(isClose == false){
                            Toast.makeText(context, "Sie sind leider zu weit entfernt von diesem Ort", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        Map<String, Object> myAct = new HashMap<>();

                        fileRef = null;
                        String name = null;

                        if(image != null){
                            fileRef= stRef.child(System.currentTimeMillis() + "." + getFileExtension(image));
                            name = fileRef.toString();
                            fileRef.putFile(image)
                                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                        @Override
                                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                            fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                @Override
                                                public void onSuccess(Uri uri) {
                                                    upload = uri.toString();
                                                    myAct.put("challenge", ActiveChallenge);
                                                    myAct.put("Titel", rTitel);
                                                    myAct.put("Beschreibung", rBeschreibung);
                                                    myAct.put("userID", uID);
                                                    myAct.put("ImageUrl", upload);

                                                    Log.d(TAG, myAct.toString());
                                                    db.collection("activities")
                                                            .add(myAct)
                                                            .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                                                @Override
                                                                public void onSuccess(DocumentReference documentReference) {
                                                                    Toast.makeText(AddAct.this, "Aktivität erfolgreich hinzugefügt", Toast.LENGTH_SHORT).show();
                                                                    checkUserWon();


                                                                }
                                                            })
                                                            .addOnFailureListener(new OnFailureListener() {
                                                                @Override
                                                                public void onFailure(@NonNull @NotNull Exception e) {
                                                                    Toast.makeText(AddAct.this, "Aktivität konnte nicht erfolgreich hinzugefügt werden: " + e.toString(), Toast.LENGTH_SHORT).show();
                                                                    startActivity(new Intent(AddAct.this, MenuActivity.class));
                                                                }
                                                            });
                                                    //Log.d(TAG, "UploadUrl: " + upload);
                                                }
                                            });
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull @NotNull Exception e) {
                                            Toast.makeText(AddAct.this, "Fehler: " + e.toString(), Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        } else {
                            Toast.makeText(AddAct.this, "No file selected", Toast.LENGTH_SHORT).show();
                        }
                        Log.d(TAG, "upload: " + upload);
                    }
                }, 2000);




            }
        });
    }

    private void openFileChooser(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null){
            image = data.getData();
            Picasso.get().load(image).into(foto);
        }
    }

    private String getFileExtension(Uri uri){
        ContentResolver cr = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cr.getType(uri));
    }

    public void checkUserWon(){
        DocumentReference doc = db.collection("users").document(uID);
        Log.d(TAG, "RefDoc: " + doc.getId());
        doc.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable @org.jetbrains.annotations.Nullable DocumentSnapshot value, @Nullable @org.jetbrains.annotations.Nullable FirebaseFirestoreException error) {
                ActiveChallenge = value.getString("ActiveChallenge");
                if(ActiveChallenge == null || ActiveChallenge.equals("")){
                    return;
                }

                //Log.d(TAG, "CheckUserWon ausgeführt");
                Log.d(TAG, "ActiveChallenge Log: " + ActiveChallenge);
                if(ActiveChallenge != null || !(ActiveChallenge.equals(""))){
                    Log.d(TAG, "ActiveChallenge!= null");
                    db.collection("challenge").document(ActiveChallenge)
                            .get()
                            .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull @NotNull Task<DocumentSnapshot> task) {
                                    if(task.isSuccessful()){
                                        DocumentSnapshot document = task.getResult();
                                        berge = (ArrayList<String>) document.get("bergTitel");
                                        int anzahlBerge = berge.size();
                                        Log.d(TAG, "Anzahl Berge: " + anzahlBerge);
                                        user = (ArrayList<String>) document.get("currentUsers");
                                        int anzahlUser = user.size();
                                        Log.d(TAG, "Anzahl User: " + anzahlUser);

                                        for(int i = 0; i < anzahlUser; i++){
                                            int counter = i;
                                            countBerge = 0;
                                            db.collection("activities")
                                                    .get()
                                                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                        @Override
                                                        public void onComplete(@NonNull @NotNull Task<QuerySnapshot> task) {
                                                            if(task.isSuccessful()){
                                                                for(QueryDocumentSnapshot docA: task.getResult()){
                                                                    if(docA.getString("challenge").equals(ActiveChallenge)){
                                                                        countBerge++;
                                                                    }
                                                                }
                                                                Log.d(TAG, "CountBerge: " + countBerge);
                                                                if(countBerge >= anzahlBerge){
                                                                    Log.d(TAG, "ich bin hier");
                                                                    String winner = user.get(counter);
                                                                    Map<String, Object> challengeWinner = new HashMap<>();
                                                                    challengeWinner.put("winner", winner);
                                                                    db.collection("challenge").document(ActiveChallenge)
                                                                            .update(challengeWinner)
                                                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                @Override
                                                                                public void onComplete(@NonNull @NotNull Task<Void> task) {
                                                                                    hasWon = true;
                                                                                    startActivity(new Intent(AddAct.this, MenuActivity.class));
                                                                                    return;
                                                                                }
                                                                            });
                                                                }
                                                            }

                                                        }
                                                    });
                                        }
                                        if(hasWon == false){
                                            startActivity(new Intent(AddAct.this, MenuActivity.class));
                                        }


                                    }
                                }
                            });
                }


            }
        });
    }

    private String checkRadius(){
        Location targetLocation = new Location("");//provider name is unnecessary
        String spinnerSelection = chooseBerg.getSelectedItem().toString();
        db.collection("challenge").document(ActiveChallenge)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull @NotNull Task<DocumentSnapshot> task) {
                      DocumentSnapshot doc = task.getResult();
                        List<String> berge = new ArrayList<String>();
                        berge = (ArrayList<String>) doc.get("bergTitel");
                        geoBerge = (ArrayList<GeoPoint>) doc.get("berge");
                        for(int i = 0; i < berge.size(); i++){
                            Log.d(TAG, "Bin im If Siiiiiir");
                            if(berge.get(i).equals(spinnerSelection)){
                                 activeBerg = geoBerge.get(i);
                                 Log.d(TAG, "GeoPunkt: " + activeBerg.toString());

                                 break;
                            }
                        }



                    }
                }).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                targetLocation.setLatitude(activeBerg.getLatitude());//your coords of course
                targetLocation.setLongitude(activeBerg.getLongitude());
                Log.d(TAG, "CuurentLoc: " + currentLocation);
                float distance = currentLocation.distanceTo(targetLocation);
                Log.d(TAG, "distance : " + distance);
                if(distance>1000){

                    isClose = false;

                }
                else {
                    isClose = true;

                }
                Log.d(TAG, "isClose: " + isClose);

            }
        });

        wait = "hallo";
        return wait;
    }

    private void updateGPS(){
        //Erst nach Erlaubnis des Users fragen
        // Die aktuelle Location herausfinden
        // Die UI updaten

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(AddAct.this);
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            // Der User hat uns die Erlaubnis bereits gegeben
            fusedLocationProviderClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() { // Benötigt Code im Manifest
                @Override
                public void onSuccess(Location location) {
                    // Wir haben die Ererlaubnis und bekommen die Werte der Location
                    currentLocation= location;
                    Log.d(TAG, "UpdateGPS Loc: " + currentLocation);
                }
            });
        }
        else{
            // Die Erlaubnis wurde noch nicht gegeben
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                requestPermissions(new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSIONS_FINE_LOCATION);
            }
        }
    }

    // Überschreiben einer der aus der APPMainCompat übergebenen Methoden, die eigetnlcih für alles zuständig ist in der MainActivity
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch(requestCode){
            case PERMISSIONS_FINE_LOCATION:
                if (grantResults[0]== PackageManager.PERMISSION_GRANTED){
                    updateGPS();
                }
                else{
                    Toast.makeText(this,"Diese App benötigit Ihre Erlaubnis den Standort zu tracken",Toast.LENGTH_SHORT).show(); //Fehlermeldung ausgeben
                    finish(); // Programm verlassen
                }
                break;
        }
    }

}