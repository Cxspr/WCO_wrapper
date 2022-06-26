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
        domain_pref.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newDomain) {
                if (!preference.getSummary().toString().equals((String) newDomain)) {
//                    Log.i("SETTINGS OLD: ", preference.getSummary().toString());
//                    Log.i("SETTINGS: ", (String) newDomain);
                    ((MainActivity) getActivity()).updateDomainPreference(preference.getSummary().toString(), (String) newDomain);
                    return true;
                }
                return false;
            }
        });

        Preference preload_limit_pref = findPreference("preload_limit_pref");
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

    }
}