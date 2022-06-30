package com.wco_fun.wco_wrapper.ui.home.watchgroups.SeriesCard;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.wco_fun.wco_wrapper.R;
import com.wco_fun.wco_wrapper.classes.series.SeriesControllable;
import com.wco_fun.wco_wrapper.ui.home.watchgroups.WatchgroupAdapter;

public abstract class SeriesCard{
    protected TextView title;
    protected ImageView seriesImg;
    protected ImageButton play, next, remove;
    protected LinearLayout btnContainer;
    protected ConstraintLayout footer;
    protected WatchgroupAdapter host;

    public void attachViews(ViewGroup parent){
        this.title = (TextView) parent.findViewById(R.id.series_card_title);
        this.seriesImg = (ImageView) parent.findViewById(R.id.series_card_img);
        this.play = (ImageButton) parent.findViewById(R.id.series_card_play);
        this.next = (ImageButton) parent.findViewById(R.id.series_card_next);
        this.remove = (ImageButton) parent.findViewById(R.id.series_card_remove);
        this.btnContainer = (LinearLayout) parent.findViewById(R.id.wg_button_bar);
        this.footer = (ConstraintLayout) parent.findViewById(R.id.constraintLayout3);
    }

    public void attachPrimaryViews(TextView title, ImageView seriesImg){
        this.title = title;
        this.seriesImg = seriesImg;
    }

    public void attachSecondaryViews(ImageButton play, ImageButton next, ImageButton remove, LinearLayout btnContainer){
        this.play = play;
        this.next = next;
        this.remove = remove;
        this.btnContainer = btnContainer;
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

