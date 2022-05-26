package com.wco_fun.wco_wrapper.ui.episodes;

import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.browser.customtabs.CustomTabsIntent;
import androidx.recyclerview.widget.RecyclerView;

import com.wco_fun.wco_wrapper.R;
import com.wco_fun.wco_wrapper.classes.episode.Episode;
import com.wco_fun.wco_wrapper.classes.series.SeriesControllable;
import com.wco_fun.wco_wrapper.classes.user_data.WatchData;

import java.util.ArrayList;

public class EpisodeAdapter extends RecyclerView.Adapter<EpisodeAdapter.ViewHolder> {
    private ArrayList<Episode> episodes = new ArrayList<Episode>();
    private SeriesControllable hostSeries;
    private WatchData watchData;

    public EpisodeAdapter(ArrayList<Episode> eps, SeriesControllable hostSeries, WatchData watchData){
        episodes = eps;
        this.hostSeries = hostSeries;
        this.watchData = watchData;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView textView;
        private SeriesControllable hostSeries;
        private WatchData watchData;
        private ArrayList<Episode> epQueue = new ArrayList<Episode>(3);

        public void setWatchData(WatchData wd) { watchData = wd; }
        public void setHostSeries(SeriesControllable s){
            hostSeries = s;
        }
        public void verifyArrayList() {
            for (int i = 0; i < epQueue.size(); i++){
                if (epQueue.get(i) == null) {
                    epQueue.remove(i);
                }
            }
        }

        private void epQueueApplicator(){
            hostSeries.overrideEpQueue(epQueue);
        }

        private void watchDataUpdater() {
            if (watchData.contains(hostSeries.getTitle())){
                watchData.update(hostSeries);
            } else {
                watchData.add(hostSeries);
            }
        }

        public ViewHolder(View view) {
            super(view);

            //define on click listener
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    epQueueApplicator();//update hostSeries episode Queue

                    CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
                    CustomTabsIntent customTabsIntent = builder.build();
                    customTabsIntent.launchUrl(view.getContext(), Uri.parse(epQueue.get(0).getSrc()));

                    watchDataUpdater();//push changes to watchData
                }
            });

            textView = (TextView) view.findViewById(R.id.wl_series_title);
        }

        public TextView getTextView() {
            return textView;
        }
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
        holder.setWatchData(watchData);
        for (int i = position; (i< position + 3); i++) {
            if (i >= episodes.size()) break;
            holder.epQueue.add(episodes.get(i));//preload with up to 3 episodes
        } holder.verifyArrayList();
    }


    @Override
    public int getItemCount() {
        return episodes.size();
    }


}


