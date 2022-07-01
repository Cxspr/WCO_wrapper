package com.wco_fun.wco_wrapper.ui.home.watchgroups.SeriesCard;

import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;

import com.wco_fun.wco_wrapper.classes.series.Series;
import com.wco_fun.wco_wrapper.classes.series.SeriesControllable;

public class SeriesCardGeneric extends SeriesCard{
    private Series series;

    public SeriesCardGeneric(Series series) {
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
            }
        });
    }

    @Override
    public Series getSeries() { return series; }

    @Override
    public void setSeries(SeriesControllable series) {
        this.series = series;
        this.series.getSeriesImage(seriesImg);
    }

    @Override
    public void configureVariant() {
        //visibility config
        this.title.setVisibility(View.VISIBLE);
        title.setText(series.getTitle());
        this.btnContainer.setVisibility(View.GONE);
        this.remove.setVisibility(View.GONE);
    }

    @Override
    public Bundle configureClickEvent() {
        Bundle bundle = new Bundle();
        bundle.putString("link", series.getSrc());
        bundle.putString("title", series.getTitle());
        return bundle;
    }
}
