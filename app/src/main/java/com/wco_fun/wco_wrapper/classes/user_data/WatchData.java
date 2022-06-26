package com.wco_fun.wco_wrapper.classes.user_data;

import android.util.Log;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.wco_fun.wco_wrapper.classes.episode.Episode;
import com.wco_fun.wco_wrapper.classes.series.Series;
import com.wco_fun.wco_wrapper.classes.series.SeriesControllable;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

public class WatchData {
    private Map<String, SeriesControllable> seriesMap = new HashMap<String, SeriesControllable>();
    private String parentDir;
    private boolean pendingChanges;

    public WatchData(ArrayList<SeriesControllable> seriesData, String parentDir) {
        this.parentDir = parentDir;
        for (SeriesControllable s: seriesData) {
            seriesMap.put(s.getTitle(), s);
        }
    }

    public boolean contains(String title){ return seriesMap.containsKey(title);}
    public boolean contains(Series series){ return seriesMap.containsKey(series);} //unsure if this would work
    public boolean isEmpty(){ return seriesMap.isEmpty();}

    public SeriesControllable get(String title) {return seriesMap.get(title);}

    public void add(SeriesControllable s){
        seriesMap.put(s.getTitle(), s);
        pendingChanges = true;
    }

    public void remove(SeriesControllable s){
        seriesMap.remove(s.getTitle());
        pendingChanges = true;
    }

    public void remove(String title){
        seriesMap.remove(title);
        pendingChanges = true;
    }

    public void update(SeriesControllable s){
        seriesMap.put(s.getTitle(), s);
        pendingChanges = true;
    }

    public void updateDomain(String oldDomain, String newDomain) {
        for (SeriesControllable s: seriesMap.values()) {
            if (s.getSrc().indexOf(newDomain) == -1) {
                int index = s.getSrc().indexOf(oldDomain);
                int padding = oldDomain.length();
                s.setSrc(newDomain + s.getSrc().substring(index + padding));
                for (Episode e : s.getEpQueue()) {
                    if (e.getSrc().indexOf(newDomain) == -1) {
                        int epIndex = e.getSrc().indexOf(oldDomain);
                        int epPadding = oldDomain.length();
                        e.setSrc(newDomain + e.getSrc().substring(epIndex + epPadding));
                    }
                }
                pendingChanges = true;
            }
        }
        updateWatchDataJson();
    }

    public void updatePreloadLimit(int preloadLimit) {
        for (SeriesControllable s : seriesMap.values()) {
            if (s.getPreloadLimit() != preloadLimit) {
                s.setPreloadLimit(preloadLimit);
                pendingChanges = true;
            }
        }
        updateWatchDataJson();
    }


    //returns an arrayList of the seriesMap contents sorted by last watched time
    public ArrayList<SeriesControllable> getWatching() {
        ArrayList<SeriesControllable> res = new ArrayList<SeriesControllable>();
        Collection<SeriesControllable> resCollect = seriesMap.values();
        res.addAll(resCollect);
        if (!res.isEmpty()) {
            Collections.sort(res, new Comparator<SeriesControllable>() {
                @Override
                public int compare(SeriesControllable series, SeriesControllable s) {
                    return Long.valueOf(s.getLastWatched()).compareTo(Long.valueOf(series.getLastWatched()));
                }
            });
        }
        return res;
    }

//    public void notifyTrigger() {
//        pendingChanges = true;
//        updateWatchDataJson();
//    }

    //convert this class instance to a JSON formatted string
    private String watchDataToJson() {
        Moshi moshi = new Moshi.Builder().build();
        JsonAdapter<WatchData> jsonAdapter = moshi.adapter(WatchData.class);
        String json = jsonAdapter.toJson(this);
        return json;
    }

    //update the stored JSON data on the device
    public void updateWatchDataJson() {
        if (!pendingChanges) {return;}
        try {
            pendingChanges = false;
            FileOutputStream stream = new FileOutputStream(parentDir + "/watch_data.json");
            Log.i("File Written at: ", parentDir + "/watch_data.json");
            stream.write(this.watchDataToJson().getBytes());
            stream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //generate watchdata object from pre-existing JSON file
    public static WatchData genWatchData(String parentDir) throws IOException {
        File watchDataFile = new File(parentDir, "watch_data.json");
        //Read JSON and build into string
        StringBuilder text = new StringBuilder();
        try {
            BufferedReader br = new BufferedReader(new FileReader(watchDataFile));
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
        JsonAdapter<WatchData> jsonAdapter = moshi.adapter(WatchData.class);
        return jsonAdapter.fromJson(json);
    }
}
