package com.example.wco_wrapper;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.ViewHolder> {

    private ArrayList<Series> results = new ArrayList<Series>();
    private ArrayList<Series> legacyRes = new ArrayList<Series>();
    private NavController parent;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView textView;
        private Series series;
        private NavController host;
        public void setSeries(Series s) {
            series = s;
        }
        public void setHost(NavController h) {
            host = h;
        }
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
                            .navigate(R.id.action_mediaSearch_to_episode_select, bundle);
                }
            });

            textView = (TextView) view.findViewById(R.id.textView);
        }

        public TextView getTextView() {
            return textView;
        }
    }

    public SearchAdapter(ArrayList<Series> res) {
//        results = new ArrayList<Series>(res);
        legacyRes = new ArrayList<Series>(res);
    }

    @Override
    public SearchAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.text_row_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SearchAdapter.ViewHolder holder, int position) {
        holder.getTextView().setText(results.get(position).getTitle());
//        holder.setHost(parent);
        holder.setSeries(results.get(position));
    }

    @Override
    public int getItemCount() {
        return results.size();
    }

    public void reflectSearch(String str) {
        if (str.length() == 0) {
            revertToLegacy();
        } else {
            searchData(str);
        }
        this.notifyDataSetChanged();
    }

    public void revertToLegacy() {
        results.clear();
//        results.addAll(legacyRes);
    }

    public void searchData(String str) {
        if (results.isEmpty()){
            for (Series s: legacyRes){
                if (s.contains(str)) {
                    results.add(s);
                }
            }
        } else {
            for (int i = 0; i < results.size(); i++){
                if (!results.get(i).contains(str)) {
                    results.remove(i);
                    i--;
                }
            }
        }

    }

}
