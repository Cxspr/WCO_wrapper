package com.wco_fun.wco_wrapper.classes.CachedContent;

import com.wco_fun.wco_wrapper.classes.series.SeriesSearchable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SearchCache {
    private HashMap<Integer, ArrayList<SeriesSearchable>> cacheMap = new HashMap<>();
    private int returnTab = -1;

    public SearchCache() {}
    public SearchCache(ArrayList<SeriesSearchable> s, int tab) {
        cacheMap.put(tab, s);
        returnTab = tab;
    }

    public void updateCache(ArrayList<SeriesSearchable> s, int tab){
        if (!(false)) { //TODO attach to a setting relating to persistent search results
            cacheMap.clear();
            cacheMap.put(tab, s);//override single entry
        } else {
            cacheMap.put(tab, s);//override potentially existing entry of multiple
        }
        returnTab = tab;
    }

    public ArrayList<SeriesSearchable> getCache() {return cacheMap.get(returnTab);}

    public boolean isEmpty() {return (cacheMap.isEmpty() || returnTab == -1);}
    public void clear() {
        cacheMap.clear();
        returnTab = -1;
    }

    public int getReturnTab() {return returnTab;}
    public void clearReturnTab() {returnTab = -1;}
    public boolean hasReturnTab() {return returnTab != -1;}

}
