package com.md.sevensummitsfinal;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.app.Activity;
import android.content.Intent;
import android.location.Location;
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
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.md.sevensummitsfinal.databinding.ActivityMapsBinding;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MapsActivity2 extends Fragment {

    // Diese Klasse ist für die Erstellung eines Wettkampfes verantwortlich
    private static final String TAG = "MainActivity2";
    private static FirebaseFirestore db= FirebaseFirestore.getInstance();
    private static String uID;
    private static String titelWettkampf = "ersterWettkampf";
    private static GoogleMap mMap;
    private ActivityMapsBinding binding;
    private static ArrayList<String> bergTitel = new ArrayList<String>(); // Für das Speichern in der App
    private static ArrayList <GeoPoint> berge = new ArrayList<GeoPoint>(); // Für das Speichern in der App
    private static ArrayList <Boolean> bergeCheck = new ArrayList<Boolean>(); // Für das Speichern in der App



    private static final int EDIT_REQUEST = 1;
    //FirebaseAuth currentUser;
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

            LatLng placeLocation = new LatLng(49,8);
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(placeLocation, 5.0f));
            int i = 0;
            if(bergTitel!=null){
                while (i<bergTitel.size()){
                    MarkerOptions gesetzt = new MarkerOptions();
                    gesetzt.title(bergTitel.get(i));
                    LatLng aktuell = new LatLng(berge.get(i).getLatitude(),berge.get(i).getLongitude());
                    gesetzt.position(aktuell);
                    mMap.addMarker(gesetzt);
                    i++;
                }
            }
            mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                @Override
                public void onMapClick(final LatLng latLng) {
                    PopUpActivity.setPosition(latLng);
                    FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                    PopUpActivity MWA = new PopUpActivity();
                    FragmentTransaction transaction = fragmentManager.beginTransaction();
                    transaction.replace(R.id.Nav_host_container, MWA);
                    transaction.addToBackStack(null);
                    transaction.commit();
                }
            });
        }
    };


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_maps_activity2, container, false);
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

    public static void saveinDB(MarkerOptions marker){
        // In Firebase speichern
        // erst nach dem letzten speichern aufrufen

        MarkerOptions markerOptions = marker;
        mMap.addMarker(markerOptions);
        GeoPoint geo = new GeoPoint(markerOptions.getPosition().latitude,markerOptions.getPosition().longitude);
        uID = FirebaseAuth.getInstance().getUid();
        List<String> currentUsers = new ArrayList<String>();
        currentUsers.add(uID);

        bergTitel.add(markerOptions.getTitle());
        berge.add(geo);
        bergeCheck.add(false);

        //DocumentReference doc = db.collection("challenge").document("e9YKFSzzdOSPrckDNBDx");
        Map<String, Object> mark = new HashMap<>();
        mark.put("bergTitel", bergTitel);
        mark.put("berge", berge);
        mark.put("bergeCheck",bergeCheck);
        String gesetzterTitel = CreateChallengeActivity.getNewTitel();
        mark.put("titel" , gesetzterTitel);
        mark.put("currentUsers", currentUsers);

        db.collection("challenge")
                .document()
                .set(mark)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                        berge.clear(); // Listen leeren
                        bergTitel.clear();
                        bergeCheck.clear();
                        SearchActivity.setTitelChallenge(gesetzterTitel); // Ide Dokument ID festlegen
                        String docID = SearchActivity.getTitelChallenge();

                        db.collection("challenge")
                                .get()
                                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull @NotNull Task<QuerySnapshot> task) {
                                        for(QueryDocumentSnapshot doc: task.getResult()){
                                            Log.d(TAG, "Titel" + doc.getString("titel"));
                                            if(doc.getString("titel").equals(gesetzterTitel)){
                                                Map<String, Object> userChallenge = new HashMap<>();
                                                String titel = doc.getId();
                                                userChallenge.put("ActiveChallenge", titel);
                                                DocumentReference ref = db.collection("users").document(uID);
                                                ref.update(userChallenge).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void unused) {

                                                    }
                                                });
                                            }
                                        }
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull @NotNull Exception e) {
                                        Log.d(TAG, "Fehler " + e.toString());
                                    }
                                });

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "Fehler");
                        //Toast.makeText(MapsActivity2.this, "Berg konnte nicht erfolgreich hinzugefügt werden" , Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public static void popUpResult(MarkerOptions marker){
        //MarkerOptions markerOptions = marker;
        mMap.addMarker(marker);
        GeoPoint geo = new GeoPoint(marker.getPosition().latitude,marker.getPosition().longitude);

        bergTitel.add(marker.getTitle());
        berge.add(geo);
        bergeCheck.add(false);
    }
}