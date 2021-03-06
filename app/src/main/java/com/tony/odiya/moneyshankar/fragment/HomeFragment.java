package com.tony.odiya.moneyshankar.fragment;


import android.app.Activity;
import android.app.LoaderManager;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.tony.odiya.moneyshankar.R;
import com.tony.odiya.moneyshankar.activity.SettingsActivity;
import com.tony.odiya.moneyshankar.activity.WelcomeActivity;
import com.tony.odiya.moneyshankar.contract.MahanadiContract;
import com.tony.odiya.moneyshankar.dialog.BudgetSetupDialogFragment;
import com.tony.odiya.moneyshankar.dialoglistener.DialogListener;
import com.tony.odiya.moneyshankar.utils.PrefManager;
import com.tony.odiya.moneyshankar.utils.Utility;

import static com.tony.odiya.moneyshankar.common.Constants.BUDGET_LEFT;
import static com.tony.odiya.moneyshankar.common.Constants.DAILY;
import static com.tony.odiya.moneyshankar.common.Constants.DELETE_EXPENSE_COUNT;
import static com.tony.odiya.moneyshankar.common.Constants.END_TIME;
import static com.tony.odiya.moneyshankar.common.Constants.MONTHLY;
import static com.tony.odiya.moneyshankar.common.Constants.REQUEST_BUDGET_EDIT_CODE;
import static com.tony.odiya.moneyshankar.common.Constants.REQUEST_BUDGET_RESET_CODE;
import static com.tony.odiya.moneyshankar.common.Constants.REQUEST_BUDGET_SETUP_CODE;
import static com.tony.odiya.moneyshankar.common.Constants.START_TIME;
import static com.tony.odiya.moneyshankar.common.Constants.TOTAL_EXPENSE_CODE;
import static com.tony.odiya.moneyshankar.common.Constants.TREND_EXPENSE_CODE;
import static com.tony.odiya.moneyshankar.common.Constants.UPDATE_BUDGET_AMOUNT_COUNT;
import static com.tony.odiya.moneyshankar.common.Constants.WEEKLY;
import static com.tony.odiya.moneyshankar.common.Constants.YEARLY;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>, AdapterView.OnItemSelectedListener, DialogListener{
    private static final String LOG_TAG = HomeFragment.class.getSimpleName();
    private static final String ARG_TREND = "trend";
    //private static final String ARG_PARAM2 = "param2";
    private static final int TREND_EXPENSE_LOADER_ID = 1;
    private static final int TOTAL_EXPENSE_LOADER_ID = 2;
    //private static final int HOME_BUDGET_EXISTS_ID = 3;
    private static final int HOME_BUDGET_LOADER_ID = 3;

    // Budget for each month
    private static Boolean budgetIsSet = Boolean.FALSE;
    private static final int BUDGET_ALERT_ITEM_ID =1;

    private String mTrend;
    //private String mParam2;
    private OnHomeFragmentInteractionListener mListener;
    private Spinner homeLayoutTrendSpinner;
//    private Toolbar homeToolbar;
    private TextView budgetLeftForMonth;
    private View mHomeView;
    private AdView mAdView;
    private Double totalExpenseAmount = 0.0;
    private Double totalBudgetAmount = 0.0;
    private Double trendAmount = 0.0;
    public HomeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param trend Default trend for Home view.
     * @return A new instance of fragment HomeFragment.
     */
    public static HomeFragment newInstance(String trend) {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_TREND, trend);
        //args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnHomeFragmentInteractionListener) {
            mListener = (OnHomeFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnHomeFragmentInteractionListener");
        }
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mTrend = getArguments().getString(ARG_TREND);
            // mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mHomeView = inflater.inflate(R.layout.fragment_home, container, false);
        //mCoordinatorLayout = (CoordinatorLayout)mHomeView.findViewById(R.id.homeCoordinator);
//        homeToolbar = (Toolbar)mHomeView.findViewById(R.id.home_toolbar);
        budgetLeftForMonth = (TextView)mHomeView.findViewById(R.id.budget_left_amount);
        if(!budgetIsSet){
            budgetLeftForMonth.setText("Not Set");
        }
        homeLayoutTrendSpinner = (Spinner)mHomeView.findViewById(R.id.home_trend_spinner);
        homeLayoutTrendSpinner.setOnItemSelectedListener(this);
        Utility.setTrendValuesInSpinner(homeLayoutTrendSpinner, getActivity().getApplicationContext());
        // Select default trend in spinner.
        Utility.selectDefaultTrendInSpinner(homeLayoutTrendSpinner, mTrend);
        /*FloatingActionButton insertFab = (FloatingActionButton)mHomeView.findViewById(R.id.insert_fab);
        //insertFab.setImageResource(R.drawable.ic_add_circle_outline_black_24dp);
        insertFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), AddExpenseActivity.class);
                startActivityForResult(intent, REQUEST_EXPENSE_ADD_CODE);
            }
        });*/
//        ((AppCompatActivity)getActivity()).setSupportActionBar(homeToolbar);
        setHasOptionsMenu(true);

        mAdView = (AdView)mHomeView.findViewById(R.id.adView);
        mAdView.loadAd(new AdRequest.Builder().build());
        mAdView.setAdListener(new AdListener(){
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
        });

        return mHomeView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // REFER https://stackoverflow.com/questions/18727414/when-call-initloader-in-fragment
        Bundle args = Utility.getDateRange(MONTHLY);
        getLoaderManager().initLoader( HOME_BUDGET_LOADER_ID, args, this);
        //getLoaderManager().initLoader(HOME_BUDGET_LOADER_ID, args, this);

    }

    @Override
    public void onStart(){
        super.onStart();

    }

    @Override
    public void onResume(){
        super.onResume();
        Log.d(LOG_TAG,"Inside onResume");
        // Set the filter arguments for query here
        Bundle args = Utility.getDateRange(mTrend);
        // Load data into cursor asynchronously.
        // REFER https://stackoverflow.com/questions/18727414/when-call-initloader-in-fragment
        getLoaderManager().initLoader(TREND_EXPENSE_LOADER_ID, args, this);
        getLoaderManager().initLoader(TOTAL_EXPENSE_LOADER_ID, null, this);
        if(budgetIsSet) {
            Bundle monthlyArgs = Utility.getDateRange(MONTHLY);
            getLoaderManager().restartLoader(HOME_BUDGET_LOADER_ID, monthlyArgs, this);
        }
        else{
            showNoBudgetAlert();
        }
        if(mAdView !=null){
            mAdView.resume();
        }
    }

    @Override
    public void onDestroy() {
        if(mAdView !=null){
            mAdView.destroy();
        }
        super.onDestroy();
    }

    @Override
    public void onPause() {
        if(mAdView !=null){
            mAdView.pause();
        }
        super.onPause();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater){
        Log.d(LOG_TAG," Drawing Options Menu");
        super.onCreateOptionsMenu(menu,inflater);
        inflater.inflate(R.menu.home_fragment_menu,menu);
        //MenuItem alertMenuItem = menu.findItem(R.id.action_alert);
        MenuItem editBudgetItem = menu.findItem(R.id.action_edit_budget);
        MenuItem resetBudgetItem = menu.findItem(R.id.action_reset);
        MenuItem appIntroSliderItem = menu.findItem(R.id.action_intro);
        MenuItem appPreferences = menu.findItem(R.id.action_prefs);
        Utility.colorMenuItem(editBudgetItem,Color.WHITE);
        Utility.colorMenuItem(resetBudgetItem, Color.WHITE);
        Utility.colorMenuItem(appIntroSliderItem,Color.WHITE);
        Utility.colorMenuItem(appPreferences,Color.WHITE);
       /* if(!budgetIsSet) {
            //int color = ResourcesCompat.getColor(getResources(), R.color.colorAccent, null);
            Utility.colorMenuItem(alertMenuItem, Color.WHITE);
        }
        else if(budgetIsSet && alertMenuItem!=null){
            Log.d(LOG_TAG,"Remove budget alert icon from Toolbar");
            menu.removeItem(R.id.action_alert);
        }*/
    }

   /* @Override
    public void onPrepareOptionsMenu(Menu menu){

    }*/

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            /*case R.id.action_alert:
                BudgetSetupDialogFragment budgetSetupDailog = new BudgetSetupDialogFragment();
                budgetSetupDailog.show(getChildFragmentManager(),"BudgetSetupDialog");
                budgetSetupDailog.setTargetFragment(this, REQUEST_BUDGET_SETUP_CODE);
                break;*/
            case R.id.action_edit_budget:
                BudgetSetupDialogFragment budgetEditDailog = new BudgetSetupDialogFragment();
                Bundle args = new Bundle();
                args.putDouble(BUDGET_LEFT,totalBudgetAmount);
                budgetEditDailog.setArguments(args);
                budgetEditDailog.show(getChildFragmentManager(),"BudgetEditDialog");
                budgetEditDailog.setTargetFragment(this, REQUEST_BUDGET_EDIT_CODE);
                break;
            case R.id.action_reset:
                BudgetSetupDialogFragment resetBudgetDailog = new BudgetSetupDialogFragment();
                resetBudgetDailog.show(getChildFragmentManager(),"BudgetResetDialog");
                resetBudgetDailog.setTargetFragment(this, REQUEST_BUDGET_RESET_CODE);
                break;
            case R.id.action_intro:
                resetFirstTimeLaunchPref();
                Intent welcomeIntent = new Intent(getActivity(), WelcomeActivity.class);
                startActivity(welcomeIntent);
                break;
            case R.id.action_prefs:
                Intent prefsActivity = new Intent(getActivity(), SettingsActivity.class);
                //Intent prefsActivity = new Intent(getActivity(), LicenseActivity.class);
                startActivity(prefsActivity);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    /*@Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode,resultCode,data);
        if(requestCode == REQUEST_EXPENSE_ADD_CODE && resultCode == Activity.RESULT_OK){
            // Data has been saved into database.
            // Reload the total data for given  trend time as well as total.
            Bundle dateRangeArgs = Utility.getDateRange(mTrend);
            getLoaderManager().restartLoader(TREND_EXPENSE_LOADER_ID,dateRangeArgs,this);
            getLoaderManager().restartLoader(TOTAL_EXPENSE_LOADER_ID,null,this);
            Bundle monthlyArgs = Utility.getDateRange(MONTHLY);
            getLoaderManager().restartLoader(HOME_BUDGET_LOADER_ID, monthlyArgs, this);
        }
    }*/

    public interface OnHomeFragmentInteractionListener {
        void onHomeTrendInteraction(String trend);
    }


    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id){
        mTrend = parent.getItemAtPosition(pos).toString();
        // Communicate the change to manager activity.
        // Notify the active callbacks(Manager activity) that trend has been selected.
        if(null != mListener){
            mListener.onHomeTrendInteraction(mTrend);
        }
        Bundle args;
        switch (mTrend){
            case YEARLY:
                args = Utility.getDateRange(YEARLY);
                getLoaderManager().restartLoader(TREND_EXPENSE_LOADER_ID,args,this);
                break;
            case MONTHLY:
                args = Utility.getDateRange(MONTHLY);
                getLoaderManager().restartLoader(TREND_EXPENSE_LOADER_ID,args,this);
                break;
            case WEEKLY:
                args = Utility.getDateRange(WEEKLY);
                getLoaderManager().restartLoader(TREND_EXPENSE_LOADER_ID,args,this);
                break;
            case DAILY:
                args = Utility.getDateRange(DAILY);
                getLoaderManager().restartLoader(TREND_EXPENSE_LOADER_ID,args,this);
                break;
        }
    }
    @Override
    public void onNothingSelected(AdapterView<?> parent){}


    /* START LoaderManager callback logic */
    String[] TREND_EXPENSE_LIST_PROJECTION = {
            "SUM("+MahanadiContract.Expense.COL_AMOUNT+") AS TREND "
    };
    String[] TOTAL_EXPENSE_LIST_PROJECTION = {
            "SUM("+MahanadiContract.Expense.COL_AMOUNT+") AS TOTAL "

    };
    String[] BUDGET_LIST_PROJECTION = {
            MahanadiContract.Budget.COL_AMOUNT +" AS BUDGET "
    };
    String MONTHLY_BUDGET_FILTER = "datetime("+MahanadiContract.Budget.COL_END_DATE +"/1000,'unixepoch') >= datetime('now','unixepoch')";

    @Override
    public Loader<Cursor> onCreateLoader(int loaderId, Bundle args) {
        String []projection = null;
        String selection= null;
        String []selectionArgs = null;
        Uri uri = null;
        switch (loaderId) {
            case TREND_EXPENSE_LOADER_ID:
                Log.d(LOG_TAG, "Cursorloader for trend expense");
                String filterClause = MahanadiContract.Expense.COL_CREATED_ON + " BETWEEN "+ args.getString(START_TIME)+" AND "+args.getString(END_TIME);
                //String [] filterArgs = {args.getString(START_TIME), args.getString(END_TIME)};
                uri = Uri.withAppendedPath(MahanadiContract.Expense.CONTENT_URI,TREND_EXPENSE_CODE);
                projection = TREND_EXPENSE_LIST_PROJECTION;
                selection = filterClause;
                break;
            case TOTAL_EXPENSE_LOADER_ID:
                Log.d(LOG_TAG, "Cursorloader for total expense");
                uri = Uri.withAppendedPath(MahanadiContract.Expense.CONTENT_URI,TOTAL_EXPENSE_CODE);
                projection = TOTAL_EXPENSE_LIST_PROJECTION;
                break;
           /* case  HOME_BUDGET_EXISTS_ID:
                Log.d(LOG_TAG, "Cursorloader to check if budget exists or not");
                String budgetExistsFilterClause = MahanadiContract.Budget.COL_CREATED_ON + " BETWEEN ? AND ?  AND "
                        +MONTHLY_BUDGET_FILTER;
                String [] budgetExistsFilterArgs = {args.getString(START_TIME), args.getString(END_TIME)};
                uri = MahanadiContract.Budget.CONTENT_URI;
                projection = BUDGET_LIST_PROJECTION;
                selection = budgetExistsFilterClause;
                selectionArgs = budgetExistsFilterArgs;
                break;*/
            case HOME_BUDGET_LOADER_ID:
                Log.d(LOG_TAG, "Cursorloader for loading budget");
                String budgetFilterClause = MahanadiContract.Budget.COL_CREATED_ON + " BETWEEN ? AND ?  AND "
                        +MONTHLY_BUDGET_FILTER;
                String [] budgetFilterArgs = {args.getString(START_TIME), args.getString(END_TIME)};
                uri = MahanadiContract.Budget.CONTENT_URI;
                projection = BUDGET_LIST_PROJECTION;
                selection = budgetFilterClause;
                selectionArgs = budgetFilterArgs;
                break;
            default:
                return null;
        }
        return new CursorLoader(getActivity(),
                uri,                        // Uri for cursors
                projection,               // List of columns to fetch
                selection,                       // Filter clauses
                selectionArgs,                         // Filter args
                null);
    }
    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor dataCursor) {

        int loaderId = loader.getId();
        //Log.d(LOG_TAG, "Inside onLoadFinished for "+loaderId);
        /*Double totalBudgetAmount = 0.0;
        Double trendAmount = 0.0;*/
        int cnt = -1;

        switch (loaderId){
            case TREND_EXPENSE_LOADER_ID:
                Log.d(LOG_TAG, "Inside onLoadFinished for "+TREND_EXPENSE_LOADER_ID);
                while (dataCursor.moveToNext()) {
                    trendAmount = dataCursor.getDouble(dataCursor.getColumnIndex("TREND"));
                }
                TextView expenseAmountTextView = (TextView)mHomeView.findViewById(R.id.expense_amount);
                expenseAmountTextView.setText(trendAmount.toString());
                Log.d(LOG_TAG, "Trend expense loaded is "+trendAmount.toString());
                break;
            case TOTAL_EXPENSE_LOADER_ID:
                while (dataCursor.moveToNext()) {
                    totalExpenseAmount = dataCursor.getDouble(dataCursor.getColumnIndex("TOTAL"));
                }
                TextView grandTotalAmountTextView = (TextView)mHomeView.findViewById(R.id.grand_total_amount);
                grandTotalAmountTextView.setText(totalExpenseAmount.toString());
                Log.d(LOG_TAG, "Total expense loaded is "+totalExpenseAmount.toString());
                break;
            case HOME_BUDGET_LOADER_ID:
                cnt = dataCursor.getCount();
                //Log.d(LOG_TAG, "Budget row count is "+cnt);
                if(cnt>0 && !budgetIsSet){
                    budgetIsSet = Boolean.TRUE;
                    //getActivity().invalidateOptionsMenu();
                }
                else if(cnt == 0 ){
                    totalBudgetAmount = 0.0;
                    budgetIsSet = Boolean.FALSE;
                }
                //here total alias will refer to total budget for a given month.
                while (dataCursor.moveToNext()) {
                    totalBudgetAmount = dataCursor.getDouble(dataCursor.getColumnIndex("BUDGET"));
                }
                budgetLeftForMonth.setText(totalBudgetAmount.toString());
                // If expense exceeds current month budget show 0.0
                if(totalBudgetAmount <0.0) {
                    budgetLeftForMonth.setTextColor(Color.RED);
                }
                //Log.d(LOG_TAG,"Budget loaded is :"+ totalBudgetAmount.toString());
                break;
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader){}

    public void onBudgetReset(Bundle args){
        budgetIsSet = Boolean.FALSE;
        getActivity().invalidateOptionsMenu();
        Bundle trendArgs = Utility.getDateRange(mTrend);
        if(args.containsKey(DELETE_EXPENSE_COUNT)){
            getLoaderManager().restartLoader(TREND_EXPENSE_LOADER_ID, trendArgs, this);
            getLoaderManager().restartLoader(TOTAL_EXPENSE_LOADER_ID, trendArgs, this);
        }
        Bundle monthlyArgs = Utility.getDateRange(MONTHLY);
        getLoaderManager().restartLoader(HOME_BUDGET_LOADER_ID, monthlyArgs, this);


    }

    public void onBudgetEdit(Bundle args){
        if(args.containsKey(UPDATE_BUDGET_AMOUNT_COUNT)){
            budgetIsSet = Boolean.TRUE;
            getActivity().invalidateOptionsMenu();
            Bundle monthlyArgs = Utility.getDateRange(MONTHLY);
            getLoaderManager().restartLoader(HOME_BUDGET_LOADER_ID, monthlyArgs, this);
        }
    }

    public void onBudgetSetup(Bundle args){
        budgetIsSet = Boolean.TRUE;
        getActivity().invalidateOptionsMenu();
        Bundle monthlyArgs = Utility.getDateRange(MONTHLY);
        getLoaderManager().restartLoader(HOME_BUDGET_LOADER_ID, monthlyArgs, this);
        /*Bundle trendArgs = Utility.getDateRange(mTrend);
        if(args.containsKey(DELETE_EXPENSE_COUNT)){
            getLoaderManager().restartLoader(TREND_EXPENSE_LOADER_ID, trendArgs, this);
            getLoaderManager().restartLoader(TOTAL_EXPENSE_LOADER_ID, trendArgs, this);
        }*/
        /*if( args.containsKey(DELETE_BUDGET_COUNT)){

        }*/
    }

    private void resetFirstTimeLaunchPref(){
        PrefManager prefManager = new PrefManager(getActivity());
        prefManager.setFirstTimeLaunch(true);
    }

    private void showNoBudgetAlert(){
        BudgetSetupDialogFragment budgetSetupDailog = new BudgetSetupDialogFragment();
        budgetSetupDailog.show(getChildFragmentManager(),"BudgetSetupDialog");
        budgetSetupDailog.setTargetFragment(this, REQUEST_BUDGET_SETUP_CODE);
    }
}
