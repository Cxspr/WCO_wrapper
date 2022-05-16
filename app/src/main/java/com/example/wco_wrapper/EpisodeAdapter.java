package com.example.wco_wrapper;

import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.browser.customtabs.CustomTabsIntent;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class EpisodeAdapter extends RecyclerView.Adapter<EpisodeAdapter.ViewHolder> {

    private ArrayList<Episode> episodes = new ArrayList<Episode>();

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView textView;
        private String url;

        public void setUrl(String s){
            url = s;
        }

        public ViewHolder(View view) {
            super(view);
            //define on click listener
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //TODO implement custom tab buttons to allow quick traversal between episodes
                    //TODO implement tracker of last episode watched
                    CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
                    CustomTabsIntent customTabsIntent = builder.build();
                    customTabsIntent.launchUrl(view.getContext(), Uri.parse(url));
                }
            });

            textView = (TextView) view.findViewById(R.id.textView);
        }

        public TextView getTextView() {
            return textView;
        }
    }

    public EpisodeAdapter(ArrayList<Episode> eps){
        episodes = eps;
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
        holder.setUrl(episodes.get(position).getSrc());
    }


    @Override
    public int getItemCount() {
        return episodes.size();
    }


}


