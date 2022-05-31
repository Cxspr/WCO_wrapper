package com.wco_fun.wco_wrapper.ui.home.watchgroups.SeriesCard;

import android.net.Uri;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.view.View;

import androidx.browser.customtabs.CustomTabsIntent;
import androidx.navigation.Navigation;

import com.wco_fun.wco_wrapper.R;
import com.wco_fun.wco_wrapper.classes.episode.Episode;
import com.wco_fun.wco_wrapper.classes.series.SeriesControllable;
import com.wco_fun.wco_wrapper.classes.user_data.WatchData;

public class SeriesCardReflective extends SeriesCard{
    private SeriesControllable series;
//    private RecyclerView.Adapter host; //TODO change later once adapter object is created
    private WatchData watchData;


    public SeriesCardReflective(WatchData watchData, SeriesControllable series){
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
        this.title.setVisibility(View.GONE);
        this.play.setVisibility(View.VISIBLE);
        this.next.setVisibility(View.VISIBLE);
        this.remove.setVisibility(View.VISIBLE);
        SeriesCard c = this;
        //button event configs
        //play button on click listener
        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                series.updateLastWatched();

                CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
                CustomTabsIntent customTabsIntent = builder.build();
                customTabsIntent.launchUrl(view.getContext(), Uri.parse(series.getCurEp().getSrc()));

                host.refreshRecycler(c);//TODO implement at some point
            }
        });

        //remove button on click listener
        remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                watchData.remove(series.getTitle());
                watchData.updateWatchDataJson();
                host.refreshRecycler(c);
            }
        });

        //next button on click listener config
        if (series.hasMoreEps()){ //has a next episode
            next.setColorFilter(next.getContext().getColor(R.color.yellow_200));
            next.setEnabled(true);
            next.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Episode refEp = series.popEpQueue();//pull next ep and trigger updater

                    CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
                    CustomTabsIntent customTabsIntent = builder.build();
                    customTabsIntent.launchUrl(view.getContext(), Uri.parse(refEp.getSrc()));

                    watchData.update(series);//notify host of changes
                    watchData.updateWatchDataJson();
                }
            });

        } else {
            next.setColorFilter(next.getContext().getColor(R.color.dark_grey));
            next.setEnabled(false);
        }
    }

    @Override
    public Bundle configureClickEvent() {
        Bundle bundle = new Bundle();
        bundle.putString("link", series.getSrc());
        bundle.putString("title", series.getTitle());
        return bundle;
    }

}
