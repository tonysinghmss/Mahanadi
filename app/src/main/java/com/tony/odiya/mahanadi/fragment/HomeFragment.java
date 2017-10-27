package com.tony.odiya.mahanadi.fragment;


import android.app.Activity;
import android.app.LoaderManager;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.app.Fragment;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.TextView;

import com.tony.odiya.mahanadi.activity.AddExpenseActivity;
import com.tony.odiya.mahanadi.R;
import com.tony.odiya.mahanadi.contract.MahanadiContract;
import com.tony.odiya.mahanadi.dialog.BudgetSetupDialogFragment;
import com.tony.odiya.mahanadi.utils.Utility;

import static com.tony.odiya.mahanadi.common.Constants.DAILY;
import static com.tony.odiya.mahanadi.common.Constants.END_TIME;
import static com.tony.odiya.mahanadi.common.Constants.MONTHLY;
import static com.tony.odiya.mahanadi.common.Constants.START_TIME;
import static com.tony.odiya.mahanadi.common.Constants.WEEKLY;
import static com.tony.odiya.mahanadi.common.Constants.YEARLY;
import static com.tony.odiya.mahanadi.common.Constants.REQUEST_EXPENSE_CODE;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>, AdapterView.OnItemSelectedListener{
    private static final String LOG_TAG = HomeFragment.class.getSimpleName();
    private static final String ARG_TREND = "trend";
    //private static final String ARG_PARAM2 = "param2";
    private static final int HOME_EXPENSE_LOADER_ID = 1;
    private static final int HOME_TOTAL_LOADER_ID = 2;
    private static final int HOME_BUDGET_LOADER_ID = 3;
    // Budget for each month
    private static Boolean isBudgetSet = Boolean.FALSE;
    private static Double monthlyBudget = 0.0;

    private String mTrend;
    //private String mParam2;
    private OnHomeTrendInteractionListener mListener;
    private Spinner homeLayoutTrendSpinner;
    private Toolbar homeToolbar;
    private View mHomeView;


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
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mTrend = getArguments().getString(ARG_TREND);
            // mParam2 = getArguments().getString(ARG_PARAM2);
        }
        /*// Set the filter arguments for query here
        Bundle args = Utility.getDateRange(mTrend);
        // Load data into cursor asynchronously.
        getLoaderManager().initLoader(HOME_EXPENSE_LOADER_ID, args, this);
        getLoaderManager().initLoader(HOME_TOTAL_LOADER_ID, null, this);*/
        /*if(!isBudgetSet) {
            if (mTrend.equals(MONTHLY)) {
                getLoaderManager().initLoader(HOME_BUDGET_LOADER_ID, args, this);
            } else {
                args = Utility.getDateRange(MONTHLY);
                getLoaderManager().initLoader(HOME_BUDGET_LOADER_ID, args, this);
            }
        }*/

    }
    @Override
    public void onResume(){
        super.onResume();
        Log.d(LOG_TAG,"Inside onResume");
        // Set the filter arguments for query here
        Bundle args = Utility.getDateRange(mTrend);
        // Load data into cursor asynchronously.
        getLoaderManager().initLoader(HOME_EXPENSE_LOADER_ID, args, this);
        getLoaderManager().initLoader(HOME_TOTAL_LOADER_ID, null, this);
        if(!mTrend.equals(MONTHLY)){
            args = Utility.getDateRange(MONTHLY);
        }
        getLoaderManager().initLoader(HOME_BUDGET_LOADER_ID, args, this);
        // Reason for invoking dialog from onResume
        // https://stackoverflow.com/questions/5019686/what-methods-are-invoked-in-the-activity-lifecycle-in-the-following-cases
        if(!isBudgetSet){
            //Show a dialog to set budget.
            // This dialog should set the budget.
            BudgetSetupDialogFragment budgetSetupDailog = new BudgetSetupDialogFragment();
            budgetSetupDailog.show(getChildFragmentManager(),"BudgetSetupDialog");
            //Bundle args = Utility.getDateRange(MONTHLY);
            //getLoaderManager().initLoader(HOME_BUDGET_LOADER_ID, args, this);
            isBudgetSet = true;
        }
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mHomeView = inflater.inflate(R.layout.layout_home, container, false);
        homeToolbar = (Toolbar)mHomeView.findViewById(R.id.home_toolbar);
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
        return mHomeView;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode,resultCode,data);
        if(requestCode == REQUEST_EXPENSE_CODE && resultCode == Activity.RESULT_OK){
            // Data has been saved into database.
            // Reload the total data for given  trend time as well as total.
            Bundle dateRangeArgs = Utility.getDateRange(mTrend);
            getLoaderManager().restartLoader(HOME_EXPENSE_LOADER_ID,dateRangeArgs,this);
            getLoaderManager().restartLoader(HOME_TOTAL_LOADER_ID,null,this);
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
                getLoaderManager().restartLoader(HOME_EXPENSE_LOADER_ID,args,this);
                break;
            case MONTHLY:
                args = Utility.getDateRange(MONTHLY);
                getLoaderManager().restartLoader(HOME_EXPENSE_LOADER_ID,args,this);
                break;
            case WEEKLY:
                args = Utility.getDateRange(WEEKLY);
                getLoaderManager().restartLoader(HOME_EXPENSE_LOADER_ID,args,this);
                break;
            case DAILY:
                args = Utility.getDateRange(DAILY);
                getLoaderManager().restartLoader(HOME_EXPENSE_LOADER_ID,args,this);
                break;
        }
    }
    @Override
    public void onNothingSelected(AdapterView<?> parent){}


    /* START LoaderManager callback logic */
    String[] EXPENSE_LIST_PROJECTION = {
            "SUM("+MahanadiContract.Expense.COL_AMOUNT+")"+" AS TOTAL "
    };
    String[] BUDGET_LIST_PROJECTION = {
            MahanadiContract.Budget.COL_AMOUNT +" AS TOTAL "
    };

    @Override
    public Loader<Cursor> onCreateLoader(int loaderId, Bundle args) {
        Log.d(LOG_TAG, "Inside onCreateLoader");
        switch (loaderId) {
            case HOME_EXPENSE_LOADER_ID:
                String filterClause = MahanadiContract.Expense.COL_CREATED_ON + " BETWEEN ? AND ?";
                String [] filterArgs = {args.getString(START_TIME), args.getString(END_TIME)};
                return new CursorLoader(getActivity(),
                        MahanadiContract.Expense.CONTENT_URI,
                        EXPENSE_LIST_PROJECTION,               // List of columns to fetch
                        filterClause,                       // Filter clauses
                        filterArgs,                         // Filter args
                        null);                              // Sort order
            case HOME_TOTAL_LOADER_ID:
                return new CursorLoader(getActivity(),
                        MahanadiContract.Expense.CONTENT_URI,
                        EXPENSE_LIST_PROJECTION,               // List of columns to fetch
                        null,                       // Filter clauses
                        null,                         // Filter args
                        null);                              // Sort order
            case HOME_BUDGET_LOADER_ID:
                String budgetFilterClause = MahanadiContract.Budget.COL_CREATED_ON + " BETWEEN ? AND ?  AND "
                        +MahanadiContract.Budget.COL_END_DATE +" >= CURRENT_TIMESTAMP";
                String [] budgetFilterArgs = {args.getString(START_TIME), args.getString(END_TIME)};
                return new CursorLoader(getActivity(),
                        MahanadiContract.Budget.CONTENT_URI,
                        BUDGET_LIST_PROJECTION,               // List of columns to fetch
                        budgetFilterClause,                       // Filter clauses
                        budgetFilterArgs,                         // Filter args
                        null);                              // Sort order

            default:
                return null;
        }
    }
    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor dataCursor) {
        Log.d(LOG_TAG, "Inside onLoadFinished");
        int loaderId = loader.getId();
        Double totalAmount = 0.0;
        while (dataCursor.moveToNext()) {
            totalAmount = dataCursor.getDouble(dataCursor.getColumnIndex("TOTAL"));
        }
        switch (loaderId){
            case HOME_TOTAL_LOADER_ID:
                TextView grandTotalAmountTextView = (TextView)mHomeView.findViewById(R.id.grand_total_amount);
                grandTotalAmountTextView.setText(totalAmount.toString());
                break;
            case HOME_EXPENSE_LOADER_ID:
                TextView expenseAmountTextView = (TextView)mHomeView.findViewById(R.id.expense_amount);
                expenseAmountTextView.setText(totalAmount.toString());
                break;
            case HOME_BUDGET_LOADER_ID:
                //here total alias will refer to total budget for a given month.
                monthlyBudget = totalAmount;
                break;
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader){}

}
