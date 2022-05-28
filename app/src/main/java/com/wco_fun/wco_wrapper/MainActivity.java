package com.wco_fun.wco_wrapper;

import android.app.ActionBar;
import android.app.StatusBarManager;
import android.graphics.Rect;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.StrictMode;

import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.wco_fun.wco_wrapper.classes.CachedContent.SearchCache;
import com.wco_fun.wco_wrapper.classes.series.Series;
import com.wco_fun.wco_wrapper.classes.series.SeriesControllable;
import com.wco_fun.wco_wrapper.classes.user_data.WatchData;
import com.wco_fun.wco_wrapper.classes.user_data.Watchlist;
import com.wco_fun.wco_wrapper.databinding.ActivityMainBinding;

import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import org.jsoup.Jsoup;

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
    private SearchCache searchCache = new SearchCache();


    //globalized GETTER for globally accessible data classes
    public Watchlist getWatchlist() {
        return watchlist;
    }
    public WatchData getWatchData() { return watchData; }
    public SearchCache getSearchCache() { return searchCache; }

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

        hideSystemUI();
        hideSystemBars();
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(binding.getRoot());


//        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);

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

    private void hideSystemBars() {
        WindowInsetsControllerCompat windowInsetsController =
                ViewCompat.getWindowInsetsController(getWindow().getDecorView());
        if (windowInsetsController == null) {
            return;
        }
        // Configure the behavior of the hidden system bars
        windowInsetsController.setSystemBarsBehavior(
                WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        );
        // Hide both the status bar and the navigation bar
        windowInsetsController.hide(WindowInsetsCompat.Type.statusBars());
    }

    private void hideSystemUI() {
        // Set the IMMERSIVE flag.
        // Set the content to appear under the system bars so that the content
        // doesn't resize when the system bars hide and show.
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_IMMERSIVE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
//                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
                        | View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
                        | View.SYSTEM_UI_FLAG_IMMERSIVE);
    }
}