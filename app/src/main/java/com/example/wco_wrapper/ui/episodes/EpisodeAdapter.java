package com.example.wco_wrapper.ui.episodes;

import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.browser.customtabs.CustomTabsIntent;
import androidx.recyclerview.widget.RecyclerView;

import com.example.wco_wrapper.R;
import com.example.wco_wrapper.classes.Episode;
import com.example.wco_wrapper.classes.Series;

import java.util.ArrayList;

public class EpisodeAdapter extends RecyclerView.Adapter<EpisodeAdapter.ViewHolder> {

    private ArrayList<Episode> episodes = new ArrayList<Episode>();
    private Series hostSeries;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView textView;
        private String url, nextUrl;
        private String abrTitle, nextAbrTitle;
        private Series hostSeries;
        private int epIdx;


        public void setUrl(String s){
            url = s;
        }
        public void setNextUrl(String s) {nextUrl = s;}
        public void setAbrTitle(String s){
            abrTitle = s;
        }
        public void setNextAbrTitle(String s) {nextAbrTitle = s;}
        public void setEpIdx(int i) {epIdx = i;}
        public void setHostSeries(Series s){
            hostSeries = s;
        }

        public ViewHolder(View view) {
            super(view);

            //define on click listener
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //TODO implement custom tab buttons to allow quick traversal between episodes
                    hostSeries.setCurEp(url);
                    hostSeries.setNextEp(nextUrl);
                    hostSeries.setEpIdx(epIdx);
                    hostSeries.setAbrEpTitle(abrTitle);
                    hostSeries.setNextAbrEpTitle(nextAbrTitle);
                    CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
                    CustomTabsIntent customTabsIntent = builder.build();
                    customTabsIntent.launchUrl(view.getContext(), Uri.parse(url));
                }
            });

            textView = (TextView) view.findViewById(R.id.wl_series_title);
        }

        public TextView getTextView() {
            return textView;
        }
    }

    public EpisodeAdapter(ArrayList<Episode> eps, Series hostSeries){
        episodes = eps;
        this.hostSeries = hostSeries;
    }

    @Override
    public EpisodeAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.text_row_item, parent, false);
        return new EpisodeAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EpisodeAdapter.ViewHolder holder, int position) {
        holder.getTextView().setText(episodes.get(position).getTitle().substring(6));

        holder.setHostSeries(hostSeries);
        holder.setUrl(episodes.get(position).getSrc());
        holder.setAbrTitle(episodes.get(position).getAbrTitle());
        holder.setEpIdx(position);

        if ((position + 1) >= episodes.size() - 1) {
            holder.setNextUrl(null);
            holder.setNextAbrTitle(null);
        }
        else {
            holder.setNextUrl(episodes.get(position + 1).getSrc());
            holder.setNextAbrTitle(episodes.get(position + 1).getAbrTitle());
        }

    }


    @Override
    public int getItemCount() {
        return episodes.size();
    }


}


