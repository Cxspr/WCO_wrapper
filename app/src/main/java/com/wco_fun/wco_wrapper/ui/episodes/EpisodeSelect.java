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
import com.wco_fun.wco_wrapper.classes.Episode_LE;
import com.wco_fun.wco_wrapper.classes.Series_LE;
import com.wco_fun.wco_wrapper.classes.Watchlist_LE;
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
    private Series_LE seriesLE;
    private EpisodeAdapter epAdapter;
    private ArrayList<Episode_LE> episodeLES = new ArrayList<Episode_LE>();
    private Button saveButton;
    private boolean saveState;
    private Watchlist_LE watchlistLE;

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
            seriesLE = new Series_LE(src, getArguments().getString("title"), "https:" + imageEl.get(0).attr("src"));

            seriesImage = binding.seriesImage;
            seriesLE.getSeriesImage(seriesImage);

            seriesHtmlData = Html.getElementsByClass("cat-eps");
            for (Element ep: seriesHtmlData) {
                ep = ep.child(0);
                Episode_LE e = new Episode_LE(ep);
                if (e.isValid()){
                    episodeLES.add(e);
                }
            }

            seriesLE.setEpisodeCount(episodeLES.size());
            Collections.reverse(episodeLES);//reverse order to have first episode displayed first
            epAdapter = new EpisodeAdapter(episodeLES, seriesLE);
            watchlistLE = ((MainActivity)getActivity()).getWatchlist();
            seriesLE.onWatchlist(watchlistLE.titleOnWatchlist(seriesLE));
            seriesLE.overrideEpInfo(watchlistLE.getStoredSeries(seriesLE));


        } catch (IOException e) {
            e.printStackTrace();
        }

        title = binding.seriesTitle;
        title.setText(seriesLE.getSeriesTitle());

        saveButton = binding.buttonSave;
//        saveState = series.onWatchlist();
        if (seriesLE.onWatchlist()) {
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
                Watchlist_LE wl = ((MainActivity)getActivity()).getWatchlist();
                if (seriesLE.onWatchlist()) {
//                    saveState = false;
                    seriesLE.onWatchlist(false);
                    wl.removeSeriesFromWatchlist(seriesLE); //no need to update instance in MainActivity, auto updates
                    saveButton.setText("Add to watchlist");
                } else {
                    seriesLE.onWatchlist(true);
                    wl.addToWatchlist(seriesLE);
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

        ((MainActivity)getActivity()).getWatchlist().updateEp(seriesLE);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (seriesLE.hasEpInfo()){ //nested if statements assure if series is timestamped that it will
            if (seriesLE.getLastWatched() >= 0){ //be added to the watchlist
                if (!seriesLE.onWatchlist()) {
                    ((MainActivity)getActivity()).getWatchlist().addToWatchlist(seriesLE);
                } ((MainActivity)getActivity()).getWatchlist().updateEp(seriesLE);
            }


            binding.buttonPlay.setText("Play " + ((seriesLE.getAbrEpTitle()==null)
                    ?"Ep. " + ((Integer) (seriesLE.getEpIdx() + 1)).toString()
                    : seriesLE.getAbrEpTitle()));

        }
        binding.buttonPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String url;
                seriesLE.setLastWatched(new Date());
                if (seriesLE.hasEpInfo()) {
                    url = seriesLE.getCurEp();
                } else { //update current episode and next to 1 and 2 respectively
                    url = episodeLES.get(0).getSrc();
                    seriesLE.setCurEp(url);
                    seriesLE.setAbrEpTitle(episodeLES.get(0).getAbrTitle());
                    seriesLE.setEpIdx(0);
                    seriesLE.setNextEp((episodeLES.size() >= 2) ? episodeLES.get(1).getSrc() : null);
                    seriesLE.setNextAbrEpTitle((episodeLES.size() >= 2) ? episodeLES.get(1).getAbrTitle() : null);
//                    ((MainActivity)getActivity()).getWatchlist().updateEp(series);

                }

                if (!seriesLE.onWatchlist()) {
                    ((MainActivity)getActivity()).getWatchlist().addToWatchlist(seriesLE);
                } ((MainActivity)getActivity()).getWatchlist().updateEp(seriesLE);

                CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
                CustomTabsIntent customTabsIntent = builder.build();
                customTabsIntent.launchUrl(view.getContext(), Uri.parse(url));
            }
        });

        if (seriesLE.hasNextEp()){
            binding.buttonContinue.setVisibility(View.VISIBLE);
            binding.buttonContinue.setText("Play Next " + ((seriesLE.getNextAbrEpTitle()==null)
                    ? "Ep. " + ((Integer) (seriesLE.getEpIdx() + 2)).toString()
                    : seriesLE.getNextAbrEpTitle()));
            binding.buttonContinue.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    seriesLE.setLastWatched(new Date());
                    String url = seriesLE.getNextEp();
                    //update current episode to next episode and update next episode if possible
                    seriesLE.setCurEp(url);
                    seriesLE.setAbrEpTitle(seriesLE.getNextAbrEpTitle());
                    seriesLE.setEpIdx(seriesLE.getEpIdx() + 1);
                    if (seriesLE.getEpIdx() >= episodeLES.size() - 1) { //current 'next' episode is last available episode
                        seriesLE.setNextEp(null);
                        seriesLE.setNextAbrEpTitle(null);
                    } else {
                        seriesLE.setNextEp(episodeLES.get(seriesLE.getEpIdx()).getSrc());
                        seriesLE.setNextAbrEpTitle(episodeLES.get(seriesLE.getEpIdx()).getAbrTitle());
                    }

                    if (!seriesLE.onWatchlist()) {
                        ((MainActivity)getActivity()).getWatchlist().addToWatchlist(seriesLE);
                    } ((MainActivity)getActivity()).getWatchlist().updateEp(seriesLE);


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