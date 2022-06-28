package com.wco_fun.wco_wrapper;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceScreen;

import com.wco_fun.wco_wrapper.MainActivity;
import com.wco_fun.wco_wrapper.R;

import java.nio.file.FileSystem;

public class SettingsFragment extends PreferenceFragmentCompat {

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.preferences, rootKey);

        Preference domain_pref = findPreference("domain_pref");
        Preference preload_limit_pref = findPreference("preload_limit_pref");
        Preference use_genre_list = findPreference("use_genre_list");
        Preference regen_genre_list = findPreference("regen_genre_list");
        domain_pref.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newDomain) {
                if (!preference.getSummary().toString().equals((String) newDomain)) {
//                    Log.i("SETTINGS OLD: ", preference.getSummary().toString());
//                    Log.i("SETTINGS: ", (String) newDomain);
                    ((MainActivity) getActivity()).updateDomainPreference(preference.getSummary().toString(), (String) newDomain);
                    if (Boolean.parseBoolean((String) use_genre_list.getSummary())) {
                        ((MainActivity) getActivity()).genGenreList(getActivity());
                    }
                    return true;
                }
                return false;
            }
        });

        preload_limit_pref.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                try {
                    int newLimit = Integer.parseInt((String) newValue);
                    if (newLimit > 0 && newLimit != Integer.parseInt((String) preference.getSummary().toString())) {
                        ((MainActivity) getActivity()).updatePreloadLimit(newLimit);
                        return true;
                    }
                } catch (Exception e) {

                }
                return false;
            }
        });

        use_genre_list.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                boolean newState = (boolean) newValue;
                if (newState) {
                    ((MainActivity) getActivity()).genGenreList(getActivity());
                }
                return true;
            }
        });

        regen_genre_list.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                ((MainActivity) getActivity()).genGenreList(getActivity());
                return false;
            }
        });




    }
}