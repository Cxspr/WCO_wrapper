package com.wco_fun.wco_wrapper.ui.home.watch_adapters;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.wco_fun.wco_wrapper.R;
import com.wco_fun.wco_wrapper.classes.series.Series;

import java.util.ArrayList;

public class WatchAdapter extends  RecyclerView.Adapter<WatchAdapter.ViewHolder> {
    //TODO implement a limiter for shown series (particularly for the continue watching category
    private ArrayList<Series> watchgroup;

    public WatchAdapter(ArrayList<Series> watchgroup) {
        this.watchgroup = watchgroup;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView textView;
        private ImageView imageView;
        private Series series;

        public void setSeriesImage() {
            series.getSeriesImage(imageView);
        }
        public void setSeries(Series series) {
            this.series = series;
            this.setSeriesImage();
        }

        public ViewHolder(View view) {
            super(view);
            //define on click listener
            view.findViewById(R.id.wg_button_bar).setVisibility(View.GONE);
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

            textView = (TextView) view.findViewById(R.id.series_card_title);
            imageView = (ImageView) view.findViewById(R.id.series_card_img);
        }

        public TextView getTextView() {
            return textView;
        }
    }

    @Override
    public WatchAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.home_recycler_entry, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull WatchAdapter.ViewHolder holder, int position) {
        if (!(watchgroup.isEmpty())){
            holder.getTextView().setText(watchgroup.get(position).getTitle());
            holder.setSeries(watchgroup.get(position));
        }

    }

    @Override
    public int getItemCount() {
        return (watchgroup.isEmpty())
                ? 0
                : watchgroup.size();
    }

    public void rebaseWatchgroup(ArrayList<Series> watchgroup) {
        this.watchgroup = watchgroup;
        this.notifyDataSetChanged();
    }

}
