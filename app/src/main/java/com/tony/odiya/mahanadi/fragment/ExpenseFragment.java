package com.tony.odiya.mahanadi.fragment;

import android.app.Activity;
import android.app.LoaderManager;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;

import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import android.widget.Toast;

import com.tony.odiya.mahanadi.R;
import com.tony.odiya.mahanadi.activity.AddExpenseActivity;
import com.tony.odiya.mahanadi.contract.MahanadiContract;
import com.tony.odiya.mahanadi.model.ExpenseData;
import com.tony.odiya.mahanadi.utils.Utility;


import java.util.ArrayList;
import java.util.List;

import static android.support.design.R.attr.alpha;
import static com.tony.odiya.mahanadi.common.Constants.DAILY;
import static com.tony.odiya.mahanadi.common.Constants.END_TIME;
import static com.tony.odiya.mahanadi.common.Constants.MONTHLY;
import static com.tony.odiya.mahanadi.common.Constants.START_TIME;
import static com.tony.odiya.mahanadi.common.Constants.WEEKLY;
import static com.tony.odiya.mahanadi.common.Constants.YEARLY;
import static com.tony.odiya.mahanadi.common.Constants.REQUEST_EXPENSE_CODE;


/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnListFragmentInteractionListener}
 * interface.
 */
public class ExpenseFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>, AdapterView.OnItemSelectedListener {
    private static final String LOG_TAG = ExpenseFragment.class.getSimpleName();
    //private static final String ARG_COLUMN_COUNT = "column_count";
    private static final String ARG_TREND = "trend";

    //private int mColumnCount = 1;
    private String mTrend;

    private OnListFragmentInteractionListener mListener;
    private List<ExpenseData> mExpenseList = new ArrayList<>();
    private MyExpenseRecyclerViewAdapter myExpenseRecyclerViewAdapter;
    private Spinner expenseTrendSpinner;
    private Toolbar expenseToolbar;

    private static final int EXPENSE_LOADER_ID = 3;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public ExpenseFragment() {
    }


    public static ExpenseFragment newInstance(String trend) {
        ExpenseFragment fragment = new ExpenseFragment();
        Bundle args = new Bundle();
        //args.putInt(ARG_COLUMN_COUNT, columnCount);
        args.putString(ARG_TREND, trend);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            //mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
            mTrend = getArguments().getString(ARG_TREND);
        }
        // Set the filter arguments for query here
        Bundle args = Utility.getDateRange(mTrend);
        // Load data into cursor asynchronously.
        getLoaderManager().initLoader(EXPENSE_LOADER_ID, args, this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_expense_list, container, false);
        View recycler = view.findViewById(R.id.expense_recycler_list);
        expenseToolbar = (Toolbar)view.findViewById(R.id.expense_toolbar);
        expenseTrendSpinner = (Spinner) view.findViewById(R.id.expense_trend_spinner);
        expenseTrendSpinner.setOnItemSelectedListener(this);
        Utility.setTrendValuesInSpinner(expenseTrendSpinner, getActivity().getApplicationContext());
        Utility.selectDefaultTrendInSpinner(expenseTrendSpinner, mTrend);
        //Set the toolbar
        ((AppCompatActivity)getActivity()).setSupportActionBar(expenseToolbar);
        // Set Options menu as true
        setHasOptionsMenu(true);
        // Set the adapter
        if (recycler instanceof RecyclerView) {
            Context context = view.getContext();
            RecyclerView recyclerView = (RecyclerView) recycler;
            recyclerView.setLayoutManager(new LinearLayoutManager(context));
            /*if (mColumnCount <= 1) {
                recyclerView.setLayoutManager(new LinearLayoutManager(context));
            } else {
                recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
            }*/
            myExpenseRecyclerViewAdapter = new MyExpenseRecyclerViewAdapter(mExpenseList, mListener);
            recyclerView.setAdapter(myExpenseRecyclerViewAdapter);
        }
        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu,MenuInflater inflater){
        super.onCreateOptionsMenu(menu,inflater);
        inflater.inflate(R.menu.expense_menu,menu);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu){
        //This code changes the menu item icon color
        Utility.colorMenuItem(menu, "white", 0); // menu , color, menu item index
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_add:
                Intent intent = new Intent(getActivity(), AddExpenseActivity.class);
                startActivityForResult(intent,REQUEST_EXPENSE_CODE);
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode,resultCode,data);
        if(requestCode == REQUEST_EXPENSE_CODE && resultCode == Activity.RESULT_OK){
            // Data has been saved into database.
            // Reload the total data for given  trend time as well as total.
            Bundle dateRangeArgs = Utility.getDateRange(mTrend);
            getLoaderManager().restartLoader(EXPENSE_LOADER_ID,dateRangeArgs,this);

        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnListFragmentInteractionListener) {
            mListener = (OnListFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnListFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }


    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnListFragmentInteractionListener {
        void onListFragmentInteraction(ExpenseData item);
        void onExpenseTrendInteraction(String trend);
    }

    /* START LoaderManager callback logic */
    String[] LIST_PROJECTION = {
            MahanadiContract.Expense._ID,
            MahanadiContract.Expense.COL_CATEGORY,
            MahanadiContract.Expense.COL_ITEM,
            MahanadiContract.Expense.COL_AMOUNT,
            MahanadiContract.Expense.COL_REMARK,
            MahanadiContract.Expense.COL_CREATED_ON
    };

    @Override
    public Loader<Cursor> onCreateLoader(int loaderId, Bundle args) {
        Log.d(LOG_TAG, "Inside onCreateLoader");
        switch (loaderId) {
            case EXPENSE_LOADER_ID:
                String filterClause = MahanadiContract.Expense.COL_CREATED_ON + " BETWEEN ? AND ?";
                String [] filterArgs = {args.getString(START_TIME), args.getString(END_TIME)};
                return new CursorLoader(getActivity(),
                        MahanadiContract.Expense.CONTENT_URI,
                        LIST_PROJECTION,               // List of columns to fetch
                        filterClause,                       // Filter clauses
                        filterArgs,                         // Filter args
                        null);                              // Sort order
            default:
                return null;

        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor dataCursor) {
        Log.d(LOG_TAG, "Inside onLoadFinished");
        if (mExpenseList == null) {
            mExpenseList = new ArrayList<>(0);
        }
        else{
            mExpenseList.clear();
        }
        while (dataCursor.moveToNext()) {
            ExpenseData s = new ExpenseData();
            s.setExpenseId(dataCursor.getString(dataCursor.getColumnIndex(MahanadiContract.Expense._ID)));
            s.setCategory(dataCursor.getString(dataCursor.getColumnIndex(MahanadiContract.Expense.COL_CATEGORY)));
            s.setItem(dataCursor.getString(dataCursor.getColumnIndex(MahanadiContract.Expense.COL_ITEM)));
            s.setAmount(dataCursor.getString(dataCursor.getColumnIndex(MahanadiContract.Expense.COL_AMOUNT)));
            s.setRemark(dataCursor.getString(dataCursor.getColumnIndex(MahanadiContract.Expense.COL_REMARK)));
            // Convert milliseconds to date string
            Long milliSeconds = dataCursor.getLong(dataCursor.getColumnIndex(MahanadiContract.Expense.COL_CREATED_ON));
            String sDate = Utility.convertMillisecondsToDateString(getActivity().getApplicationContext(), milliSeconds);
            s.setCreatedOn(sDate);
            mExpenseList.add(s);
        }
        if(myExpenseRecyclerViewAdapter!=null){
            myExpenseRecyclerViewAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mExpenseList = null;
    }
    /* END LoaderManager callbacklogic */

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
        String trend = parent.getItemAtPosition(pos).toString();
        if(null != mListener){
            mListener.onExpenseTrendInteraction(trend);
        }
        Bundle args;
        switch (trend) {
            case YEARLY:
                args = Utility.getDateRange(YEARLY);
                // restart loader with new set of arguments.
                getLoaderManager().restartLoader(EXPENSE_LOADER_ID,args,this);
                break;
            case MONTHLY:
                args = Utility.getDateRange(MONTHLY);
                getLoaderManager().restartLoader(EXPENSE_LOADER_ID,args,this);
                break;
            case WEEKLY:
                args = Utility.getDateRange(WEEKLY);
                getLoaderManager().restartLoader(EXPENSE_LOADER_ID,args,this);
                break;
            case DAILY:
                args = Utility.getDateRange(DAILY);
                getLoaderManager().restartLoader(EXPENSE_LOADER_ID,args,this);
                break;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent){}

}
