package com.wco_fun.wco_wrapper.ui.home.parallel_procs;

import com.wco_fun.wco_wrapper.classes.Episode_LE;
import com.wco_fun.wco_wrapper.classes.Series_LE;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.RunnableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class ThreadedEpUpdater implements RunnableFuture<Series_LE> {

    private Series_LE resSeriesLE;

    public ThreadedEpUpdater(Series_LE passedSeriesLE) {
        resSeriesLE = new Series_LE(passedSeriesLE);
    }

    @Override
    public void run() {
        Document Html;
        try {
            String src = resSeriesLE.getSeriesSrc();
            Html = Jsoup.connect(src)
                    .userAgent("Mozilla/5.0 (Windows; U; WindowsNT 5.1; en-US; rv1.8.1.6) Gecko/20070725 Firefox/2.0.0.6")
                    .get();

            Elements foundEps = Html.getElementsByClass("cat-eps");
            if (foundEps.size() - resSeriesLE.getEpIdx() - 3 >= 0) {
                Episode_LE e = new Episode_LE(foundEps.get(foundEps.size() - resSeriesLE.getEpIdx() - 3).child(0));
                if (e.isValid()){
                    resSeriesLE.epInfoShift();
                    resSeriesLE.setNextEp(e.getSrc());
                    resSeriesLE.setNextAbrEpTitle(e.getAbrTitle());
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
    public Series_LE get() throws ExecutionException, InterruptedException {
        return resSeriesLE;
    }

    @Override
    public Series_LE get(long l, TimeUnit timeUnit) throws ExecutionException, InterruptedException, TimeoutException {
        return null;
    }
}