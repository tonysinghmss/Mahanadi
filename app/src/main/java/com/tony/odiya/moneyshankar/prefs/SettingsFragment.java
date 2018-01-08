package com.tony.odiya.moneyshankar.prefs;

import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.support.annotation.Nullable;

import com.tony.odiya.moneyshankar.R;

/**
 * Created by tony on 8/1/18.
 */

public class SettingsFragment extends PreferenceFragment {
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // addPreferencesFromResource();
        String settings = getArguments().getString("settings");
        if ("help".equals(settings)) {
            addPreferencesFromResource(R.xml.settings_help);
        } /*else if ("sync".equals(settings)) {
            addPreferencesFromResource(R.xml.settings_sync);
        }*/
    }
}
