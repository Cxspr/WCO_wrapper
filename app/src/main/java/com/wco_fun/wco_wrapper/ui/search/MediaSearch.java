package com.wco_fun.wco_wrapper.ui.search;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.wco_fun.wco_wrapper.MainActivity;
import com.wco_fun.wco_wrapper.R;
import com.wco_fun.wco_wrapper.classes.CachedContent.SearchCache;
import com.wco_fun.wco_wrapper.classes.series.SeriesSearchable;
import com.wco_fun.wco_wrapper.databinding.FragmentMediaSearchBinding;

import java.util.ArrayList;


public class MediaSearch extends Fragment {
    private FragmentMediaSearchBinding binding;
    private RecyclerView recyclerView;
    private TextView searchbar;

    private String url;
    private ConnectedSearchThread searchThread;
    private SearchAdapter searchAdapter;

    private SharedPreferences sharedPrefs;
    private ArrayList<SeriesSearchable> dubbed, subbed, cartoon;
    int cachedTab, retTab;
    boolean backNav = false;

    public MediaSearch() {}

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        binding = FragmentMediaSearchBinding.inflate(inflater, container, false);
        sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        url = getArguments().getString("link");

        searchAdapter = new SearchAdapter(binding.searchStateContainer);
        //check cache for preserved results
        cachedTab = readCache();
        if (cachedTab == -1) {
            searchAdapter.searchState(0);
            searchThread = new ConnectedSearchThread(getActivity(), searchAdapter, url); //start search thread
            searchThread.start();
        } else {
            binding.tabLayout.getTabAt(cachedTab).select();
            searchAdapter.rebaseLegacyData(searchCatByIdx(cachedTab));
            searchAdapter.searchState(1);
        }

        recyclerView = binding.resultContainer;
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(
                recyclerView.getContext(), DividerItemDecoration.VERTICAL);
        recyclerView.addItemDecoration(dividerItemDecoration);
        recyclerView.setAdapter(searchAdapter);

        binding.resultIndicator.setVisibility(View.VISIBLE);
        binding.resultIndicator.setText("Start typing to search...");

        //responsive search listener
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
                if (searchThread != null && searchThread.isAlive()) {
                    searchThread.cancel();
                    try {
                        searchThread.join(); //ensure old thread is dead before opening a new one
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                String prefDomain = sharedPrefs.getString("domain_pref", "https://www.wcofun.com");
                boolean toRun = true;
                if (tab.getPosition() == 0 ) { //dubbed
                    if (dubbed == null || dubbed.isEmpty()) {
                        url = prefDomain + "/dubbed-anime-list";
                    } else {
                        searchAdapter.rebaseLegacyData(dubbed);
                        toRun = false;
                    }
                } else if (tab.getPosition() == 1) { //cartoons
                    if (cartoon == null || cartoon.isEmpty()) {
                        url = prefDomain + "/cartoon-list";
                    } else {
                        searchAdapter.rebaseLegacyData(cartoon);
                        toRun = false;
                    }
                } else { //subbed
                    if (subbed == null || subbed.isEmpty()) {
                        url = prefDomain + "/subbed-anime-list";
                    } else {
                        searchAdapter.rebaseLegacyData(subbed);
                        toRun = false;
                    }
                }
                retTab = tab.getPosition();
                if (toRun) {
                    searchAdapter.searchState(0);
                    searchThread = new ConnectedSearchThread(getActivity(), searchAdapter, url);
                    searchThread.start();
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) { //ensure that prev results are stored to reduce later loads
                ArrayList<SeriesSearchable> dataImport = searchAdapter.getLegacyData();
                if (dataImport.isEmpty()) return; //don't save if contents don't exist
                if (tab.getPosition() == 0 ) { //dubbed
                    if ((dubbed == null || dubbed.isEmpty()) && !searchAdapter.getThreadActive()) {
                        dubbed = new ArrayList<SeriesSearchable>(searchAdapter.getLegacyData());
                    }
                } else if (tab.getPosition() == 1) { //cartoons
                    if ((cartoon == null || cartoon.isEmpty()) && !searchAdapter.getThreadActive()) {
                        cartoon = new ArrayList<SeriesSearchable>(searchAdapter.getLegacyData());
                    }
                } else { //subbed
                    if ((subbed == null || subbed.isEmpty()) && !searchAdapter.getThreadActive()) {
                        subbed = new ArrayList<SeriesSearchable>(searchAdapter.getLegacyData());
                    }
                }
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) { }
        });

        //back nav override
        OnBackPressedCallback callback = new OnBackPressedCallback(true /* enabled by default */) {
            @Override
            public void handleOnBackPressed() {
                backNav = true;
                NavHostFragment.findNavController(MediaSearch.this).popBackStack();
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), callback);

        return binding.getRoot();
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.searchRetry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchAdapter.searchState(0);
                searchThread = new ConnectedSearchThread(getActivity(), searchAdapter, url);
                searchThread.start();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!binding.searchbar.getText().toString().isEmpty()) binding.resultIndicator.setVisibility(View.GONE);
    }

    @Override
    public void onDestroyView() {
        SearchCache cache = ((MainActivity)getActivity()).getSearchCache();
        NavController navController = Navigation.findNavController(this.getActivity(), R.id.nav_host_fragment_content_main);
        if (navController.getCurrentDestination().getId() == R.id.homeScreen) {
            cache.clear();
        } else {
            cache.updateCache(searchAdapter.getLegacyData(), retTab);
        }
        super.onDestroyView();
        binding = null;
    }

    private int readCache() {
        SearchCache cache = ((MainActivity)getActivity()).getSearchCache();
        if (cache.hasReturnTab()) {
            int rTab = cache.getReturnTab();
            switch (rTab) {
                case 0:
                    dubbed = cache.getCache();
                    break;
                case 1:
                    cartoon = cache.getCache();
                    break;
                case 2:
                    subbed = cache.getCache();
                    break;
            }
            return rTab;
        }
        return -1; //default to dubbed if no return Tab defined in cache
    }

    private ArrayList<SeriesSearchable> searchCatByIdx(int idx) {
        switch (idx){
            case 0:
                return dubbed;
            case 1:
                return cartoon;
            case 2:
                return subbed;
        }
        return new ArrayList<>();
    }

}