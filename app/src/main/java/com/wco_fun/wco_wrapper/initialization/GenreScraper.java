package com.wco_fun.wco_wrapper.initialization;

import static com.wco_fun.wco_wrapper.ui.search.ConnectedSearchThread.ERR_CODES.INTERRUPT;

import android.app.Activity;
import android.os.Handler;
import android.util.Log;

import com.wco_fun.wco_wrapper.classes.series.Series;
import com.wco_fun.wco_wrapper.classes.series.SeriesSearchable;
import com.wco_fun.wco_wrapper.ui.home.watchgroups.WatchgroupAdapter;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

public class GenreScraper extends Thread {
    private GenreList.Genre genre;
    private Activity retActivity;
    private WatchgroupAdapter retLoc;
    private Handler resHandler;
    private String domain;
    private final String TAG = "GenreScraper: ";


    public GenreScraper(GenreList.Genre genre, Activity activity, WatchgroupAdapter retLoc) {
        this.genre = genre;
        domain = genre.getSrc().substring(0,  genre.getSrc().indexOf("/search-by-genre/"));
        this.retActivity = activity;
        this.resHandler = new Handler(retActivity.getMainLooper());
        this.retLoc = retLoc;
    }

    public GenreScraper(GenreList.Genre genre, Activity activity) {
        this.genre = genre;
        domain = genre.getSrc().substring(0,  genre.getSrc().indexOf("/search-by-genre/"));
        this.retActivity = activity;
        this.resHandler = new Handler(retActivity.getMainLooper());
    }

    public void attachRetLoc(WatchgroupAdapter retLoc) {
        this.retLoc = retLoc;
    }

    public void cancel() {
        this.interrupt();
    }

    @Override
    public void run() {
        Log.i(TAG, "STARTED");
        long startTime = new Date().getTime();
        ArrayList<Series> retSeries = new ArrayList<>();
        try {
                Connection connection = Jsoup.connect(genre.getSrc())
                                .userAgent("Mozilla/5.0 (Windows; U; WindowsNT 5.1; en-US; rv1.8.1.6) Gecko/20070725 Firefox/2.0.0.6")
                                .timeout(10000)
                                .maxBodySize(0);
                Document doc = connection.newRequest()
                        .timeout(10000)
                        .maxBodySize(0)
                        .get();

                Log.i(TAG, "HTML RETRIEVED");

                if (Thread.currentThread().isInterrupted()) { //check for external interrupt call
                    Log.i(TAG, "INTERRUPTION");
                    notifyError();
                    throw new InterruptedException();
                } //escape clause

                Elements seriesNodes = doc.getElementById("ddmcc_container").getElementsByTag("li");
                int numEntries = seriesNodes.size();
                for (Element el : seriesNodes) {
                    if (Thread.currentThread().isInterrupted()) { //check for external interrupt call
                        Log.i("Search Thread: ", "INTERRUPTION");
//                        notifyError(INTERRUPT);
                        throw new InterruptedException();
                    }
                    SeriesSearchable s = new SeriesSearchable(domain + el.child(0).attr("href"), el.child(0).text());
                    if (s.isValid()){
//                        connection.url(s.getSrc());
//                        s.setImgUrl("https:" + connection
//                                .timeout(2000)
//                                .maxBodySize(0)
//                                .get()
//                                .getElementsByClass("img5").get(0).attr("src"));

                        retSeries.add(s);
                    }

                }
                Collections.shuffle(retSeries);
                for (int i = 0; i < 12 && i < retSeries.size(); i++) {
                    connection.url(retSeries.get(i).getSrc());
                    retSeries.get(i).setImgUrl("https:" + connection
                            .timeout(2000)
                            .maxBodySize(0)
                            .get()
                            .getElementsByClass("img5").get(0).attr("src"));
                }


            Log.i(TAG, "SUCCESS||TIME: " + ((new Date().getTime()) - startTime));
            notifyResult(retSeries);

        } catch (SocketTimeoutException e) {
            Log.i(TAG, "TIMEOUT");
            notifyError();
        } catch (IOException e) {
            notifyError();
//            e.printStackTrace();
        } catch (InterruptedException e) {
            notifyError();
//            e.printStackTrace();
        }
    }

    private void notifyResult(ArrayList<Series> retList) {
        resHandler.post(new Runnable() {
            @Override
            public void run() {
                retLoc.onThreadConcluded(retList);
            }
        });
    }

    private void notifyError() {
        resHandler.post(new Runnable() {
            @Override
            public void run() {
                retLoc.onThreadErr();
            }
        });
    }

}

