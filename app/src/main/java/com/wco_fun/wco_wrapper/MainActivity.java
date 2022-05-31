package com.wco_fun.wco_wrapper;

import android.app.ActionBar;
import android.app.StatusBarManager;
import android.graphics.Rect;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.os.StrictMode;

import androidx.core.content.ContextCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

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
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toolbar;

import org.jsoup.Jsoup;

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

    //globalized GETTER for globally accessible data classes
    public Watchlist getWatchlist() {
        return watchlist;
    }
    public WatchData getWatchData() { return watchData; }
    public SearchCache getSearchCache() { return searchCache; }

    public void setSeeAllCache(ArrayList<SeriesCard> seeAllCache) { this.seeAllCache = seeAllCache; }
    public ArrayList<SeriesCard> getSeeAllCache() { return seeAllCache; }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //TODO get rid of this and ensure no parallel procs run on main thread
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Jsoup.connect("https://www.wcofun.com") //Warm up Jsoup || first usage always seems significantly slower than rest
                            .userAgent("Mozilla/5.0 (Windows; U; WindowsNT 5.1; en-US; rv1.8.1.6) Gecko/20070725 Firefox/2.0.0.6")
                            .timeout(2000)
                            .maxBodySize(0)
                            .get();
                    Log.i("JSOUP: ", "WARMED UP");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();

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
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        appBarConfiguration = new AppBarConfiguration.Builder(navController.getGraph()).build();
//        NavigationUI.setupWithNavController(binding.toolbar, navController, appBarConfiguration);
//        NavigationUI.setu(this, navController, appBarConfiguration);
        navController.addOnDestinationChangedListener(new NavController.OnDestinationChangedListener() {
            @Override
            public void onDestinationChanged(@NonNull NavController controller, @NonNull NavDestination destination, @Nullable Bundle arguments) {
                if (destination.getId() != R.id.homeScreen){
                    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                } else {
                    getSupportActionBar().setDisplayHomeAsUpEnabled(false);
                }
            }
        });
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

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, appBarConfiguration)
                || super.onSupportNavigateUp();
    }
}