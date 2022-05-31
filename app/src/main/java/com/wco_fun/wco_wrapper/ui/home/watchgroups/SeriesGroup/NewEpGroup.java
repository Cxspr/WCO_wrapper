package com.wco_fun.wco_wrapper.ui.home.watchgroups.SeriesGroup;

import android.app.Activity;

import com.wco_fun.wco_wrapper.MainActivity;
import com.wco_fun.wco_wrapper.classes.series.Series;
import com.wco_fun.wco_wrapper.initialization.NewEpScraper;
import com.wco_fun.wco_wrapper.ui.home.watchgroups.SeriesCard.SeriesCard;
import com.wco_fun.wco_wrapper.ui.home.watchgroups.WatchgroupAdapter;

import java.util.ArrayList;

public class NewEpGroup extends SeriesGroup{
    NewEpScraper runThread;
    WatchgroupAdapter adapter;
    Activity activity;
    ArrayList<Series> series;

    public NewEpGroup(ArrayList<Series> series, MainActivity activity){
        this.title = "New Episodes";
        this.variant = 3;
        this.series = series;
        this.activity = activity;
        this.runThread = new NewEpScraper(series, activity);
    }

    @Override
    public void variantOperation() {
        runThread.cancel();
        try {
            runThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        runThread = new NewEpScraper(series, activity, adapter);
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
