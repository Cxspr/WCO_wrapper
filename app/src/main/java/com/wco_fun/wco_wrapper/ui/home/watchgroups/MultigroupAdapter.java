package com.wco_fun.wco_wrapper.ui.home.watchgroups;

import android.app.Activity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.wco_fun.wco_wrapper.MainActivity;
import com.wco_fun.wco_wrapper.R;
import com.wco_fun.wco_wrapper.ui.home.watchgroups.SeriesCard.SeriesCard;
import com.wco_fun.wco_wrapper.ui.home.watchgroups.SeriesGroup.SeriesGroup;

import java.util.ArrayList;

public class MultigroupAdapter extends RecyclerView.Adapter<MultigroupAdapter.MultigroupViewHolder>{
    private RecyclerView.RecycledViewPool viewPool = new RecyclerView.RecycledViewPool();
    private ArrayList<SeriesGroup> seriesGroups = new ArrayList<>();
    private MainActivity activity;
    private DisplayMetrics displayMetrics;
    private int heightPerContainer;
    final int spreadAcross = 3;

    public MultigroupAdapter(ArrayList<SeriesGroup> seriesGroups, MainActivity activity, DisplayMetrics displayMetrics){
        for (SeriesGroup s: seriesGroups){
            if (s.getVariant() < 2 && s.getContents().isEmpty()) continue;
            this.seriesGroups.add(s);
        }
        this.activity = activity;
        this.displayMetrics = displayMetrics;
    }

    @NonNull
    @Override
    public MultigroupAdapter.MultigroupViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.watchgroup_container, parent, false);
        return new MultigroupViewHolder(view, this);
    }

    @Override
    public void onBindViewHolder(@NonNull MultigroupAdapter.MultigroupViewHolder holder, int position) {
        if (seriesGroups.isEmpty()) return;
        SeriesGroup group = seriesGroups.get(position);
        holder.setSeriesGroup(group);

        LinearLayoutManager layoutManager = new LinearLayoutManager(holder.getRecycler().getContext(), LinearLayoutManager.HORIZONTAL, false);
        WatchgroupAdapter adapter = new WatchgroupAdapter(group.getContents(), displayMetrics);
        if (group.getVariant() >= 2) {
            group.attachAdapter(adapter);
        }
        holder.configRecycler(layoutManager, adapter, viewPool);
    }

    @Override
    public int getItemCount() {
        return seriesGroups.size();
    }

    class MultigroupViewHolder extends RecyclerView.ViewHolder {
        private TextView title;
        private RecyclerView seriesRecycler;
        private SeriesGroup seriesGroup;
        private TextView emptyInd;
        private ImageButton seeAll, refresh;
        private ProgressBar progBar;
        private MultigroupAdapter host;

        public MultigroupViewHolder(@NonNull View view, MultigroupAdapter host) {
            super(view);
            this.host = host;
            title = view.findViewById(R.id.watchgroup_title);
            seriesRecycler = view.findViewById(R.id.watchgroup_recycler);
            emptyInd = view.findViewById(R.id.watchgroup_empty_ind);
            refresh = view.findViewById(R.id.watchgroup_refresh);
            progBar = view.findViewById(R.id.watchgroup_prog);

            seeAll = view.findViewById(R.id.watchgroup_see_all);
            seeAll.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (seriesGroup.getContents().isEmpty()) return;
                    Bundle bundle = new Bundle();
                    bundle.putString("title", seriesGroup.getTitle());
                    bundle.putInt("variant", seriesGroup.getVariant());
                    host.pushToSeeAllCache(seriesGroup.getContents());//push to cache temporarily
                    Navigation.findNavController(view)
                            .navigate(R.id.action_homeScreen_to_seeAllSeries, bundle);
                }
            });

            double displayHeightDP = displayMetrics.heightPixels / displayMetrics.density; //get height, convert to dp
            final double uiScalar = displayHeightDP / 800; //UI was built on a simulated display with ~800dp height
            title.post(new Runnable() {
                @Override
                public void run() {
                    title.setTextSize(0, (float) (title.getTextSize() * uiScalar));
                    ViewGroup.LayoutParams params = seeAll.getLayoutParams();
                    params.height = (int) (params.height * uiScalar);
                    params.width = (int) (params.width * uiScalar);
                    seeAll.setLayoutParams(params);
                }
            });


        }

        public void setSeriesGroup(SeriesGroup group) {
            seriesGroup = group;
            title.setText(seriesGroup.getTitle());
            if (seriesGroup.getVariant() < 2 && seriesGroup.getContents().isEmpty()){
                emptyInd.setVisibility(View.VISIBLE);
            } else {
                emptyInd.setVisibility(View.GONE);
            }
        }

        public RecyclerView getRecycler() {return seriesRecycler;}
        public void configRecycler(LinearLayoutManager layoutManager, WatchgroupAdapter adapter, RecyclerView.RecycledViewPool viewPool){
            seriesRecycler.setLayoutManager(layoutManager);
            seriesRecycler.setRecycledViewPool(viewPool);
            if (seriesGroup.getVariant() >= 2){
                progBar.setVisibility(View.VISIBLE);
                adapter.attachEls(progBar, refresh, seriesGroup, host);
            }
            seriesRecycler.setAdapter(adapter);
        }

    }

    public void pushToSeeAllCache(ArrayList<SeriesCard> cache){
        activity.setSeeAllCache(cache);
    }

    public void removeGroup(SeriesGroup seriesGroup){
        this.removeGroup(seriesGroup.getTitle());
    }

    public void removeGroup(String title){
        for (SeriesGroup s : seriesGroups) {
            if (s.getTitle().matches(title)){
                seriesGroups.remove(s);
                this.notifyDataSetChanged();
                return;
            }
        }
    }
}

