package com.tony.odiya.mahanadi.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.tony.odiya.mahanadi.R;
import com.tony.odiya.mahanadi.contract.MahanadiContract;
import com.tony.odiya.mahanadi.dialoglistener.DialogListener;
import com.tony.odiya.mahanadi.utils.Utility;

import static com.tony.odiya.mahanadi.common.Constants.BUDGET_LEFT;
import static com.tony.odiya.mahanadi.common.Constants.BUDGET_TYPE_MONTHLY;
import static com.tony.odiya.mahanadi.common.Constants.DELETE_BUDGET_COUNT;
import static com.tony.odiya.mahanadi.common.Constants.DELETE_EXPENSE_COUNT;
import static com.tony.odiya.mahanadi.common.Constants.END_TIME;
import static com.tony.odiya.mahanadi.common.Constants.MONTHLY;
import static com.tony.odiya.mahanadi.common.Constants.REQUEST_BUDGET_EDIT_CODE;
import static com.tony.odiya.mahanadi.common.Constants.REQUEST_BUDGET_RESET_CODE;
import static com.tony.odiya.mahanadi.common.Constants.REQUEST_BUDGET_SETUP_CODE;
import static com.tony.odiya.mahanadi.common.Constants.SAVE_BUDGET_AMOUNT_KEY;
import static com.tony.odiya.mahanadi.common.Constants.SAVE_BUDGET_CODE;
import static com.tony.odiya.mahanadi.common.Constants.SAVE_BUDGET_ROW_KEY;
import static com.tony.odiya.mahanadi.common.Constants.START_TIME;
import static com.tony.odiya.mahanadi.common.Constants.TREND_EXPENSE_CODE;
import static com.tony.odiya.mahanadi.common.Constants.UPDATE_BUDGET_AMOUNT_COUNT;
import static com.tony.odiya.mahanadi.common.Constants.UPDATE_BUDGET_CODE;

/**
 * Created by TONY on 10/26/2017.
 */

public class BudgetSetupDialogFragment extends DialogFragment {
    private DialogListener mListener;
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // inflate the layout of dialog
        LayoutInflater inflater = getActivity().getLayoutInflater();
        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        final View budgetSetupView = inflater.inflate(R.layout.dialog_budget_setup, null);
        final View budgetResetView = inflater.inflate(R.layout.dialog_budget_reset, null);
        TextView budgetMessage = (TextView)budgetSetupView.findViewById(R.id.dialog_budget_message);
        int requestCode = this.getTargetRequestCode();
        switch (requestCode){
            case REQUEST_BUDGET_SETUP_CODE:
                builder.setView(budgetSetupView)
                       .setTitle(R.string.title_budget_amount)
                        //.setMessage(R.string.dialog_budget_setup)
                       .setPositiveButton(R.string.budget_save, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                EditText budgetAmountEditText = (EditText)budgetSetupView.findViewById(R.id.dialog_budget_setup_amount);
                                String budgetAmountString = budgetAmountEditText.getText().toString();
                                if(!budgetAmountString.equals("") && !budgetAmountString.equals("0.0")){
                                    // get the budget amount from dialog view
                                    Double amount = Double.valueOf(budgetAmountString);
                                    // fetch expenses for the current month and subtract it from total amount.
                                    Double expensesForThisMonth = findCurrentMonthExpenses();
                                    if(expensesForThisMonth>0){
                                        amount = amount - expensesForThisMonth;
                                    }
                                    ContentValues budgetDetails = new ContentValues();
                                    budgetDetails.put(MahanadiContract.Budget.COL_AMOUNT, amount);
                                    budgetDetails.put(MahanadiContract.Budget.COL_TYPE, BUDGET_TYPE_MONTHLY);
                                    budgetDetails.put(MahanadiContract.Budget.COL_CREATED_ON, System.currentTimeMillis());
                                    budgetDetails.put(MahanadiContract.Budget.COL_END_DATE, Utility.getMonthEndInMilliSeconds());
                                    Uri insertUri = getActivity().getContentResolver().insert(Uri.withAppendedPath(MahanadiContract.Budget.CONTENT_URI,SAVE_BUDGET_CODE),budgetDetails);
                                    Long rowId = ContentUris.parseId(insertUri);
                                    Bundle dataForView = new Bundle();
                                    dataForView.putLong(SAVE_BUDGET_ROW_KEY,rowId);
                                    dataForView.putDouble(SAVE_BUDGET_AMOUNT_KEY, amount);
                                    mListener.onBudgetSetup(dataForView);
                                }
                                else{
                                    Toast.makeText(budgetSetupView.getContext(), R.string.dialog_budget_empty_setup_msg, Toast.LENGTH_SHORT).show();
                                }
                            }
                        })
                        .setNegativeButton(R.string.budget_later, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // User cancelled the dialog
                                BudgetSetupDialogFragment.this.getDialog().cancel();
                            }
                        });
                break;
            case REQUEST_BUDGET_EDIT_CODE:
                budgetMessage.setText(getString(R.string.dialog_budget_edit_msg));
                Bundle args = getArguments();
                EditText budgetAmountText = (EditText)budgetSetupView.findViewById(R.id.dialog_budget_setup_amount);
                final Double budgetLeft = args.getDouble(BUDGET_LEFT);
                budgetAmountText.setText(budgetLeft.toString());
                builder.setView(budgetSetupView)
                        .setTitle(R.string.title_budget_amount)
                        .setPositiveButton(R.string.budget_save, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                EditText budgetAmountEditText = (EditText)budgetSetupView.findViewById(R.id.dialog_budget_setup_amount);
                                String budgetAmountString = budgetAmountEditText.getText().toString();
                                ContentValues budgetDetails = null;
                                if(!budgetAmountString.equals("")){
                                    if(Double.valueOf(budgetAmountString)>=budgetLeft) {
                                        // get the budget amount edited from dialog view
                                        Double amount = Double.valueOf(budgetAmountString);
                                        budgetDetails = new ContentValues();
                                        budgetDetails.put(MahanadiContract.Budget.COL_AMOUNT, amount);
                                        //budgetDetails.put(MahanadiContract.Budget.COL_TYPE, BUDGET_TYPE_MONTHLY);
                                        //budgetDetails.put(MahanadiContract.Budget.COL_LAST_UPDATED_ON, System.currentTimeMillis());

                                    }
                                    else {
                                        // Get total expense for month.
                                        Double expensesForThisMonth = findCurrentMonthExpenses();
                                        if(expensesForThisMonth > 0){
                                            Toast.makeText(budgetSetupView.getContext(),R.string.dialog_budget_decrease_msg, Toast.LENGTH_SHORT).show();
                                        } else {
                                            Double amount = Double.valueOf(budgetAmountString);
                                            budgetDetails = new ContentValues();
                                            budgetDetails.put(MahanadiContract.Budget.COL_AMOUNT, amount);
                                            //budgetDetails.put(MahanadiContract.Budget.COL_TYPE, BUDGET_TYPE_MONTHLY);
                                            //budgetDetails.put(MahanadiContract.Budget.COL_LAST_UPDATED_ON, System.currentTimeMillis());

                                        }
                                    }
                                    //Call content resolver to update budget if changes have been made.
                                    if(budgetDetails !=null) {
                                        String monthlyBudgetFilter = "datetime(" + MahanadiContract.Budget.COL_END_DATE + "/1000,'unixepoch') >= datetime('now','unixepoch')";
                                        String filterClause = MahanadiContract.Budget.COL_CREATED_ON + " BETWEEN ? AND ?";
                                        Bundle args = Utility.getDateRange(MONTHLY);
                                        String[] budgetFilterArgs = {args.getString(START_TIME), args.getString(END_TIME)};
                                        int updateCount = getActivity().getContentResolver().update(Uri.withAppendedPath(MahanadiContract.Budget.CONTENT_URI, UPDATE_BUDGET_CODE)
                                                , budgetDetails
                                                , DatabaseUtils.concatenateWhere(filterClause, monthlyBudgetFilter)
                                                , budgetFilterArgs);

                                        // Budget row doesn't exist in database and hence we have to insert a row.
                                        Long rowId = 0l;
                                        Bundle updateArgs = new Bundle();
                                        if(updateCount == 0){
                                            Double amount = Double.valueOf(budgetAmountString);
                                            budgetDetails.put(MahanadiContract.Budget.COL_TYPE, BUDGET_TYPE_MONTHLY);
                                            budgetDetails.put(MahanadiContract.Budget.COL_CREATED_ON, System.currentTimeMillis());
                                            budgetDetails.put(MahanadiContract.Budget.COL_END_DATE, Utility.getMonthEndInMilliSeconds());
                                            Uri insertUri = getActivity().getContentResolver().insert(Uri.withAppendedPath(MahanadiContract.Budget.CONTENT_URI, SAVE_BUDGET_CODE)
                                                , budgetDetails);
                                            rowId = ContentUris.parseId(insertUri);
                                            Bundle dataForView = new Bundle();
                                            updateArgs.putLong(SAVE_BUDGET_ROW_KEY,rowId);
                                            updateArgs.putDouble(SAVE_BUDGET_AMOUNT_KEY, amount);
                                            updateCount = 1;
                                        }
                                        updateArgs.putInt(UPDATE_BUDGET_AMOUNT_COUNT,updateCount);
                                        mListener.onBudgetEdit(updateArgs);
                                    }

                                }
                                else{
                                    Toast.makeText(budgetSetupView.getContext(),R.string.dialog_budget_empty_setup_msg, Toast.LENGTH_SHORT).show();
                                }
                            }
                        })
                        .setNegativeButton(R.string.budget_later, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // User cancelled the dialog
                                BudgetSetupDialogFragment.this.getDialog().cancel();
                            }
                        });
                break;

            case REQUEST_BUDGET_RESET_CODE:
                //This will reset all the budget and expenses to zero.
                builder.setView(budgetResetView)
                        .setTitle(R.string.title_budget_reset)
                        //.setMessage(R.string.dialog_budget_setup)
                        .setPositiveButton(R.string.action_reset, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                    String monthlyBudgetFilter = "datetime(" + MahanadiContract.Budget.COL_END_DATE + "/1000,'unixepoch') >= datetime('now','unixepoch')";
                                    String expenseFilterClause = MahanadiContract.Expense.COL_CREATED_ON + " BETWEEN ? AND ?";
                                    String budgetFilterClause = MahanadiContract.Budget.COL_CREATED_ON + " BETWEEN ? AND ?";
                                    Bundle args = Utility.getDateRange(MONTHLY);
                                    String[] filterArgs = {args.getString(START_TIME), args.getString(END_TIME)};
                                    int deleteBudgetCount = getActivity().getContentResolver().delete(MahanadiContract.Budget.CONTENT_URI
                                            ,DatabaseUtils.concatenateWhere(monthlyBudgetFilter,budgetFilterClause),filterArgs);
                                    int deleteExpenseCount = getActivity().getContentResolver().delete(MahanadiContract.Expense.CONTENT_URI
                                            ,expenseFilterClause,filterArgs);
                                    Bundle deleteArgs = new Bundle();
                                    if(deleteExpenseCount>0){
                                        deleteArgs.putInt(DELETE_EXPENSE_COUNT,deleteExpenseCount);
                                        Toast.makeText(budgetSetupView.getContext(), R.string.dialog_expense_confirm_reset, Toast.LENGTH_SHORT).show();
                                    }
                                    if(deleteBudgetCount>0){
                                        deleteArgs.putInt(DELETE_BUDGET_COUNT,deleteBudgetCount);
                                        Toast.makeText(budgetSetupView.getContext(), R.string.dialog_budget_confirm_reset, Toast.LENGTH_SHORT).show();
                                    }
                                    mListener.onBudgetReset(deleteArgs);

                            }
                        })
                        .setNegativeButton(R.string.budget_later, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // User cancelled the dialog
                                BudgetSetupDialogFragment.this.getDialog().cancel();
                            }
                        });
                break;

        }
        // Create the AlertDialog object and return it
        return builder.create();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mListener = (DialogListener)getTargetFragment();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    private Double findCurrentMonthExpenses(){
        Bundle timeLimits = Utility.getDateRange(MONTHLY);
        String filterClause = MahanadiContract.Expense.COL_CREATED_ON + " BETWEEN "+ timeLimits.getString(START_TIME)+" AND "+timeLimits.getString(END_TIME);
        Uri uri = Uri.withAppendedPath(MahanadiContract.Expense.CONTENT_URI,TREND_EXPENSE_CODE);
        String[] trendExpenseProjection = {
                "SUM("+MahanadiContract.Expense.COL_AMOUNT+") AS TREND "
        };
        Cursor c = getActivity().getContentResolver().query(uri, trendExpenseProjection, filterClause,null,null);
        Double totalExpenses = 0.0;
        while (c.moveToNext()){
            totalExpenses = c.getDouble(c.getColumnIndex("TREND"));
        }
        return totalExpenses;
    }
}
