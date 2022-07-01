package com.wco_fun.wco_wrapper.initialization;

import static com.wco_fun.wco_wrapper.ui.search.ConnectedSearchThread.ERR_CODES.INTERRUPT;

import android.app.Activity;
import android.os.Handler;
import android.util.Log;

import com.wco_fun.wco_wrapper.MainActivity;
import com.wco_fun.wco_wrapper.classes.episode.Episode;
import com.wco_fun.wco_wrapper.classes.series.Series;
import com.wco_fun.wco_wrapper.ui.home.watchgroups.MultigroupAdapter;
import com.wco_fun.wco_wrapper.ui.home.watchgroups.WatchgroupAdapter;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

public class NewEpScraper extends Thread {
    private ArrayList<Series> searchList;
    private Activity retActivity;
    private WatchgroupAdapter retLoc;
    private Handler resHandler;
    private final String TAG = "NewEp: ";


    public NewEpScraper(ArrayList<Series> searchList, Activity activity, WatchgroupAdapter retLoc) {
        this.searchList = searchList;
        this.retActivity = activity;
        this.resHandler = new Handler(retActivity.getMainLooper());
        this.retLoc = retLoc;
    }

    public NewEpScraper(ArrayList<Series> searchList, Activity activity) {
        this.searchList = searchList;
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
//        Log.i(TAG, "STARTED");
        long startTime = new Date().getTime();
        ArrayList<Series> retSeries = new ArrayList<>();
        try {
            for (Series s : searchList) {
                String url = s.getSrc();

                Document doc = Jsoup.connect(url)
                        .userAgent("Mozilla/5.0 (Windows; U; WindowsNT 5.1; en-US; rv1.8.1.6) Gecko/20070725 Firefox/2.0.0.6")
                        .timeout(10000)
                        .maxBodySize(0)
                        .get();

//                Log.i(TAG, "HTML RETRIEVED");

                if (Thread.currentThread().isInterrupted()) { //check for external interrupt call
//                    Log.i(TAG, "INTERRUPTION");
                    notifyError();
                    throw new InterruptedException();
                } //escape clause

                int numEps = doc.getElementsByClass("cat-eps").size();
                if (numEps > s.getNumEps()) {
                    retSeries.add(s);
                }
                continue;


            }
//            Log.i(TAG, "SUCCESS||TIME: " + ((new Date().getTime()) - startTime));
            notifyResult(retSeries);

        } catch (SocketTimeoutException e) {
//            Log.i(TAG, "TIMEOUT");
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
                ((MainActivity) retActivity).updateNewEpGroup(retList);
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
