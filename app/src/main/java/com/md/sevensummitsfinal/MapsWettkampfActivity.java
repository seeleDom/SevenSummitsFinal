package com.md.sevensummitsfinal;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
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

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class MapsWettkampfActivity extends Fragment {

    private GoogleMap mMap;
    private static final String TAG = "MainActivity2";
    private static FirebaseFirestore db;

    //SearchActivity.getTitelChallenge();

    private static List<GeoPoint> berg; // Für das Speichern in der DB
    private static List<String> titel; // Für das Speichern in der DB
    private static List<Boolean> geschafft; // Für das Speichern in der DB
    String uPath;
    String path;
    String winner;
    NavController navController;


    private OnMapReadyCallback callback = new OnMapReadyCallback() {

        /**
         * Manipulates the map once available.
         * This callback is triggered when the map is ready to be used.
         * This is where we can add markers or lines, add listeners or move the camera.
         * In this case, we just add a marker near Sydney, Australia.
         * If Google Play services is not installed on the device, the user will be prompted to
         * install it inside the SupportMapFragment. This method will only be triggered once the
         * user has installed Google Play services and returned to the app.
         */
        @Override
        public void onMapReady(GoogleMap googleMap) {
            mMap = googleMap;
            String pathSearch = SearchActivity.getTitelChallenge();
            if(uPath != null){
                path = uPath;

            } else {
                path = pathSearch;

            }

            Log.d(TAG, "Path: " + path);
            DocumentReference docRef = db.collection("challenge").document(path); //SearchActivity.getTitelChallenge() es funktioniert nur hard coded
            docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                    if (task.isSuccessful()) {
                        DocumentSnapshot snap = task.getResult();

                        if (snap.exists()) {
                            //Log.d(TAG, "DocumentSnapshot data: " + snap.getData() + " und berge : " + snap.get("berge"));
                            //Log.d(TAG, "Titel: " + SearchActivity.getTitelChallenge() + snap.get("bergeCheck"));

                            berg = (List<GeoPoint>)snap.get("berge");
                            titel = (List<String>)snap.get("bergTitel");
                            geschafft = (List<Boolean>)snap.get("bergeCheck");
                        } else {
                            //Log.d(TAG, "Kein Dokument gefunden");
                        }
                    } else {
                        //Log.d(TAG, "Fehlermeldung ", task.getException());
                    }
                }

            }).addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete( Task<DocumentSnapshot> task) {
                    int i = 0;
                    while(i< berg.size()){ // Wenn die Datenbankabfrage abgeschlossen ist werden die marker auf die Map gesetzt
                        LatLng m = new LatLng(berg.get(i).getLatitude(),berg.get(i).getLongitude());
                        Log.d(TAG, "Titel: " + geschafft.get(i));
                        if(geschafft.get(i)==true){ // Wenn der User den berg bereits bestiegen hat soll der Marker Grün sein
                            mMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.defaultMarker(180)).position(m).title(titel.get(i)));
                        }
                        else{
                            mMap.addMarker(new MarkerOptions().position(m).title(titel.get(i)));
                        }
                        i++;
                        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(m, 8.0f));
                    }
                }
            });



            mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() { // Hier kann definiert werden was bei Click auf den marker ausgeführt werden soll
                @Override
                public boolean onMarkerClick(Marker marker) {
                    String markerName = marker.getTitle();
                    Intent edit = new Intent(MapsWettkampfActivity.this.getActivity(), AddAct.class);
                    edit.putExtra("marker", markerName); // Gibt dem Entent Werte mit
                    startActivity(edit);
                    return false;
                }
            });
        }
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        db = FirebaseFirestore.getInstance();
        FirebaseAuth currentUser = FirebaseAuth.getInstance();
        String userID = currentUser.getUid();
        DocumentReference doc = db.collection("users").document(userID);
        doc.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable @org.jetbrains.annotations.Nullable DocumentSnapshot value, @Nullable @org.jetbrains.annotations.Nullable FirebaseFirestoreException error) {
                uPath = value.getString("ActiveChallenge");
                Log.d(TAG, "ActiveChallenge" + uPath);

            }
        });
        return inflater.inflate(R.layout.fragment_maps_wettkampf_activity, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SupportMapFragment mapFragment =
                (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(callback);
        }

    }



}