package com.wco_fun.wco_wrapper;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.wco_fun.wco_wrapper.databinding.FragmentMediaSelectBinding;

public class MediaSelect extends Fragment {

    private FragmentMediaSelectBinding binding;

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {

        binding = FragmentMediaSelectBinding.inflate(inflater, container, false);
        return binding.getRoot();

    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

//        binding.buttonFirst.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                NavHostFragment.findNavController(MediaSelect.this)
//                        .navigate(R.id.action_FirstFragment_to_SecondFragment);
//            }
//        });
        binding.buttonAnimeDubbed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle = new Bundle();
                bundle.putString("link","https://www.wcofun.com/dubbed-anime-list");
                NavHostFragment.findNavController(MediaSelect.this)
                        .navigate(R.id.action_mediaSelect_to_mediaSearch, bundle);
            }
        });

        binding.buttonCartoons.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle = new Bundle();
                bundle.putString("link", "https://www.wcofun.com/cartoon-list");
                NavHostFragment.findNavController(MediaSelect.this)
                        .navigate(R.id.action_mediaSelect_to_mediaSearch, bundle);
            }
        });

        binding.buttonAnimeSubbed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle = new Bundle();
                bundle.putString("link", "https://www.wcofun.com/subbed-anime-list");
                NavHostFragment.findNavController(MediaSelect.this)
                        .navigate(R.id.action_mediaSelect_to_mediaSearch, bundle);
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}