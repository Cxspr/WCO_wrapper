package com.wco_fun.wco_wrapper.ui.home.watchgroups.SeriesGroup;

import com.wco_fun.wco_wrapper.classes.series.Series;
import com.wco_fun.wco_wrapper.ui.home.watchgroups.SeriesCard.SeriesCard;
import com.wco_fun.wco_wrapper.ui.home.watchgroups.SeriesCard.SeriesCardGeneric;
import com.wco_fun.wco_wrapper.ui.home.watchgroups.WatchgroupAdapter;

import java.sql.Array;
import java.util.ArrayList;

public abstract class SeriesGroup {
    protected String title;
    protected ArrayList<SeriesCard> contents = new ArrayList<>();
    protected int variant;

    public void setTitle(String title) {this.title = title;}
    public String getTitle() {return this.title;}

    public ArrayList<SeriesCard> getContents() {
        return contents;
    }
    public void setContents(ArrayList<SeriesCard> contents) {
        this.contents = contents;
    }

    public int getVariant() {
        return variant;
    }
    public void attachAdapter(WatchgroupAdapter adapter) {}
    public void variantOperation() {};
}
