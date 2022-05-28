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
import androidx.fragment.app.Fragment;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.wco_fun.wco_wrapper.MainActivity;
import com.wco_fun.wco_wrapper.classes.episode.Episode;
import com.wco_fun.wco_wrapper.classes.series.Series;
import com.wco_fun.wco_wrapper.classes.series.SeriesControllable;
import com.wco_fun.wco_wrapper.classes.user_data.WatchData;
import com.wco_fun.wco_wrapper.classes.user_data.Watchlist;
import com.wco_fun.wco_wrapper.databinding.FragmentEpisodeSelectBinding;

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
    private ImageView seriesImage;
    private Elements seriesHtmlData;
    private SeriesControllable series;
    private EpisodeAdapter epAdapter;
    private ArrayList<Episode> episodes = new ArrayList<Episode>();
    private ImageButton saveButton;
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
        try {
            String src = getArguments().getString("link");
            final Document[] Html = new Document[1];
            Thread runThread = new Thread(new Runnable() {
                @Override //TODO improve threaded efficiency of this proc
                public void run() {
                    {
                        try {
                            Html[0] = Jsoup.connect(src)
                                    .userAgent("Mozilla/5.0 (Windows; U; WindowsNT 5.1; en-US; rv1.8.1.6) Gecko/20070725 Firefox/2.0.0.6")
                                    .timeout(5000)
                                    .maxBodySize(0)
                                    .get();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
            runThread.start(); //split to be thread policy safe
            runThread.join();

            //get series image link
            Elements imageEl = Html[0].getElementsByClass("img5");
            //create series object
            refSeries = new Series(src, getArguments().getString("title"), "https:" + imageEl.get(0).attr("src"));
            seriesImage = binding.seriesImage;
//            refSeries.getSeriesImage(seriesImage, 1.5);

            seriesHtmlData = Html[0].getElementsByClass("cat-eps");
            for (Element ep: seriesHtmlData) {
                ep = ep.child(0);
                Episode e = new Episode(ep);
                if (e.isValid()){
                    episodes.add(e);
                }
            }

//            refSeries.setNumEps(episodes.size());
            Collections.reverse(episodes);//reverse order to have first episode displayed first
            for (int i = 0; i < episodes.size(); i++) {
                episodes.get(i).setIdx(i);
            }

            watchlist = ((MainActivity)getActivity()).getWatchlist();
            watchData = ((MainActivity)getActivity()).getWatchData();
            series = (watchData.contains(refSeries.getTitle()))
                    ? watchData.get(refSeries.getTitle())
                    : new SeriesControllable(refSeries);
            series.setNumEps(episodes.size());
            series.fitSeriesImage(seriesImage); //queue the series image addition
//
            //post proc constrainer width setting
            binding.getRoot().post(new Runnable() {
                @Override
                public void run() {
                    int height = binding.epSelectHeader.getHeight();
                    binding.epSelectHeader.setLayoutParams(new ConstraintLayout
                            .LayoutParams(binding.getRoot().getWidth(), height));
                }
            });

            epAdapter = new EpisodeAdapter(episodes, series, watchData);

        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        title = binding.seriesTitle;
        title.setText(series.getTitle());

        saveButton = binding.buttonSave;
        if (watchlist.contains(series)) {
            saveButton.setImageDrawable(Drawable.createFromPath("@drawable/ic_done"));
        } else {
            saveButton.setImageDrawable(Drawable.createFromPath("@drawable/ic_add"));
        }


        recyclerView = binding.resultContainer;
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(
                recyclerView.getContext(), DividerItemDecoration.VERTICAL);
        recyclerView.addItemDecoration(dividerItemDecoration);
        recyclerView.setAdapter(epAdapter);

        //define back press behavior
//        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
//            @Override
//            public void handleOnBackPressed() {
//                NavHostFragment.findNavController(EpisodeSelect.this).popBackStack();
//            }
//        };
//        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), callback);


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
                    saveButton.setImageDrawable(Drawable.createFromPath("@drawable/ic_done"));
                } else {
                    watchlist.add(series);
                    saveButton.setImageDrawable(Drawable.createFromPath("@drawable/ic_done"));
                }
                watchlist.updateWatchlistJson();
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

    }

    @Override
    public void onResume() {
        super.onResume(); //TODO clean up write to watchdata logic
        if (series.hasEp()) { //nested if statements assure if series is timestamped that it will
            if (watchData.contains(series.getTitle())){ //series on watch data, attempt to bring up to date
                watchData.update(series);
                watchData.updateWatchDataJson();
            } else if (series.hasEp()) { //series has episode data but not on watchdata
                watchData.add(series);
                watchData.updateWatchDataJson();
            }

//            binding.buttonPlay.setText("Play " + ((series.getCurEp().getAbrTitle()==null)
//                    ?"Ep. " + ((Integer) (series.getCurEp().getIdx() + 1)).toString()
//                    : series.getCurEp().getAbrTitle()));
        }
        binding.buttonPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String url;
                series.updateLastWatched();
                if (series.hasEp()) {
                    url = series.getCurEp().getSrc();
                } else { //update current episode and next to 1 and 2 respectively
                    url = episodes.get(0).getSrc();
                    for (int i = 0; (i < 3) || (i >= episodes.size()); i++) {
                        series.addEp(episodes.get(i));//preload with up to 3 episodes
                    }
//                  //need to add to watchdata
                    ((MainActivity)getActivity()).getWatchData().add(series);
                }

//                if (!watchData.contains(series.getTitle())) {
//                    watchData.add(series)
//                }

                CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
                CustomTabsIntent customTabsIntent = builder.build();
                customTabsIntent.launchUrl(view.getContext(), Uri.parse(url));
            }
        });
//
        if (series.hasMoreEps()){
//            queueButtonUpdate(binding.buttonContinue, View.VISIBLE);
//            binding.buttonContinue.setText("Play Next " + ((series.getNextEp().getAbrTitle()==null)
//                    ? "Ep. " + ((Integer) (series.getNextEp().getIdx() + 2)).toString()
//                    : series.getNextEp().getAbrTitle()));
            binding.buttonNext.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    series.updateLastWatched();
                    String url = series.getNextEp().getSrc(); //update episodes

                    ArrayList<Episode> epQueue = new ArrayList<Episode>();
                    epQueue.add(series.getCurEp());
                    for (int i = series.getNextEp().getIdx(); (i < series.getNextEp().getIdx() + 2) || (i >= episodes.size()); i++) {
                        epQueue.add(episodes.get(i));//preload with up to 3 episodes
                    } series.overrideEpQueue(epQueue);

                    //sync with watchdata
                    watchData.update(series);
                    watchData.updateWatchDataJson();

                    CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
                    CustomTabsIntent customTabsIntent = builder.build();
                    customTabsIntent.launchUrl(view.getContext(), Uri.parse(url));
                }
            });
        } else {
//            queueButtonUpdate(binding.buttonContinue, View.GONE);
        }
    }

    //adds the button update to the end of the UI operation thread, allows post-generation setting of visibility
    private void queueButtonUpdate(Button btn, int status){
        getView().post(new Runnable() {
            @Override
            public void run() {
                btn.setVisibility(status);
            }
        });
    }
}