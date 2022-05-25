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
import com.wco_fun.wco_wrapper.classes.Episode_LE;
import com.wco_fun.wco_wrapper.classes.Series_LE;

import java.util.ArrayList;
import java.util.Date;

public class EpisodeAdapter extends RecyclerView.Adapter<EpisodeAdapter.ViewHolder> {

    private ArrayList<Episode_LE> episodeLES = new ArrayList<Episode_LE>();
    private Series_LE hostSeriesLE;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView textView;
        private String url, nextUrl;
        private String abrTitle, nextAbrTitle;
        private Series_LE hostSeriesLE;
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
        public void setHostSeries(Series_LE s){
            hostSeriesLE = s;
        }

        public ViewHolder(View view) {
            super(view);

            //define on click listener
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    hostSeriesLE.setCurEp(url);
                    hostSeriesLE.setNextEp(nextUrl);
                    hostSeriesLE.setEpIdx(epIdx);
                    hostSeriesLE.setAbrEpTitle(abrTitle);
                    hostSeriesLE.setNextAbrEpTitle(nextAbrTitle);
                    hostSeriesLE.setLastWatched(new Date());

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

    public EpisodeAdapter(ArrayList<Episode_LE> eps, Series_LE hostSeriesLE){
        episodeLES = eps;
        this.hostSeriesLE = hostSeriesLE;
    }

    @Override
    public EpisodeAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.text_row_item, parent, false);
        return new EpisodeAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EpisodeAdapter.ViewHolder holder, int position) {
        holder.getTextView().setText(episodeLES.get(position).getTitle().substring(6));

        holder.setHostSeries(hostSeriesLE);
        holder.setUrl(episodeLES.get(position).getSrc());
        holder.setAbrTitle(episodeLES.get(position).getAbrTitle());
        holder.setEpIdx(position);

        if ((position + 1) >= episodeLES.size() - 1) {
            holder.setNextUrl(null);
            holder.setNextAbrTitle(null);
        }
        else {
            holder.setNextUrl(episodeLES.get(position + 1).getSrc());
            holder.setNextAbrTitle(episodeLES.get(position + 1).getAbrTitle());
        }

    }


    @Override
    public int getItemCount() {
        return episodeLES.size();
    }


}


