package com.wco_fun.wco_wrapper;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import androidx.browser.customtabs.CustomTabsIntent;
import androidx.fragment.app.Fragment;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceScreen;

import com.wco_fun.wco_wrapper.MainActivity;
import com.wco_fun.wco_wrapper.R;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.nio.file.FileSystem;

public class SettingsFragment extends PreferenceFragmentCompat {
    private boolean updateAvail = false;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.preferences, rootKey);

        Preference domain_pref = findPreference("domain_pref");
        Preference preload_limit_pref = findPreference("preload_limit_pref");
        Preference use_genre_list = findPreference("use_genre_list");
        Preference regen_genre_list = findPreference("regen_genre_list");
        Preference report_feedback = findPreference("report_feedback");
        Preference app_version = findPreference("app_version");
        app_version.setSummary("Current version: " + BuildConfig.VERSION_NAME);

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

        report_feedback.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                String url = "https://forms.gle/FK9fKKvkU1955gbW7";
                CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
                CustomTabsIntent customTabsIntent = builder.build();
                customTabsIntent.launchUrl(getActivity(), Uri.parse(url));
                return true;
            }
        });

        app_version.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                if (!updateAvail) {
                    ConnectedThread thread = new ConnectedThread((Fragment) getParentFragment()){
                        @Override
                        public void run() {
                            try {
                                String versionRead = Jsoup.connect("https://github.com/Cxspr/WCO_wrapper")
                                        .userAgent("Mozilla/5.0 (Windows; U; WindowsNT 5.1; en-US; rv1.8.1.6) Gecko/20070725 Firefox/2.0.0.6")
                                        .timeout(5000)
                                        .maxBodySize(0)
                                        .get()
                                        .getElementsByClass("css-truncate css-truncate-target text-bold mr-2")
                                        .get(0)
                                        .text();
                                if (!versionRead.substring(1).equals(BuildConfig.VERSION_NAME)){
                                    updateAvail = true;
                                    retMsg = "New version found, click again to open the release page.";
                                } else {
                                    retMsg = "App is up to date with latest release";
                                }
                            } catch (SocketTimeoutException e) {
                                retMsg = "An error occurred while searching";
                                e.printStackTrace();
                            } catch (IOException e) {
                                retMsg = "An error occurred while searching";
                                e.printStackTrace();
                            } finally {
                                super.run();
                            }
                        }
                    };
                    thread.start();
                } else {
                    String url = "https://github.com/Cxspr/WCO_wrapper/releases";
                    CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
                    CustomTabsIntent customTabsIntent = builder.build();
                    customTabsIntent.launchUrl(getActivity(), Uri.parse(url));
                }


                return true;
            }
        });

    }

    public void threadReturnHandler(String retMsg) {
        Toast.makeText(this.getContext(), retMsg, Toast.LENGTH_LONG).show();
    }

    private class ConnectedThread extends Thread {
        private Handler resHandler;
        protected Fragment retLoc;
        protected String retMsg;

        public ConnectedThread(Fragment retLoc) {
            this.resHandler = new Handler(((MainActivity) retLoc.getActivity()).getMainLooper());
            this.retLoc = retLoc;
        }
        @Override
        public void run() {
            notifyResult(retMsg);
        }
        private void notifyResult(String retMsg){
            resHandler.post(() -> { threadReturnHandler(retMsg); });
        }
    }
}