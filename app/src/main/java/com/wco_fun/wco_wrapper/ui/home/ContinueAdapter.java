package com.wco_fun.wco_wrapper.ui.home;

import android.content.Context;
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

import com.squareup.picasso.Picasso;
import com.wco_fun.wco_wrapper.R;
import com.wco_fun.wco_wrapper.classes.Series_LE;
import com.wco_fun.wco_wrapper.classes.Watchlist_LE;
import com.wco_fun.wco_wrapper.ui.home.parallel_procs.ThreadedEpUpdater;

import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.ExecutionException;

public class ContinueAdapter extends  RecyclerView.Adapter<ContinueAdapter.ViewHolder> {
    //TODO implement a limiter for shown series (particularly for the continue watching category
    private ArrayList<Series_LE> seriesLEList;
    private Watchlist_LE watchlistLE;
    private Context parentContext;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private ContinueAdapter host;
        private Watchlist_LE watchlistLE;
        private ImageView imageView;
        private ImageButton delete,resume,next;
        private Series_LE seriesLE;

        public void setHost(ContinueAdapter host) {this.host = host;}
        public void setSeriesImage() {
            Picasso.get().load(seriesLE.getSeriesImgUrl()).into(imageView);
        }
        public void setSeries(Series_LE seriesLE) {
            this.seriesLE = seriesLE;
            this.setSeriesImage();
            if (seriesLE.hasNextEp()){
                next.setEnabled(true);
                next.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        ThreadedEpUpdater epUpdater = new ThreadedEpUpdater(seriesLE);
                        Thread updater = new Thread(epUpdater);
                        updater.start();

                        CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
                        CustomTabsIntent customTabsIntent = builder.build();
                        customTabsIntent.launchUrl(view.getContext(), Uri.parse(seriesLE.getNextEp()));

                        try {
                            updater.join();
                            Series_LE resSeriesLE = epUpdater.get();
                            if (resSeriesLE != null) {
                                seriesLE.overrideAll(resSeriesLE);
                                seriesLE.setLastWatched(new Date());
                                watchlistLE.updateEp(seriesLE);
                                host.refreshRecycler();
                            }
                        } catch (InterruptedException | ExecutionException e) {
                            e.printStackTrace();
                        }

                    }
                });
            } else {
                next.setEnabled(false);
            }
        }

        public void setWatchlist(Watchlist_LE wl){ this.watchlistLE = wl; }

        public ViewHolder(View view) {
            super(view);
            //define on click listener
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Bundle bundle = new Bundle();
                    bundle.putString("link", seriesLE.getSeriesSrc());
                    bundle.putString("title", seriesLE.getSeriesTitle());
                    Navigation.findNavController(view)
                            .navigate(R.id.action_homeScreen_to_episode_select, bundle);
                }
            });

            view.findViewById(R.id.wl_series_title).setVisibility(View.GONE);;
            imageView = (ImageView) view.findViewById(R.id.wl_series_Img);
            delete = (ImageButton) view.findViewById(R.id.continue_remove);
            delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    watchlistLE.removeEpInfoFromWatchlist(seriesLE);
                    host.refreshRecycler();
                }
            });
            resume = (ImageButton) view.findViewById(R.id.continue_play);
            resume.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    seriesLE.setLastWatched(new Date());
                    watchlistLE.updateEp(seriesLE);
                    CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
                    CustomTabsIntent customTabsIntent = builder.build();
                    customTabsIntent.launchUrl(view.getContext(), Uri.parse(seriesLE.getCurEp()));
                    host.refreshRecycler();
                }
            });
            next = (ImageButton) view.findViewById(R.id.continue_next);

            //visibility setters
            delete.setVisibility(View.VISIBLE);
            next.setVisibility(View.VISIBLE);
            resume.setVisibility(View.VISIBLE);
        }

    }

    public ContinueAdapter(Watchlist_LE watchlistLE) {
        if (watchlistLE == null) return;
        this.watchlistLE = watchlistLE;
        this.seriesLEList = watchlistLE.getWatching();
    }

    @Override
    public ContinueAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.home_recycler_entry, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ContinueAdapter.ViewHolder holder, int position) {
        if (!(seriesLEList.isEmpty())){
//            holder.getTextView().setText(watchlist.get(position).getSeriesTitle());
            holder.setSeries(seriesLEList.get(position));
            holder.setWatchlist(watchlistLE);
            holder.setHost(this);
        }

    }

    @Override
    public int getItemCount() {
        return (watchlistLE == null)
                ? 0
                : seriesLEList.size();
    }

    public void rebaseWatchlist(ArrayList<Series_LE> watchlist) {
        this.seriesLEList = watchlist;
        this.notifyDataSetChanged();
    }

    public void refreshRecycler() {
        this.seriesLEList = (watchlistLE == null)
                ? new ArrayList<Series_LE>()
                : watchlistLE.getWatching();
        this.notifyDataSetChanged();
    }



}
