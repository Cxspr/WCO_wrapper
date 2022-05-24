package com.wco_fun.wco_wrapper.ui.home.parallel_procs;

import com.wco_fun.wco_wrapper.MainActivity;
import com.wco_fun.wco_wrapper.classes.Episode;
import com.wco_fun.wco_wrapper.classes.Series;
import com.wco_fun.wco_wrapper.classes.SeriesSearchable;
import com.wco_fun.wco_wrapper.ui.episodes.EpisodeAdapter;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.RunnableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class ThreadedEpUpdater implements RunnableFuture<Series> {

    private Series resSeries;

    public ThreadedEpUpdater(Series passedSeries) {
        resSeries = new Series(passedSeries);
    }

    @Override
    public void run() {
        Document Html;
        try {
            String src = resSeries.getSeriesSrc();
            Html = Jsoup.connect(src)
                    .userAgent("Mozilla/5.0 (Windows; U; WindowsNT 5.1; en-US; rv1.8.1.6) Gecko/20070725 Firefox/2.0.0.6")
                    .get();

            Elements foundEps = Html.getElementsByClass("cat-eps");
            if (foundEps.size() - resSeries.getEpIdx() - 3 >= 0) {
                Episode e = new Episode(foundEps.get(foundEps.size() - resSeries.getEpIdx() - 3).child(0));
                if (e.isValid()){
                    resSeries.epInfoShift();
                    resSeries.setNextEp(e.getSrc());
                    resSeries.setAbrEpTitle(e.getAbrTitle());
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
    public Series get() throws ExecutionException, InterruptedException {
        return resSeries;
    }

    @Override
    public Series get(long l, TimeUnit timeUnit) throws ExecutionException, InterruptedException, TimeoutException {
        return null;
    }
}