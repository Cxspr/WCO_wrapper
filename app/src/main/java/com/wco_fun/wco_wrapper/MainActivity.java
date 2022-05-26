package com.wco_fun.wco_wrapper;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.StrictMode;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.wco_fun.wco_wrapper.classes.series.Series;
import com.wco_fun.wco_wrapper.classes.series.SeriesControllable;
import com.wco_fun.wco_wrapper.classes.user_data.WatchData;
import com.wco_fun.wco_wrapper.classes.user_data.Watchlist;
import com.wco_fun.wco_wrapper.databinding.ActivityMainBinding;

import android.view.Menu;
import android.view.MenuItem;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {


    private AppBarConfiguration appBarConfiguration;
    private ActivityMainBinding binding;
    private Menu menu;
    private File watchlistFile;


    private String parentDir;
    private Watchlist watchlist;
    private WatchData watchData;


    //globalized GETTER for the watchlist
    public Watchlist getWatchlist() {
        return watchlist;
    }
    public WatchData getWatchData() { return watchData; }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //TODO get rid of this and ensure no parallel procs run on main thread
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        parentDir = String.valueOf(this.getFilesDir());

        //import Watchlist from files
        if (!(new File(parentDir + "/watchlist.json")).exists()){
            watchlist = new Watchlist(new ArrayList<Series>(), parentDir);
        } else {
            try {
                watchlist = Watchlist.genWatchlist(parentDir);
            } catch (IOException e) {
                e.printStackTrace();
            } if (watchlist == null) {watchlist = new Watchlist(new ArrayList<Series>(), parentDir);}
        }
        //import Watchdata from files
        if (!(new File(parentDir + "/watch_data.json")).exists()){
            watchData = new WatchData(new ArrayList<SeriesControllable>(), parentDir);
        } else {
            try {
                watchData = WatchData.genWatchData(parentDir);
            } catch (IOException e) {
                e.printStackTrace();
            } if (watchData == null) {watchData = new WatchData(new ArrayList<SeriesControllable>(), parentDir);}
        }

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        appBarConfiguration = new AppBarConfiguration.Builder(navController.getGraph()).build();
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        this.menu = menu;
        menu.findItem(R.id.menu_search).setVisible(true);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.menu_search){
            Bundle bundle = new Bundle();
            bundle.putString("link","https://www.wcofun.com/dubbed-anime-list");
            NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
            navController.navigate(R.id.media_search, bundle);
            menu.findItem(R.id.menu_search).setVisible(false);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPause() {
        super.onPause();
        //modify JSON file only when the app is paused
        watchlist.updateWatchlistJson();
        watchData.updateWatchDataJson();
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