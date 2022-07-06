package com.wco_fun.wco_wrapper.ui.home.watchgroups.SeriesCard;

import android.net.Uri;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;

import androidx.browser.customtabs.CustomTabsIntent;

import com.wco_fun.wco_wrapper.R;
import com.wco_fun.wco_wrapper.classes.episode.Episode;
import com.wco_fun.wco_wrapper.classes.series.Series;
import com.wco_fun.wco_wrapper.classes.series.SeriesControllable;
import com.wco_fun.wco_wrapper.classes.user_data.WatchData;

/**
 * Reflective SeriesCard is associated with the series card variant on the home screen
 * that allows the user to play episodes directly from said screen
 */
public class SeriesCardReflective extends SeriesCard{
    private SeriesControllable series;
    private WatchData watchData;


    public SeriesCardReflective(WatchData watchData, SeriesControllable series){
        this.watchData = watchData;
        this.series = series;
    }

    @Override
    public void setSeriesImage() {
        seriesImg.post(new Runnable() {
            @Override
            public void run() {
                DisplayMetrics displayMetrics = host.getDisplayMetrics();
                double displayWidthDP = displayMetrics.widthPixels / displayMetrics.density; //get width, convert to dp
                final double uiScalar = displayWidthDP / 400; //UI was built on a simulated display with ~400dp width

                int width = (int) ((host.getDisplayMetrics().widthPixels) / (4 + (uiScalar / 2.5)) );

                ViewGroup.LayoutParams params = seriesImg.getLayoutParams();
                params.width = (int) (width);
                seriesImg.setLayoutParams(params);
                series.fitSeriesImage2Width(seriesImg, width, container);

                ViewGroup.LayoutParams footerParams = footer.getLayoutParams();
                footerParams.height = (int) ((width * 1.42) * 0.25);//20% of the height
                footerParams.width = width;
                footer.setLayoutParams(footerParams);
                ViewGroup.LayoutParams removeParams = remove.getLayoutParams();
                removeParams.width = (int) (width * .35);
                remove.setLayoutParams(removeParams);
            }
        });
    }

    @Override
    public void setSeries(SeriesControllable series) {
        this.series = series;
    }

    @Override
    public Series getSeries() { return series; }

    @Override
    public void configureVariant() {
        //visibility config
        this.title.setVisibility(View.GONE);
        this.btnContainer.setVisibility(View.VISIBLE);
        this.remove.setVisibility(View.VISIBLE);
        SeriesCard c = this;
        //button event configs
        //play button on click listener
        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                series.updateLastWatched();
                series.epUpdater();

                CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
                CustomTabsIntent customTabsIntent = builder.build();
                customTabsIntent.launchUrl(view.getContext(), Uri.parse(series.getCurEp().getSrc()));

                host.refreshRecycler(watchData);
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
            final TypedValue value = new TypedValue ();
            next.getContext().getTheme().resolveAttribute(android.R.attr.colorAccent, value, true);
            next.setColorFilter(value.data);
            next.setEnabled(true);
            next.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (series.getEpQueue().size() <= 2){
                        disableNext();
                    }
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

    private void disableNext() {
        next.setColorFilter(next.getContext().getColor(R.color.dark_grey));
        next.setEnabled(false);
    }

    @Override
    public Bundle configureClickEvent() {
        Bundle bundle = new Bundle();
        bundle.putString("link", series.getSrc());
        bundle.putString("title", series.getTitle());
        return bundle;
    }



}
