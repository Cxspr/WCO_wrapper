package com.wco_fun.wco_wrapper.ui.home.watchgroups.SeriesGroup;

import android.app.Activity;

import com.wco_fun.wco_wrapper.MainActivity;
import com.wco_fun.wco_wrapper.classes.series.Series;
import com.wco_fun.wco_wrapper.classes.series.SeriesControllable;
import com.wco_fun.wco_wrapper.classes.user_data.WatchData;
import com.wco_fun.wco_wrapper.initialization.GenreList;
import com.wco_fun.wco_wrapper.initialization.GenreScraper;
import com.wco_fun.wco_wrapper.initialization.NewEpScraper;
import com.wco_fun.wco_wrapper.ui.home.watchgroups.SeriesCard.SeriesCard;
import com.wco_fun.wco_wrapper.ui.home.watchgroups.SeriesCard.SeriesCardGeneric;
import com.wco_fun.wco_wrapper.ui.home.watchgroups.SeriesCard.SeriesCardReflective;
import com.wco_fun.wco_wrapper.ui.home.watchgroups.WatchgroupAdapter;

import java.util.ArrayList;

public class LoadableGroup extends SeriesGroup{

    public LoadableGroup(){
        this.variant = 2;
    }
    GenreScraper runThread;
    WatchgroupAdapter adapter;
    Activity activity;
    GenreList.Genre genre;

    public LoadableGroup(GenreList.Genre genre, MainActivity activity){
        this.title = genre.getTitle();
        this.variant = 2;
        this.genre = genre;
        this.activity = activity;
        this.runThread = new GenreScraper(genre, activity);
    }

    @Override
    public void variantOperation() {
        runThread.cancel();
        try {
            runThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        runThread = new GenreScraper(genre, activity, adapter);
        runThread.start();
    }

    @Override
    public void attachAdapter(WatchgroupAdapter adapter) {
        super.attachAdapter(adapter);
        this.adapter = adapter;
        runThread.attachRetLoc(adapter);
        runThread.start();
    }

}
