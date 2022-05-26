package com.wco_fun.wco_wrapper.ui.home.see_all;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.wco_fun.wco_wrapper.classes.series.Series;
import com.wco_fun.wco_wrapper.databinding.FragmentSeriesSeeAllBinding;
import com.wco_fun.wco_wrapper.MainActivity;
import com.wco_fun.wco_wrapper.ui.home.watch_adapters.ReactiveWatchAdapter;
import com.wco_fun.wco_wrapper.ui.home.watch_adapters.WatchAdapter;

import java.util.ArrayList;
import java.util.Collections;


public class SeeAllSeries extends Fragment {


    public SeeAllSeries() {
        // Required empty public constructor
    }

    private FragmentSeriesSeeAllBinding binding;
    private RecyclerView recycler;
//    private SeeAllAdapter adapter;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentSeriesSeeAllBinding.inflate(inflater,container,false);
        recycler = binding.seeAllRecycler;
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getActivity(), 3);
        recycler.setLayoutManager(layoutManager);
        ArrayList<Series> dispSeriesLEList = new ArrayList<Series>();
        String passedArg = getArguments().getString("variant");
        if (passedArg.equals("Watchlist")){
            ArrayList<Series> ref = new ArrayList<Series>(((MainActivity)getActivity()).getWatchlist().getWatchgroup());
            Collections.reverse(ref);
            recycler.setAdapter(new WatchAdapter(ref));
            binding.textView.setText("Watchlist");
        } else if (passedArg.equals("Continue")) {
            recycler.setAdapter(new ReactiveWatchAdapter(((MainActivity)getActivity()).getWatchData()));
            binding.textView.setText("Continue");
        } else { recycler.setAdapter(null); }


        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}