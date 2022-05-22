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

import com.wco_fun.wco_wrapper.classes.SeriesSearchable;
import com.wco_fun.wco_wrapper.databinding.FragmentMediaSearchBinding;

import org.jsoup.nodes.Element;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;


public class MediaSearch extends Fragment {

    //    private String[] testArray = {"string 1", "string 2", "string 3", "string 4"};
    private FragmentMediaSearchBinding binding;
    private RecyclerView recyclerView;
    private TextView searchbar;
    private Element seriesHtmlData;

    private String url;
    private ThreadedSearch threadedSearch;
    private Thread searchThread;

    //live search variables
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

        url = getArguments().getString("link");
        threadedSearch = new ThreadedSearch(url);
        searchThread = new Thread(threadedSearch);
        searchThread.start();

        searchAdapter = new SearchAdapter(allSeries);
        recyclerView = binding.resultContainer;
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(
                recyclerView.getContext(), DividerItemDecoration.VERTICAL);
        recyclerView.addItemDecoration(dividerItemDecoration);
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