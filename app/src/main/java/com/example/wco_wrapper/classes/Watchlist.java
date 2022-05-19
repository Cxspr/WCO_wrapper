package com.example.wco_wrapper.classes;

import android.util.Log;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Watchlist {
    private List<Series> watchlist;
    private String parentDir;
    private boolean pendingChanges;

    public Watchlist(ArrayList<Series> list) {
        watchlist = list;
    };
    public Watchlist(ArrayList<Series> list, String parentDir) {
        watchlist = list;
        this.parentDir = parentDir;
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
        pendingChanges = true;
    }
    public void removeFromWatchlist(Series series) {
        for (Series s: watchlist) {
            if (s.compare(series)){
                watchlist.remove(s);
                pendingChanges = true;
                return;
            }
        }
    }

    public void updateEp(Series series){
        int i;
        for (i = 0; i < watchlist.size(); i++){
            if ((watchlist.get(i).compare(series))){
                watchlist.get(i).overrideEpInfo(series);
                pendingChanges = true;
                return;
            }
        }
    }

    public Series getStoredSeries(Series series) {
        for (Series s: watchlist) {
            if (s.compare(series)){
                return s;
            }
        }
        return null;
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

    public static Watchlist genWatchlistFromJson(String json) throws IOException {
        Moshi moshi = new Moshi.Builder().build();
        JsonAdapter<Watchlist> jsonAdapter = moshi.adapter(Watchlist.class);
        return jsonAdapter.fromJson(json);
    }

    public static Watchlist genWatchlist(String parentDir) throws IOException {
        File watchlistFile = new File(parentDir, "watchlist.json");
        //Read JSON and build into string
        StringBuilder text = new StringBuilder();
        try {
            BufferedReader br = new BufferedReader(new FileReader(watchlistFile));
            String line;

            while ((line = br.readLine()) != null){
                text.append(line);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        String json = text.toString();

        //Create Watchlist object from JSON using Moshi library
        Moshi moshi = new Moshi.Builder().build();
        JsonAdapter<Watchlist> jsonAdapter = moshi.adapter(Watchlist.class);
        return jsonAdapter.fromJson(json);
    }

    public void updateWatchlistJSON() {
        if (!pendingChanges) {return;}
        try {
            pendingChanges = false;
            FileOutputStream stream = new FileOutputStream(parentDir + "/watchlist.json");
            Log.i("File Written at: ", parentDir + "/watchlist.json");
            stream.write(this.watchlistToJson().getBytes());
            stream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void pushChange(){
        pendingChanges = true;
    }
    public boolean getChangeStatus(){
        return pendingChanges;
    }

}
