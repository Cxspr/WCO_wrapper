package com.wco_fun.wco_wrapper.initialization;

import android.app.Activity;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.wco_fun.wco_wrapper.R;
import com.wco_fun.wco_wrapper.classes.series.Series;
import com.wco_fun.wco_wrapper.classes.series.SeriesControllable;
import com.wco_fun.wco_wrapper.classes.user_data.Watchlist;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Random;

public class GenreList {
    public static class Genre {
        private String src;
        private String title;

        public Genre() {}
        public Genre(String src, String title) {
            this.src = src;
            this.title = title;
        }

        public String getSrc() {
            return src;
        }

        public void setSrc(String src) {
            this.src = src;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }
    }

    private List<Genre> genreList = new ArrayList<>();
    private String parentDir;

    public GenreList() {}
    //TODO IMPLEMENT AWAIT RESPONSE UI BLOCKING
    public GenreList(String domain, Activity activity, String parentDir) {
        this.parentDir = parentDir;
        Handler handler = new Handler(activity.getMainLooper());
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                String TAG = "GenreScrape: ";
                Log.i(TAG, "STARTED");
                long startTime = new Date().getTime();
                try {

                    Elements els = Jsoup.connect(domain)
                            .userAgent("Mozilla/5.0 (Windows; U; WindowsNT 5.1; en-US; rv1.8.1.6) Gecko/20070725 Firefox/2.0.0.6")
                            .timeout(10000)
                            .maxBodySize(0)
                            .get()
                            .getElementsByClass("cerceve");

                    Log.i(TAG, "HTML RETRIEVED");

                    if (Thread.currentThread().isInterrupted()) { //check for external interrupt call
                        Log.i(TAG, "INTERRUPTION");
//                            notifyError();
                        throw new InterruptedException();
                    } //escape clause

//                        Elements els = doc.getElementsByClass("cerceve");
                    for (Element el : els) {
                        genreList.add(new Genre(el.child(0).attr("href"), el.child(0).text()));
                    }

                    Log.i(TAG, "SUCCESS||TIME: " + ((new Date().getTime()) - startTime));
//                    notifyResult(retSeries);
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(activity.getApplicationContext(), "Genre List Loaded", Toast.LENGTH_LONG);
                        }
                    });
                    Collections.sort(genreList, new Comparator<Genre>() {
                        @Override
                        public int compare(Genre genre, Genre g) {
                            return genre.getTitle().toLowerCase().compareTo(g.getTitle().toLowerCase());
                        }
                    });
                    updateGenreListJson();

                } catch (SocketTimeoutException e) {
//                    Log.i(TAG, "TIMEOUT");
//                    notifyError();
                } catch (IOException e) {
//                    notifyError();
////            e.printStackTrace();
                } catch (InterruptedException e) {
//                    notifyError();
////            e.printStackTrace();
                }
            }
        });
        thread.start();
    }

    public ArrayList<Genre> getGenreList() {
        return (ArrayList<Genre>) genreList;
    }

    public Genre getGenre(int index) {
        return genreList.get(index);
    }

    public Genre getRandomGenre() {
//        Random randInt = new Random();
        return genreList.get((int) (Math.random() * genreList.size()));
    }

    public boolean isEmpty() {
        return genreList.isEmpty();
    }

    //convert this class instance to a JSON formatted string
    private String genreListToJson() {
        Moshi moshi = new Moshi.Builder().build();
        JsonAdapter<GenreList> jsonAdapter = moshi.adapter(GenreList.class);
        String json = jsonAdapter.toJson(this);
        return json;
    }

    public void updateGenreListJson() {
        try {
            FileOutputStream stream = new FileOutputStream(parentDir + "/genre_list.json");
            Log.i("File Written at: ", parentDir + "/genre_list.json");
            stream.write(this.genreListToJson().getBytes());
            stream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static GenreList genGenreList(String parentDir) throws IOException {
        File genreListFile = new File(parentDir, "genre_list.json");
        //Read JSON and build into string
        StringBuilder text = new StringBuilder();
        try {
            BufferedReader br = new BufferedReader(new FileReader(genreListFile));
            String line;

            while ((line = br.readLine()) != null){
                text.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        String json = text.toString();
        //Create Watchlist object from JSON using Moshi library
        Moshi moshi = new Moshi.Builder().build();
        JsonAdapter<GenreList> jsonAdapter = moshi.adapter(GenreList.class);
        return jsonAdapter.fromJson(json);
    }
}