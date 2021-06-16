package com.md.sevensummitsfinal;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ProfilFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProfilFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private EditText name, vorname, email, straße, hr, ort, plz, datum, telefon;
    private FloatingActionButton btnEdit;
    private Button saveChange;
    FirebaseFirestore db;
    FirebaseAuth currentUser;
    String userID;


    public ProfilFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ProfilFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ProfilFragment newInstance(String param1, String param2) {
        ProfilFragment fragment = new ProfilFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profil, container, false);
        db = FirebaseFirestore.getInstance();
        currentUser = FirebaseAuth.getInstance();

        name = view.findViewById(R.id.editTextName);
        vorname = view.findViewById(R.id.editTextVorname);
        email = view.findViewById(R.id.editTextEmail);
        straße = view.findViewById(R.id.editTextStraße);
        hr = view.findViewById(R.id.editTextHr);
        plz = view.findViewById(R.id.editTextPLZ);
        ort = view.findViewById(R.id.editTextOrt);
        telefon = view.findViewById(R.id.editTextTelefon);
        saveChange = view.findViewById(R.id.btnSaveChanges);
        btnEdit = view.findViewById(R.id.btnEdit);

        name.setText("");
        name.setFocusable(false);

        vorname.setText("");
        vorname.setFocusable(false);

        email.setText("");
        email.setFocusable(false);

        straße.setText("");
        straße.setFocusable(false);

        hr.setText("");
        hr.setFocusable(false);

        ort.setText("");
        ort.setFocusable(false);

        plz.setText("");
        plz.setFocusable(false);

        telefon.setText("");
        telefon.setFocusable(false);

        btnEdit.setVisibility(View.VISIBLE);
        saveChange.setVisibility(View.GONE);

        btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                name.setFocusableInTouchMode(true);
                vorname.setFocusableInTouchMode(true);
                straße.setFocusableInTouchMode(true);
                hr.setFocusableInTouchMode(true);
                plz.setFocusableInTouchMode(true);
                ort.setFocusableInTouchMode(true);
                telefon.setFocusableInTouchMode(true);
                saveChange.setVisibility(View.VISIBLE);
                btnEdit.setVisibility(View.GONE);
            }
        });


        saveChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                name.setFocusable(false);
                vorname.setFocusable(false);
                straße.setFocusable(false);
                hr.setFocusable(false);
                plz.setFocusable(false);
                ort.setFocusable(false);
                telefon.setFocusable(false);
                saveChange.setVisibility(View.GONE);
                btnEdit.setVisibility(View.VISIBLE);
                putUserData();
                getUserData();
            }
        });

        getUserData();



        return view;
    }


    public void getUserData(){
        userID = currentUser.getCurrentUser().getUid();

        DocumentReference doc = db.collection("users").document(userID);
        doc.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable @org.jetbrains.annotations.Nullable DocumentSnapshot value, @Nullable @org.jetbrains.annotations.Nullable FirebaseFirestoreException error) {
                email.setText(value.getString("email"));
                if(value.getString("vorname") != null){
                    vorname.setText(value.getString("vorname"));
                }
                if(value.getString("name") != null){
                    name.setText(value.getString("name"));
                }
                if(value.getString("strasse") != null){
                    straße.setText(value.getString("strasse"));
                }
                if(value.getString("ort") != null){
                    ort.setText(value.getString("ort"));
                }
                if(value.getString("plz") != null){
                    plz.setText(value.getString("plz"));
                }
                if(value.getString("hnr") != null){
                    hr.setText(value.getString("hnr"));
                }
                if(value.getString("telefon") != null) {
                    telefon.setText(value.getString("telefon"));
                }
            }
        });
    }

    public void putUserData(){
        userID = currentUser.getCurrentUser().getUid();

        DocumentReference doc = db.collection("users").document(userID);
        String nachname = name.getText().toString().trim();
        String cVorname = vorname.getText().toString().trim();
        String cOrt = ort.getText().toString().trim();
        String cPLZ = plz.getText().toString().trim();
        String strasse = straße.getText().toString().trim();
        String hnr = hr.getText().toString().trim();
        String phone = telefon.getText().toString().trim();

        Map<String, Object> uUser = new HashMap<>();
        uUser.put("name", nachname);
        uUser.put("vorname", cVorname);
        uUser.put("ort", cOrt);
        uUser.put("plz", cPLZ);
        uUser.put("strasse", strasse);
        uUser.put("hnr", hnr);
        uUser.put("telefon", phone);

        doc.update(uUser).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull @NotNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(getActivity(), "Daten wurden erfolgreich aktualisiert", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getActivity(),"Bei der Aktualisierung ist ein Fehler aufgetreten!", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
}
