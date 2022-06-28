package com.wco_fun.wco_wrapper.ui.genres;

import android.os.Bundle;
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
    private TextView textView;

    public GenreSelection() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentGenreSelectionBinding.inflate(inflater, container, false);
        // Inflate the layout for this fragment

        textView = binding.textView;
        textView.setText("Genres");
        textView.setTextSize(32);

        genreList = ((MainActivity)getActivity()).getGenreList();
        recyclerView = binding.genreSelectionRecycler;
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(
                recyclerView.getContext(), DividerItemDecoration.VERTICAL);
        recyclerView.addItemDecoration(dividerItemDecoration);
        recyclerView.setAdapter(new GenreSelectionAdapter(genreList));

        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}