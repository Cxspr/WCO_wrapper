package com.wco_fun.wco_wrapper.ui.home.see_all;

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
import com.wco_fun.wco_wrapper.classes.Series_LE;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class SeeAllAdapter extends  RecyclerView.Adapter<SeeAllAdapter.ViewHolder> {

    private ArrayList<Series_LE> watchlist;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView textView;
        private ImageView imageView;
        private Series_LE seriesLE;

        public void setSeriesImage() {
            Picasso.get().load(seriesLE.getSeriesImgUrl()).into(imageView);
        }
        public void setSeries(Series_LE seriesLE) {
            this.seriesLE = seriesLE;
            this.setSeriesImage();
        }

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
                            .navigate(R.id.action_seeAllSeries_to_episode_select, bundle);
                }
            });

            textView = (TextView) view.findViewById(R.id.wl_series_title);
            imageView = (ImageView) view.findViewById(R.id.wl_series_Img);
        }

        public TextView getTextView() {
            return textView;
        }
    }

    public SeeAllAdapter(ArrayList<Series_LE> watchlist) {
        this.watchlist = watchlist;
    }

    @Override
    public SeeAllAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.home_recycler_entry, parent, false);
        return new SeeAllAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SeeAllAdapter.ViewHolder holder, int position) {
        if (!(watchlist.isEmpty())){
            holder.getTextView().setText(watchlist.get(position).getSeriesTitle());
            holder.setSeries(watchlist.get(position));
//            holder.setSeriesImage();
        }

    }

    @Override
    public int getItemCount() {
        return (watchlist == null)
                ? 0
                : watchlist.size();
    }

    public void rebaseWatchlist(ArrayList<Series_LE> watchlist) {
        this.watchlist = watchlist;
        this.notifyDataSetChanged();
    }







}
