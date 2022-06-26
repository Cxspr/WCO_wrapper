package com.wco_fun.wco_wrapper.classes.series;

import com.wco_fun.wco_wrapper.classes.episode.Episode;
import com.wco_fun.wco_wrapper.classes.user_data.WatchData;

import org.jsoup.Jsoup;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * SeriesControllable is the variant of the series class that tracks position and episode links
 * in relation to a series, it features class methods that allow for the retrieval of next
 * episodes in a series and will add them to a 'buffer' of episode links upon request
 */
public class SeriesControllable extends Series{
    private List<Episode> epQueue = new ArrayList<Episode>();
    private int preloadLimit = 2;
    long lastWatched = 0;

    public SeriesControllable(Series s){
        super(s);
        lastWatched = new Date().getTime();
    }

    public SeriesControllable(Series s, Episode curEp) {
        super(s);
        epQueue.add(curEp);
        lastWatched = new Date().getTime();
    }

    public void addEp(Episode ep){
        epQueue.add(ep);
    }
    public List<Episode> getEpQueue(){
        return epQueue;
    }

    //override episode queue with the provided one
    public void overrideEpQueue(ArrayList<Episode> newEps){
        epQueue.clear();
        epQueue.addAll(newEps);
    }

    public boolean hasEp() {return !epQueue.isEmpty();}
    public boolean hasMoreEps() {return epQueue.size()>= 2;}
    public Episode getCurEp() {return epQueue.get(0);}
    public Episode getNextEp() {return (hasMoreEps()) ? epQueue.get(1) : null;}
    public Episode popEpQueue() {
        shiftEpQueue();//remove old curEp and start parallel ep retrieval process
        return getCurEp();//return former nextEp
    }

    public void setPreloadLimit(int preloadLimit) {
        this.preloadLimit = preloadLimit;
    }
    public int getPreloadLimit() {
        return preloadLimit;
    }

    public void shiftEpQueue() {
        epUpdater();//run the queue populator
        epQueue.remove(0); //get rid of first element // current episode
    }

    public void updateLastWatched() { lastWatched = new Date().getTime();}
    public long getLastWatched() {return lastWatched;}


    /**
     * this function will make an HTML request to the source page associated with the series and
     * attempt to retrieve the necessary number of episodes to fill a buffer, the quantity of this
     * buffer will later be associated with a setting
     */
    public void epUpdater() {
        int bIdx = epQueue.get(epQueue.size() - 1).getIdx(); //get ep index of last ep in queue
        if (bIdx + 1 >= this.numEps) return; //this is the last episode
        Thread runThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Elements foundEps = Jsoup.connect(src)
                            .userAgent("Mozilla/5.0 (Windows; U; WindowsNT 5.1; en-US; rv1.8.1.6) Gecko/20070725 Firefox/2.0.0.6")
                            .timeout(10000)
                            .get()
                            .getElementsByClass("cat-eps");

                    int lIdx = bIdx;
                    for (int i = foundEps.size() - bIdx - 2; i >= 0; i--) { //eps are reverse ordered, i.e. ep 1 is the last in list
                        if (epQueue.size() >= preloadLimit + 1) {
                            break;
                        }
                        Episode e = new Episode(foundEps.get(i).child(0));
                        if (e.isValid()) {
                            e.setIdx(++lIdx);
                            addEp(e);
                        }
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }});
        runThread.start();
    }
}
