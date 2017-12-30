package com.tony.odiya.moneyshankar.activity;

import android.app.Activity;
import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.tony.odiya.moneyshankar.R;
import com.tony.odiya.moneyshankar.contract.MahanadiContract;
import com.tony.odiya.moneyshankar.utils.Utility;

import org.threeten.bp.ZoneId;
import org.threeten.bp.ZonedDateTime;

import static com.tony.odiya.moneyshankar.common.Constants.CURRENT_BUDGET_PROJECTION;
import static com.tony.odiya.moneyshankar.common.Constants.END_TIME;
import static com.tony.odiya.moneyshankar.common.Constants.MONTHLY;
import static com.tony.odiya.moneyshankar.common.Constants.QUERY_BUDGET_CODE;
import static com.tony.odiya.moneyshankar.common.Constants.SAVE_EXPENSE_CODE;
import static com.tony.odiya.moneyshankar.common.Constants.START_TIME;
import static com.tony.odiya.moneyshankar.common.Constants.UPDATE_BUDGET_CODE;


public class AddExpenseActivity extends AppCompatActivity {
// public class AddExpenseActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    private static final String LOG_TAG = AddExpenseActivity.class.getSimpleName();
    private Toolbar addExpenseToolbar;
    //private Spinner mCategorySpinner;
    private String mCategory;
    private String mItem;
    private String mRemark;
    private Double mAmount;
    private InterstitialAd mInterstitialAd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_expense);
        addExpenseToolbar = (Toolbar) findViewById(R.id.add_expense_toolbar);
        /*mCategorySpinner = (Spinner) findViewById(R.id.expense_category_spinner);
        mCategorySpinner.setOnItemSelectedListener(this);
        Utility.setCategoryValuesInSpinner(mCategorySpinner,getApplicationContext());
        Utility.selectDefaultCategoryInSpinner(mCategorySpinner, HOME);*/
        setSupportActionBar(addExpenseToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Initialize the Mobile Ads SDK.
        MobileAds.initialize(this, "ca-app-pub-3940256099942544~3347511713");
        // Create the InterstitialAd and set the adUnitId.
        mInterstitialAd = new InterstitialAd(this);
        // Defined in res/values/strings.xml
        mInterstitialAd.setAdUnitId(getString(R.string.my_ad_unit_id));
        mInterstitialAd.loadAd(new AdRequest.Builder().build());
        mInterstitialAd.setAdListener(new AdListener(){
            @Override
            public void onAdClosed() {
                super.onAdClosed();
                finish();
            }

            @Override
            public void onAdLeftApplication() {
                super.onAdLeftApplication();
                finish();
            }
        });

    }

    private void showInterstitial() {
        // Show the ad if it's ready. Otherwise toast and restart the game.
        if (mInterstitialAd != null && mInterstitialAd.isLoaded()) {
            mInterstitialAd.show();
        } else {
            Log.d(LOG_TAG, "Ad didn't load properly.");
            finish();
        }
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
                EditText editCategory = (EditText)findViewById(R.id.edit_category);
                mCategory = editCategory.getText().toString();
                EditText editItem = (EditText)findViewById(R.id.edit_item);
                mItem = editItem.getText().toString();
                EditText editAmount = (EditText)findViewById(R.id.edit_amount);
                String sAmount = editAmount.getText().toString();

                EditText editRemark = (EditText)findViewById(R.id.edit_remark);
                mRemark = editRemark.getText().toString();
                if(mItem.equals("")||sAmount.equals("")){
                    Toast.makeText(this,"Item, Category and Amount are mandatory.", Toast.LENGTH_SHORT).show();
                }
                else {
                    mAmount = Double.valueOf(sAmount);
                    // Save Category, item, amount and remark in database
                    ContentValues expenseDetails = new ContentValues();
                    expenseDetails.put(MahanadiContract.Expense.COL_CATEGORY,mCategory);
                    expenseDetails.put(MahanadiContract.Expense.COL_ITEM,mItem);
                    expenseDetails.put(MahanadiContract.Expense.COL_AMOUNT,mAmount);
                    expenseDetails.put(MahanadiContract.Expense.COL_REMARK,mRemark);
                    ZonedDateTime zdtUtc = ZonedDateTime.now(ZoneId.of("UTC"));
                    expenseDetails.put(MahanadiContract.Expense.COL_CREATED_ON,zdtUtc.toInstant().toEpochMilli());
                    Uri insertUri = getContentResolver().insert(Uri.withAppendedPath(MahanadiContract.Expense.CONTENT_URI,SAVE_EXPENSE_CODE),expenseDetails);
                    Long rowId = ContentUris.parseId(insertUri);

                    // UPDATE BUDGET table
                    int rowsUpdatedCount = updateCurrentMonthBudgetRow();
                    // Return back to parent fragment after data has been saved into database.
                    setResult(Activity.RESULT_OK);
                    //finish();
                    showInterstitial();
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


    /*@Override
    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id){
        mCategory = parent.getItemAtPosition(pos).toString();
    }
    @Override
    public void onNothingSelected(AdapterView<?> parent){}*/

    /**
     * This method will update the budget for current month.
     * For each new expense added, budget will be subtracted by the same amount as that of expense.
     * @return
     */
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
