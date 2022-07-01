package com.wco_fun.wco_wrapper.ui.search;

import static com.wco_fun.wco_wrapper.ui.search.ConnectedSearchThread.ERR_CODES.*;

import android.app.Activity;
import android.os.Handler;
import android.util.Log;

import com.wco_fun.wco_wrapper.MainActivity;
import com.wco_fun.wco_wrapper.classes.series.SeriesSearchable;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.Date;

public class ConnectedSearchThread extends Thread {
    private Handler resHandler;
    private String url;
    private SearchAdapter retLoc;
    private MainActivity mainActivity;

    public static final class ERR_CODES {
        public static final int INTERRUPT = 0;
        public static final int TIMEOUT = 1;
        public static final int IOEXCEPTION = 2;
    }

    public ConnectedSearchThread(Activity mainActivity, SearchAdapter retLoc, String url) {
        this.url = url;
        this.resHandler = new Handler(mainActivity.getMainLooper());
        this.retLoc = retLoc; //return location being a SearchAdapter object
        this.mainActivity = (MainActivity) mainActivity;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void cancel() {
        this.interrupt();
    }

    @Override
    public void run() {
        Log.i("Search Thread: ", "STARTED");
        try {
            long startTime = new Date().getTime();
            ArrayList<SeriesSearchable> retList = new ArrayList<SeriesSearchable>(1000);
            try {
                Document doc = Jsoup.connect(url)
                        .userAgent("Mozilla/5.0 (Windows; U; WindowsNT 5.1; en-US; rv1.8.1.6) Gecko/20070725 Firefox/2.0.0.6")
                        .maxBodySize(0)
                        .timeout(10000)
                        .get();
                Log.i("Search Thread: ", "HTML RETRIEVED");

                if (Thread.currentThread().isInterrupted()) { //check for external interrupt call
                    Log.i("Search Thread: ", "INTERRUPTION");
                    notifyError(INTERRUPT);
                    throw new InterruptedException();
                } //escape clause

                Log.i("Search Thread: ", "JSOUP_PASSED");

                Elements seriesNodes = doc.getElementById("ddmcc_container").getElementsByTag("li");
                int numEntries = seriesNodes.size();
                int i = 0;
                for (Element el : seriesNodes) {
                    if (Thread.currentThread().isInterrupted()) { //check for external interrupt call
                        Log.i("Search Thread: ", "INTERRUPTION");
                        notifyError(INTERRUPT);
                        throw new InterruptedException();
                    }
                    SeriesSearchable s = new SeriesSearchable((el.child(0)));
                    if (s.isValid()){
                        s.setSrc((mainActivity.getDomain()) + s.getSrc());
                        retList.add(s);
                    }
//                    Log.d("Search Thread: ", "PROG: " + (++i) + "/" + numEntries );
                }

                Log.i("Search Thread: ", "SUCCESS||TIME: " + ((new Date().getTime()) - startTime));
                notifyResult(retList);
            } catch (SocketTimeoutException e) {
                Log.i("Search Thread: ", "TIMED_OUT");
                notifyError(TIMEOUT);
//                e.printStackTrace();
            } catch (IOException | NullPointerException | IndexOutOfBoundsException e) {
                Log.i("Search Thread: ", "IO_EXCEPTION//NULL_PTR");
                notifyError(IOEXCEPTION);
//                e.printStackTrace();
            }
        } catch (InterruptedException consumed) {
            Log.i("Search Thread: ", "INTERRUPTION");
            notifyError(INTERRUPT);
//            consumed.printStackTrace();
            //force thread to exit
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

    private void notifyError(int errCode) {
        resHandler.post(new Runnable() {
            @Override
            public void run() {
                retLoc.onThreadErr(errCode);
            }
        });
    }
}
