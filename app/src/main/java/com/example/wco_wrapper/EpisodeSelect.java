package com.example.wco_wrapper;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.wco_wrapper.databinding.FragmentEpisodeSelectBinding;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

public class EpisodeSelect extends Fragment {

    private FragmentEpisodeSelectBinding binding;
    private RecyclerView recyclerView;
    private TextView title;
    private Elements seriesHtmlData;

    private EpisodeAdapter epAdapter;
    private ArrayList<Episode> episodes = new ArrayList<Episode>();

    public EpisodeSelect() {}

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {

        binding = FragmentEpisodeSelectBinding.inflate(inflater, container, false);

        try {
            seriesHtmlData = (Jsoup.connect(getArguments().getString("link"))
                    .userAgent("Mozilla/5.0 (Windows; U; WindowsNT 5.1; en-US; rv1.8.1.6) Gecko/20070725 Firefox/2.0.0.6")
                    .get()).getElementsByClass("cat-eps");
            for (Element ep: seriesHtmlData) {
                ep = ep.child(0);
                Episode e = new Episode(ep);
                if (e.isValid()){
                    episodes.add(e);
                }
            }
            Collections.reverse(episodes);//reverse order to have first episode displayed first
            epAdapter = new EpisodeAdapter(episodes);
        } catch (IOException e) {
            e.printStackTrace();
        }

        title = binding.seriesTitle;
        title.setText(getArguments().getString("title"));

        recyclerView = binding.resultContainer;
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(epAdapter);

        return binding.getRoot();
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //TODO implement the ability to save series for quick access from home scree
        //TODO implement the ability to track the last episode watched
//        binding.buttonFirst.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                NavHostFragment.findNavController(MediaSelect.this)
//                        .navigate(R.id.action_FirstFragment_to_SecondFragment);
//            }
//        });

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}