package com.wco_fun.wco_wrapper.ui.episodes;

import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
import androidx.browser.customtabs.CustomTabsIntent;
import androidx.fragment.app.Fragment;
import androidx.annotation.NonNull;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.wco_fun.wco_wrapper.MainActivity;
import com.wco_fun.wco_wrapper.classes.Episode;
import com.wco_fun.wco_wrapper.classes.Series;
import com.wco_fun.wco_wrapper.classes.Watchlist;
import com.wco_fun.wco_wrapper.databinding.FragmentEpisodeSelectBinding;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

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
            epAdapter = new EpisodeAdapter(episodes, series);
            watchlist = ((MainActivity)getActivity()).getWatchlist();
            series.onWatchlist((watchlist == null) //get watchlist state
                    ? false
                    : watchlist.containsTitle(series.getSeriesTitle()));
            series.overrideEpInfo(watchlist.getStoredSeries(series));


        } catch (IOException e) {
            e.printStackTrace();
        }

        title = binding.seriesTitle;
        title.setText(series.getSeriesTitle());

        saveButton = binding.buttonSave;
//        saveState = series.onWatchlist();
        if (series.onWatchlist()) {
            saveButton.setText("Remove from watchlist");
        } else {
            saveButton.setText("Add to watchlist");
        }


        recyclerView = binding.resultContainer;
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(
                recyclerView.getContext(), DividerItemDecoration.VERTICAL);
        recyclerView.addItemDecoration(dividerItemDecoration);
        recyclerView.setAdapter(epAdapter);

        //define back press behavior
        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                NavHostFragment.findNavController(EpisodeSelect.this).popBackStack();
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), callback);


        return binding.getRoot();
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Watchlist wl = ((MainActivity)getActivity()).getWatchlist();
                if (series.onWatchlist()) {
//                    saveState = false;
                    series.onWatchlist(false);
                    wl.removeFromWatchlist(series); //no need to update instance in MainActivity, auto updates
                    saveButton.setText("Add to watchlist");
                } else {
                    series.onWatchlist(true);
                    wl.addToWatchlist(series);
//                    saveState = true;
                    saveButton.setText("Remove from watchlist");
                }
                wl.updateWatchlistJSON();

            }

        });


    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onPause() {
        super.onPause();

        ((MainActivity)getActivity()).getWatchlist().updateEp(series);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (series.hasEpInfo()){ //nested if statements assure if series is timestamped that it will
            if (series.getLastWatched() >= 0){ //be added to the watchlist
                if (!series.onWatchlist()) {
                    ((MainActivity)getActivity()).getWatchlist().addToWatchlist(series);
                } ((MainActivity)getActivity()).getWatchlist().updateEp(series);
            }


            binding.buttonPlay.setText("Play\n" + ((series.getAbrEpTitle()==null)
                    ?"Ep. " + ((Integer) (series.getEpIdx() + 1)).toString()
                    : series.getAbrEpTitle()));

        }
        binding.buttonPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String url;
                series.setLastWatched(new Date());
                if (series.hasEpInfo()) {
                    url = series.getCurEp();
                } else { //update current episode and next to 1 and 2 respectively
                    url = episodes.get(0).getSrc();
                    series.setCurEp(url);
                    series.setAbrEpTitle(episodes.get(0).getAbrTitle());
                    series.setEpIdx(0);
                    series.setNextEp((episodes.size() >= 2) ? episodes.get(1).getSrc() : null);
                    series.setNextAbrEpTitle((episodes.size() >= 2) ? episodes.get(1).getAbrTitle() : null);
//                    ((MainActivity)getActivity()).getWatchlist().updateEp(series);

                }

                if (!series.onWatchlist()) {
                    ((MainActivity)getActivity()).getWatchlist().addToWatchlist(series);
                } ((MainActivity)getActivity()).getWatchlist().updateEp(series);

                CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
                CustomTabsIntent customTabsIntent = builder.build();
                customTabsIntent.launchUrl(view.getContext(), Uri.parse(url));
            }
        });

        if (series.hasNextEp()){
            binding.buttonContinue.setVisibility(View.VISIBLE);
            binding.buttonContinue.setText("Play Next\n" + ((series.getNextAbrEpTitle()==null)
                    ? "Ep. " + ((Integer) (series.getEpIdx() + 2)).toString()
                    : series.getNextAbrEpTitle()));
            binding.buttonContinue.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    series.setLastWatched(new Date());
                    String url = series.getNextEp();
                    //update current episode to next episode and update next episode if possible
                    series.setCurEp(url);
                    series.setAbrEpTitle(series.getNextAbrEpTitle());
                    series.setEpIdx(series.getEpIdx() + 1);
                    if (series.getEpIdx() >= episodes.size() - 1) { //current 'next' episode is last available episode
                        series.setNextEp(null);
                        series.setNextAbrEpTitle(null);
                    } else {
                        series.setNextEp(episodes.get(series.getEpIdx()).getSrc());
                        series.setNextAbrEpTitle(episodes.get(series.getEpIdx()).getAbrTitle());
                    }

                    if (!series.onWatchlist()) {
                        ((MainActivity)getActivity()).getWatchlist().addToWatchlist(series);
                    } ((MainActivity)getActivity()).getWatchlist().updateEp(series);


                    CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
                    CustomTabsIntent customTabsIntent = builder.build();
                    customTabsIntent.launchUrl(view.getContext(), Uri.parse(url));
                }
            });
        } else {
            binding.buttonContinue.setVisibility(View.GONE);
        }
    }
}