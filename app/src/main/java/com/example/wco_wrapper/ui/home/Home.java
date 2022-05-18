package com.example.wco_wrapper.ui.home;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.wco_wrapper.MainActivity;
import com.example.wco_wrapper.R;
import com.example.wco_wrapper.classes.Episode;
import com.example.wco_wrapper.classes.Watchlist;
import com.example.wco_wrapper.databinding.FragmentEpisodeSelectBinding;
import com.example.wco_wrapper.databinding.FragmentHomeBinding;
import com.example.wco_wrapper.ui.episodes.EpisodeAdapter;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.util.Collections;

public class Home extends Fragment {


    private FragmentHomeBinding binding;
    private RecyclerView watchlistRecycler;
    private Watchlist watchlist;
    private WatchlistAdapter watchlistAdapter;

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        watchlist = ((MainActivity)getActivity()).getWatchlist();
        watchlistRecycler = binding.watchlistRecycler;
        RecyclerView.LayoutManager watchlistLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
//        watchlistLayoutManager.
        watchlistRecycler.setLayoutManager(watchlistLayoutManager);
        watchlistAdapter = new WatchlistAdapter((watchlist==null)
                ? null
                : watchlist.getReversed());
        watchlistRecycler.setAdapter(watchlistAdapter);


        return binding.getRoot();
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
//        binding.buttonFirst.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                NavHostFragment.findNavController(MediaSelect.this)
//                        .navigate(R.id.action_FirstFragment_to_SecondFragment);
//            }
//        });

    }

    @Override
    public void onResume() {
        super.onResume();
        watchlist = ((MainActivity)getActivity()).getWatchlist();
        watchlistAdapter.rebaseWatchlist((watchlist==null)
                ? null
                : watchlist.getReversed());
    }

//    @Override
//    public void onStart() {
//        super.onStart();
//        watchlist = ((MainActivity)getActivity()).getWatchlist();
//        watchlistAdapter.rebaseWatchlist((watchlist==null)
//                ? null
//                : watchlist.getReversed());
//    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}