package com.wco_fun.wco_wrapper.ui.episodes;

import android.content.Context;
import android.net.Uri;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.browser.customtabs.CustomTabsIntent;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.wco_fun.wco_wrapper.MainActivity;
import com.wco_fun.wco_wrapper.R;
import com.wco_fun.wco_wrapper.classes.episode.Episode;
import com.wco_fun.wco_wrapper.classes.series.Series;
import com.wco_fun.wco_wrapper.classes.series.SeriesControllable;
import com.wco_fun.wco_wrapper.classes.user_data.WatchData;
import com.wco_fun.wco_wrapper.classes.user_data.Watchlist;

import java.util.ArrayList;

public class EpisodeAdapter extends RecyclerView.Adapter<EpisodeAdapter.ViewHolder> {
    private ArrayList<Episode> episodes = new ArrayList<Episode>();
    private SeriesControllable hostSeries;
    private ProgressBar progressBar;
    private ImageButton retryBtn;
    private ImageView seriesImg;
    private WatchData watchData;
    private Watchlist watchlist;
    private MainActivity mainActivity;
    private DisplayMetrics displayMetrics;

    public EpisodeAdapter(){}
    public EpisodeAdapter(SeriesControllable hostSeries, WatchData watchData, MainActivity mainActivity, DisplayMetrics displayMetrics){
        this.hostSeries = hostSeries;
        this.watchData = watchData;
        this.mainActivity = mainActivity;
        this.displayMetrics = displayMetrics;
    }

    public EpisodeAdapter(ArrayList<Episode> eps, SeriesControllable hostSeries, WatchData watchData){
        episodes = eps;
        this.hostSeries = hostSeries;
        this.watchData = watchData;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView textView;
        private SeriesControllable hostSeries;
        private WatchData watchData;
        private ArrayList<Episode> epQueue = new ArrayList<Episode>(3);

        public void setWatchData(WatchData wd) { watchData = wd; }
        public void setHostSeries(SeriesControllable s){
            hostSeries = s;
        }
        public void setEpQueue(ArrayList<Episode> epQueue){
            this.epQueue = epQueue;
        }

        private void epQueueApplicator(){
            hostSeries.overrideEpQueue(epQueue);
        }

        private void watchDataUpdater() {
            if (watchData.contains(hostSeries.getTitle())){
                watchData.update(hostSeries);
            } else {
                watchData.add(hostSeries);
            }
        }

        public ViewHolder(View view) {
            super(view);

            //define on click listener
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    epQueueApplicator();//update hostSeries episode Queue

                    CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
                    CustomTabsIntent customTabsIntent = builder.build();
                    customTabsIntent.launchUrl(view.getContext(), Uri.parse(epQueue.get(0).getSrc()));

                    watchDataUpdater();//push changes to watchData
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
    public EpisodeAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.text_row_item, parent, false);
        return new EpisodeAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EpisodeAdapter.ViewHolder holder, int position) {
        if (!episodes.isEmpty()) holder.getTextView().setText(episodes.get(position).getTitle());
        holder.setHostSeries(hostSeries);
        holder.setWatchData(watchData);
        ArrayList<Episode> epQueue = new ArrayList<>();
        for (int i = position; (i< position + 3); i++) {
            if (i >= episodes.size()) break;
            epQueue.add(episodes.get(i));//preload with up to 3 episodes
        }
        holder.setEpQueue(epQueue);
    }

    @Override
    public int getItemCount() {
        return episodes.size();
    }

    public void attachWatchlist(Watchlist watchlist) { this.watchlist = watchlist; }
    public void attachSeriesImg(ImageView s) {this.seriesImg = s;}
    public void attachProgBar(ProgressBar progressBar){ this.progressBar = progressBar;}
    public void attachRetryBtn(ImageButton imageButton) {this.retryBtn = imageButton;}
    public void showProgBar(boolean show) {progressBar.setVisibility((show) ? View.VISIBLE : View.GONE);}
    public void showRetryBtn(boolean show) {retryBtn.setVisibility((show) ? View.VISIBLE : View.GONE);}

    private boolean threadActive = false;
    public void setThreadActive() {this.threadActive = true;}
    public boolean isThreadActive() {return threadActive;}

    public void searchState(int state) {
        switch (state) {
            case -1:
                showProgBar(false);
                showRetryBtn(true);
                this.threadActive = false;
                return;
            case 0:
                showProgBar(true);
                showRetryBtn(false);
                this.threadActive = true;
                return;
            case 1:
                showProgBar(false);
                showRetryBtn(false);
                this.threadActive = false;
                return;
        }
    }

    public void onThreadConcluded(ArrayList<Episode> retList){
        this.episodes = retList;
        this.searchState(1);
        this.notifyDataSetChanged();
        fulfillBtnRqsts();
    }

    public void onThreadErr(int errCode) {
        searchState(-1);
    }

    boolean newEps = false;
    public void onThreadMilestone(int numEps, String imgUrl){
        hostSeries.setImgUrl(imgUrl);
        hostSeries.setNumEps(numEps);
        Series refSeries = watchlist.get(hostSeries);
        if (refSeries != null) {
            if (hostSeries.getNumEps() > refSeries.getNumEps()){
                mainActivity.epGroupUpToDate = false;
                refSeries.setNumEps(hostSeries.getNumEps());
                watchlist.pushChanges();
                this.newEps = true;
            }
        }

        hostSeries.fitSeriesImage2Width(seriesImg);
    }

    ImageButton playBtn, nextBtn;
    TextView nextTxt;
    Context context;
    boolean playRqst, nextRqst;
    private void fulfillBtnRqsts() {
        if (playRqst) {
            playBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String url;
                    hostSeries.updateLastWatched();
                    if (hostSeries.hasEp()) {
                        url = hostSeries.getCurEp().getSrc();
                    } else { //update current episode and next to 1 and 2 respectively
                        url = episodes.get(0).getSrc();
                        for (int i = 0; (i < 3) || (i >= episodes.size()); i++) {
                            hostSeries.addEp(episodes.get(i));//preload with up to 3 episodes
                        }
                    }

                    CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
                    CustomTabsIntent customTabsIntent = builder.build();
                    customTabsIntent.launchUrl(view.getContext(), Uri.parse(url));
                }
            });
            playRqst = false;
        }
        if (nextRqst && hostSeries.hasMoreEps()){
            final TypedValue value = new TypedValue ();
            context.getTheme().resolveAttribute(android.R.attr.colorAccent, value, true);
            nextBtn.setColorFilter(value.data);
            nextBtn.setEnabled(true);
            nextTxt.setText("Next Ep: " + ((hostSeries.getNextEp().getAbrTitle(hostSeries.getTitle())==null)
                    ? "Ep. " + ((Integer) (hostSeries.getNextEp().getIdx() + 2)).toString()
                    : hostSeries.getNextEp().getAbrTitle(hostSeries.getTitle())));
            nextBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    hostSeries.updateLastWatched();
                    String url = hostSeries.getNextEp().getSrc(); //update episodes

                    ArrayList<Episode> epQueue = new ArrayList<>(hostSeries.getEpQueue());
                    epQueue.remove(0);
                    int preloadLimit = hostSeries.getPreloadLimit();
                    for  (int i = epQueue.get(epQueue.size() - 1).getIdx() + 1;
                         (epQueue.size() <= preloadLimit) && (i < episodes.size());
                         i++) {
                        epQueue.add(episodes.get(i));//preload with episodes
                    } hostSeries.overrideEpQueue(epQueue);

                    //sync with watchdata
                    watchData.update(hostSeries);
                    watchData.updateWatchDataJson();

                    CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
                    CustomTabsIntent customTabsIntent = builder.build();
                    customTabsIntent.launchUrl(view.getContext(), Uri.parse(url));
                }
            });
            nextRqst = false;
        } else {
            nextBtn.setColorFilter(context.getColor(R.color.dark_grey));
            nextBtn.setEnabled(false);
            nextTxt.setText("Next Ep: none");
        }
//        if (nextRqst && hostSeries.hasMoreEps()) {
//
//            nextRqst = false;
//        } else {
//            nextBtn.setColorFilter(getContext().getColor(R.color.dark_grey));
//            nextBtn.setEnabled(false);
//        }
        postBtn = true;
    }

    public void makePlayRqst(ImageButton playBtn) {
        this.playBtn = playBtn;
        playRqst = true;
    }
    public void makeNextRqst(ImageButton nextBtn, TextView nextTxt, Context context) {
        this.nextBtn = nextBtn;
        this.nextTxt = nextTxt;
        this.context = context;
        nextRqst = true;
        if (postBtn) fulfillBtnRqsts();
    }

    private boolean postBtn = false;
    public boolean getPostBtn() {return postBtn;}

}


