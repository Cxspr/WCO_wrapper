package com.wco_fun.wco_wrapper.ui.genres;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.wco_fun.wco_wrapper.R;
import com.wco_fun.wco_wrapper.initialization.GenreList;

import java.util.ArrayList;

public class GenreSelectionAdapter extends RecyclerView.Adapter<GenreSelectionAdapter.ViewHolder> {

    private ArrayList<GenreList.Genre> genreList = new ArrayList<>();
    public GenreSelectionAdapter(GenreList genreList) {
        this.genreList = genreList.getGenreList();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.text_row_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GenreSelectionAdapter.ViewHolder holder, int position) {
        holder.setGenre(genreList.get(position));
    }

    @Override
    public int getItemCount() {
        return genreList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private GenreList.Genre genre;
        private TextView textView;
        public void setGenre(GenreList.Genre genre) {
            this.genre = genre;
            textView.setText(genre.getTitle());
            textView.setTextSize(18);
        }

        public ViewHolder(@NonNull View view) {
            super(view);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Bundle bundle = new Bundle();
                    bundle.putString("link", genre.getSrc());
                    bundle.putString("title", genre.getTitle());
                    Navigation.findNavController(view)
                            .navigate(R.id.action_genreSelection_to_genreSeries, bundle);
                }
            });

            textView = (TextView) view.findViewById(R.id.series_card_title);
        }
    }
}
