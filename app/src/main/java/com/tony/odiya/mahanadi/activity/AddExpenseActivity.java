package com.tony.odiya.mahanadi.activity;

import android.app.Activity;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.tony.odiya.mahanadi.R;
import com.tony.odiya.mahanadi.contract.MahanadiContract;
import com.tony.odiya.mahanadi.utils.Utility;

import java.util.ArrayList;
import java.util.List;

import static com.tony.odiya.mahanadi.common.Constants.CLOTHES;
import static com.tony.odiya.mahanadi.common.Constants.CURRENT_BUDGET_PROJECTION;
import static com.tony.odiya.mahanadi.common.Constants.CUSTOM;
import static com.tony.odiya.mahanadi.common.Constants.ELECTRONICS;
import static com.tony.odiya.mahanadi.common.Constants.END_TIME;
import static com.tony.odiya.mahanadi.common.Constants.FOOD;
import static com.tony.odiya.mahanadi.common.Constants.GROCERY;
import static com.tony.odiya.mahanadi.common.Constants.HOME;
import static com.tony.odiya.mahanadi.common.Constants.MONTHLY;
import static com.tony.odiya.mahanadi.common.Constants.QUERY_BUDGET_CODE;
import static com.tony.odiya.mahanadi.common.Constants.SAVE_EXPENSE_CODE;
import static com.tony.odiya.mahanadi.common.Constants.START_TIME;
import static com.tony.odiya.mahanadi.common.Constants.UPDATE_BUDGET_CODE;


public class AddExpenseActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    private static final String LOG_TAG = AddExpenseActivity.class.getSimpleName();
    private Toolbar addExpenseToolbar;
    private Spinner mCategorySpinner;
    private String mCategory;
    private String mItem;
    private String mRemark;
    private Double mAmount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_expense);
        addExpenseToolbar = (Toolbar) findViewById(R.id.add_expense_toolbar);
        mCategorySpinner = (Spinner) findViewById(R.id.expense_category_spinner);
        mCategorySpinner.setOnItemSelectedListener(this);
        setCategoryValuesInSpinner(mCategorySpinner,getApplicationContext());
        selectDefaultCategoryInSpinner(mCategorySpinner, HOME);
        setSupportActionBar(addExpenseToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.add_expense_menu,menu);
        /*MenuItem saveOption = menu.findItem(R.id.action_save);
        Utility.colorMenuItem(saveOption,"white");*/
        return true;
    }

    /*@Override
    public boolean onPrepareOptionsMenu(Menu menu){
        //This code changes the menu item icon color
        Utility.colorMenuItem(menu, "white", 0); // menu , color, menu item index
        return true;
    }*/

    @Override
    public boolean onOptionsItemSelected(MenuItem item){

        switch (item.getItemId()) {
            case R.id.action_save:
                //User chose to save the details inserted.
                EditText editItem = (EditText)findViewById(R.id.edit_item);
                mItem = editItem.getText().toString();
                EditText editAmount = (EditText)findViewById(R.id.edit_amount);
                String sAmount = editAmount.getText().toString();

                EditText editRemark = (EditText)findViewById(R.id.edit_remark);
                mRemark = editRemark.getText().toString();
                if(mItem.equals("")||sAmount.equals("")){
                    Toast.makeText(this,"Item and Amount are mandatory.", Toast.LENGTH_SHORT).show();
                }
                else {
                    mAmount = Double.valueOf(sAmount);
                    // Save Category, item, amount and remark in database
                    ContentValues expenseDetails = new ContentValues();
                    expenseDetails.put(MahanadiContract.Expense.COL_CATEGORY,mCategory);
                    expenseDetails.put(MahanadiContract.Expense.COL_ITEM,mItem);
                    expenseDetails.put(MahanadiContract.Expense.COL_AMOUNT,mAmount);
                    expenseDetails.put(MahanadiContract.Expense.COL_REMARK,mRemark);
                    expenseDetails.put(MahanadiContract.Expense.COL_CREATED_ON,System.currentTimeMillis());
                    Uri insertUri = getContentResolver().insert(Uri.withAppendedPath(MahanadiContract.Expense.CONTENT_URI,SAVE_EXPENSE_CODE),expenseDetails);
                    Long rowId = ContentUris.parseId(insertUri);

                    // Update budget table
                    int rowsUpdatedCount = updateCurrentMonthBudgetRow();
                    // Return back to parent fragment after data has been saved into database.
                    setResult(Activity.RESULT_OK);
                    finish();
                }
                return true;
            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                // Useful for Up navigation in action bar.
                setResult(Activity.RESULT_CANCELED);
                finish();
                return super.onOptionsItemSelected(item);
        }
    }

    private void setCategoryValuesInSpinner(Spinner spinner, Context context){
        List<String> valueList = new ArrayList<>();
        valueList.add(GROCERY);
        valueList.add(ELECTRONICS);
        valueList.add(FOOD);
        valueList.add(HOME);
        valueList.add(CLOTHES);
        valueList.add(CUSTOM);
        ArrayAdapter<String> trendAdapter = new ArrayAdapter<>(context,
                android.R.layout.simple_spinner_item,
                valueList);
        trendAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(trendAdapter);
    }
    private void selectDefaultCategoryInSpinner(Spinner spinner, String category    ){
        ArrayAdapter<String> trendAdapter = (ArrayAdapter<String>) spinner.getAdapter();
        if(category!=null && !category.isEmpty()){
            int pos = trendAdapter.getPosition(category);
            spinner.setSelection(pos);
        }
    }
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id){
        mCategory = parent.getItemAtPosition(pos).toString();
    }
    @Override
    public void onNothingSelected(AdapterView<?> parent){}

    private int updateCurrentMonthBudgetRow(){
        String MONTHLY_BUDGET_FILTER = "datetime("+MahanadiContract.Budget.COL_END_DATE +"/1000,'unixepoch') >= datetime('now','unixepoch')";
        String filterClause = MahanadiContract.Expense.COL_CREATED_ON + " BETWEEN ? AND ?";
        Bundle args = Utility.getDateRange(MONTHLY);
        String [] budgetFilterArgs = {args.getString(START_TIME), args.getString(END_TIME)};
        Cursor cursor = getContentResolver().query(Uri.withAppendedPath(MahanadiContract.Budget.CONTENT_URI,QUERY_BUDGET_CODE)
                            ,CURRENT_BUDGET_PROJECTION
                            ,DatabaseUtils.concatenateWhere(filterClause,MONTHLY_BUDGET_FILTER)
                            ,budgetFilterArgs
                            ,null);
        int updateCount = -1;
        Log.d(LOG_TAG, "Row count of cursor for budget updation is "+cursor.getCount());
        if(cursor.getCount()>0) {
            //If budget has been set for the current month only then update budget.
            ContentValues budgetDetails = new ContentValues();
            Double budgetAmount = 0.0;
            //Long budgetRowId = -1l;
            // cursor.moveToFirst();
            while (cursor.moveToNext()) {
                //budgetRowId = cursor.getLong(cursor.getColumnIndex(MahanadiContract.Budget._ID));
                budgetAmount = cursor.getDouble(cursor.getColumnIndex("BUDGET"));
            }
            budgetDetails.put(MahanadiContract.Budget.COL_AMOUNT, Double.toString(budgetAmount-mAmount));
            Log.d(LOG_TAG,"Budget amount after updation : "+Double.toString(budgetAmount-mAmount));
            /*if((budgetAmount - mAmount)>=0){
                //
                budgetDetails.put(MahanadiContract.Budget.COL_AMOUNT, Double.toString(budgetAmount-mAmount));
                Log.d(LOG_TAG,"Budget amount after updation : "+Double.toString(budgetAmount-mAmount));
            }
            else{
                //When expense exceeds left out budget amount, set budget to zero.
                budgetDetails.put(MahanadiContract.Budget.COL_AMOUNT, Double.toString(0.0));
                Log.d(LOG_TAG,"Budget amount (Expense exceeds budget amount) after updation : "+0);
            }*/
            updateCount = getContentResolver().update(Uri.withAppendedPath(MahanadiContract.Budget.CONTENT_URI, UPDATE_BUDGET_CODE)
                            ,budgetDetails
                            ,DatabaseUtils.concatenateWhere(filterClause,MONTHLY_BUDGET_FILTER)
                            ,budgetFilterArgs);
            Log.d(LOG_TAG, "Budget update count : "+ updateCount);
        }
        return updateCount;
    }

}
