package com.md.sevensummitsfinal;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PopUpActivity#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PopUpActivity extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public PopUpActivity() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment PopUpActivity.
     */
    // TODO: Rename and change types and number of parameters
    public static PopUpActivity newInstance(String param1, String param2) {
        PopUpActivity fragment = new PopUpActivity();
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

        View view = inflater.inflate(R.layout.fragment_pop_up_activity, container, false);
        //final LatLng latlng = (LatLng) getIntent().getParcelableExtra("location");


        final EditText title = view.findViewById(R.id.title);
        Button boton = view.findViewById(R.id.save);

        // Normaler Marker hinzufuegen
        boton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                //MarkerOptions marker = new MarkerOptions().position(latlng);
                if (title.getText() != null) {
                  //  marker.title(title.getText().toString());
                }

                Intent resultIntent = new Intent();
                //resultIntent.putExtra("marker", marker);
                //setResult(Activity.RESULT_OK, resultIntent);
                //finish();
            }
        });

        // Letzten Marker hinzufuegen
        Button absenden = view.findViewById(R.id.button);
        absenden.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                //MarkerOptions marker = new MarkerOptions().position(latlng);
                if (title.getText() != null) {
                //    marker.title(title.getText().toString());
                }

                //Intent resultIntent = new Intent();
                //resultIntent.putExtra("marker", marker);
                //setResult(Activity.RESULT_OK, resultIntent);

                // Unterschied zu oben ist es jetzt in die DB zu schreiben und eine andere Seite aufzurufen
                //MapsActivity2.saveinDB(marker);

                //finish();

            }
        });
        return view;
    }
}