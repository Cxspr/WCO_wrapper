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
import com.wco_fun.wco_wrapper.ui.home.watchgroups.MultigroupAdapter;
import com.wco_fun.wco_wrapper.ui.home.watchgroups.SeriesGroup.GenericGroup;
import com.wco_fun.wco_wrapper.ui.home.watchgroups.SeriesGroup.NewEpGroup;
import com.wco_fun.wco_wrapper.ui.home.watchgroups.SeriesGroup.ReflectiveGroup;
import com.wco_fun.wco_wrapper.ui.home.watchgroups.SeriesGroup.SeriesGroup;

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

    private MultigroupAdapter multiAdapter;
    private RecyclerView homeRecycler;
    ArrayList<SeriesGroup> watchgroups;


    //TODO modify UI of continue watching to allow user to play episode directly from the home screen
    //TODO create a new episode detector for watched series that checks for new episodes online

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        watchgroups = new ArrayList<>();
        wd = ((MainActivity)getActivity()).getWatchData();
        wl = ((MainActivity)getActivity()).getWatchlist();

        NewEpGroup newEpGroup = new NewEpGroup(wl.getWatchgroup(), (MainActivity)getActivity());
        watchgroups.add(newEpGroup);

        watchlist = new ArrayList<Series>(wl.getWatchgroup());
        Collections.reverse(watchlist);
        watchgroups.add(new GenericGroup("Watchlist", watchlist));

        watchgroups.add(new ReflectiveGroup(wd));

        homeRecycler = binding.homeRecycler;
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false);
        homeRecycler.setLayoutManager(layoutManager);

        multiAdapter = new MultigroupAdapter(watchgroups, (MainActivity)getActivity());
        homeRecycler.setAdapter(multiAdapter);



        return binding.getRoot();
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


    }

    @Override
    public void onStart() {
        super.onStart();

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}