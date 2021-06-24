package com.md.sevensummitsfinal;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.md.sevensummitsfinal.R;
import com.md.sevensummitsfinal.ui.home.HomeFragment;

import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.CheckReturnValue;

import static android.content.ContentValues.TAG;
import static java.lang.reflect.Array.getLength;

public class MenuActivity extends AppCompatActivity {
    private AppBarConfiguration mAppBar;
    FirebaseFirestore db;
    FirebaseAuth currentuser;
    String ActiveChallenge;
    ArrayList<String> berge;
    ArrayList<String> user;
    int countBerge;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);



        db = FirebaseFirestore.getInstance();
        currentuser = FirebaseAuth.getInstance();

        checkUserWon();

        checkChallenge(currentuser);
        Menu menu = navigationView.getMenu();
        MenuItem menuItem = menu.findItem(R.id.nav_logout);

        menuItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                logout();
                return true;
            }
        });

        mAppBar = new AppBarConfiguration.Builder(R.id.nav_home, R.id.nav_profil, R.id.nav_myActivities)
                .setDrawerLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBar);
        NavigationUI.setupWithNavController(navigationView, navController);

        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.add(R.id.HomeFrag, HomeFragment.class, null);
        transaction.commit();

        Button btnCreate = findViewById(R.id.btn_wettkampErstellen);
        Button btnSearch = findViewById(R.id.btn_wettkampfBeitreten);

        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = getSupportFragmentManager();
                SearchActivity search = new SearchActivity();
                FragmentTransaction transaction = fragmentManager.beginTransaction();
                transaction.replace(R.id.Nav_host_container, search);
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });

        btnCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = getSupportFragmentManager();
                CreateChallengeActivity create = new CreateChallengeActivity();
                FragmentTransaction transaction = fragmentManager.beginTransaction();
                transaction.replace(R.id.Nav_host_container, create);
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });
    }


    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBar)
                || super.onSupportNavigateUp();
    }

    public void logout(){
        FirebaseAuth.getInstance().signOut();
        startActivity(new Intent(this,Login.class));
    }

    public void checkChallenge(FirebaseAuth user){
        String userID = user.getUid();
        DocumentReference doc = db.collection("users").document(userID);
        doc.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable @org.jetbrains.annotations.Nullable DocumentSnapshot value, @Nullable @org.jetbrains.annotations.Nullable FirebaseFirestoreException error) {
                if(value.getString("ActiveChallenge") != null){
                    ActiveChallenge = value.getString("ActiveChallenge");
                    FragmentManager fragmentManager = getSupportFragmentManager();
                    MapsWettkampfActivity create = new MapsWettkampfActivity();
                    FragmentTransaction transaction = fragmentManager.beginTransaction();
                    transaction.replace(R.id.Nav_host_container, create);
                    transaction.addToBackStack(null);
                    transaction.commit();
                }
            }
        });
    }

    public void checkUserWon(){

        String userID = FirebaseAuth.getInstance().getUid();
        DocumentReference doc = db.collection("users").document(userID);
        doc.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable @org.jetbrains.annotations.Nullable DocumentSnapshot value, @Nullable @org.jetbrains.annotations.Nullable FirebaseFirestoreException error) {
                if(value.getString("ActiveChallenge") != null){
                    ActiveChallenge = value.getString("ActiveChallenge");
                } else {
                    return;
                }

                db.collection("challenge").document(ActiveChallenge)
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull @NotNull Task<DocumentSnapshot> task) {
                                if(task.isSuccessful()){
                                    DocumentSnapshot document = task.getResult();
                                    berge = (ArrayList<String>) document.get("bergTitel");
                                    int anzahlBerge = berge.size();
                                    user = (ArrayList<String>) document.get("currentUsers");
                                    int anzahlUser = user.size();

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
                                                            if(countBerge == anzahlBerge){
                                                                String winner = user.get(counter);
                                                                Map<String, Object> challengeWinenr = new HashMap<>();
                                                                challengeWinenr.put("winner", winner);
                                                            }
                                                        }
                                                    }
                                                });
                                    }

                                }
                            }
                        });

            }
        });
    }

}