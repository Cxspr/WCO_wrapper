package com.example.wco_wrapper.ui.search;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.wco_wrapper.classes.SeriesSearchable;
import com.example.wco_wrapper.databinding.FragmentMediaSearchBinding;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;


public class MediaSearch extends Fragment {

    //TODO find and fix what's causing the bug allowing for duplicate entries when backing out from episode list to media search

    //    private String[] testArray = {"string 1", "string 2", "string 3", "string 4"};
    private FragmentMediaSearchBinding binding;
    private RecyclerView recyclerView;
    private TextView searchbar;
    private Element seriesHtmlData;

    private String url;
    private ThreadedSearch threadedSearch;
    private Thread searchThread;

    //live search variables
    private int numChars = 0;
    private SearchAdapter searchAdapter;
//    private char firstCharArch;
    private ArrayList<SeriesSearchable> allSeries = new ArrayList<SeriesSearchable>();
    private ArrayList<SeriesSearchable> seriesSubList = new ArrayList<SeriesSearchable>();

    public MediaSearch() {}

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {

        binding = FragmentMediaSearchBinding.inflate(inflater, container, false);

//        try {
//            //scrape for series list html
//            //TODO get to run on separate thread;
//            seriesHtmlData = (Jsoup.connect(getArguments().getString("link"))
//                    .userAgent("Mozilla/5.0 (Windows; U; WindowsNT 5.1; en-US; rv1.8.1.6) Gecko/20070725 Firefox/2.0.0.6")
//                    .get()).getElementById("ddmcc_container").child(0).child(0);
//            //pre-collect series data
//            for (int i = 0; i <= (int) 'Z' - 64; i++) {
//                int idx = i * 3 + 2;
//                Element charCollect = seriesHtmlData.child(idx);
//                for (Element el : charCollect.children()) {
//                    el = el.child(0);
//                    SeriesSearchable e = new SeriesSearchable(el);
//                    if (e.isValid()) {
//                        allSeries.add(e);
//                    }
//                }
//            }
//            searchAdapter = new SearchAdapter(allSeries);
//
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
        url = getArguments().getString("link");
        threadedSearch = new ThreadedSearch(url);
        searchThread = new Thread(threadedSearch);
        searchThread.start();

        searchAdapter = new SearchAdapter(allSeries);
        recyclerView = binding.resultContainer;
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
//        recyclerView.setVisibility(View.INVISIBLE);
        recyclerView.setAdapter(searchAdapter);

        binding.resultIndicator.setVisibility(View.VISIBLE);
        binding.resultIndicator.setText("Start typing to search...");

        searchbar = binding.searchbar;
        searchbar.addTextChangedListener(new TextWatcher() {
//            boolean wasEmpty = true;

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



        return binding.getRoot();
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        try {
            searchThread.join();
            searchAdapter.rebaseLegacyData(threadedSearch.get());
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }


}