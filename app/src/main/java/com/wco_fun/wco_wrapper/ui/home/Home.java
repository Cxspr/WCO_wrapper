package com.wco_fun.wco_wrapper.ui.home;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.wco_fun.wco_wrapper.MainActivity;
import com.wco_fun.wco_wrapper.R;
import com.wco_fun.wco_wrapper.classes.series.Series;
import com.wco_fun.wco_wrapper.classes.series.SeriesControllable;
import com.wco_fun.wco_wrapper.classes.user_data.WatchData;
import com.wco_fun.wco_wrapper.classes.user_data.Watchlist;
import com.wco_fun.wco_wrapper.databinding.FragmentHomeBinding;
import com.wco_fun.wco_wrapper.ui.home.watch_adapters.ReactiveWatchAdapter;
import com.wco_fun.wco_wrapper.ui.home.watch_adapters.WatchAdapter;

import java.util.ArrayList;
import java.util.Collections;

public class Home extends Fragment {


    private FragmentHomeBinding binding;
    private RecyclerView watchlistRecycler;
    private RecyclerView watchDataRecycler;
    private Watchlist wl;
    private WatchData wd;
    private ArrayList<Series> watchlist;
    private ArrayList<SeriesControllable> watchData;
    private WatchAdapter watchAdapter;
    private ReactiveWatchAdapter reactiveWatchAdapter;


    //TODO modify UI of continue watching to allow user to play episode directly from the home screen
    //TODO create a new episode detector for watched series that checks for new episodes online

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {

        binding = FragmentHomeBinding.inflate(inflater, container, false);

        wl = ((MainActivity)getActivity()).getWatchlist();
        watchlist = new ArrayList<Series>(wl.getWatchgroup());
        Collections.reverse(watchlist);
        watchlistRecycler = binding.wlRecycler;
        watchlistRecycler.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        watchAdapter = new WatchAdapter(watchlist);
        watchlistRecycler.setAdapter(watchAdapter);

        wd = ((MainActivity)getActivity()).getWatchData();
        watchData = wd.getWatching();
        watchDataRecycler = binding.wdRecycler;
        watchDataRecycler.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        reactiveWatchAdapter = new ReactiveWatchAdapter(wd);
        watchDataRecycler.setAdapter(reactiveWatchAdapter);

        return binding.getRoot();
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.wlAccess.setOnClickListener((new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (wl.isEmpty()) return;
                //if true watchlist contains anything then see all is enabled
                Bundle bundle = new Bundle();
                bundle.putString("variant","Watchlist");
                NavHostFragment.findNavController(Home.this)
                        .navigate(R.id.action_homeScreen_to_seeAllSeries, bundle);
            }
        }));

        binding.wdAccess.setOnClickListener((new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (wd.isEmpty()) return;
                //if true watchlist contains anything then see all is enabled
                Bundle bundle = new Bundle();
                bundle.putString("variant","Continue");
                NavHostFragment.findNavController(Home.this)
                        .navigate(R.id.action_homeScreen_to_seeAllSeries, bundle);
            }
        }));

    }

    @Override
    public void onStart() {
        super.onStart();
        if (watchlist.isEmpty()) {
            binding.wlEmptyInd.setVisibility(View.VISIBLE);
        } else {
            binding.wlEmptyInd.setVisibility(View.GONE);
        }
        if (watchData.isEmpty()) {
            binding.wdEmptyInd.setVisibility(View.VISIBLE);
        } else {
            binding.wdEmptyInd.setVisibility(View.GONE);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}