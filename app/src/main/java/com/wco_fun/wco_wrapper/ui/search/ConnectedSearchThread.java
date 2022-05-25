package com.wco_fun.wco_wrapper.ui.search;

import android.app.Activity;
import android.os.Handler;
import android.util.Log;

import com.wco_fun.wco_wrapper.classes.SeriesSearchable_LE;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.util.ArrayList;

public class ConnectedSearchThread extends Thread {
    private Thread worker;
    private Handler resHandler;
    private String url;
    private SearchAdapter retLoc;
    public volatile boolean running = true;

    public ConnectedSearchThread(Activity mainActivity, SearchAdapter retLoc, String url) {
        this.url = url;
        this.resHandler = new Handler(mainActivity.getMainLooper());
        this.retLoc = retLoc;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void cancel() { this.interrupt(); }

    @Override
    public void run() {
        try {
            ArrayList<SeriesSearchable_LE> retList = new ArrayList<SeriesSearchable_LE>();
            try {
                //scrape for series list html
                Element seriesHtmlData = (Jsoup.connect(url)
                        .userAgent("Mozilla/5.0 (Windows; U; WindowsNT 5.1; en-US; rv1.8.1.6) Gecko/20070725 Firefox/2.0.0.6")
                        .get()).getElementById("ddmcc_container").child(0).child(0);
                //pre-collect series data
                for (int i = 0; i <= (int) 'Z' - 64; i++) {
                    int idx = i * 3 + 2;
                    Element charCollect = seriesHtmlData.child(idx);
                    for (Element el : charCollect.children()) {
                        el = el.child(0);
                        SeriesSearchable_LE e = new SeriesSearchable_LE(el);
                        if (e.isValid()) {
                            retList.add(e);
                        }
                        if (Thread.currentThread().isInterrupted() || !running) {
                            throw new InterruptedException();
                        } //escape clause
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            Log.i("Search Thread: ", "SUCCESS");
            notifyResult(retList);
        } catch (InterruptedException consumed) {
            Log.i("Search Thread: ", "INTERRUPTED");
            //force thread to exit
        }
    }

    private void notifyResult(ArrayList<SeriesSearchable_LE> retList) {
        resHandler.post(new Runnable() {
            @Override
            public void run() {
                retLoc.onThreadConcluded(retList);
            }
        });
    }
}
