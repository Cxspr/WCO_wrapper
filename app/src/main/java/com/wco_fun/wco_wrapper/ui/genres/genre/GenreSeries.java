package com.wco_fun.wco_wrapper.ui.genres.genre;

import android.app.Activity;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.wco_fun.wco_wrapper.MainActivity;
import com.wco_fun.wco_wrapper.R;
import com.wco_fun.wco_wrapper.classes.CachedContent.SearchCache;
import com.wco_fun.wco_wrapper.databinding.FragmentGenreSeriesBinding;
import com.wco_fun.wco_wrapper.ui.search.ConnectedSearchThread;

public class GenreSeries extends Fragment {
    private FragmentGenreSeriesBinding binding;
    private RecyclerView recyclerView;
    private TextView textView;
    private TextView searchbar;

    private String url;
    private ConnectedGenreSeriesThread genreThread;
    private GenreSeriesAdapter genreAdapter;

    int cachedTab;

    public GenreSeries() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentGenreSeriesBinding.inflate(inflater,container,false);

        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((Activity) getActivity())
                .getWindowManager()
                .getDefaultDisplay()
                .getMetrics(displayMetrics);

        double displayHeightDP = displayMetrics.heightPixels / displayMetrics.density; //get height, convert to dp
        final double uiScalar = displayHeightDP / 800; //UI was built on a simulated display with ~800dp height

        textView = binding.textView11;
        textView.setText(getArguments().getString("title"));
        textView.post(new Runnable() {
            @Override
            public void run() {
                textView.setTextSize(0, (float) ( textView.getTextSize() * (uiScalar) ));
            }
        });

        url = getArguments().getString("link");

        genreAdapter = new GenreSeriesAdapter(binding.genreStateContainer, displayMetrics);

        SearchCache cache = ((MainActivity)getActivity()).getSearchCache();
        if (cache.hasReturnTab()) {
            genreAdapter.rebaseLegacyData(cache.getCache());
            genreAdapter.searchState(1);
        } else {
            genreAdapter.searchState(0);
            genreThread = new ConnectedGenreSeriesThread(getActivity(), genreAdapter, url);
            genreThread.start();
        }

        recyclerView = binding.resultContainer;
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(
                recyclerView.getContext(), DividerItemDecoration.VERTICAL);
        recyclerView.addItemDecoration(dividerItemDecoration);
        recyclerView.setAdapter(genreAdapter);

        searchbar = binding.searchBar;
        searchbar.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void afterTextChanged(Editable s) {
                if (!(s.toString() == null)){ //added to address infrequent error due to the argument being a null reference
                    genreAdapter.reflectSearch(s.toString());
                    if (s.toString().length() == 0) {
                        binding.resultIndicator.setVisibility(View.VISIBLE);
                        binding.resultIndicator.setText("Start typing to search...");
                    } else if (genreAdapter.getItemCount() == 0) {
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

        binding.genreRetry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                genreAdapter.searchState(0);
                genreThread = new ConnectedGenreSeriesThread(getActivity(), genreAdapter, url);
                genreThread.start();
            }
        });
    }

    @Override
    public void onDestroyView() {
        SearchCache cache = ((MainActivity)getActivity()).getSearchCache();
        NavController navController = Navigation.findNavController(this.getActivity(), R.id.nav_host_fragment_content_main);
        if (navController.getCurrentDestination().getId() == R.id.genreSelection) {
            cache.clear();
        } else {
            cache.updateCache(genreAdapter.getLegacyData(), 3);
        }
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!binding.searchBar.getText().toString().isEmpty()) binding.resultIndicator.setVisibility(View.GONE);
    }
}