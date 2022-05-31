package com.wco_fun.wco_wrapper.ui.home.watchgroups.SeriesCard;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.wco_fun.wco_wrapper.classes.series.SeriesControllable;
import com.wco_fun.wco_wrapper.ui.home.watchgroups.WatchgroupAdapter;

public abstract class SeriesCard{ //TODO add host recycler attachment function
    protected TextView title;
    protected ImageView seriesImg;
    protected ImageButton play, next, remove;
    protected WatchgroupAdapter host;

    public void attachViews(TextView title, ImageView seriesImg, ImageButton play, ImageButton next, ImageButton remove){
        this.title = title;
        this.seriesImg = seriesImg;
        this.play = play;
        this.next = next;
        this.remove = remove;
    }
    //should attach local series object
    public abstract void setSeries(SeriesControllable series);
    public abstract void setSeriesImage();

    public void bindAdapter(WatchgroupAdapter host) {this.host = host;}

    //should configure variant specifics like which parts are visible or setting up button click events
    public abstract void configureVariant();

    //should perform the primary on click event
    public abstract Bundle configureClickEvent();
}

