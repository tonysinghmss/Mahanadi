package com.tony.odiya.moneyshankar.prefs;

import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import android.util.Log;
import android.widget.Toolbar;

import com.tony.odiya.moneyshankar.R;

import java.util.List;

/**
 * Created by tony on 8/1/18.
 */

public class SettingsActivity extends PreferenceActivity{
    private static final String LOG_TAG = SettingsActivity.class.getSimpleName();
    private static final String BACK_STACK_ROOT_TAG = "root_help_fragment";
    private static final String SETTINGS_FRAGMENT = "home_fragment";
    private Toolbar settingsToolbar;
    private Fragment fragment;
    private FragmentManager fragmentManager;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // setContentView(R.layout.activity_settings);
//        settingsToolbar = (Toolbar)findViewById(R.id.settings_toolbar);
//        setActionBar(settingsToolbar);
//        getActionBar().setDisplayHomeAsUpEnabled(true);
//        setSupportActionBar(settingsToolbar);
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        /*if (savedInstanceState == null) {
            // Create the fragment only when the activity is created for the first time.
            // ie. not after orientation changes
            fragmentManager = getFragmentManager();
            fragment = fragmentManager.findFragmentByTag(SettingsFragment.SETTING_FRAGMENT_TAG);
            if (fragment == null) {
                fragment = new SettingsFragment();
            }
            fragmentManager.beginTransaction()
                    .replace(R.id.settings_content, fragment, SETTINGS_FRAGMENT)
                    .addToBackStack(BACK_STACK_ROOT_TAG)
                    .commit();
        }*/
    }

    @Override
    public void onBuildHeaders(List<Header> target) {
        loadHeadersFromResource(R.xml.settings_header, target);
    }

    @Override
    protected boolean isValidFragment(String fragmentName) {
        if(SettingsFragment.class.getName().equals(fragmentName)){
            return true;
        }
        return false;
    }

    /*@Override
    public boolean onPreferenceStartScreen(PreferenceFragmentCompat caller, PreferenceScreen pref) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        MyInnerPreferenceFragment fragment = new MyInnerPreferenceFragment();
        Bundle args = new Bundle();
        args.putString(PreferenceFragmentCompat.ARG_PREFERENCE_ROOT, preferenceScreen.getKey());
        fragment.setArguments(args);
        ft.add(R.id.fragment_container, fragment, preferenceScreen.getKey());
        ft.addToBackStack(preferenceScreen.getKey());
        ft.commit();
        return true;
    }*/

     /*public void onBackPressed() {
        int count = getFragmentManager().getBackStackEntryCount();
        Log.d(LOG_TAG, "Count of backstack "+count);
        if(count >1){
            // We have lots of fragment on backstack to be popped.
            // Pop till the root fragment.
            getFragmentManager().popBackStack(BACK_STACK_ROOT_TAG, FragmentManager.POP_BACK_STACK_INCLUSIVE);
            //fragmentManager.popBackStack(BACK_STACK_ROOT_TAG, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        }
        else{
            //TODO: Close the activity and go back to home fragment of manager activity.
            // supportFinishAfterTransition();
        }
    }*/
}


