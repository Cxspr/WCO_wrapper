package com.example.wco_wrapper;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.wco_wrapper.databinding.FragmentMediaSearchBinding;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class MediaSearch extends Fragment {



    //    private String[] testArray = {"string 1", "string 2", "string 3", "string 4"};
    private FragmentMediaSearchBinding binding;
    private RecyclerView recyclerView;
    private TextView searchbar;
    private Element seriesHtmlData;

    //live search variables
    private int numChars = 0;
    private SearchAdapter searchAdapter;
//    private char firstCharArch;
    private ArrayList<Series> allSeries = new ArrayList<Series>();
    private ArrayList<Series> seriesSubList = new ArrayList<Series>();

    public MediaSearch() {}

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {

        binding = FragmentMediaSearchBinding.inflate(inflater, container, false);

        try {
            //scrape for series list html
            seriesHtmlData = (Jsoup.connect(getArguments().getString("link"))
                    .userAgent("Mozilla/5.0 (Windows; U; WindowsNT 5.1; en-US; rv1.8.1.6) Gecko/20070725 Firefox/2.0.0.6")
                    .get()).getElementById("ddmcc_container").child(0).child(0);
            //pre-collect series data
            for (int i = 0; i <= (int) 'Z' - 64; i++) {
                int idx = i * 3 + 2;
                Element charCollect = seriesHtmlData.child(idx);
                for (Element el : charCollect.children()) {
                    el = el.child(0);
                    Series e = new Series(el);
                    if (e.isValid()) {
                        allSeries.add(e);
                    }
                }
            }
            searchAdapter = new SearchAdapter(allSeries);

        } catch (IOException e) {
            e.printStackTrace();
        }

        recyclerView = binding.resultContainer;
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
//        recyclerView.setVisibility(View.INVISIBLE);
        recyclerView.setAdapter(searchAdapter);

        searchbar = binding.searchbar;
        searchbar.addTextChangedListener(new TextWatcher() {
//            boolean wasEmpty = true;

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void afterTextChanged(Editable s) {
                searchAdapter.reflectSearch(s.toString());
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
        });



        return binding.getRoot();
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

//        binding.buttonSecond.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                NavHostFragment.findNavController(MediaSearch.this)
//                        .navigate(R.id.action_SecondFragment_to_FirstFragment);
//            }
//        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }


}