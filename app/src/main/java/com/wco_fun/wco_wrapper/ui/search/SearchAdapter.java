package com.wco_fun.wco_wrapper.ui.search;

import static com.wco_fun.wco_wrapper.ui.search.ConnectedSearchThread.ERR_CODES.*;

import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.Group;
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
    private ImageButton retryBtn;
    private TextView errorText;
    private Group errorGroup;
    private DisplayMetrics displayMetrics;

    public SearchAdapter() {}

    public SearchAdapter(ViewGroup searchEl, DisplayMetrics displayMetrics){
        this.progressBar = searchEl.findViewById(R.id.search_prog);
        this.errorGroup = searchEl.findViewById(R.id.search_retry_group);
        this.retryBtn = searchEl.findViewById(R.id.search_retry);
        this.errorText = searchEl.findViewById(R.id.search_fail_response);
        this.displayMetrics = displayMetrics;
    }

    //TODO remove if deprecated
    public SearchAdapter(ArrayList<SeriesSearchable> res) {
        legacyData = res;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
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

            textView = (TextView) view.findViewById(R.id.series_card_title);
            textView.setVisibility(View.INVISIBLE);

            double displayHeightDP = displayMetrics.heightPixels / displayMetrics.density; //get height, convert to dp
            final double uiScalar = displayHeightDP / 800; //UI was built on a simulated display with ~800dp height

            textView.post(new Runnable() {
                @Override
                public void run() {
                    textView.setTextSize(0, (float) ( textView.getTextSize() * (uiScalar * 1.125) ));
                    textView.setVisibility(View.VISIBLE);
                }
            });
        }



        public TextView getTextView() {
            return textView;
        }
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
        results.addAll(legacyData);
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

    public ArrayList<SeriesSearchable> getLegacyData() {
        return legacyData;
    }

    public void showProgressSpinner(boolean show) {progressBar.setVisibility((show) ? View.VISIBLE : View.GONE);}

    public void showSearchError(boolean show) {errorGroup.setVisibility((show) ? View.VISIBLE : View.GONE);}

    public void searchState(int state) {
        switch (state) {
            case -1:
                showProgressSpinner(false);
                showSearchError(true);
                this.threadActive = false;
                return;
            case 0:
                showProgressSpinner(true);
                showSearchError(false);
                this.legacyData.clear();
                this.results.clear();
                this.threadActive = true;
                return;
            case 1:
                showProgressSpinner(false);
                showSearchError(false);
                this.threadActive = false;
                return;
        }
    }

    //handlers related to attachment, response, and communication with the connected search thread
    private boolean threadActive;
    public boolean getThreadActive() {return threadActive;}

    public void onThreadConcluded(ArrayList<SeriesSearchable> retList) {
        this.rebaseLegacyData(retList);
        this.searchState(1);
        threadActive=false;
    }

    public void onThreadErr(int errCode) {
        switch (errCode) {
            case INTERRUPT: //TODO: interrupt would be user-triggered. Is it really an error?
//                errorText.setText("Error while running search...");
                this.searchState(-1);
                break;
            case TIMEOUT:
                errorText.setText("Search request timed out...");
                this.searchState(-1);
                break;
            case IOEXCEPTION:
                errorText.setText("Error while running search...");
                this.searchState(-1);
                break;
        }
    }

}
