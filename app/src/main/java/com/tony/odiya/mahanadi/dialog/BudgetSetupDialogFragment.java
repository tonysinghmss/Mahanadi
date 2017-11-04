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
import static com.tony.odiya.mahanadi.common.Constants.END_TIME;
import static com.tony.odiya.mahanadi.common.Constants.MONTHLY;
import static com.tony.odiya.mahanadi.common.Constants.REQUEST_BUDGET_EDIT_CODE;
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
        final View view = inflater.inflate(R.layout.dialog_budget_setup, null);
        int requestCode = this.getTargetRequestCode();
        switch (requestCode){
            case REQUEST_BUDGET_SETUP_CODE:
                builder.setView(view)
                       .setTitle(R.string.title_budget_amount)
                        //.setMessage(R.string.dialog_budget_setup)
                       .setPositiveButton(R.string.budget_save, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                EditText budgetAmountEditText = (EditText)view.findViewById(R.id.dialog_budget_setup_amount);
                                String budgetAmountString = budgetAmountEditText.getText().toString();
                                if(!budgetAmountString.equals("")){
                                    // get the budget amount from dialog view
                                    Double amount = Double.valueOf(budgetAmountString);
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
                                    mListener.onDialogInteraction(dataForView);
                                }
                                else{
                                    Toast.makeText(view.getContext(), R.string.dialog_budget_empty_setup_msg, Toast.LENGTH_SHORT).show();
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
                TextView budgetMessage = (TextView)view.findViewById(R.id.dialog_budget_message);
                budgetMessage.setText(getString(R.string.dialog_budget_edit_msg));
                Bundle args = getArguments();
                EditText budgetAmountText = (EditText)view.findViewById(R.id.dialog_budget_setup_amount);
                final Double budgetLeft = args.getDouble(BUDGET_LEFT);
                budgetAmountText.setText(budgetLeft.toString());
                builder.setView(view)
                        .setTitle(R.string.title_budget_amount)
                        .setPositiveButton(R.string.budget_save, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                EditText budgetAmountEditText = (EditText)view.findViewById(R.id.dialog_budget_setup_amount);
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
                                            Toast.makeText(view.getContext(),R.string.dialog_budget_decrease_msg, Toast.LENGTH_SHORT).show();
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
                                        String MONTHLY_BUDGET_FILTER = "datetime(" + MahanadiContract.Budget.COL_END_DATE + "/1000,'unixepoch') >= datetime('now','unixepoch')";
                                        String filterClause = MahanadiContract.Expense.COL_CREATED_ON + " BETWEEN ? AND ?";
                                        Bundle args = Utility.getDateRange(MONTHLY);
                                        String[] budgetFilterArgs = {args.getString(START_TIME), args.getString(END_TIME)};
                                        int updateCount = getActivity().getContentResolver().update(Uri.withAppendedPath(MahanadiContract.Budget.CONTENT_URI, UPDATE_BUDGET_CODE)
                                                , budgetDetails
                                                , DatabaseUtils.concatenateWhere(filterClause, MONTHLY_BUDGET_FILTER)
                                                , budgetFilterArgs);
                                        Bundle updateArgs = new Bundle();
                                        updateArgs.putInt(UPDATE_BUDGET_AMOUNT_COUNT,updateCount);
                                        mListener.onDialogInteraction(updateArgs);
                                    }

                                }
                                else{
                                    Toast.makeText(view.getContext(),R.string.dialog_budget_empty_setup_msg, Toast.LENGTH_SHORT).show();
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
