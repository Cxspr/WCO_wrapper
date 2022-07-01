package com.wco_fun.wco_wrapper.ui.genres.genre;

import android.app.Activity;
import android.os.Handler;
import android.util.Log;

import com.wco_fun.wco_wrapper.classes.series.SeriesSearchable;
import com.wco_fun.wco_wrapper.ui.search.SearchAdapter;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.Date;

public class ConnectedGenreSeriesThread extends Thread {
    private Handler resHandler;
    private String url;
    private GenreSeriesAdapter retLoc;
    private String TAG = "Genre Thread: ";

    public ConnectedGenreSeriesThread(Activity mainActivity, GenreSeriesAdapter retLoc, String url) {
        this.url = url;
        this.resHandler = new Handler(mainActivity.getMainLooper());
        this.retLoc = retLoc; //return location being a SearchAdapter object
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void cancel() {
        this.interrupt();
    }

    @Override
    public void run() {
        Log.i(TAG, "STARTED");
        long startTime = new Date().getTime();
        String domain = url.substring(0, url.indexOf("/search-by-genre/"));
        ArrayList<SeriesSearchable> retSeries = new ArrayList<>();
        try {
            Document doc = Jsoup.connect(url)
                    .userAgent("Mozilla/5.0 (Windows; U; WindowsNT 5.1; en-US; rv1.8.1.6) Gecko/20070725 Firefox/2.0.0.6")
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
                    retSeries.add(s);
                }

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

    private void notifyResult(ArrayList<SeriesSearchable> retList) {
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
                retLoc.onThreadErr(0);
            }
        });
    }

}
