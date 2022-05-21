package com.example.wco_wrapper.classes;

import android.widget.ImageView;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.picasso.Picasso;

public class Series {
    private String src, imgUrl, title, curEp, nextEp;
    private int numEpisodes, curEpIdx;
    private String abrEpTitle, nextAbrEpTitle;

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
        this.curEp = lastEpisode;
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
    public boolean onWatchlist() {return onWatchlist;}

    public String seriesToJson() {
        Moshi moshi = new Moshi.Builder().build();
        JsonAdapter<Series> jsonAdapter = moshi.adapter((Series.class));
        String json = jsonAdapter.toJson(this);
        return json;
    }

    //EPISODE STUFF
    public void setCurEp(String url){
        curEp = url;
    }
    public void setAbrEpTitle(String title) { abrEpTitle = title;}
    public String getAbrEpTitle() {return abrEpTitle;}
    public String getCurEp() {
        return curEp;
    }

    public void setNextEp(String url){
        nextEp = url;
    }
    public void setNextAbrEpTitle(String title) { nextAbrEpTitle = title;}
    public String getNextAbrEpTitle() {return nextAbrEpTitle;}
    public String getNextEp() {
        return nextEp;
    }

    public void setEpIdx(int i){
        curEpIdx = i;
    }
    public int getEpIdx() {
        return curEpIdx;
    }
    

    public void overrideEpInfo(Series series){
        if (series == null) return;
        if (series.getSeriesTitle().matches(this.title)){
            this.curEp = series.getCurEp();
            this.nextEp = series.getNextEp();
            this.curEpIdx = series.getEpIdx();
        }
    }

    public boolean hasEpInfo() {
        return !(curEp == null);
    }
    public boolean hasNextEp() {
        return !(nextEp == null);
    }

    public boolean compare(Series comp) {
        if (this.src.matches(comp.getSeriesSrc()) && this.imgUrl.matches(comp.getSeriesImgUrl()) && this.title.matches(comp.getSeriesTitle())){
            return true;
        } return false;
    }
}
