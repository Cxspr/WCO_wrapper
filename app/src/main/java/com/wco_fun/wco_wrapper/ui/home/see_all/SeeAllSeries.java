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
import com.wco_fun.wco_wrapper.ui.home.watchgroups.WatchgroupAdapter;

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

        String title = getArguments().getString("title");
        binding.textView.setText(title);
        int variant = getArguments().getInt("variant");

        recycler = binding.seeAllRecycler;
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getActivity(), 3);
        recycler.setLayoutManager(layoutManager);
        if (variant <= 3){
            WatchgroupAdapter adapter = new WatchgroupAdapter(((MainActivity)getActivity()).getSeeAllCache());
            recycler.setAdapter(adapter);
        }

        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}