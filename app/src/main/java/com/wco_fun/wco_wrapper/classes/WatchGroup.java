package com.wco_fun.wco_wrapper.classes;

import android.util.Log;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.wco_fun.wco_wrapper.classes.series.Series;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class WatchGroup {
    protected List<Series> watchgroup = new ArrayList<Series>();

    public WatchGroup() {}
    public WatchGroup(ArrayList<Series> s){
        watchgroup = s;
    }

    public boolean contains(Series series){
        for (Series s: watchgroup){
            if (s.equals(series)) {
                return true;
            }
        } return false;
    }

    public void override(Series series) {
        for (Series s: watchgroup) {
            if (s.equals(series)) {
                s.override(series);
                return;
            }
        }
    }

    public void add(Series series){
        for (Series s: watchgroup){
            if (s.equals(series)) {
                s.override(series);
                return;
            }
        }
        watchgroup.add(series);
    }

    public void remove(Series series) {
        for (Series s: watchgroup){
            if (s.equals(series)) {
                watchgroup.remove(s);
                return;
            }
        }
    }

    public ArrayList<Series> getWatchgroup() {return (ArrayList<Series>) watchgroup;}

    public boolean isEmpty() { return watchgroup.isEmpty(); }

}
