package com.wco_fun.wco_wrapper.classes.series;

import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.cardview.widget.CardView;

import com.squareup.picasso.Picasso;

import org.jsoup.Jsoup;

import java.io.IOException;

public class Series {
    protected String title;
    protected String src;
    protected String imgUrl;
    protected int numEps = 0;

    //constructers
    public Series(Series s) {
        this.src = s.getSrc();
        this.title = s.getTitle();
        this.imgUrl = s.getImgUrl();
    }

    public Series(String src, String title, String imgUrl) {
        this.src = src;
        this.title = title;
        this.imgUrl = imgUrl;
    }

    public Series(String src, String title){
        this.src = src;
        this.title = title;
    }

    public Series() { }

    //get the series image and apply it to the provided imageview
    public void getSeriesImage(ImageView view) {
        double imgScalar = 1.1;
        this.getSeriesImage(view, imgScalar);
    }

    public void getSeriesImage(ImageView view, Double imgScalar) {
        if (!hasSeriesImage()) return;
        Picasso.get().load(imgUrl).resize((int) (240*imgScalar), (int) (340*imgScalar)).into(view);
    }

    public void fitSeriesImage2Width(ImageView view){ this.fitSeriesImage2Width(view, -1);}
    public void fitSeriesImage2Width(ImageView view, final int width){
        if (!hasSeriesImage()) return;
        final double w2h_scalar = 1.42;
        view.post(new Runnable() {
            @Override
            public void run() {
                int thisWidth = (width != -1) ? width : view.getWidth();
                Picasso.get().load(imgUrl).resize((int) (thisWidth), (int) (thisWidth*w2h_scalar)).into(view);
            }
        });
    }
    public void fitSeriesImage2Width(ImageView view, final int width, CardView container){
        if (!hasSeriesImage()) return;
        final double w2h_scalar = 1.42;
        view.post(new Runnable() {
            @Override
            public void run() {
                int thisWidth = (width != -1) ? width : view.getWidth();
                Picasso.get().load(imgUrl).resize((int) (thisWidth), (int) (thisWidth*w2h_scalar)).into(view);
                container.setVisibility(View.VISIBLE);
            }
        });
    }


    public void setTitle(String title) {this.title = title;}
    public String getTitle() {return title; }

    public String getSrc() {return src;}
    public void setSrc(String src) {this.src = src;}

    public void setImgUrl(String imgUrl) {this.imgUrl = imgUrl;}
    public String getImgUrl() {return imgUrl;}

    public int getNumEps() {return numEps;}
    public void setNumEps(int numEps) {this.numEps = numEps;}

    public void override(Series s) {
        this.src = s.getSrc();
        this.title = s.getTitle();
        this.imgUrl = s.getImgUrl();
        this.numEps = s.getNumEps();
    }

    public boolean equals(Series s) {
        return (this.title.matches(s.getTitle())
//                this.src.matches(s.getSrc())
//                && this.imgUrl.matches(s.getImgUrl())
        );
    }

    public boolean hasSeriesImage(){
        if (imgUrl == null || imgUrl.isEmpty()) {
            try {
                Thread runThread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            imgUrl = Jsoup.connect(src)
                                    .userAgent("Mozilla/5.0 (Windows; U; WindowsNT 5.1; en-US; rv1.8.1.6) Gecko/20070725 Firefox/2.0.0.6")
                                    .timeout(10000) //10 second timeout
                                    .get()
                                    .getElementsByClass("img5")
                                    .get(0)
                                    .attr("src");
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }});
                runThread.start();
                runThread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
                return false;
            }
        }
        return true;
    }

}
