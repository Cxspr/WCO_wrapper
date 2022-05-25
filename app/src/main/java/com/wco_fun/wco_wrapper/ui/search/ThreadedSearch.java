package com.wco_fun.wco_wrapper.ui.search;

import com.wco_fun.wco_wrapper.classes.SeriesSearchable_LE;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.RunnableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class ThreadedSearch implements RunnableFuture<ArrayList<SeriesSearchable_LE>> {

    private String url;
    private ArrayList<SeriesSearchable_LE> allSeries = new ArrayList<SeriesSearchable_LE>();

    public ThreadedSearch(String url){
        this.url = url;
    }

    @Override
    public void run() {
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
                        allSeries.add(e);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean cancel(boolean b) {
        return false;
    }

    @Override
    public boolean isCancelled() {
        return false;
    }

    @Override
    public boolean isDone() {
        return false;
    }

    @Override
    public ArrayList<SeriesSearchable_LE> get() throws ExecutionException, InterruptedException {
        return allSeries;
    }

    @Override
    public ArrayList<SeriesSearchable_LE> get(long l, TimeUnit timeUnit) throws ExecutionException, InterruptedException, TimeoutException {
        return null;
    }
}
