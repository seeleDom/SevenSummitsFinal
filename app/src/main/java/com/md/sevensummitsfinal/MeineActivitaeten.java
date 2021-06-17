package com.md.sevensummitsfinal;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Array;
import java.util.ArrayList;

import static android.content.ContentValues.TAG;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MeineActivitaeten#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MeineActivitaeten extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    String userID;
    FirebaseFirestore db;
    FirebaseAuth currentUser;
    private RecyclerView rv;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager llm;

    public MeineActivitaeten() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MeineActivitaeten.
     */
    // TODO: Rename and change types and number of parameters
    public static MeineActivitaeten newInstance(String param1, String param2) {
        MeineActivitaeten fragment = new MeineActivitaeten();
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
        View view = inflater.inflate(R.layout.fragment_meine_activitaeten, container, false);
        db = FirebaseFirestore.getInstance();
        currentUser = FirebaseAuth.getInstance();
        userID = currentUser.getUid();
        rv = view.findViewById(R.id.recyclerView);
        //rv.setHasFixedSize(true);
        llm = new LinearLayoutManager(getContext());
        rv.setLayoutManager(llm);
        FloatingActionButton add = view.findViewById(R.id.addActivity);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), AddAct.class));
            }
        });

        getActivities();
        return view;
    }

    public void getActivities(){
        userID = currentUser.getCurrentUser().getUid();
        ArrayList<exampleActivity> myactivities = new ArrayList<exampleActivity>();
        db.collection("activities")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull @NotNull Task<QuerySnapshot> task) {
                        for(QueryDocumentSnapshot doc : task.getResult()){
                            if(doc.getString("userID").equals(userID)){
                                String titel = doc.getString("Titel");
                                String beschreibung = doc.getString("Beschreibung");
                                myactivities.add(new exampleActivity(titel, beschreibung, R.drawable.ic_baseline_home_24));
                            }
                        }
                        Log.d(TAG, "Array: " + myactivities);
                        adapter = new CardAdapter(myactivities);
                        rv.setAdapter(adapter);
                    }
                });

        Log.d(TAG, "Array: " + myactivities);

    }
}