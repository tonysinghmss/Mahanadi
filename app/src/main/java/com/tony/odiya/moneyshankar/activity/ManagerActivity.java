package com.tony.odiya.moneyshankar.activity;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.app.Fragment;
import android.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.tony.odiya.moneyshankar.R;
import com.tony.odiya.moneyshankar.fragment.AnalysisFragment;
import com.tony.odiya.moneyshankar.fragment.ExpenseFragment;
import com.tony.odiya.moneyshankar.fragment.HomeFragment;
import com.tony.odiya.moneyshankar.model.ExpenseData;

import static com.tony.odiya.moneyshankar.common.Constants.MONTHLY;

public class ManagerActivity extends AppCompatActivity implements HomeFragment.OnHomeFragmentInteractionListener, ExpenseFragment.OnListFragmentInteractionListener,
        AnalysisFragment.OnAnalysisFragmentInteractionListener{

    private static final String LOG_TAG = ManagerActivity.class.getSimpleName();
    private Fragment fragment;
    private FragmentManager fragmentManager;
    private String selectedTrend;
    private static final String BACK_STACK_ROOT_TAG = "root_home_fragment";
    public static final String HOME_FRAGMENT = "home_fragment";
    private BottomNavigationView mNavigation;
    //private InterstitialAd mInterstitialAd;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            boolean selected = false;
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    fragment = HomeFragment.newInstance(selectedTrend);
                    selected =  true;
                    break;
                case R.id.navigation_expense:
                    //fragment = ExpenseFragment.newInstance(5, selectedTrend);
                    //showInterstitial();
                    fragment = ExpenseFragment.newInstance(selectedTrend);
                    selected =  true;
                    break;
                case R.id.navigation_analysis:
                    fragment = AnalysisFragment.newInstance(selectedTrend);
                    selected = true;
                    break;
            }
            if(fragment !=null){
                fragmentManager = getFragmentManager();
                switch (item.getItemId()) {
                    case R.id.navigation_home:
                        // Pop every fragment from backstack including home fragment.
                        fragmentManager.popBackStack(BACK_STACK_ROOT_TAG, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                        // Now insert home fragment on top of stack.
                        fragmentManager.beginTransaction()
                                .replace(R.id.content, fragment, HOME_FRAGMENT)
                                .addToBackStack(BACK_STACK_ROOT_TAG)
                                .commit();
                        break;
                    default:
                        fragmentManager.beginTransaction()
                                .replace(R.id.content, fragment)
                                .addToBackStack(null)
                                .commit();
                        break;

                }

            }
            return selected;
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manager);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        selectedTrend = MONTHLY;
        mNavigation = (BottomNavigationView) findViewById(R.id.navigation);
        mNavigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        mNavigation.setSelectedItemId(R.id.navigation_home);

        // Initialize the Mobile Ads SDK.
        /*MobileAds.initialize(this, "ca-app-pub-3940256099942544~3347511713");
        // Create the InterstitialAd and set the adUnitId.
        mInterstitialAd = new InterstitialAd(this);
        // Defined in res/values/strings.xml
        mInterstitialAd.setAdUnitId(getString(R.string.my_ad_unit_id));
        mInterstitialAd.loadAd(new AdRequest.Builder().build());
        mInterstitialAd.setAdListener(new AdListener(){
            @Override
            public void onAdClosed() {
                super.onAdClosed();
            }

            @Override
            public void onAdLeftApplication() {
                super.onAdLeftApplication();
            }

            @Override
            public void onAdFailedToLoad(int i) {
                super.onAdFailedToLoad(i);
                Log.e(LOG_TAG, "Ad failed to load with error code "+Integer.toString(i)+".");
            }
        });*/

    }

    /**
     *  This method communicates trend selection on Home fragment to Manager activity
     *  which eventually can be used by other fragments.
     * @param trend Trend set on Home fragment.
     */
    @Override
    public void onHomeTrendInteraction(String trend){
        this.selectedTrend = trend;
    }

    /**
     *  This method communicates trend selection on Expense fragment to Manager activity
     *  which eventually can be used by other fragments.
     * @param trend Trend set on Expense fragment.
     */
    public void onExpenseTrendInteraction(String trend){
        this.selectedTrend = trend;
    }

    public void onListFragmentInteraction(ExpenseData item){}

    /**
     *  This method communicates trend selection on Analysis fragment to Manager activity
     *  which eventually can be used by other fragments.
     * @param trend Trend set on Analysis fragment.
     */
    public void onAnalysisTrendInteraction(String trend){ this.selectedTrend = trend;}


    public void onBackPressed() {
        int count = getFragmentManager().getBackStackEntryCount();
        Log.d(LOG_TAG, "Count of backstack "+count);
        if(count >1){
            // We have lots of fragment on backstack to be popped.
            // Pop till the root fragment.
            getFragmentManager().popBackStack(BACK_STACK_ROOT_TAG, FragmentManager.POP_BACK_STACK_INCLUSIVE);
            //fragmentManager.popBackStack(BACK_STACK_ROOT_TAG, FragmentManager.POP_BACK_STACK_INCLUSIVE);
            mNavigation.setSelectedItemId(R.id.navigation_home);
        }
        else{
            // Close the application when we are on home fragment.
            supportFinishAfterTransition();
        }
    }

    /*private void showInterstitial() {
        // Show the ad if it's ready. Otherwise toast and restart the game.
        if (mInterstitialAd != null && mInterstitialAd.isLoaded()) {
            mInterstitialAd.show();
        } else {
            Log.d(LOG_TAG, "Ad didn't load properly.");
        }
    }*/
}
