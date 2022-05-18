package com.example.wco_wrapper.classes;

import android.content.Context;
import android.widget.ImageView;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.picasso.Picasso;

public class Series {
    private String src, imgUrl, title, lastEpisode;
    private int numEpisodes;
    private boolean onWatchlist = false;


    //todo docstrings for functions
    //todo add episode tracking

    public Series (String src, String title, String imgUrl) {
        this.src = src;
        this.imgUrl = imgUrl;
        this.title = title;
    }
    public Series (String src, String title, String imgUrl, int numEpisodes, String lastEpisode) {
        this.src = src;
        this.imgUrl = imgUrl;
        this.title = title;
        this.numEpisodes = numEpisodes;
        this.lastEpisode = lastEpisode;
    }

    public void getSeriesImage(ImageView toWrite){
        Picasso.get().load(imgUrl).into(toWrite);
    }

    public void setEpisodeCount(int count) {
        numEpisodes = count;
    }

    public String getSeriesTitle() {
        return title;
    }
    public String getSeriesSrc(){
        return src;
    }
    public String getSeriesImgUrl() {return imgUrl;}
    public void onWatchlist(boolean state) {
        onWatchlist = state;
    }
    public boolean isOnWatchlist() {return onWatchlist;}

    public String seriesToJson() {
        Moshi moshi = new Moshi.Builder().build();
        JsonAdapter<Series> jsonAdapter = moshi.adapter((Series.class));
        String json = jsonAdapter.toJson(this);
        return json;
    }

    public boolean compare(Series comp) {
        if (this.src.matches(comp.getSeriesSrc()) && this.imgUrl.matches(comp.getSeriesImgUrl()) && this.title.matches(comp.getSeriesTitle())){
            return true;
        } return false;
    }
}
