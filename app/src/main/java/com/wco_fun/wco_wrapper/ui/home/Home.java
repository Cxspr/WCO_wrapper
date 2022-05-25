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
import com.wco_fun.wco_wrapper.classes.Series_LE;
import com.wco_fun.wco_wrapper.classes.Watchlist_LE;
import com.wco_fun.wco_wrapper.databinding.FragmentHomeBinding;

import java.util.ArrayList;

public class Home extends Fragment {


    private FragmentHomeBinding binding;
    private RecyclerView watchlistRecycler;
    private RecyclerView continueRecycler;
//    private RecyclerView homeRecycler;
//    private WatchgroupsAdapter watchgroupsAdapter;
    private Watchlist_LE watchlistLE;
//    private ArrayList<WatchgroupData> watchgroupData;
    private ArrayList<Series_LE> trueWatchlist, continueWatchlist;
    private WatchlistAdapter watchlistAdapter;
    private ContinueAdapter continueAdapter;


    //TODO modify UI of continue watching to allow user to play episode directly from the home screen
    //TODO create a new episode detector for watched series that checks for new episodes online

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {

        binding = FragmentHomeBinding.inflate(inflater, container, false);

        watchlistLE = ((MainActivity)getActivity()).getWatchlist();
//        watchgroupData = new ArrayList<WatchgroupData>();
//        watchgroupData.add(new WatchgroupData( //add watchlist
//                new WatchlistAdapter(watchlist.getTrueWatchlist()),
//                "Watchlist"
//        ));
//        watchgroupData.add(new WatchgroupData( //add continue watching data
//                new ContinueAdapter(watchlist),
//                "Continue"
//        ));

//        homeRecycler = binding.homeRecycler;
//        RecyclerView.LayoutManager homeLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
//        homeRecycler.setLayoutManager(homeLayoutManager);
//        watchgroupsAdapter = new WatchgroupsAdapter((watchgroupData.isEmpty())
//                ? null
//                : watchgroupData);
//        homeRecycler.setAdapter(watchgroupsAdapter);




        watchlistRecycler = binding.watchlistRecycler;
        RecyclerView.LayoutManager watchlistLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
//        watchlistLayoutManager.
        watchlistRecycler.setLayoutManager(watchlistLayoutManager);
        trueWatchlist = watchlistLE.getTrueWatchlist();
        watchlistAdapter = new WatchlistAdapter(trueWatchlist.isEmpty()
                ? null
                : trueWatchlist);
        watchlistRecycler.setAdapter(watchlistAdapter);


        continueRecycler = binding.continueRecycler;
        RecyclerView.LayoutManager continueLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        continueRecycler.setLayoutManager(continueLayoutManager);
        continueWatchlist = watchlistLE.getWatching();
        continueAdapter = new ContinueAdapter(continueWatchlist.isEmpty()
                ? null
                : watchlistLE);
        continueRecycler.setAdapter(continueAdapter);

        return binding.getRoot();
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.watchlistAccess.setOnClickListener((new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (trueWatchlist.isEmpty()) return;
                //if true watchlist contains anything then see all is enabled
                Bundle bundle = new Bundle();
                bundle.putString("variant","TrueWatchlist");
                NavHostFragment.findNavController(Home.this)
                        .navigate(R.id.action_homeScreen_to_seeAllSeries, bundle);
            }
        }));

        binding.continueAccess.setOnClickListener((new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (continueWatchlist.isEmpty()) return;
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
        watchlistLE = ((MainActivity) getActivity()).getWatchlist();
        watchlistAdapter.rebaseWatchlist((watchlistLE == null)
                ? null
                : watchlistLE.getTrueWatchlist());
        if (watchlistLE.getTrueWatchlist().isEmpty()) {
            binding.emptyWlIndic.setVisibility(View.VISIBLE);
        } else {
            binding.emptyWlIndic.setVisibility(View.GONE);
        }
        if (watchlistLE.getWatching().isEmpty()) {
            binding.emptyCtIndic.setVisibility(View.VISIBLE);
        } else {
            binding.emptyCtIndic.setVisibility(View.GONE);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}