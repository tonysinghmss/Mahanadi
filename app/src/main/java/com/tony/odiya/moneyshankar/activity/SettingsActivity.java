package com.tony.odiya.moneyshankar.activity;

import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import android.widget.Toolbar;

import com.tony.odiya.moneyshankar.R;
import com.tony.odiya.moneyshankar.fragment.SettingsFragment;

/**
 * Created by tony on 8/1/18.
 */

public class SettingsActivity extends AppCompatActivity{
    private static final String LOG_TAG = SettingsActivity.class.getSimpleName();
    private static final String BACK_STACK_ROOT_TAG = "root_help_fragment";
    private static final String SETTINGS_FRAGMENT = "home_fragment";
    private Toolbar settingsToolbar;
    private Fragment fragment;
    private FragmentManager fragmentManager;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
//        settingsToolbar = (Toolbar)findViewById(R.id.settings_toolbar);
//        setActionBar(settingsToolbar);
//        getActionBar().setDisplayHomeAsUpEnabled(true);
//        setSupportActionBar(settingsToolbar);d(true);
        if (savedInstanceState == null) {
            // Create the fragment only when the activity is created
//        getSupportActionBar().setDisplayHomeAsUpEnablefor the first time.
            // ie. not after orientation changes
            fragmentManager = getFragmentManager();
            // fragment = fragmentManager.findFragmentByTag(SettingsFragment.SETTING_FRAGMENT_TAG);
            if (fragment == null) {
                fragment = SettingsFragment.newInstance();
            }
            fragmentManager.beginTransaction()
                    .replace(R.id.settings_content, fragment, SETTINGS_FRAGMENT)
                    .addToBackStack(BACK_STACK_ROOT_TAG)
                    .commit();
        }
    }

    /*@Override
    public void onBuildHeaders(List<Header> target) {
        loadHeadersFromResource(R.xml.settings_header, target);
    }

    @Override
    protected boolean isValidFragment(String fragmentName) {
        if(SettingsFragment.class.getName().equals(fragmentName)){
            return true;
        }
        return false;
    }*/


}


