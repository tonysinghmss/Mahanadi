package com.tony.odiya.mahanadi.fragment;

import android.app.Activity;
import android.app.LoaderManager;
import android.app.SearchManager;
import android.app.SearchableInfo;
import android.content.ContentValues;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;

import android.database.DatabaseUtils;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
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

import com.tony.odiya.mahanadi.R;
import com.tony.odiya.mahanadi.activity.AddExpenseActivity;
import com.tony.odiya.mahanadi.activity.ExpenseDetailActivity;
import com.tony.odiya.mahanadi.adapter.ExpenseDataset;
import com.tony.odiya.mahanadi.adapter.MyExpenseRecyclerViewAdapter;
import com.tony.odiya.mahanadi.contract.MahanadiContract;
import com.tony.odiya.mahanadi.model.ExpenseData;
import com.tony.odiya.mahanadi.utils.Utility;


import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.tony.odiya.mahanadi.common.Constants.CURRENT_BUDGET_PROJECTION;
import static com.tony.odiya.mahanadi.common.Constants.DAILY;
import static com.tony.odiya.mahanadi.common.Constants.END_TIME;
import static com.tony.odiya.mahanadi.common.Constants.EXPENSE_ID;
import static com.tony.odiya.mahanadi.common.Constants.MONTHLY;
import static com.tony.odiya.mahanadi.common.Constants.QUERY_BUDGET_CODE;
import static com.tony.odiya.mahanadi.common.Constants.REQUEST_EXPENSE_EDIT_CODE;
import static com.tony.odiya.mahanadi.common.Constants.START_TIME;
import static com.tony.odiya.mahanadi.common.Constants.UPDATE_BUDGET_CODE;
import static com.tony.odiya.mahanadi.common.Constants.WEEKLY;
import static com.tony.odiya.mahanadi.common.Constants.YEARLY;
import static com.tony.odiya.mahanadi.common.Constants.REQUEST_EXPENSE_ADD_CODE;


/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnListFragmentInteractionListener}
 * interface.
 */
public class ExpenseFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>
        , AdapterView.OnItemSelectedListener, MyExpenseRecyclerViewAdapter.OnRecyclerItemClickedListener
        , ActionMode.Callback, SearchView.OnQueryTextListener {
    private static final String LOG_TAG = ExpenseFragment.class.getSimpleName();
    //private static final String ARG_COLUMN_COUNT = "column_count";
    private static final String ARG_TREND = "trend";

    //private int mColumnCount = 1;
    private String mTrend;

    private OnListFragmentInteractionListener mListener;
    private Set<ExpenseData> mExpenseSet = new HashSet<>(0);
    private MyExpenseRecyclerViewAdapter myExpenseRecyclerViewAdapter;
    private ExpenseDataset mExpenseDataset;
    // private MyExpenseRecyclerViewAdapter.OnRecyclerItemClickedListener  expenseItemLongClickListener;
    private RecyclerView mRecyclerView;
    private Spinner expenseTrendSpinner;
    private Toolbar expenseToolbar;


    private static final int EXPENSE_LOADER_ID = 21;

    private ActionMode mActionMode;
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
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            //mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
            mTrend = getArguments().getString(ARG_TREND);
        }
        // Set the filter arguments for query here
        // Bundle args = Utility.getDateRange(mTrend);
        // Load data into cursor asynchronously.
        // getLoaderManager().initLoader(EXPENSE_LOADER_ID, args, this);
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
            mRecyclerView = (RecyclerView) recycler;
            mRecyclerView.setLayoutManager(new LinearLayoutManager(context));

            myExpenseRecyclerViewAdapter = new MyExpenseRecyclerViewAdapter(mListener, this);
            mExpenseDataset = new ExpenseDataset(mRecyclerView, myExpenseRecyclerViewAdapter);
            myExpenseRecyclerViewAdapter.dataset(mExpenseDataset);
            //myExpenseRecyclerViewAdapter = new MyExpenseRecyclerViewAdapter(mExpenseSet, mListener);
            mRecyclerView.setAdapter(myExpenseRecyclerViewAdapter);
        }
        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu,MenuInflater inflater){
        super.onCreateOptionsMenu(menu,inflater);
        inflater.inflate(R.menu.expense_menu,menu);
        // Associate searchable configuration with the SearchView
        SearchManager searchManager =
                (SearchManager)getActivity().getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView =
                (SearchView) menu.findItem(R.id.action_search).getActionView();
        SearchableInfo searchableInfo = searchManager.getSearchableInfo(getActivity().getComponentName());
        searchView.setSearchableInfo(searchableInfo);
        //Set Query text listener
        searchView.setOnQueryTextListener(this);
        //Change the color of menu items
        MenuItem addOption = menu.findItem(R.id.action_add);
        MenuItem searchOption = menu.findItem(R.id.action_search);
        Utility.colorMenuItem(searchOption,"white");
        Utility.colorMenuItem(addOption,"white");
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu){
        //This code changes the menu item icon color
//        Utility.colorMenuItem(menu, "white", 0); // menu , color, menu item index
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_add:
                Intent intent = new Intent(getActivity(), AddExpenseActivity.class);
                startActivityForResult(intent, REQUEST_EXPENSE_ADD_CODE);
                break;
            case R.id.action_search:
                Log.d(LOG_TAG, " Inside search option.");
                break;



        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode,resultCode,data);
        if(requestCode == REQUEST_EXPENSE_ADD_CODE && resultCode == Activity.RESULT_OK){
            // Data has been saved into database.
            // Reload the total data for given  trend time as well as total.
            Bundle dateRangeArgs = Utility.getDateRange(mTrend);
            getLoaderManager().restartLoader(EXPENSE_LOADER_ID,dateRangeArgs,this);

        }
        if(requestCode == REQUEST_EXPENSE_EDIT_CODE && resultCode == Activity.RESULT_OK) {
            Bundle dateRangeArgs = Utility.getDateRange(mTrend);
            getLoaderManager().restartLoader(EXPENSE_LOADER_ID,dateRangeArgs,this);
        }
    }



    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
        // Clear action mode when fragment gets detached
        if(mActionMode != null){
            mActionMode.finish();
        }
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
        /*if (mExpenseSet == null) {
            mExpenseSet = new ArrayList<>(0);
        }
        else{
            mExpenseSet.clear();
        }*/
        if(!mExpenseSet.isEmpty()){
            Log.d(LOG_TAG, "Clear expense list.");
            mExpenseSet.clear();
        }
        Log.d(LOG_TAG, "Count of rows selected : "+dataCursor.getCount());
        Log.d(LOG_TAG, "Expenses selected are : ");
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
            Log.d(LOG_TAG, s.toString());
            mExpenseSet.add(s);
        }
        mExpenseDataset.replaceAll(mExpenseSet);
        //Clear all data from the set.
        // mExpenseSet.clear();
        /*if(myExpenseRecyclerViewAdapter!=null){
            myExpenseRecyclerViewAdapter.notifyDataSetChanged();
        }*/
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mExpenseSet = null;
    }
    /* END LoaderManager callbacklogic */

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
        Log.d(LOG_TAG, "Trend spinner onItemSelected");
        String trend = parent.getItemAtPosition(pos).toString();
        Log.i(LOG_TAG, "Trend selected :"+trend);
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

   /* @Override
    public void onItemLongClicked(int position){
        DeleteExpenseDialogFragment deleteExpenseDialog = new DeleteExpenseDialogFragment();
        deleteExpenseDialog.show(getChildFragmentManager(),"DeleteExpenseDialog");
        // deleteExpenseDialog.setTargetFragment(this, REQUEST_DELETE_EXPENSE_CODE);
        //return true;
    }*/
/*
    public void onDialogInteraction(Bundle args){
        int requestCode = (int) args.get("REQUEST_CODE");
        int status = (int) args.get("STATUS");
        if(requestCode == REQUEST_DELETE_EXPENSE_CODE && status == RESPONSE_OK){

        }
    }*/

    /*
        Overridden methods of RecyclerViewAdapter
     */
    public void onItemClicked(int position){
        Log.d(LOG_TAG, "You have clicked on "+position);
        if(mActionMode != null){
            // Toggle selection highlight.
            toggleSelection(position);
        }
        else{
            // Open the activity to show details of a particular expense.
            Bundle expenseDataId = findExpenseDataId(position);
            Intent intent = new Intent(getActivity(), ExpenseDetailActivity.class);
            intent.putExtras(expenseDataId);
            startActivityForResult(intent, REQUEST_EXPENSE_EDIT_CODE);
        }
    }

    public boolean onItemLongClicked(int position){
        Log.d(LOG_TAG, "You have long clicked on "+position);
        // Start ActionMode
        if (mActionMode != null) {
            return false;
        }
        // Start the CAB using the ActionMode.Callback defined above
        mActionMode = ((AppCompatActivity)getActivity()).startSupportActionMode(this);
        mActionMode.setTitle(String.valueOf(1));
        myExpenseRecyclerViewAdapter.toggleSelection(position);
        return true;
    }

    public int updateCurrentMonthBudgetRow(List<String> expenseIdList){
        Log.d(LOG_TAG, "updateCurrentMonthBudgetRow");
        // Step 1: Delete the rows from expense table.
        int deleteCount = deleteExpenses(expenseIdList);
        // Step 2: Update the budget row of current month.
        String monthlyBudgetFilter = "datetime("+MahanadiContract.Budget.COL_END_DATE +"/1000,'unixepoch') >= datetime('now','unixepoch')";
        String filterClause = MahanadiContract.Budget.COL_CREATED_ON + " BETWEEN ? AND ?";
        int updateCount = 0;
        Double existingBudgetForMonth = findCurrentBudgetAmount();
        if(deleteCount>0){
            Bundle args = Utility.getDateRange(MONTHLY);
            String [] budgetFilterArgs = {args.getString(START_TIME), args.getString(END_TIME)};
            ContentValues budgetDetails = new ContentValues();
            Double totalBudget = mExpenseDataset.getTotalExpenseAmount()+ existingBudgetForMonth;
            budgetDetails.put(MahanadiContract.Budget.COL_AMOUNT, Double.toString(totalBudget));
            updateCount = getActivity().getContentResolver().update(Uri.withAppendedPath(MahanadiContract.Budget.CONTENT_URI, UPDATE_BUDGET_CODE)
                    ,budgetDetails
                    ,DatabaseUtils.concatenateWhere(filterClause,monthlyBudgetFilter)
                    ,budgetFilterArgs);
        }
        return  updateCount;
    }

    private void toggleSelection(int position){
        Log.d(LOG_TAG, "toggleSelection");
        myExpenseRecyclerViewAdapter.toggleSelection(position);
        int count = myExpenseRecyclerViewAdapter.getSelectedItemCount();
        if(count == 0){
            mActionMode.finish();
        }
        else {
            mActionMode.setTitle(String.valueOf(count));
            mActionMode.invalidate();
        }

    }

    private Bundle findExpenseDataId(int position){
        ExpenseData expense = mExpenseDataset.getSortedList().get(position);
        Bundle args = new Bundle();
        args.putString(EXPENSE_ID, expense.getExpenseId());
        return args;
    }

    /**
     *  ActionMode callbacks
     */
    // Called when the action mode is created; startActionMode() was called
    @Override
    public boolean onCreateActionMode(ActionMode mode, Menu menu) {
        Log.d(LOG_TAG, "onCreateActionMode");
        // Inflate a menu resource providing context menu items
        MenuInflater inflater = mode.getMenuInflater();
        inflater.inflate(R.menu.expense_action_mode_menu, menu);
        /*MenuItem deleteOption =  menu.findItem(R.id.action_delete);
        Utility.colorMenuItem(deleteOption,"white");*/
        return true;
    }

    // Called each time the action mode is shown. Always called after onCreateActionMode, but
    // may be called multiple times if the mode is invalidated.
    @Override
    public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
        return false; // Return false if nothing is done
    }
    // Called when the user selects a contextual menu item
    @Override
    public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
        Log.d(LOG_TAG, "onActionItemClicked");
        switch (item.getItemId()) {
            case R.id.action_delete:
                //remove Selected Items from dataset
                Log.d(LOG_TAG, "Before removal : "+myExpenseRecyclerViewAdapter.getSelectedItems().toString());
                mExpenseDataset.removeItems(myExpenseRecyclerViewAdapter.getSelectedItems());

                int updateCount = updateCurrentMonthBudgetRow(mExpenseDataset.getExpenseDataIdList());
                Log.d(LOG_TAG, "Update count : "+updateCount);
                if(updateCount>0) {
                    mode.finish(); // Action picked, so close the Contextual ActionBar
                    return true;
                }
                return false;
            default:
                return false;
        }
    }
    // Called when the user exits the action mode
    @Override
    public void onDestroyActionMode(ActionMode mode) {
        Log.d(LOG_TAG, "onDestroyActionMode");
        myExpenseRecyclerViewAdapter.clearSelection();
        mActionMode = null;
    }

    private int deleteExpenses(List<String> expenseIds){
        Log.d(LOG_TAG, "deleteExpenses");
        StringBuilder where = new StringBuilder(MahanadiContract.Expense._ID + " IN (");
        String delim = "";
        for(String id : expenseIds){
            where.append(delim).append(id);
            delim = ",";
        }
        where.append(")");
        int deleteCount = getActivity().getContentResolver().delete(MahanadiContract.Expense.CONTENT_URI, where.toString(), null);
        return deleteCount;
    }

    private Double findCurrentBudgetAmount(){
        Log.d(LOG_TAG, "findCurrentBudgetAmount");
        String MONTHLY_BUDGET_FILTER = "datetime("+MahanadiContract.Budget.COL_END_DATE +"/1000,'unixepoch') >= datetime('now','unixepoch')";
        String filterClause = MahanadiContract.Budget.COL_CREATED_ON + " BETWEEN ? AND ?";
        Bundle args = Utility.getDateRange(MONTHLY);
        String [] budgetFilterArgs = {args.getString(START_TIME), args.getString(END_TIME)};
        Cursor cursor = getActivity().getContentResolver().query(Uri.withAppendedPath(MahanadiContract.Budget.CONTENT_URI,QUERY_BUDGET_CODE)
                ,CURRENT_BUDGET_PROJECTION
                ,DatabaseUtils.concatenateWhere(filterClause,MONTHLY_BUDGET_FILTER)
                ,budgetFilterArgs
                ,null);
        Double budgetAmount = 0.0;
        if(cursor.getCount()>0) {
            while (cursor.moveToNext()) {
                //budgetRowId = cursor.getLong(cursor.getColumnIndex(MahanadiContract.Budget._ID));
                budgetAmount = cursor.getDouble(cursor.getColumnIndex("BUDGET"));
            }
        }
        return budgetAmount;
    }
    // CALLBACKS FOR SEARCH VIEW
    @Override
    public boolean onQueryTextChange(String query) {
        Log.d(LOG_TAG, "onQueryTextChange");
        // Here is where we are going to implement the filter logic
        final Set<ExpenseData> filteredModelList = filter(query);
        mExpenseDataset.replaceAll(filteredModelList);
        //mRecyclerView.scrollToPosition(0);
        return true;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    private Set<ExpenseData> filter(String query) {
        Log.d(LOG_TAG, "filter");
        List<ExpenseData> models = new ArrayList<>(mExpenseSet);
        final String lowerCaseQuery = query.toLowerCase();
        final Set<ExpenseData> expenseDataHashSet = new HashSet<>(0);
        if(query.length() == 0){
            expenseDataHashSet.addAll(models);
        }
        else {
            for (ExpenseData expense : models) {
                final String itemText = expense.getItem().toLowerCase();
                final String amountText = expense.getAmount().toLowerCase();
                final String remarkText = expense.getRemark().toLowerCase();
                final String categoryText = expense.getCategory().toLowerCase();

                if (itemText.contains(lowerCaseQuery)) {
                    expenseDataHashSet.add(expense);
                }
                if (amountText.contains(lowerCaseQuery)) {
                    expenseDataHashSet.add(expense);
                }
                if (remarkText.contains(lowerCaseQuery)) {
                    expenseDataHashSet.add(expense);
                }
                if (categoryText.contains(lowerCaseQuery)) {
                    expenseDataHashSet.add(expense);
                }
            }
        }
        return expenseDataHashSet;
    }
}
