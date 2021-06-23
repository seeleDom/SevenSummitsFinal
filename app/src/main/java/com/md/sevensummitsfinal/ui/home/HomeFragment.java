package com.md.sevensummitsfinal.ui.home;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.md.sevensummitsfinal.CreateChallengeActivity;
import com.md.sevensummitsfinal.R;
import com.md.sevensummitsfinal.SearchActivity;
import com.md.sevensummitsfinal.databinding.FragmentHomeBinding;

import org.jetbrains.annotations.NotNull;

public class HomeFragment extends Fragment {

    private HomeViewModel homeViewModel;
    private FragmentHomeBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();


        Button btn_Beitritt = binding.btnWettkampfBeitreten;
        Button btn_Erstellen = binding.btnWettkampErstellen;

        // if user hat einen Wettkampf in Firebase, dann soll diese Seite übersprungen werden
        // In Search ACtivitxy muss dann der Titel über setTitel() gesetzt werden
        // Probleme: das bleib ist das nach dem Erstellen nicht auf eine andere Seite gewechselt wird da von static aus kein AUfruf dees Inten möglich ist
        // Der letzte Berg wird nicht gespeichert

        btn_Beitritt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                SearchActivity search = new SearchActivity();
                FragmentTransaction transaction = fragmentManager.beginTransaction();
                transaction.replace(R.id.Nav_host_container, search);
                transaction.addToBackStack(null);
                transaction.commit();

            }
        });

        btn_Erstellen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                CreateChallengeActivity create = new CreateChallengeActivity();
                FragmentTransaction transaction = fragmentManager.beginTransaction();
                transaction.replace(R.id.Nav_host_container, create);
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}