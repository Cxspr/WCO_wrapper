package com.wco_fun.wco_wrapper.ui.home;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.wco_fun.wco_wrapper.MainActivity;
import com.wco_fun.wco_wrapper.classes.series.Series;
import com.wco_fun.wco_wrapper.classes.user_data.WatchData;
import com.wco_fun.wco_wrapper.classes.user_data.Watchlist;
import com.wco_fun.wco_wrapper.databinding.FragmentHomeBinding;
import com.wco_fun.wco_wrapper.ui.home.watchgroups.MultigroupAdapter;
import com.wco_fun.wco_wrapper.ui.home.watchgroups.SeriesGroup.GenericGroup;
import com.wco_fun.wco_wrapper.ui.home.watchgroups.SeriesGroup.NewEpGroup;
import com.wco_fun.wco_wrapper.ui.home.watchgroups.SeriesGroup.ReflectiveGroup;
import com.wco_fun.wco_wrapper.ui.home.watchgroups.SeriesGroup.SeriesGroup;

import org.jsoup.Jsoup;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

public class Home extends Fragment {

    private FragmentHomeBinding binding;
    private Watchlist wl;
    private WatchData wd;
    private ArrayList<Series> watchlist;

    private MultigroupAdapter multiAdapter;
    private RecyclerView homeRecycler;
    private ArrayList<SeriesGroup> watchgroups;

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
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }


}