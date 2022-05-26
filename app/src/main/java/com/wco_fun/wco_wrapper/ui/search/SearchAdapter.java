package com.wco_fun.wco_wrapper.ui.search;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.wco_fun.wco_wrapper.R;
import com.wco_fun.wco_wrapper.classes.series.SeriesSearchable;

import java.util.ArrayList;

public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.ViewHolder> {

    private ArrayList<SeriesSearchable> results = new ArrayList<SeriesSearchable>();
    private ArrayList<SeriesSearchable> legacyData = new ArrayList<SeriesSearchable>();
    private ProgressBar progressBar;


    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView textView;
        private SeriesSearchable series;
        private NavController host;
        public void setSeries(SeriesSearchable s) {
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

            textView = (TextView) view.findViewById(R.id.wl_series_title);
        }

        public TextView getTextView() {
            return textView;
        }
    }

    public SearchAdapter(ArrayList<SeriesSearchable> res) {
//        results = new ArrayList<Series>(res);
        legacyData = res;
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

    public void rebaseLegacyData(ArrayList<SeriesSearchable> newData) {
        legacyData = newData;
        results.clear();
        reflectSearch(localSearch);
        this.notifyDataSetChanged();
    }

    private int prevSearchLen = 0;
    private String localSearch = "";
    public void reflectSearch(String str) {
        if (str.length() == 0 ) {
            revertToLegacy();
        } else {
            searchData(str);
        }
        this.prevSearchLen = str.length();
        this.localSearch = str;
        this.notifyDataSetChanged();
    }

    public void revertToLegacy() {
        results.clear();
//        results.addAll(legacyRes);
    }

    public void searchData(String str) {
        if (results.isEmpty() || str.length() <= prevSearchLen){
            for (SeriesSearchable s: legacyData){
                if (s.contains(str) && !results.contains(s)) {
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

    public void onThreadConcluded(ArrayList<SeriesSearchable> retList) {
        this.rebaseLegacyData(retList);
        this.hideProgressSpinner();
        threadActive=false;
    }

    public void attachProgressSpinner(ProgressBar progressBar) {this.progressBar = progressBar; }
    public void hideProgressSpinner() {progressBar.setVisibility(View.GONE);}
    public void showProgressSpinner() {progressBar.setVisibility(View.VISIBLE);}

    public ArrayList<SeriesSearchable> getLegacyData() {
        return legacyData;
    }

    private boolean threadActive;
    public void setThreadActive() {
        threadActive = true;
        this.showProgressSpinner();
    }
    public boolean getThreadActive() {return threadActive;}


}
