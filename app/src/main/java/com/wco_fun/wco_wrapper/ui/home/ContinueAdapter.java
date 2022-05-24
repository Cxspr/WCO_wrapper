package com.wco_fun.wco_wrapper.ui.home;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.browser.customtabs.CustomTabsIntent;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;
import com.wco_fun.wco_wrapper.R;
import com.wco_fun.wco_wrapper.classes.Series;
import com.wco_fun.wco_wrapper.classes.Watchlist;
import com.wco_fun.wco_wrapper.ui.home.parallel_procs.ThreadedEpUpdater;

import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.ExecutionException;

public class ContinueAdapter extends  RecyclerView.Adapter<ContinueAdapter.ViewHolder> {
    //TODO implement a limiter for shown series (particularly for the continue watching category
    private ArrayList<Series> seriesList;
    private Watchlist watchlist;
    private Context parentContext;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private ContinueAdapter host;
        private Watchlist watchlist;
        private ImageView imageView;
        private ImageButton delete,resume,next;
        private Series series;

        public void setHost(ContinueAdapter host) {this.host = host;}
        public void setSeriesImage() {
            Picasso.get().load(series.getSeriesImgUrl()).into(imageView);
        }
        public void setSeries(Series series) {
            this.series = series;
            this.setSeriesImage();
            if (series.hasNextEp()){
                next.setVisibility(View.VISIBLE);
                next.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        ThreadedEpUpdater epUpdater = new ThreadedEpUpdater(series);
                        Thread updater = new Thread(epUpdater);
                        updater.start();

                        CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
                        CustomTabsIntent customTabsIntent = builder.build();
                        customTabsIntent.launchUrl(view.getContext(), Uri.parse(series.getNextEp()));

                        try {
                            updater.join();
                            Series resSeries = epUpdater.get();
                            if (resSeries != null) {
                                series.overrideAll(resSeries);
                                series.setLastWatched(new Date());
                                watchlist.updateEp(series);
                                host.refreshRecycler();
                            }
                        } catch (InterruptedException | ExecutionException e) {
                            e.printStackTrace();
                        }

                    }
                });
            } else {
                next.setVisibility(View.GONE);
            }
        }
        public void setWatchlist(Watchlist wl){ this.watchlist = wl; }

        public ViewHolder(View view) {
            super(view);
            //define on click listener
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Bundle bundle = new Bundle();
                    bundle.putString("link", series.getSeriesSrc());
                    bundle.putString("title", series.getSeriesTitle());
                    Navigation.findNavController(view)
                            .navigate(R.id.action_homeScreen_to_episode_select, bundle);
                }
            });

//            textView = (TextView) view.findViewById(R.id.wl_series_title);
            imageView = (ImageView) view.findViewById(R.id.wl_series_Img);
            delete = (ImageButton) view.findViewById(R.id.continue_remove);
            delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    watchlist.removeEpInfoFromWatchlist(series);
                    host.refreshRecycler();
                }
            });
            resume = (ImageButton) view.findViewById(R.id.continue_play);
            resume.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    series.setLastWatched(new Date());
                    watchlist.updateEp(series);
                    CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
                    CustomTabsIntent customTabsIntent = builder.build();
                    customTabsIntent.launchUrl(view.getContext(), Uri.parse(series.getCurEp()));
                    host.refreshRecycler();
                }
            });
            next = (ImageButton) view.findViewById(R.id.continue_next);


        }

    }

    public ContinueAdapter(Watchlist watchlist) {
        if (watchlist == null) return;
        this.watchlist = watchlist;
        this.seriesList = watchlist.getWatching();
    }

    @Override
    public ContinueAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.watchlist_continue_entry, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ContinueAdapter.ViewHolder holder, int position) {
        if (!(seriesList.isEmpty())){
//            holder.getTextView().setText(watchlist.get(position).getSeriesTitle());
            holder.setSeries(seriesList.get(position));
            holder.setWatchlist(watchlist);
            holder.setHost(this);
        }

    }

    @Override
    public int getItemCount() {
        return (watchlist == null)
                ? 0
                : seriesList.size();
    }

    public void rebaseWatchlist(ArrayList<Series> watchlist) {
        this.seriesList = watchlist;
        this.notifyDataSetChanged();
    }

    public void refreshRecycler() {
        this.seriesList = (watchlist == null)
                ? new ArrayList<Series>()
                : watchlist.getWatching();
        this.notifyDataSetChanged();
    }



}
