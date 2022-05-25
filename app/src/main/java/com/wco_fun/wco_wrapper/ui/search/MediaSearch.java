package com.wco_fun.wco_wrapper.ui.search;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.tabs.TabLayout;
import com.wco_fun.wco_wrapper.classes.SeriesSearchable_LE;
import com.wco_fun.wco_wrapper.databinding.FragmentMediaSearchBinding;

import org.jsoup.nodes.Element;

import java.util.ArrayList;


public class MediaSearch extends Fragment {

    //    private String[] testArray = {"string 1", "string 2", "string 3", "string 4"};
    private FragmentMediaSearchBinding binding;
    private RecyclerView recyclerView;
    private TextView searchbar;
    private Element seriesHtmlData;

    private String url;
    private ThreadedSearch threadedSearch;
    private ConnectedSearchThread searchThread;

    private ArrayList<SeriesSearchable_LE> dubbed, subbed, cartoon;

    //live search variables
    private SearchAdapter searchAdapter;

//    private char firstCharArch;
    private ArrayList<SeriesSearchable_LE> allSeries = new ArrayList<SeriesSearchable_LE>();
    private ArrayList<SeriesSearchable_LE> seriesSubList = new ArrayList<SeriesSearchable_LE>();

    public MediaSearch() {}

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {

        binding = FragmentMediaSearchBinding.inflate(inflater, container, false);

        url = getArguments().getString("link");

        searchAdapter = new SearchAdapter(allSeries);
        searchAdapter.attachProgressSpinner(binding.progressBar);
//        binding.progressBar.setVisibility(View.VISIBLE);
        searchAdapter.setThreadActive();
        searchThread = new ConnectedSearchThread(getActivity(), searchAdapter, url); //start search thread
        searchThread.start();

        recyclerView = binding.resultContainer;
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(
                recyclerView.getContext(), DividerItemDecoration.VERTICAL);
        recyclerView.addItemDecoration(dividerItemDecoration);
        recyclerView.setAdapter(searchAdapter);

        binding.resultIndicator.setVisibility(View.VISIBLE);
        binding.resultIndicator.setText("Start typing to search...");

        searchbar = binding.searchbar;
        searchbar.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void afterTextChanged(Editable s) {
                if (!(s.toString() == null)){ //added to address infrequent error due to the argument being a null reference
                    searchAdapter.reflectSearch(s.toString());
                    if (s.toString().length() == 0) {
                        binding.resultIndicator.setVisibility(View.VISIBLE);
                        binding.resultIndicator.setText("Start typing to search...");
                    } else if (searchAdapter.getItemCount() == 0) {
                        binding.resultIndicator.setVisibility(View.VISIBLE);
                        binding.resultIndicator.setText("No results found...");
                    } else {
                        binding.resultIndicator.setVisibility(View.GONE);
                    }
                }

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
        });

        binding.tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) { //run thread with corresponding urls for catagories
                //todo maybe verify a better workflow of the tread
                searchThread.cancel();
                try {
                    searchThread.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                boolean toRun = true;
                if (tab.getPosition() == 0 ) { //dubbed
                    if (dubbed == null) {
                        url = "https://www.wcofun.com/dubbed-anime-list";
                    } else {
                        searchAdapter.rebaseLegacyData(dubbed);
                        toRun = false;
                    }
                } else if (tab.getPosition() == 1) { //cartoons
                    if (cartoon == null) {
                        url = "https://www.wcofun.com/cartoon-list";
                    } else {
                        searchAdapter.rebaseLegacyData(cartoon);
                        toRun = false;
                    }
                } else { //subbed
                    if (subbed == null) {
                        url = "https://www.wcofun.com/subbed-anime-list";
                    } else {
                        searchAdapter.rebaseLegacyData(subbed);
                        toRun = false;
                    }
                }
                if (toRun) {
                    searchThread = new ConnectedSearchThread(getActivity(), searchAdapter, url);
                    searchThread.start();
                    searchAdapter.setThreadActive();
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) { //ensure that prev results are stored to reduce later loads
                if (tab.getPosition() == 0 ) { //dubbed
                    if (dubbed == null && !searchAdapter.getThreadActive()) {
                        dubbed = searchAdapter.getLegacyData();
                    }
                } else if (tab.getPosition() == 1) { //cartoons
                    if (cartoon == null && !searchAdapter.getThreadActive()) {
                        cartoon = searchAdapter.getLegacyData();
                    }
                } else { //subbed
                    if (subbed == null && !searchAdapter.getThreadActive()) {
                        subbed = searchAdapter.getLegacyData();
                    }
                }
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        return binding.getRoot();
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!binding.searchbar.getText().toString().isEmpty()) binding.resultIndicator.setVisibility(View.GONE);

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}