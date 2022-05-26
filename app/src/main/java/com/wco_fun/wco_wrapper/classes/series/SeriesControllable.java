package com.wco_fun.wco_wrapper.classes.series;

import com.wco_fun.wco_wrapper.classes.episode.Episode;
import com.wco_fun.wco_wrapper.classes.user_data.WatchData;

import org.jsoup.Jsoup;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class SeriesControllable extends Series{
    private List<Episode> epQueue = new ArrayList<Episode>();
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


    //functions to attach and interact with host WatchData object to push changes to file
//    public void attach(WatchData host){this.host = host;}
//    public WatchData getHost() {return host;}
//    public void notifyHost() {
//        if (host != null) {
//            host.notifyTrigger();
//        }
//    }

    public void addEp(Episode ep){
        epQueue.add(ep);
    }

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

    public void shiftEpQueue() {
        epUpdater();//run the queue populator
        epQueue.remove(0); //get rid of first element // current episode
    }

    public void updateLastWatched() { lastWatched = new Date().getTime();}
    public long getLastWatched() {return lastWatched;}

    private void epUpdater() {
        int preloadLimit = 2;
        int bIdx = epQueue.get(0).getIdx();
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
                    int lIdx = 0; //load index
                    while (lIdx <= preloadLimit) {
                        if (foundEps.size() - bIdx - 3 + lIdx >= 0) { //load episodes until the preload limit is reached
                            Episode e = new Episode(foundEps.get(foundEps.size() - bIdx - 3 + lIdx).child(0));
                            if (e.isValid()) {
                                e.setIdx(bIdx + 1 + lIdx);
                                addEp(e);
                                lIdx++;
                            }
                        } else { //escape clause if no episodes left
                            break;
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }});
        runThread.start();
    }

}
