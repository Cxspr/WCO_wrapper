package com.wco_fun.wco_wrapper.ui.home.see_all;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.wco_fun.wco_wrapper.databinding.FragmentSeriesSeeAllBinding;
import com.wco_fun.wco_wrapper.MainActivity;
import com.wco_fun.wco_wrapper.classes.Series;
import com.wco_fun.wco_wrapper.classes.Watchlist;
import com.wco_fun.wco_wrapper.ui.home.ContinueAdapter;
import com.wco_fun.wco_wrapper.ui.home.WatchlistAdapter;
import com.wco_fun.wco_wrapper.ui.home.see_all.SeeAllAdapter;

import java.util.ArrayList;


public class SeeAllSeries extends Fragment {


    public SeeAllSeries() {
        // Required empty public constructor
    }

    private FragmentSeriesSeeAllBinding binding;
    private RecyclerView recycler;
    private Watchlist watchlist;
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
        watchlist = ((MainActivity)getActivity()).getWatchlist();
        recycler = binding.seeAllRecycler;
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getActivity(), 3);
        recycler.setLayoutManager(layoutManager);
        ArrayList<Series> dispSeriesList = new ArrayList<Series>();
        String passedArg = getArguments().getString("variant");
        if (passedArg.equals("TrueWatchlist")){
//            dispSeriesList = watchlist.getTrueWatchlist();
            recycler.setAdapter(new WatchlistAdapter(watchlist.getTrueWatchlist()));
            binding.textView.setText("Watchlist");
        } else if (passedArg.equals("Continue")) {
//            dispSeriesList = watchlist.getWatching();
            recycler.setAdapter(new ContinueAdapter(watchlist));
            binding.textView.setText("Continue");
        } else { recycler.setAdapter(null); }


//        adapter = new SeeAllAdapter((dispSeriesList.isEmpty())
//                ? null
//                : dispSeriesList);
//        recycler.setAdapter(adapter);


        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}