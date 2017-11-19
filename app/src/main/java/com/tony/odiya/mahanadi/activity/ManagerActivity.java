package com.tony.odiya.mahanadi.activity;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.app.Fragment;
import android.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;

import com.tony.odiya.mahanadi.R;
import com.tony.odiya.mahanadi.fragment.AnalysisFragment;
import com.tony.odiya.mahanadi.fragment.ExpenseFragment;
import com.tony.odiya.mahanadi.fragment.HomeFragment;
import com.tony.odiya.mahanadi.model.ExpenseData;

import static com.tony.odiya.mahanadi.common.Constants.MONTHLY;

public class ManagerActivity extends AppCompatActivity implements HomeFragment.OnHomeTrendInteractionListener, ExpenseFragment.OnListFragmentInteractionListener,
        AnalysisFragment.OnAnalysisFragmentInteractionListener{

    private static final String LOG_TAG = ManagerActivity.class.getSimpleName();
    private Fragment fragment;
    private FragmentManager fragmentManager;
    private String selectedTrend;
    private static final String BACK_STACK_ROOT_TAG = "root_home_fragment";
    private static final String HOME_FRAGMENT = "home_fragment";
    private BottomNavigationView mNavigation;

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
        selectedTrend = MONTHLY;
        mNavigation = (BottomNavigationView) findViewById(R.id.navigation);
        mNavigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        mNavigation.setSelectedItemId(R.id.navigation_home);

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
}
