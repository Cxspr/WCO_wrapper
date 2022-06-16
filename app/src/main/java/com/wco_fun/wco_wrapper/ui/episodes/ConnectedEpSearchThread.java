package com.wco_fun.wco_wrapper.ui.episodes;

import static com.wco_fun.wco_wrapper.ui.search.ConnectedSearchThread.ERR_CODES.INTERRUPT;

import android.app.Activity;
import android.os.Handler;
import android.util.Log;
import android.widget.ImageView;

import com.wco_fun.wco_wrapper.classes.episode.Episode;
import com.wco_fun.wco_wrapper.classes.series.Series;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

public class ConnectedEpSearchThread extends Thread {
    private Handler resHandler;
    private String url;
    private EpisodeAdapter retLoc;

    private final String TAG = "Ep Thread: ";

    public static final class ERR_CODES {
        public static final int INTERRUPT = 0;
        public static final int TIMEOUT = 1;
        public static final int IOEXCEPTION = 2;
    }

    public ConnectedEpSearchThread(Activity mainActivity, EpisodeAdapter retLoc, String url) {
        this.url = url;
        this.resHandler = new Handler(mainActivity.getMainLooper());
        this.retLoc = retLoc;
    }

    public void cancel() {this.interrupt();}

    @Override
    public void run() {
        Log.i(TAG, "STARTED");
        long startTime = new Date().getTime();
        ArrayList<Episode> episodes = new ArrayList<>();
        try {
            Document doc = Jsoup.connect(url)
                    .userAgent("Mozilla/5.0 (Windows; U; WindowsNT 5.1; en-US; rv1.8.1.6) Gecko/20070725 Firefox/2.0.0.6")
                    .timeout(10000)
                    .maxBodySize(0)
                    .get();

            Log.i(TAG, "HTML RETRIEVED");

            if (Thread.currentThread().isInterrupted()) { //check for external interrupt call
                Log.i(TAG, "INTERRUPTION");
                notifyError(INTERRUPT);
                throw new InterruptedException();
            } //escape clause

            String imgUrl = "https:" + doc.getElementsByClass("img5").get(0).attr("src");

            Elements eps = doc.getElementsByClass("cat-eps");
            for (Element ep: eps) {
                ep = ep.child(0);
                Episode e = new Episode(ep);
                if (e.isValid()){
                    episodes.add(e);
                }
            }

            notifyMilestone(episodes.size(), imgUrl);
            Log.i(TAG, "MILESTONE");

            Collections.reverse(episodes);
            for(int i = 0; i < episodes.size(); i++){
                episodes.get(i).setIdx(i);
            }

            Log.i(TAG, "SUCCESS||TIME: " + ((new Date().getTime()) - startTime));
            notifyResult(episodes);

        } catch (SocketTimeoutException e) {
            Log.i(TAG, "TIMEOUT");
            notifyError(1);
        } catch (IOException e) {
            notifyError(2);
//            e.printStackTrace();
        } catch (InterruptedException e) {
            notifyError(2);
//            e.printStackTrace();
        }
    }

    private void notifyResult(ArrayList<Episode> retList){
        resHandler.post(new Runnable() {
            @Override
            public void run() {
                retLoc.onThreadConcluded(retList);
            }
        });
    }

    private void notifyError(int errCode){
        resHandler.post(new Runnable() {
            @Override
            public void run() {
                retLoc.onThreadErr(errCode);
            }
        });
    }

    private void notifyMilestone(int numEps, String imgUrl){
        resHandler.post(new Runnable() {
            @Override
            public void run() {
                retLoc.onThreadMilestone(numEps, imgUrl);
            }
        });
    }
}
