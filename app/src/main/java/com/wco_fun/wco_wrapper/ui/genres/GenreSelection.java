package com.wco_fun.wco_wrapper.ui.genres;

import android.app.Activity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.wco_fun.wco_wrapper.MainActivity;
import com.wco_fun.wco_wrapper.R;
import com.wco_fun.wco_wrapper.databinding.FragmentGenreSelectionBinding;
import com.wco_fun.wco_wrapper.initialization.GenreList;


public class GenreSelection extends Fragment {

    private FragmentGenreSelectionBinding binding;
    private GenreList genreList;
    private RecyclerView recyclerView;
    private TextView title;

    public GenreSelection() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentGenreSelectionBinding.inflate(inflater, container, false);
        // Inflate the layout for this fragment


        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((Activity) getContext())
                .getWindowManager()
                .getDefaultDisplay()
                .getMetrics(displayMetrics);

        double displayHeightDP = displayMetrics.heightPixels / displayMetrics.density; //get height, convert to dp
        final double uiScalar = displayHeightDP / 800; //UI was built on a simulated display with ~800dp height


        title = binding.textView;
        title.setText("Genres");
        title.post(new Runnable() {
            @Override
            public void run() {
                title.setTextSize(0, (float) (title.getTextSize() * uiScalar));
            }
        });

        genreList = ((MainActivity)getActivity()).getGenreList();
        recyclerView = binding.genreSelectionRecycler;
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(
                recyclerView.getContext(), DividerItemDecoration.VERTICAL);
        recyclerView.addItemDecoration(dividerItemDecoration);
        recyclerView.setAdapter(new GenreSelectionAdapter(genreList, displayMetrics));

        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}