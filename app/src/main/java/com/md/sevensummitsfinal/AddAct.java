package com.md.sevensummitsfinal;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
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


        currentUser = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        titel.setText("");
        titel.setFocusableInTouchMode(true);

        beschreibung.setText("");
        beschreibung.setFocusableInTouchMode(true);
        String android_id = Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID);

        chooseBerg = findViewById(R.id.spinnerBerg);
        userID = currentUser.getUid();
        db.collection("users").document(userID)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull @NotNull Task<DocumentSnapshot> task) {
                        if(task.isSuccessful()){
                            DocumentSnapshot doc = task.getResult();
                            if(doc.getString("ActiveChallenge") != null){
                                ActiveChallenge = doc.getString("ActiveChallenge");
                                db.collection("challenge").document(ActiveChallenge)
                                        .get()
                                        .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull @NotNull Task<DocumentSnapshot> task) {
                                                DocumentSnapshot docBerge = task.getResult();
                                                List<String> spinnerArray = (ArrayList<String>) docBerge.get("bergTitel");
                                                ArrayAdapter<String> adapter = new ArrayAdapter<String>(android_id, chooseBerg, spinnerArray);
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
                String uID = currentUser.getUid();
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
                                            Log.d(TAG, "UploadUrl: " + upload);
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
}