package com.wco_fun.wco_wrapper.ui.home.watchgroups.SeriesGroup;

import com.wco_fun.wco_wrapper.classes.series.Series;
import com.wco_fun.wco_wrapper.classes.series.SeriesControllable;
import com.wco_fun.wco_wrapper.classes.user_data.WatchData;
import com.wco_fun.wco_wrapper.ui.home.watchgroups.SeriesCard.SeriesCardReflective;

import java.util.ArrayList;

public class ReflectiveGroup extends SeriesGroup{
    private WatchData watchData;

    public ReflectiveGroup(WatchData watchData){
        this.setTitle("Continue");
        this.variant = 1;
        this.watchData = watchData;
        if (!watchData.isEmpty()) {
            //create the collection of series cards // image view bindings come with adapter level config
            for (SeriesControllable s : watchData.getWatching()){
                contents.add(new SeriesCardReflective(watchData, s));
            }
        }
    }

}
