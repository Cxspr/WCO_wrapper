package com.example.wco_wrapper.classes;

import com.example.wco_wrapper.ui.home.WatchlistAdapter;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Watchlist {
    public List<Series> watchlist;
    public boolean pendingChanges = false;

    public Watchlist(ArrayList<Series> list) {
        watchlist = list;
    };
    //Reconstruct watchlist from JSON
    public String watchlistToJson() {
        Moshi moshi = new Moshi.Builder().build();
        JsonAdapter<Watchlist> jsonAdapter = moshi.adapter(Watchlist.class);
        String json = jsonAdapter.toJson(this);
        return json;
    }


    public void addToWatchlist(Series series) {
        for (Series s: watchlist) {
            if (s.compare(series)){
                return;
            }
        }
        Collections.reverse(watchlist);
        watchlist.add(series);
        Collections.reverse(watchlist);
    }
    public void removeFromWatchlist(Series series) {
        for (Series s: watchlist) {
            if (s.compare(series)){
                watchlist.remove(s);
                return;
            }
        }
    }

    public ArrayList<Series> getWatchlist() {
        return (ArrayList<Series>) watchlist;
    }

    //checks if a series with the given title is already on the watchlist
    public boolean containsTitle(String title){
        for (Series series: watchlist){
            if (series.getSeriesTitle().matches(title)){
                return true;
            }
        }
        return false;
    }

    public ArrayList<Series> getReversed() {
        ArrayList<Series> revWatchlist = (ArrayList<Series>) watchlist;
        Collections.reverse(revWatchlist);
        return revWatchlist;
    }

    public static Watchlist genWatchlist(String json) throws IOException {
        Moshi moshi = new Moshi.Builder().build();
        JsonAdapter<Watchlist> jsonAdapter = moshi.adapter(Watchlist.class);
        return jsonAdapter.fromJson(json);
    }
}
