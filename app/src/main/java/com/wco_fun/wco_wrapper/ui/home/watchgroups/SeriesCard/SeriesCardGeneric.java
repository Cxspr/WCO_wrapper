package com.wco_fun.wco_wrapper.ui.home.watchgroups.SeriesCard;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.view.View;

import com.wco_fun.wco_wrapper.classes.series.Series;
import com.wco_fun.wco_wrapper.classes.series.SeriesControllable;

public class SeriesCardGeneric extends SeriesCard{
    private Series series;

    public SeriesCardGeneric(Series series) {
        this.series = series;
    }

    //TODO maybe deprecated
    @Override
    public void setSeriesImage() {
        series.getSeriesImage(seriesImg);
    }

    //TODO maybe deprecated
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
        this.play.setVisibility(View.GONE);
        this.next.setVisibility(View.GONE);
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
