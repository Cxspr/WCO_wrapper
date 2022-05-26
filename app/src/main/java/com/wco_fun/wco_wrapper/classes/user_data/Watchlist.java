package com.wco_fun.wco_wrapper.classes.user_data;

import android.util.Log;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.wco_fun.wco_wrapper.classes.WatchGroup;
import com.wco_fun.wco_wrapper.classes.series.Series;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class Watchlist extends WatchGroup {
    private String parentDir;
    private boolean pendingChanges;

    public Watchlist(ArrayList<Series> watchlist, String parentDir) {
        watchgroup = watchlist;
        this.parentDir = parentDir;
    }

    @Override
    public void add(Series s) {
        super.add(s);
        pendingChanges = true;
    }

    @Override
    public void override(Series series) {
        super.override(series);
        pendingChanges = true;
    }

    @Override
    public void remove(Series s) {
        super.remove(s);
        pendingChanges = true;
    }

    //convert this class instance to a JSON formatted string
    private String watchlistToJson() {
        Moshi moshi = new Moshi.Builder().build();
        JsonAdapter<Watchlist> jsonAdapter = moshi.adapter(Watchlist.class);
        String json = jsonAdapter.toJson(this);
        return json;
    }

    //update the stored JSON data on the device
    public void updateWatchlistJson() {
        if (!pendingChanges) {return;}
        try {
            pendingChanges = false;
            FileOutputStream stream = new FileOutputStream(parentDir + "/watchlist.json");
            Log.i("File Written at: ", parentDir + "/watch_data.json");
            stream.write(this.watchlistToJson().getBytes());
            stream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //generate watchlist object from pre-existing JSON file
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
        } catch (IOException e) {
            e.printStackTrace();
        }
        String json = text.toString();
        //Create Watchlist object from JSON using Moshi library
        Moshi moshi = new Moshi.Builder().build();
        JsonAdapter<Watchlist> jsonAdapter = moshi.adapter(Watchlist.class);
        return jsonAdapter.fromJson(json);
    }
}
