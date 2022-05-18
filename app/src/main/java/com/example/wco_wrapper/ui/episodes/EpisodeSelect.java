package com.example.wco_wrapper.ui.episodes;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.wco_wrapper.MainActivity;
import com.example.wco_wrapper.classes.Episode;
import com.example.wco_wrapper.classes.Series;
import com.example.wco_wrapper.classes.Watchlist;
import com.example.wco_wrapper.databinding.FragmentEpisodeSelectBinding;
import com.example.wco_wrapper.ui.episodes.EpisodeAdapter;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.picasso.Picasso;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;

public class EpisodeSelect extends Fragment {

    private FragmentEpisodeSelectBinding binding;
    private RecyclerView recyclerView;
    private TextView title;
    private ImageView seriesImage;
    private Elements seriesHtmlData;
    private Series series;
    private EpisodeAdapter epAdapter;
    private ArrayList<Episode> episodes = new ArrayList<Episode>();
    private Button saveButton;
    private boolean saveState;
    private Watchlist watchlist;

    public EpisodeSelect() {}
    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {

        binding = FragmentEpisodeSelectBinding.inflate(inflater, container, false);
        Document Html;
        try {
            String src = getArguments().getString("link");
            Html = Jsoup.connect(src)
                    .userAgent("Mozilla/5.0 (Windows; U; WindowsNT 5.1; en-US; rv1.8.1.6) Gecko/20070725 Firefox/2.0.0.6")
                    .get();

            //get series image link
            Elements imageEl = Html.getElementsByClass("img5");
            //create series object
            series = new Series(src, getArguments().getString("title"), "https:" + imageEl.get(0).attr("src"));

            seriesImage = binding.seriesImage;
            series.getSeriesImage(seriesImage);

            seriesHtmlData = Html.getElementsByClass("cat-eps");
            for (Element ep: seriesHtmlData) {
                ep = ep.child(0);
                Episode e = new Episode(ep);
                if (e.isValid()){
                    episodes.add(e);
                }
            }
            series.setEpisodeCount(episodes.size());
            Collections.reverse(episodes);//reverse order to have first episode displayed first
            epAdapter = new EpisodeAdapter(episodes);
            watchlist = ((MainActivity)getActivity()).getWatchlist();

            series.onWatchlist((watchlist == null)
                    ? false
                    : watchlist.containsTitle(series.getSeriesTitle()));
        } catch (IOException e) {
            e.printStackTrace();
        }

        title = binding.seriesTitle;
        title.setText(series.getSeriesTitle());

        saveButton = binding.buttonSave;
        saveState = series.isOnWatchlist();
        if (saveState) {
            saveButton.setText("Remove from watchlist");
        } else {
            saveButton.setText("Add to watchlist");
        }


        recyclerView = binding.resultContainer;
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(epAdapter);

        return binding.getRoot();
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //TODO implement the ability to track the last episode watched
        //TODO implement the play button to play the last episode watched

        binding.buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Watchlist wl = ((MainActivity)getActivity()).getWatchlist();
                if (saveState) {
                    saveState = false;
                    wl.removeFromWatchlist(series);
                    wl.pendingChanges = true;
                    saveButton.setText("Add to watchlist");
                    ((MainActivity)getActivity()).updateWatchlist(wl);
                    ((MainActivity)getActivity()).updateWatchlistJson();
                } else {
                    if (!wl.containsTitle(series.getSeriesTitle())){ //double verification to prevent multiple instances
                        series.onWatchlist(true);
                        wl.addToWatchlist(series);
                        wl.pendingChanges = true;
                        ((MainActivity)getActivity()).updateWatchlist(wl);
                        ((MainActivity)getActivity()).updateWatchlistJson();
                        saveState = true;
                        saveButton.setText("Remove from watchlist");
                    }
                }


            }

        });

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    //https://stackoverflow.com/questions/6407324/how-to-display-image-from-url-on-android
    public static Drawable LoadImageFromWebOperations(String url) {
        try {
            InputStream is = (InputStream) new URL(url).getContent();
            Drawable d = Drawable.createFromStream(is, "src name");
            return d;
        } catch (Exception e) {
            return null;
        }
    }
}