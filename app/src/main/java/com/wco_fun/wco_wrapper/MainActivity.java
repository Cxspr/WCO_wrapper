package com.wco_fun.wco_wrapper;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.os.StrictMode;

import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.wco_fun.wco_wrapper.classes.Series;
import com.wco_fun.wco_wrapper.classes.Watchlist;
import com.wco_fun.wco_wrapper.databinding.ActivityMainBinding;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

import android.view.MenuItem;
import android.view.View;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration appBarConfiguration;
    private ActivityMainBinding binding;
    private String parentDir;
    private File watchlistFile;
    Watchlist wl;

    //globalized GETTER for the watchlist
    public Watchlist getWatchlist() {
        return wl;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        parentDir = String.valueOf(this.getFilesDir());

        if (!(new File(parentDir + "/watchlist.json")).exists()){
            wl = new Watchlist(new ArrayList<Series>(), parentDir);
        } else {
            try {
                wl = Watchlist.genWatchlist(parentDir);
            } catch (IOException e) {
                e.printStackTrace();
            } if (wl == null) {wl = new Watchlist(new ArrayList<Series>(), parentDir);}
        }

//        if (wl == null) { wl = new Watchlist(new ArrayList<Series>());}

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);
        BottomNavigationView bottomNav = findViewById(R.id.nav_view);
        bottomNav.setSelectedItemId(R.id.homeScreen);

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        appBarConfiguration = new AppBarConfiguration.Builder(navController.getGraph()).build();
        NavigationUI.setupWithNavController(bottomNav, navController);

        navController.addOnDestinationChangedListener(new NavController.OnDestinationChangedListener() {
            @Override
            public void onDestinationChanged(@NonNull NavController controller, @NonNull NavDestination destination, @Nullable Bundle arguments) {
                if (destination.getId() == R.id.mediaSelect || destination.getId() == R.id.homeScreen){
                    bottomNav.setVisibility(View.VISIBLE);
                } else {
                    bottomNav.setVisibility(View.INVISIBLE);
                }
            }
        });

        bottomNav.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.homeScreen:
                        navController.navigate(R.id.homeScreen);
                        return true;
                    case R.id.mediaSelect:
                        navController.navigate(R.id.mediaSelect);
                        return true;
                }

                return false;
            }
        });


        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(bottomNav, navController);


    }

    @Override
    protected void onPause() {
        super.onPause();
        //modify JSON file only when the app is paused
        wl.updateWatchlistJSON();
    }

//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, appBarConfiguration)
                || super.onSupportNavigateUp();
    }
}