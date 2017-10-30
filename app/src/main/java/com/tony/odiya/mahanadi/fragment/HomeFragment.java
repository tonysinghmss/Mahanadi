package com.tony.odiya.mahanadi.fragment;


import android.app.Activity;
import android.app.LoaderManager;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
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

import com.tony.odiya.mahanadi.activity.AddExpenseActivity;
import com.tony.odiya.mahanadi.R;
import com.tony.odiya.mahanadi.contract.MahanadiContract;
import com.tony.odiya.mahanadi.dialog.BudgetSetupDialogFragment;
import com.tony.odiya.mahanadi.dialoglistener.DialogListener;
import com.tony.odiya.mahanadi.utils.Utility;

import static com.tony.odiya.mahanadi.common.Constants.DAILY;
import static com.tony.odiya.mahanadi.common.Constants.END_TIME;
import static com.tony.odiya.mahanadi.common.Constants.MONTHLY;
import static com.tony.odiya.mahanadi.common.Constants.REQUEST_BUDGET_SETUP_CODE;
import static com.tony.odiya.mahanadi.common.Constants.START_TIME;
import static com.tony.odiya.mahanadi.common.Constants.TOTAL_EXPENSE_CODE;
import static com.tony.odiya.mahanadi.common.Constants.TREND_EXPENSE_CODE;
import static com.tony.odiya.mahanadi.common.Constants.WEEKLY;
import static com.tony.odiya.mahanadi.common.Constants.YEARLY;
import static com.tony.odiya.mahanadi.common.Constants.REQUEST_EXPENSE_CODE;

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
    private OnHomeTrendInteractionListener mListener;
    private Spinner homeLayoutTrendSpinner;
    private Toolbar homeToolbar;
    private TextView budgetLeftForMonth;
    private View mHomeView;
    private Double totalAmount = 0.0;
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
        if (context instanceof OnHomeTrendInteractionListener) {
            mListener = (OnHomeTrendInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnHomeTrendInteractionListener");
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
        mHomeView = inflater.inflate(R.layout.layout_home, container, false);
        homeToolbar = (Toolbar)mHomeView.findViewById(R.id.home_toolbar);
        budgetLeftForMonth = (TextView)mHomeView.findViewById(R.id.budget_left_amount);
        if(!budgetIsSet){
            budgetLeftForMonth.setText("Not Set");
        }
        homeLayoutTrendSpinner = (Spinner)mHomeView.findViewById(R.id.home_trend_spinner);
        homeLayoutTrendSpinner.setOnItemSelectedListener(this);
        Utility.setTrendValuesInSpinner(homeLayoutTrendSpinner, getActivity().getApplicationContext());
        // Select default trend in spinner.
        Utility.selectDefaultTrendInSpinner(homeLayoutTrendSpinner, mTrend);
        FloatingActionButton insertFab = (FloatingActionButton)mHomeView.findViewById(R.id.insert_fab);
        //insertFab.setImageResource(R.drawable.ic_add_circle_outline_black_24dp);
        insertFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), AddExpenseActivity.class);
                startActivityForResult(intent,REQUEST_EXPENSE_CODE);
            }
        });
        ((AppCompatActivity)getActivity()).setSupportActionBar(homeToolbar);
        setHasOptionsMenu(true);
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
        MenuItem alertMenuItem = menu.findItem(R.id.action_alert);
        if(!budgetIsSet) {
            int color = ResourcesCompat.getColor(getResources(), R.color.colorAccent, null);
            Utility.colorMenuItem(alertMenuItem, color);
        }
        else if(budgetIsSet && alertMenuItem!=null){
            Log.d(LOG_TAG,"Remove alert icon from Toolbar");
            menu.removeItem(R.id.action_alert);
        }
    }

   /* @Override
    public void onPrepareOptionsMenu(Menu menu){

    }*/

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_alert:
                BudgetSetupDialogFragment budgetSetupDailog = new BudgetSetupDialogFragment();
                budgetSetupDailog.show(getChildFragmentManager(),"BudgetSetupDialog");
                budgetSetupDailog.setTargetFragment(this, REQUEST_BUDGET_SETUP_CODE);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode,resultCode,data);
        if(requestCode == REQUEST_EXPENSE_CODE && resultCode == Activity.RESULT_OK){
            // Data has been saved into database.
            // Reload the total data for given  trend time as well as total.
            Bundle dateRangeArgs = Utility.getDateRange(mTrend);
            getLoaderManager().restartLoader(TREND_EXPENSE_LOADER_ID,dateRangeArgs,this);
            getLoaderManager().restartLoader(TOTAL_EXPENSE_LOADER_ID,null,this);
            Bundle monthlyArgs = Utility.getDateRange(MONTHLY);
            getLoaderManager().restartLoader(HOME_BUDGET_LOADER_ID, monthlyArgs, this);
        }
    }

    public interface OnHomeTrendInteractionListener {
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
                Log.d(LOG_TAG,"After calling cursorloader for trend expense");
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
        /*Double totalAmount = 0.0;
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
                    totalAmount = dataCursor.getDouble(dataCursor.getColumnIndex("TOTAL"));
                }
                TextView grandTotalAmountTextView = (TextView)mHomeView.findViewById(R.id.grand_total_amount);
                grandTotalAmountTextView.setText(totalAmount.toString());
                Log.d(LOG_TAG, "Total expense loaded is "+totalAmount.toString());
                break;
            case HOME_BUDGET_LOADER_ID:
                cnt = dataCursor.getCount();
                //Log.d(LOG_TAG, "Budget row count is "+cnt);
                if(cnt>0 && !budgetIsSet){
                    budgetIsSet = Boolean.TRUE;
                    getActivity().invalidateOptionsMenu();
                }
                //here total alias will refer to total budget for a given month.
                while (dataCursor.moveToNext()) {
                    totalAmount = dataCursor.getDouble(dataCursor.getColumnIndex("BUDGET"));
                }
                budgetLeftForMonth.setText(totalAmount.toString());
                //Log.d(LOG_TAG,"Budget loaded is :"+ totalAmount.toString());
                break;
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader){}


    public void onDialogInteraction(Bundle args){
        budgetIsSet = Boolean.TRUE;
        getActivity().invalidateOptionsMenu();
        Bundle monthlyArgs = Utility.getDateRange(MONTHLY);
        getLoaderManager().restartLoader(HOME_BUDGET_LOADER_ID, monthlyArgs, this);
    }
}
