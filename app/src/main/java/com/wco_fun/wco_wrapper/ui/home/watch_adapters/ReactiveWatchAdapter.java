package com.wco_fun.wco_wrapper.ui.home.watch_adapters;

import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.browser.customtabs.CustomTabsIntent;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.wco_fun.wco_wrapper.R;
import com.wco_fun.wco_wrapper.classes.episode.Episode;
import com.wco_fun.wco_wrapper.classes.series.SeriesControllable;
import com.wco_fun.wco_wrapper.classes.user_data.WatchData;

import java.util.ArrayList;

public class ReactiveWatchAdapter extends  RecyclerView.Adapter<ReactiveWatchAdapter.ViewHolder> {
    //TODO implement a limiter for shown series (particularly for the continue watching category
    private ArrayList<SeriesControllable> seriesList;
    private WatchData watchData;

    public ReactiveWatchAdapter(WatchData watchData) {
        this.watchData = watchData;
        this.seriesList = watchData.getWatching();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private ReactiveWatchAdapter host;
        private WatchData watchData;
        private ImageView imageView;
        private ImageButton delete,resume,next;
        private SeriesControllable series;

        public void setHost(ReactiveWatchAdapter host) {this.host = host;}
        public void setSeriesImage() {
            series.getSeriesImage(imageView);
//            series.fitSeriesImage(imageView);
        }
        public void setWatchData(WatchData watchData) {this.watchData = watchData;}
        public void setSeries(SeriesControllable series) {
            this.series = series;
            this.setSeriesImage();
            if (series.hasMoreEps()){
                next.setEnabled(true);
                next.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Episode refEp = series.popEpQueue();//pull next ep and trigger updater

                        CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
                        CustomTabsIntent customTabsIntent = builder.build();
                        customTabsIntent.launchUrl(view.getContext(), Uri.parse(refEp.getSrc()));

                        watchData.update(series);//notify host of changes
                        watchData.updateWatchDataJson();
                    }
                });
            } else {
                next.setEnabled(false);
            }
        }

//        public void setWatchlist(Watchlist_LE wl){ this.watchlistLE = wl; }

        public ViewHolder(View view) {
            super(view);
            //define on click listener
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Bundle bundle = new Bundle();
                    bundle.putString("link", series.getSrc());
                    bundle.putString("title", series.getTitle());
                    Navigation.findNavController(view)
                            .navigate(R.id.action_homeScreen_to_episode_select, bundle);
                }
            });

            view.findViewById(R.id.series_card_title).setVisibility(View.GONE);;
            imageView = (ImageView) view.findViewById(R.id.series_card_img);
            delete = (ImageButton) view.findViewById(R.id.series_card_remove);
            delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    watchData.remove(series.getTitle());
                    watchData.updateWatchDataJson();
                    host.refreshRecycler();
                }
            });
            resume = (ImageButton) view.findViewById(R.id.series_card_play);
            resume.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    series.updateLastWatched();

                    CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
                    CustomTabsIntent customTabsIntent = builder.build();
                    customTabsIntent.launchUrl(view.getContext(), Uri.parse(series.getCurEp().getSrc()));

                    host.refreshRecycler();
                }
            });
            next = (ImageButton) view.findViewById(R.id.series_card_next);

            //visibility setters
            delete.setVisibility(View.VISIBLE);
            next.setVisibility(View.VISIBLE);
            resume.setVisibility(View.VISIBLE);
        }

    }

    @Override
    public ReactiveWatchAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.home_recycler_entry, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReactiveWatchAdapter.ViewHolder holder, int position) {
        if (!(seriesList.isEmpty())){
            holder.setSeries(seriesList.get(position));
            holder.setHost(this);
            holder.setWatchData(watchData);
        }
    }

    @Override
    public int getItemCount() {
        return (seriesList.isEmpty())
                ? 0
                : seriesList.size();
    }

    public void rebaseSeriesList(ArrayList<SeriesControllable> seriesList) {
        this.seriesList = seriesList;
        this.notifyDataSetChanged();
    }

    public void refreshRecycler() {
        this.seriesList = (watchData.isEmpty())
                ? new ArrayList<SeriesControllable>()
                : watchData.getWatching();
        this.notifyDataSetChanged();
    }
}
