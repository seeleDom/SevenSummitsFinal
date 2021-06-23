package com.md.sevensummitsfinal;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SearchActivity#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SearchActivity extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // Auf dieser Seite soll der user einen Wettkamp suchen können

    private static String titelChallenge; // Für das Speichern in der DB
    public static final String TAG = "SearchActivity";
    public static FirebaseFirestore db= FirebaseFirestore.getInstance();

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public SearchActivity() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SearchActivity.
     */
    // TODO: Rename and change types and number of parameters
    public static SearchActivity newInstance(String param1, String param2) {
        SearchActivity fragment = new SearchActivity();
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
        View view = inflater.inflate(R.layout.fragment_search_activity, container, false);
        Button reg = view.findViewById(R.id.btn_register);
        EditText sea = view.findViewById(R.id.tv_search);

        reg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String val = sea.getText().toString().trim();
                titelChallenge = null;
                db.collection("challenge")
                        .whereEqualTo("titel", val)
                        .limit(1)
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    String a = task.getResult().getDocuments().get(0).getId();
                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                        Log.d(TAG, document.getId() + " => " + document.getData());
                                        titelChallenge = document.getId();
                                        Log.d(TAG, titelChallenge);
                                        Intent i = new Intent(SearchActivity.this.getActivity(), MapsWettkampfActivity.class);
                                        startActivity(i);

                                        //Den Wettkampf dem User hinzufügen!!!!!!!!!!!!!!!!!!!
                                        //
                                        //
                                        //
                                        //

                                    }
                                } else {
                                    Log.d(TAG, "Error getting documents: ", task.getException());
                                }
                            }
                        });
            }
        });
        return view;
    }

    public static String getTitelChallenge(){

        return titelChallenge;
    }

    public static void setTitelChallenge(String param){

        titelChallenge = null;
        db.collection("challenge")
                .whereEqualTo("titel", param)
                .limit(1)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            String a = task.getResult().getDocuments().get(0).getId();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + " => " + document.getData());
                                titelChallenge = document.getId();
                                Log.d(TAG, titelChallenge);
                                //Intent i = new Intent(getAppContext(), MapsWettkampfActivity.class);
                                //getAppContext().startActivity(i);
                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
        titelChallenge = param;
    }
}