package com.wco_fun.wco_wrapper.ui.episodes;

import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.browser.customtabs.CustomTabsIntent;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.wco_fun.wco_wrapper.MainActivity;
import com.wco_fun.wco_wrapper.R;
import com.wco_fun.wco_wrapper.classes.episode.Episode;
import com.wco_fun.wco_wrapper.classes.series.Series;
import com.wco_fun.wco_wrapper.classes.series.SeriesControllable;
import com.wco_fun.wco_wrapper.classes.user_data.WatchData;
import com.wco_fun.wco_wrapper.classes.user_data.Watchlist;
import com.wco_fun.wco_wrapper.databinding.FragmentEpisodeSelectBinding;
import com.wco_fun.wco_wrapper.ui.search.ConnectedSearchThread;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

public class EpisodeSelect extends Fragment {

    private FragmentEpisodeSelectBinding binding;
    private RecyclerView recyclerView;
    private TextView title;
    private ImageButton saveButton;
    private ImageView seriesImage;
    private Elements seriesHtmlData;

    private ConnectedEpSearchThread searchThread;
    private EpisodeAdapter epAdapter;

    private SeriesControllable series;

    private Watchlist watchlist;
    private WatchData watchData;

    public EpisodeSelect() {}
    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        binding = FragmentEpisodeSelectBinding.inflate(inflater, container, false);

        Series refSeries;
        String src = getArguments().getString("link");
        refSeries = new Series(src,getArguments().getString("title"));

        watchlist = ((MainActivity)getActivity()).getWatchlist();
        watchData = ((MainActivity)getActivity()).getWatchData();
        series = (watchData.contains(refSeries.getTitle()))
                ? watchData.get(refSeries.getTitle())
                : new SeriesControllable(refSeries);

        epAdapter = new EpisodeAdapter(series, watchData);
        epAdapter.attachProgBar(binding.epSearchProg);
        epAdapter.attachRetryBtn(binding.epSearchRetry);
        epAdapter.attachSeriesImg(binding.seriesImage);
        epAdapter.attachWatchlist(watchlist);

        searchThread = new ConnectedEpSearchThread(getActivity(), epAdapter, src);
        searchThread.start();

        title = binding.seriesTitle;
        title.setText(series.getTitle());

        saveButton = binding.buttonSave;
        if (watchlist.contains(series)) {
            saveButton.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.ic_done));
        } else {
            saveButton.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.ic_add));
        }

        recyclerView = binding.resultContainer;
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(
                recyclerView.getContext(), DividerItemDecoration.VERTICAL);
        recyclerView.addItemDecoration(dividerItemDecoration);
        recyclerView.setAdapter(epAdapter);

        return binding.getRoot();
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                watchlist = ((MainActivity)getActivity()).getWatchlist();
                if (watchlist.contains(series)) {
                    watchlist.remove(series); //no need to update instance in MainActivity, auto updates
                    saveButton.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.ic_add));
                } else {
                    watchlist.add(series);
                    saveButton.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.ic_done));
                }
                watchlist.updateWatchlistJson();
            }
        });

        binding.epSearchRetry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                epAdapter.searchState(0);
                searchThread = new ConnectedEpSearchThread(getActivity(), epAdapter, series.getSrc());
                searchThread.start();
            }
        });
    }

    @Override
    public void onDestroyView() {

        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (series.hasEp()) { //nested if statements assure if series is timestamped that it will
            if (watchData.contains(series.getTitle())){ //series on watch data, attempt to bring up to date
                watchData.update(series);
                watchData.updateWatchDataJson();
            } else if (series.hasEp()) { //series has episode data but not on watchdata
                watchData.add(series);
                watchData.updateWatchDataJson();
            }

            binding.curEpText.setText("Current Ep: " + ((series.getCurEp().getAbrTitle()==null)
                    ?"Ep. " + ((Integer) (series.getCurEp().getIdx() + 1)).toString()
                    : series.getCurEp().getAbrTitle()));
        } else {
            binding.curEpText.setText("Current Ep: none");
        }
        epAdapter.makePlayRqst(binding.buttonPlay);

        if (series.hasMoreEps()){
            binding.buttonNext.setColorFilter(getContext().getColor(R.color.yellow_200));
            binding.buttonNext.setEnabled(true);
            binding.nextEpText.setText("Next Ep: " + ((series.getNextEp().getAbrTitle()==null)
                    ? "Ep. " + ((Integer) (series.getNextEp().getIdx() + 2)).toString()
                    : series.getNextEp().getAbrTitle()));
            epAdapter.makeNextRqst(binding.buttonNext);

        } else {
            binding.buttonNext.setColorFilter(getContext().getColor(R.color.dark_grey));
            binding.buttonNext.setEnabled(false);
            binding.nextEpText.setText("Next Ep: none");
        }
    }


}