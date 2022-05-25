package com.wco_fun.wco_wrapper.classes;

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
import java.util.Comparator;
import java.util.List;

public class Watchlist_LE {
    private List<Series_LE> watchlist;
    private String parentDir;
    private boolean pendingChanges;

    public Watchlist_LE(ArrayList<Series_LE> list) {
        watchlist = list;
    };
    public Watchlist_LE(ArrayList<Series_LE> list, String parentDir) {
        watchlist = list;
        this.parentDir = parentDir;
    }

    //Reconstruct watchlist from JSON
    public String watchlistToJson() {
        Moshi moshi = new Moshi.Builder().build();
        JsonAdapter<Watchlist_LE> jsonAdapter = moshi.adapter(Watchlist_LE.class);
        String json = jsonAdapter.toJson(this);
        return json;
    }

    public void addToWatchlist(Series_LE seriesLE) {
        for (Series_LE s: watchlist) {
            if (s.compare(seriesLE)){ //escape if entry found and it's not epInfo
                if (s.hasEpInfo() && !s.onWatchlist()) {
                    seriesLE.overrideAll(s);
                    watchlist.remove(s);
                    break;
                } else {
                    return;
                }
            }
        }
//        Collections.reverse(watchlist);
        watchlist.add(seriesLE);
//        Collections.reverse(watchlist);
        pendingChanges = true;
    }

    public void removeFromWatchlist(Series_LE seriesLE) {
        for (Series_LE s: watchlist) {
            if (s.compare(seriesLE)){
                watchlist.remove(s);
                pendingChanges = true;
                return;
            }
        }
    }

    public void removeSeriesFromWatchlist(Series_LE seriesLE) {
        for (Series_LE s: watchlist) {
            if (s.compare(seriesLE)){
                if (s.hasEpInfo()) {
                    s.onWatchlist(false);
                } else {
                    watchlist.remove(s);
                }
                pendingChanges = true;
                return;
            }
        }
    }

    public void removeEpInfoFromWatchlist(Series_LE seriesLE) {
        for (Series_LE s: watchlist) {
            if (s.compare(seriesLE)){
                s.removeEpInfo();
                pendingChanges = true;
                this.updateWatchlistJSON(); //done immediately since most triggers of this function don't cause a pause
                return;
            }
        }
    }

    public void updateEp(Series_LE seriesLE){
        int i;
        for (i = 0; i < watchlist.size(); i++){
            if ((watchlist.get(i).compare(seriesLE))){
                watchlist.get(i).overrideAll(seriesLE);
                pendingChanges = true;
                return;
            }
        }
    }

    public Series_LE getStoredSeries(Series_LE seriesLE) {
        for (Series_LE s: watchlist) {
            if (s.compare(seriesLE)){
                return s;
            }
        }
        return null;
    }

    //getters for watchlist and variants
    public ArrayList<Series_LE> getWatchlist() {
        return (ArrayList<Series_LE>) watchlist;
    }
    //get true watchlist elements
    public ArrayList<Series_LE> getTrueWatchlist() {
        ArrayList<Series_LE> res = new ArrayList<Series_LE>();
        for (Series_LE s : this.watchlist){
            if (s.onWatchlist()){
                res.add(s);
            }
        }
        Collections.reverse(res);
        return res;
    }
    //get series that user has started watching
    public ArrayList<Series_LE> getWatching() {
        ArrayList<Series_LE> res = new ArrayList<Series_LE>();
        for (Series_LE s : this.watchlist) {
            if (s.hasEpInfo()) {
                res.add(s);
            }
        }
        if (!res.isEmpty()) { //sort by last watched timestamp
            Collections.sort(res, new Comparator<Series_LE>() {
                @Override
                public int compare(Series_LE seriesLE, Series_LE s) {
                    return Long.valueOf(s.getLastWatched()).compareTo(Long.valueOf(seriesLE.getLastWatched()));
                }
            });
        }

        return res;
    }

    public boolean titleOnWatchlist(Series_LE seriesLE){
        for (Series_LE s: watchlist) {
            if (s.compare(seriesLE)) return s.onWatchlist();
        }
        return false;
    }

    public static Watchlist_LE genWatchlist(String parentDir) throws IOException {
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
        JsonAdapter<Watchlist_LE> jsonAdapter = moshi.adapter(Watchlist_LE.class);
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
