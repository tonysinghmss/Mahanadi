package com.tony.odiya.moneyshankar.fragment;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.tony.odiya.moneyshankar.R;

/**
 * Created by tony on 8/1/18.
 */

public class SettingsFragment extends Fragment {
    private View mSettingsView;
    private Toolbar settingsToolbar;
    private RecyclerView mRecyclerView;
//    @Override
//    public void onCreate(@Nullable Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        // addPreferencesFromResource();
//        String settings = getArguments().getString("settings");
//        if ("help".equals(settings)) {
//            addPreferencesFromResource(R.xml.settings_help);
//        } /*else if ("sync".equals(settings)) {
//            addPreferencesFromResource(R.xml.settings_sync);
//        }*/
//    }

    public static SettingsFragment newInstance(){
        SettingsFragment fragment = new SettingsFragment();
        // Bundle args = new Bundle();
        // args.putString(ARG_TREND, trend);
        // args.putString(ARG_PARAM2, param2);
        // fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        if (getArguments() != null) {
//            mTrend = getArguments().getString(ARG_TREND);
//            // mParam2 = getArguments().getString(ARG_PARAM2);
//        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mSettingsView = inflater.inflate(R.layout.fragment_settings, container, false);
        View recycler = mSettingsView.findViewById(R.id.settings_recycler_list);
        settingsToolbar = (Toolbar)mSettingsView.findViewById(R.id.settings_toolbar);
        //Set the toolbar
        ((AppCompatActivity)getActivity()).setSupportActionBar(settingsToolbar);
        if (recycler instanceof RecyclerView) {
            Context context = mSettingsView.getContext();
            mRecyclerView = (RecyclerView) recycler;
            mRecyclerView.setLayoutManager(new LinearLayoutManager(context));
            //TODO: Fragment should belong to Manager Activity and settingsActivity should be removed
            //TODO: Set the value of settings in recycler view
            //TODO: on Clicking an individual item of list, it should open a website on browser.
        }
        return mSettingsView;
    }
}
