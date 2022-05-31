package com.wco_fun.wco_wrapper.ui.home.watchgroups.SeriesGroup;

import com.wco_fun.wco_wrapper.classes.series.Series;
import com.wco_fun.wco_wrapper.ui.home.watchgroups.SeriesCard.SeriesCard;
import com.wco_fun.wco_wrapper.ui.home.watchgroups.SeriesCard.SeriesCardGeneric;

import java.util.ArrayList;

public class GenericGroup extends SeriesGroup{
    public GenericGroup(String title, ArrayList<Series> series){
        this.title = title;
        this.variant = 0;
        if (!series.isEmpty()){
            for (Series s: series){
                contents.add(new SeriesCardGeneric(s));
            }
        }
    }
}
