package com.wco_fun.wco_wrapper.classes;

import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.Date;

public class Series_LE {
    private String src, imgUrl, title, curEp, nextEp;
    private int numEpisodes, curEpIdx;
    private String abrEpTitle, nextAbrEpTitle;
    private long lastWatched;

    private boolean onWatchlist = false;

    //todo docstrings for functions
    public Series_LE(Series_LE s) {
        this.src = s.getSeriesSrc();
        this.imgUrl = s.getSeriesImgUrl();
        this.title = s.getSeriesTitle();
        this.overrideAll(s);
    }

    public Series_LE(String src, String title, String imgUrl) {
        this.src = src;
        this.imgUrl = imgUrl;
        this.title = title;
    }

    public Series_LE(String src, String title, String imgUrl, int numEpisodes, String lastEpisode) {
        this.src = src;
        this.imgUrl = imgUrl;
        this.title = title;
        this.numEpisodes = numEpisodes;
        this.curEp = lastEpisode;
    }

    public void getSeriesImage(ImageView toWrite){
        //TODO implement adaptive sizing to fit different screen variants
//        Picasso.get().load(imgUrl).into(toWrite);
        Picasso.get().load(imgUrl).resize(360, 510).into(toWrite);
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

    //watchlist state boolean getter and setter
    public void onWatchlist(boolean state) {
        onWatchlist = state;
    }
    public boolean onWatchlist() {return onWatchlist;}

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

    public void overrideEpInfo(Series_LE seriesLE){
        if (seriesLE == null) return;
        if (seriesLE.getSeriesTitle().matches(this.title)){
            this.curEp = seriesLE.getCurEp();
            this.abrEpTitle = seriesLE.getAbrEpTitle();
            this.nextEp = seriesLE.getNextEp();
            this.nextAbrEpTitle = seriesLE.getNextAbrEpTitle();
            this.curEpIdx = seriesLE.getEpIdx();
        }
    }

    public boolean hasEpInfo() {
        return !(curEp == null);
    }
    public boolean hasNextEp() {
        return !(nextEp == null);
    }

    public void setLastWatched(Date time) { this.lastWatched = time.getTime(); }
    public long getLastWatched() { return this.lastWatched; }

    //true comparison for series objects, compares only the non volatile parameters
    public boolean compare(Series_LE comp) {
        if (this.src.matches(comp.getSeriesSrc()) && this.imgUrl.matches(comp.getSeriesImgUrl()) && this.title.matches(comp.getSeriesTitle())){
            return true;
        } return false;
    }

    public void overrideAll(Series_LE seriesLE){
        if (seriesLE == null) return;
        if (seriesLE.getSeriesTitle().matches(this.title)){
            this.curEp = seriesLE.getCurEp();
            this.abrEpTitle = seriesLE.getAbrEpTitle();
            this.nextEp = seriesLE.getNextEp();
            this.nextAbrEpTitle = seriesLE.getNextAbrEpTitle();
            this.curEpIdx = seriesLE.getEpIdx();
            this.lastWatched = seriesLE.getLastWatched();
        }
    }

    public void removeEpInfo(){
        this.curEp = null;
        this.abrEpTitle = null;
        this.nextEp = null;
        this.nextAbrEpTitle = null;
        this.curEpIdx = 0;
        this.lastWatched = 0;
    }

    public void epInfoShift(){
        if (!this.hasNextEp()) return;
        this.curEp = this.nextEp;
        this.abrEpTitle = this.nextAbrEpTitle;
        this.curEpIdx += 1;
        this.nextEp = null;
        this.nextAbrEpTitle = null;
    }
}
