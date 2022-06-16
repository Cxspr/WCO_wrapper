package com.wco_fun.wco_wrapper.ui.home.watchgroups;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.wco_fun.wco_wrapper.R;
import com.wco_fun.wco_wrapper.classes.series.Series;
import com.wco_fun.wco_wrapper.classes.series.SeriesControllable;
import com.wco_fun.wco_wrapper.classes.user_data.WatchData;
import com.wco_fun.wco_wrapper.ui.home.watchgroups.SeriesCard.SeriesCard;
import com.wco_fun.wco_wrapper.ui.home.watchgroups.SeriesCard.SeriesCardGeneric;
import com.wco_fun.wco_wrapper.ui.home.watchgroups.SeriesGroup.ReflectiveGroup;
import com.wco_fun.wco_wrapper.ui.home.watchgroups.SeriesGroup.SeriesGroup;

import java.util.ArrayList;

public class WatchgroupAdapter extends RecyclerView.Adapter<WatchgroupAdapter.WatchgroupViewHolder>{

    private ArrayList<SeriesCard> seriesData;
    private ProgressBar progBar;
    private ImageButton refresh;
    private SeriesGroup seriesGroup;
    private MultigroupAdapter host;

    public WatchgroupAdapter () { this.seriesData = new ArrayList<>(); }
    public WatchgroupAdapter (ArrayList<SeriesCard> seriesData){
        this.seriesData = seriesData;
    }

    public void attachEls(ProgressBar progBar, ImageButton refresh, SeriesGroup seriesGroup, MultigroupAdapter host){
        this.progBar = progBar;
        this.refresh = refresh;
        this.seriesGroup = seriesGroup;
        this.host = host;
        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                seriesGroup.variantOperation();//for variations that load from HTML scrape, this acts as an
                //access function to restart the threaded process which is already attached to this adapter
                progBar.setVisibility(View.VISIBLE);
                refresh.setVisibility(View.GONE);
            }
        });
    }

    @NonNull
    @Override
    public WatchgroupAdapter.WatchgroupViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.home_recycler_entry, parent, false);
        return new WatchgroupViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull WatchgroupAdapter.WatchgroupViewHolder holder, int position) {
        if (seriesData.isEmpty()) return;
        SeriesCard seriesCard = seriesData.get(position);
        seriesCard.bindAdapter(this);
        holder.bindViews(seriesCard);
        seriesCard.setSeriesImage();
        seriesCard.configureVariant();
        seriesCard.configureClickEvent();
        holder.setSeriesCard(seriesCard);
    }

    @Override
    public int getItemCount() {
        if (seriesData == null) return 0;
        return seriesData.size();
    }

    public class WatchgroupViewHolder extends RecyclerView.ViewHolder{
        private SeriesCard seriesCard;
        private ViewGroup viewParent;

        public WatchgroupViewHolder(@NonNull View view) {
            super(view);
            viewParent = view.findViewById(R.id.series_card_container);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Navigation.findNavController(view)
                            .navigate(R.id.episode_select, seriesCard.configureClickEvent());
                }
            });
        }

        public void bindViews(SeriesCard s) {
            s.attachViews(viewParent);
        }

        public void setSeriesCard(SeriesCard s){
            this.seriesCard = s;
        }
    }

    public void refreshRecycler(SeriesCard c) {
        seriesData.remove(c);
        if (seriesData.isEmpty()) { //recycler is empty, remove from home screen
            host.removeGroup(seriesGroup.getTitle());
            return;
        }
        this.notifyDataSetChanged();
    }

    public void refreshRecycler(WatchData watchData){
        SeriesGroup override = new ReflectiveGroup(watchData);
        this.seriesGroup = override;
        this.seriesData = seriesGroup.getContents();
        this.notifyDataSetChanged();
    }

    public void onThreadConcluded(ArrayList<Series> retList) {
        progBar.setVisibility(View.GONE);
        ArrayList<SeriesCard> sCards = new ArrayList<>();
        for (Series s : retList) {
            sCards.add(new SeriesCardGeneric(s));
        }
        seriesData = sCards;
        if (seriesData.isEmpty()) {
            host.removeGroup(seriesGroup);
            return;
        }
        this.seriesGroup.setContents(seriesData);
        this.notifyDataSetChanged();
    }

    public void onThreadErr(){
        progBar.setVisibility(View.GONE);
        refresh.setVisibility(View.VISIBLE);
    }
}
