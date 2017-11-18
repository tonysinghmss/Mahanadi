package com.tony.odiya.mahanadi.activity;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
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
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.tony.odiya.mahanadi.R;
import com.tony.odiya.mahanadi.contract.MahanadiContract;
import com.tony.odiya.mahanadi.utils.Utility;

import static com.tony.odiya.mahanadi.common.Constants.CURRENT_BUDGET_PROJECTION;
import static com.tony.odiya.mahanadi.common.Constants.END_TIME;
import static com.tony.odiya.mahanadi.common.Constants.EXPENSE_ID;
import static com.tony.odiya.mahanadi.common.Constants.HOME;
import static com.tony.odiya.mahanadi.common.Constants.MONTHLY;
import static com.tony.odiya.mahanadi.common.Constants.QUERY_BUDGET_CODE;
import static com.tony.odiya.mahanadi.common.Constants.QUERY_EXPENSE_CODE;
import static com.tony.odiya.mahanadi.common.Constants.START_TIME;
import static com.tony.odiya.mahanadi.common.Constants.UPDATE_BUDGET_CODE;
import static com.tony.odiya.mahanadi.common.Constants.UPDATE_EXPENSE_CODE;

public class ExpenseDetailActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener{
    private static final String LOG_TAG = ExpenseDetailActivity.class.getSimpleName();
    private Toolbar expenseDetailToolbar;
    //private Spinner mCategorySpinner;
    private TextView mCategoryTextView;
    private TextView mItemTextView;
    private TextView mRemarkTextView;
    private TextView mAmountTextView;

    private EditText mCategoryEditText;
    private EditText mItemEditText;
    private EditText mRemarkEditText;
    private EditText mAmountEditText;
    private String mExpenseId;
    private String mCategory;
    private String mItem;
    private String mRemark;
    private String mAmount;

    private Boolean editFlag = Boolean.FALSE;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expense_detail);
        expenseDetailToolbar = (Toolbar) findViewById(R.id.expense_detail_toolbar);
        setSupportActionBar(expenseDetailToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        /*mCategorySpinner = (Spinner) findViewById(R.id.expense_detail_edit_category_spinner);
        mCategorySpinner.setOnItemSelectedListener(this);
        Utility.setCategoryValuesInSpinner(mCategorySpinner,getApplicationContext());*/
        Intent intent = getIntent();
        mExpenseId = intent.getStringExtra(EXPENSE_ID);
        // Initialize views
        mCategoryTextView = (TextView) findViewById(R.id.expense_detail_category);
        mItemTextView = (TextView) findViewById(R.id.expense_detail_item);
        mRemarkTextView = (TextView) findViewById(R.id.expense_detail_remark);
        mAmountTextView = (TextView) findViewById(R.id.expense_detail_amount);
        mCategoryEditText = (EditText) findViewById(R.id.expense_detail_edit_category);
        mItemEditText = (EditText) findViewById(R.id.expense_detail_edit_item);
        mRemarkEditText = (EditText)findViewById(R.id.expense_detail_edit_remark);
        mAmountEditText = (EditText)findViewById(R.id.expense_detail_edit_amount);

    }

    @Override
    protected void onResume() {
        super.onResume();
        String[] projections = {
                MahanadiContract.Expense._ID,
                MahanadiContract.Expense.COL_CATEGORY,
                MahanadiContract.Expense.COL_ITEM,
                MahanadiContract.Expense.COL_AMOUNT,
                MahanadiContract.Expense.COL_REMARK,
                MahanadiContract.Expense.COL_CREATED_ON
        };
        String where = MahanadiContract.Expense._ID + " = ? ";
        String[] whereArgs = {mExpenseId};
        Cursor c =  getContentResolver().query(Uri.withAppendedPath(MahanadiContract.Expense.CONTENT_URI,QUERY_EXPENSE_CODE), projections, where, whereArgs, null);
        Log.d(LOG_TAG, "Record fetched from database : "+c.getCount()+".");
        while (c.moveToNext()){
            mCategory = c.getString(c.getColumnIndex(MahanadiContract.Expense.COL_CATEGORY));
            mItem = c.getString(c.getColumnIndex(MahanadiContract.Expense.COL_ITEM));
            mRemark = c.getString(c.getColumnIndex(MahanadiContract.Expense.COL_REMARK));
            mAmount = c.getString(c.getColumnIndex(MahanadiContract.Expense.COL_AMOUNT));
        }
        setVisibilityOfViews();
        setValuesOfView();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.expense_detail_menu,menu);

        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if(editFlag){
            menu.findItem(R.id.action_save_expense).setVisible(true);
            menu.findItem(R.id.action_edit_expense).setVisible(false);
        }
        else{
            menu.findItem(R.id.action_save_expense).setVisible(false);
            menu.findItem(R.id.action_edit_expense).setVisible(true);
        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()) {
            case R.id.action_edit_expense:
                editFlag = Boolean.TRUE;
                setVisibilityOfViews();
                setValuesOfView();
                invalidateOptionsMenu();
                return true;
            case R.id.action_save_expense:

                editFlag = Boolean.FALSE;
                String sCategory = mCategoryEditText.getText().toString();
                String sAmount = mAmountEditText.getText().toString();
                String sRemark = mRemarkEditText.getText().toString();
                String sItem = mItemEditText.getText().toString();
                if(mItem.equals("")||sAmount.equals("")){
                    Toast.makeText(this,"Item and Amount are mandatory.", Toast.LENGTH_SHORT).show();
                }
                else{
                    Double dAmount = Double.valueOf(sAmount);
                    ContentValues expenseDetails = new ContentValues();
                    expenseDetails.put(MahanadiContract.Expense.COL_CATEGORY,sCategory);
                    expenseDetails.put(MahanadiContract.Expense.COL_ITEM,sItem);
                    expenseDetails.put(MahanadiContract.Expense.COL_AMOUNT,dAmount);
                    expenseDetails.put(MahanadiContract.Expense.COL_REMARK,sRemark);
                    String where = MahanadiContract.Expense._ID + " = ? ";
                    String[] whereArgs = {mExpenseId};
                    // Update expense
                    int updateExpenseCount = getContentResolver().update(Uri.withAppendedPath(MahanadiContract.Expense.CONTENT_URI,UPDATE_EXPENSE_CODE), expenseDetails,
                            where, whereArgs);
                    // Update budget
                    if(updateExpenseCount>0){
                       Double budgetDiff  = dAmount - Double.valueOf(mAmount);
                        int updateBudgetCount = updateCurrentMonthBudgetRow(budgetDiff);
                        if(updateBudgetCount > 0){
                            setResult(Activity.RESULT_OK);
                            finish();
                        }
                    }
                }
                // Save the changes in database and return back.
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

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id){
        mCategory = parent.getItemAtPosition(pos).toString();
    }
    @Override
    public void onNothingSelected(AdapterView<?> parent){}

    private void setVisibilityOfViews(){
        if(editFlag) {
            mCategoryTextView.setVisibility(View.GONE);
            mItemTextView.setVisibility(View.GONE);
            mRemarkTextView.setVisibility(View.GONE);
            mAmountTextView.setVisibility(View.GONE);

            //mCategorySpinner.setVisibility(View.VISIBLE);
            mCategoryEditText.setVisibility(View.VISIBLE);
            mItemEditText.setVisibility(View.VISIBLE);
            mRemarkEditText.setVisibility(View.VISIBLE);
            mAmountEditText.setVisibility(View.VISIBLE);
        }
        else{
            mCategoryTextView.setVisibility(View.VISIBLE);
            mItemTextView.setVisibility(View.VISIBLE);
            mRemarkTextView.setVisibility(View.VISIBLE);
            mAmountTextView.setVisibility(View.VISIBLE);

            //mCategorySpinner.setVisibility(View.GONE);
            mCategoryEditText.setVisibility(View.GONE);
            mItemEditText.setVisibility(View.GONE);
            mRemarkEditText.setVisibility(View.GONE);
            mAmountEditText.setVisibility(View.GONE);
        }
    }

    private void setValuesOfView(){
        if(editFlag) {
            /*if(mCategory!=null && !mCategory.isEmpty()) {
                Utility.selectDefaultCategoryInSpinner(mCategorySpinner, mCategory);
            }else {
                Utility.selectDefaultCategoryInSpinner(mCategorySpinner, HOME);
            }*/
            if(mCategory!=null){
                mCategoryEditText.setText(mCategory);
            }
            else{
                mCategoryEditText.setText("");
            }
            if(mItem !=null && mRemark != null && mAmount!=null ){
                mItemEditText.setText(mItem);
                mRemarkEditText.setText(mRemark);
                mAmountEditText.setText(mAmount);
            }
            else {
                Log.i(LOG_TAG, "Item or remark or amount or all of them didn't load from database properly.");
                mItemEditText.setText("");
                mRemarkEditText.setText("");
                mAmountEditText.setText("0.0");
            }
        }
        else {
            if(mCategory!=null && mItem !=null && mRemark != null && mAmount!=null ) {
                mCategoryTextView.setText(mCategory);
                mItemTextView.setText(mItem);
                mRemarkTextView.setText(mRemark);
                mAmountTextView.setText(mAmount);
            }
            else{
                Log.i(LOG_TAG, "Item or remark or amount or all of them didn't load from database properly.");
                Toast.makeText(this,"Item details didn't load properly !!!", Toast.LENGTH_SHORT).show();
                mCategoryTextView.setText("");
                mItemTextView.setText("");
                mRemarkTextView.setText("");
                mAmountTextView.setText("0.0");
            }
        }
    }

    private int updateCurrentMonthBudgetRow(Double difference) {
        String MONTHLY_BUDGET_FILTER = "datetime(" + MahanadiContract.Budget.COL_END_DATE + "/1000,'unixepoch') >= datetime('now','unixepoch')";
        String filterClause = MahanadiContract.Expense.COL_CREATED_ON + " BETWEEN ? AND ?";
        Bundle args = Utility.getDateRange(MONTHLY);
        String[] budgetFilterArgs = {args.getString(START_TIME), args.getString(END_TIME)};
        Cursor cursor = getContentResolver().query(Uri.withAppendedPath(MahanadiContract.Budget.CONTENT_URI, QUERY_BUDGET_CODE)
                , CURRENT_BUDGET_PROJECTION
                , DatabaseUtils.concatenateWhere(filterClause, MONTHLY_BUDGET_FILTER)
                , budgetFilterArgs
                , null);
        int updateCount = -1;
        Log.d(LOG_TAG, "Row count of cursor for budget updation is " + cursor.getCount());
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
            budgetDetails.put(MahanadiContract.Budget.COL_AMOUNT, Double.toString(budgetAmount - difference));
            Log.d(LOG_TAG, "Budget amount after updation : " + Double.toString(budgetAmount - difference));
            updateCount = getContentResolver().update(Uri.withAppendedPath(MahanadiContract.Budget.CONTENT_URI, UPDATE_BUDGET_CODE)
                    ,budgetDetails
                    ,DatabaseUtils.concatenateWhere(filterClause,MONTHLY_BUDGET_FILTER)
                    ,budgetFilterArgs);
            Log.d(LOG_TAG, "Budget update count : "+ updateCount);
        }
        return updateCount;
    }
}
