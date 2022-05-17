package com.example.wco_wrapper.classes;

import android.content.Context;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

public class Series {
    private String src, imgUrl, title;
    private int lastEpWatched;

    //todo docstrings for functions
    //todo add episode tracking
    //todo add function to save to/read from file
    //todo make importable from file using series title?

    public Series (String src, String title, String imgUrl) {
        this.src = src;
        this.imgUrl = imgUrl;
        this.title = title;
    }

    public void getSeriesImage(Context context, ImageView toWrite){
        Picasso.with(context).load(imgUrl).into(toWrite);
    }

    public String getSeriesTitle() {
        return title;
    }
    public String getSeriesSrc(){
        return src;
    }

}
