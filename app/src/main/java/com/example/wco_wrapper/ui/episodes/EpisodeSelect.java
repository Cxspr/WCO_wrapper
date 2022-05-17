package com.example.wco_wrapper.ui.episodes;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.wco_wrapper.classes.Episode;
import com.example.wco_wrapper.classes.Series;
import com.example.wco_wrapper.databinding.FragmentEpisodeSelectBinding;
import com.example.wco_wrapper.ui.episodes.EpisodeAdapter;
import com.squareup.picasso.Picasso;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;

public class EpisodeSelect extends Fragment {

    private FragmentEpisodeSelectBinding binding;
    private RecyclerView recyclerView;
    private TextView title;
    private ImageView seriesImage;
    private Elements seriesHtmlData;
    private Series series;
    private EpisodeAdapter epAdapter;
    private ArrayList<Episode> episodes = new ArrayList<Episode>();

    public EpisodeSelect() {}
    //TODO integrate series class fully
    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {

        binding = FragmentEpisodeSelectBinding.inflate(inflater, container, false);
        Document Html;
        try {
            String src = getArguments().getString("link");
            Html = Jsoup.connect(src)
                    .userAgent("Mozilla/5.0 (Windows; U; WindowsNT 5.1; en-US; rv1.8.1.6) Gecko/20070725 Firefox/2.0.0.6")
                    .get();
            Elements imageEl = Html.getElementsByClass("img5");
            series = new Series(src, getArguments().getString("title"), "https:" + imageEl.get(0).attr("src"));
//            seriesImgUrl = ;
            seriesImage = binding.seriesImage;
//            Picasso.with(getContext()).load(seriesImgUrl).into(seriesImage);
            series.getSeriesImage(getContext(),seriesImage);
            seriesHtmlData = Html.getElementsByClass("cat-eps");
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
//        title.setText(getArguments().getString("title"));
        title.setText(series.getSeriesTitle());
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

    //https://stackoverflow.com/questions/6407324/how-to-display-image-from-url-on-android
    public static Drawable LoadImageFromWebOperations(String url) {
        try {
            InputStream is = (InputStream) new URL(url).getContent();
            Drawable d = Drawable.createFromStream(is, "src name");
            return d;
        } catch (Exception e) {
            return null;
        }
    }
}