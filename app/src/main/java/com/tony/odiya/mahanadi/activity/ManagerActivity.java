package com.tony.odiya.mahanadi.activity;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.app.Fragment;
import android.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.tony.odiya.mahanadi.R;
import com.tony.odiya.mahanadi.fragment.ExpenseFragment;
import com.tony.odiya.mahanadi.fragment.HomeFragment;
import com.tony.odiya.mahanadi.model.ExpenseData;

import java.util.ArrayList;
import java.util.List;

import static com.tony.odiya.mahanadi.common.Constants.MONTHLY;

public class ManagerActivity extends AppCompatActivity implements HomeFragment.OnHomeTrendInteractionListener, ExpenseFragment.OnListFragmentInteractionListener{

    private Fragment fragment;
    private FragmentManager fragmentManager;
    private String selectedTrend;

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
                    // TODO: Future release.
                    //return false;
                    break;
            }
            if(fragment !=null){
                fragmentManager = getFragmentManager();
                fragmentManager.beginTransaction()
                        .replace(R.id.content, fragment)
                        .addToBackStack(null)
                        .commit();
            }
            return selected;
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manager);
        selectedTrend = MONTHLY;
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        navigation.setSelectedItemId(R.id.navigation_home);

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
}
