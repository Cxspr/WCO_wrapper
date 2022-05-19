//package com.example.wco_wrapper.ui.home;
//
//import android.content.Context;
//import android.os.Bundle;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.ImageView;
//import android.widget.TextView;
//
//import androidx.annotation.NonNull;
//import androidx.navigation.Navigation;
//import androidx.recyclerview.widget.RecyclerView;
//
//import com.example.wco_wrapper.R;
//import com.example.wco_wrapper.classes.Series;
//import com.squareup.picasso.Picasso;
//
//import java.util.ArrayList;
//
//public class SeeAllAdapter extends  RecyclerView.Adapter<com.example.wco_wrapper.ui.home.SeeAllAdapter.ViewHolder> {
//
//        private ArrayList<Series> watchlist;
//        private Context parentContext;
//
//        public static class ViewHolder extends RecyclerView.ViewHolder {
//            private final TextView textView;
//            private ImageView imageView;
//            private Series series;
//
//            public void setSeriesImage() {
//                Picasso.get().load(series.getSeriesImgUrl()).into(imageView);
//            }
//            public void setSeries(Series series) {
//                this.series = series;
//                this.setSeriesImage();
//            }
//
//            public ViewHolder(View view) {
//                super(view);
//                //define on click listener
//                view.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View view) {
//                        Bundle bundle = new Bundle();
//                        bundle.putString("link", series.getSeriesSrc());
//                        bundle.putString("title", series.getSeriesTitle());
//                        Navigation.findNavController(view)
//                                .navigate(R.id.action_homeScreen_to_episode_select, bundle);
//                    }
//                });
//
//                textView = (TextView) view.findViewById(R.id.wl_series_title);
//                imageView = (ImageView) view.findViewById(R.id.wl_series_Img);
//            }
//
//            public TextView getTextView() {
//                return textView;
//            }
//        }
//
//        public WatchlistAdapter(ArrayList<Series> watchlist) {
//            this.watchlist = watchlist;
//        }
//
//        @Override
//        public com.example.wco_wrapper.ui.home.WatchlistAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
//            View view = LayoutInflater.from(parent.getContext())
//                    .inflate(R.layout.watchlist_entry, parent, false);
//            return new com.example.wco_wrapper.ui.home.WatchlistAdapter.ViewHolder(view);
//        }
//
//        @Override
//        public void onBindViewHolder(@NonNull com.example.wco_wrapper.ui.home.WatchlistAdapter.ViewHolder holder, int position) {
//            if (!(watchlist.isEmpty())){
//                holder.getTextView().setText(watchlist.get(position).getSeriesTitle());
//                holder.setSeries(watchlist.get(position));
////            holder.setSeriesImage();
//            }
//
//        }
//
//        @Override
//        public int getItemCount() {
//            return (watchlist == null)
//                    ? 0
//                    : watchlist.size();
//        }
//
//        public void rebaseWatchlist(ArrayList<Series> watchlist) {
//            this.watchlist = watchlist;
//            this.notifyDataSetChanged();
//        }
//
//
//
//
//
//}
