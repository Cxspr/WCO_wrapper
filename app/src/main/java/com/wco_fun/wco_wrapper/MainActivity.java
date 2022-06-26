package com.wco_fun.wco_wrapper;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.preference.PreferenceManager;

import com.wco_fun.wco_wrapper.classes.CachedContent.SearchCache;
import com.wco_fun.wco_wrapper.classes.series.Series;
import com.wco_fun.wco_wrapper.classes.series.SeriesControllable;
import com.wco_fun.wco_wrapper.classes.user_data.WatchData;
import com.wco_fun.wco_wrapper.classes.user_data.Watchlist;
import com.wco_fun.wco_wrapper.databinding.ActivityMainBinding;
import com.wco_fun.wco_wrapper.ui.home.watchgroups.SeriesCard.SeriesCard;

import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration appBarConfiguration;
    private ActivityMainBinding binding;
    private Menu menu;

    private String parentDir;
    private Watchlist watchlist;
    private WatchData watchData;
    private SearchCache searchCache = new SearchCache();
    private ArrayList<SeriesCard> seeAllCache = new ArrayList();
    private SharedPreferences sharedPrefs;


    //globalized GETTER for globally accessible data classes
    public Watchlist getWatchlist() {
        return watchlist;
    }
    public WatchData getWatchData() { return watchData; }
    public SearchCache getSearchCache() { return searchCache; }

    public void setSeeAllCache(ArrayList<SeriesCard> seeAllCache) { this.seeAllCache = seeAllCache; }
    public ArrayList<SeriesCard> getSeeAllCache() { return seeAllCache; }
    public void updateDomainPreference(String oldDomain, String newDomain) {
        this.watchlist.updateDomain(oldDomain, newDomain);
        this.watchData.updateDomain(oldDomain, newDomain);
    }
    public void updatePreloadLimit(int limit) {
        this.watchData.updatePreloadLimit(limit);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

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

        sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);

        setSupportActionBar(binding.toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        appBarConfiguration = new AppBarConfiguration.Builder(navController.getGraph()).build();

        navController.addOnDestinationChangedListener(new NavController.OnDestinationChangedListener() {
            @Override
            public void onDestinationChanged(@NonNull NavController controller, @NonNull NavDestination destination, @Nullable Bundle arguments) {
                if (destination.getId() != R.id.homeScreen){
                    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                    menu.findItem(R.id.menu_search).setVisible(false);
                    menu.findItem(R.id.menu_settings).setVisible(false);
                } else {
                    getSupportActionBar().setDisplayHomeAsUpEnabled(false);
                    invalidateOptionsMenu();
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        this.menu = menu;
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.menu_search) {
            Bundle bundle = new Bundle();

            String prefLink = sharedPrefs.getString("domain_pref", "https://www.wcofun.com") + "/dubbed-anime-list";
            Log.i("SETTINGS: ", prefLink);
            bundle.putString("link", prefLink);
            Navigation.findNavController(this, R.id.nav_host_fragment_content_main)
                    .navigate(R.id.action_homeScreen_to_mediaSearch, bundle);
        } else if (id == R.id.menu_settings) {
            Navigation.findNavController(this, R.id.nav_host_fragment_content_main).navigate(R.id.action_homeScreen_to_settingsFragment);
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

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, appBarConfiguration)
                || super.onSupportNavigateUp();
    }
}