package com.wco_fun.wco_wrapper.ui.home.watchgroups.SeriesCard;

import android.net.Uri;
import android.os.Bundle;
import android.view.View;

import androidx.browser.customtabs.CustomTabsIntent;

import com.wco_fun.wco_wrapper.R;
import com.wco_fun.wco_wrapper.classes.episode.Episode;
import com.wco_fun.wco_wrapper.classes.series.SeriesControllable;
import com.wco_fun.wco_wrapper.classes.user_data.WatchData;

public class SeriesCardRespond extends SeriesCard{
    private SeriesControllable series;
//    private RecyclerView.Adapter host; //TODO change later once adapter object is created
    private WatchData watchData;


    public SeriesCardRespond(WatchData watchData, SeriesControllable series){
        this.watchData = watchData;
        this.series = series;
    }

    @Override
    public void setSeriesImage() {
        series.getSeriesImage(seriesImg);
    }

    @Override
    public void setSeries(SeriesControllable series) {
        this.series = series;
    }

    @Override
    public void configureVariant() {
        //visibility config
        this.title.setVisibility(View.VISIBLE);
        this.play.setVisibility(View.GONE);
        this.next.setVisibility(View.GONE);
        this.remove.setVisibility(View.VISIBLE);
        SeriesCard c = this;
        //button event configs

        //remove button on click listener
        remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                watchData.remove(series.getTitle());
//                watchData.updateWatchDataJson();
//                host.refreshRecycler(c);
            }
        });
    }

    @Override
    public Bundle configureClickEvent() {
        Bundle bundle = new Bundle();
        bundle.putString("link", series.getSrc());
        bundle.putString("title", series.getTitle());
        return bundle;
    }

}
