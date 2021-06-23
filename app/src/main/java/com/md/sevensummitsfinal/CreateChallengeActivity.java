package com.md.sevensummitsfinal;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CreateChallengeActivity#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CreateChallengeActivity extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private static String newTitel;

    public CreateChallengeActivity() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment CreateChallengeActivity.
     */
    // TODO: Rename and change types and number of parameters
    public static CreateChallengeActivity newInstance(String param1, String param2) {
        CreateChallengeActivity fragment = new CreateChallengeActivity();
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
        View view = inflater.inflate(R.layout.fragment_create_challenge_activity, container, false);
        Button best = view.findViewById(R.id.btn_firstStep);
        EditText name = view.findViewById(R.id.tv_name);

        best.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                newTitel = name.getText().toString().trim();
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                MapsActivity2 ma2 = new MapsActivity2();
                FragmentTransaction transaction = fragmentManager.beginTransaction();
                transaction.replace(R.id.Nav_host_container, ma2);
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });
        return view;
    }

    public static String getNewTitel(){
        return newTitel;
    }
}