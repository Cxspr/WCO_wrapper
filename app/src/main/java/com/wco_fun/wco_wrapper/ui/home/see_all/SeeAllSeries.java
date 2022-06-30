package com.wco_fun.wco_wrapper.ui.home.see_all;

import android.app.Activity;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.wco_fun.wco_wrapper.R;
import com.wco_fun.wco_wrapper.databinding.FragmentSeriesSeeAllBinding;
import com.wco_fun.wco_wrapper.MainActivity;
import com.wco_fun.wco_wrapper.ui.home.watchgroups.WatchgroupAdapter;

public class SeeAllSeries extends Fragment {


    public SeeAllSeries() {
        // Required empty public constructor
    }

    private FragmentSeriesSeeAllBinding binding;
    private RecyclerView recycler;
    private TextView title;

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

        String titleStr = getArguments().getString("title");

        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((Activity) getContext())
                .getWindowManager()
                .getDefaultDisplay()
                .getMetrics(displayMetrics);

        double displayHeightDP = displayMetrics.heightPixels / displayMetrics.density; //get height, convert to dp
        final double uiScalar = displayHeightDP / 800; //UI was built on a simulated display with ~800dp height

        title = binding.textView;
        title.setText(titleStr);
        title.post(new Runnable() {
            @Override
            public void run() {
                title.setTextSize(0, (float) (title.getTextSize() * uiScalar));
            }
        });

        int variant = getArguments().getInt("variant");

        recycler = binding.seeAllRecycler;
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(this.getContext(), 3);
//        RecyclerView.LayoutManager layoutManager = new GridAutofitLayoutManager(this.getContext(), 200);
        recycler.setLayoutManager(layoutManager);
        if (variant <= 3){
            WatchgroupAdapter adapter = new WatchgroupAdapter(((MainActivity)getActivity()).getSeeAllCache(), displayMetrics);
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