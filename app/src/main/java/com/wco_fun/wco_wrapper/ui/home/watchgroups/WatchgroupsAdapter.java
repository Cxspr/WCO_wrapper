package com.wco_fun.wco_wrapper.ui.home.watchgroups;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.wco_fun.wco_wrapper.R;

import java.util.ArrayList;

public class WatchgroupsAdapter extends RecyclerView.Adapter<WatchgroupsAdapter.ViewHolder> {
    //TODO implement a limiter for shown series (particularly for the continue watching category
    private ArrayList<WatchgroupData> watchgroupData;
//    private Context context;

    public static class WatchgroupData {
        public WatchgroupData(RecyclerView.Adapter adapter, String title) {
            this.adapter = adapter;
            this.title = title;
        }
        public RecyclerView.Adapter adapter;
        public String title;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        //UI stuff
        private TextView groupTitle;
        private TextView groupEmpty;
        private ImageButton groupSeeAllBtn;
        private RecyclerView groupRecycler;
        //Parameters
        private RecyclerView.Adapter adapter;
        private Context context;

        //Setter functions
        public void setAdapter(RecyclerView.Adapter adapter) {this.adapter = adapter; }

        public ViewHolder(View view) {
            super(view);
            this.context = view.getContext();
            //define on click listener
            groupTitle = (TextView) view.findViewById(R.id.watchgroup_title);
            groupSeeAllBtn = (ImageButton) view.findViewById(R.id.watchgroup_see_all);
            groupRecycler = (RecyclerView) view.findViewById(R.id.watchgroup_recycler);
            groupEmpty = (TextView) view.findViewById(R.id.watchgroup_empty_ind);
        }

        public void populateViewHolder() {
            RecyclerView.LayoutManager watchlistLayoutManager = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
            groupRecycler.setLayoutManager(watchlistLayoutManager);
            groupRecycler.setAdapter(adapter);
            if ((adapter.getItemCount() == 0)) {
                groupEmpty.setVisibility(View.VISIBLE);
            } else {
                groupEmpty.setVisibility(View.GONE);
            }
        }

        public void setGroupTitle(String title) {
            groupTitle.setText(title);
        }
    }

    public WatchgroupsAdapter(ArrayList<WatchgroupData> watchgroupData) {
//        this.context = context;
        this.watchgroupData = watchgroupData;
    }

    @Override
    public WatchgroupsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.home_display_catagory, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull WatchgroupsAdapter.ViewHolder holder, int position) {
        holder.setGroupTitle(watchgroupData.get(position).title);
        holder.setAdapter(watchgroupData.get(position).adapter);
        holder.populateViewHolder();
    }

    @Override
    public int getItemCount() {
        return 0;
    }

//    @Override
//    public int getItemCount() {
//        return (watchlist == null)
//                ? 0
//                : watchlist.size();
//    }
//
//    public void rebaseWatchlist(ArrayList<Series> watchlist) {
//        this.watchlist = watchlist;
//        this.notifyDataSetChanged();
//    }
//
}
