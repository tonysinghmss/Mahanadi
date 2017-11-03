package com.tony.odiya.mahanadi.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.tony.odiya.mahanadi.R;
import com.tony.odiya.mahanadi.contract.MahanadiContract;
import com.tony.odiya.mahanadi.dialoglistener.DialogListener;
import com.tony.odiya.mahanadi.utils.Utility;

import static com.tony.odiya.mahanadi.common.Constants.BUDGET_TYPE_MONTHLY;
import static com.tony.odiya.mahanadi.common.Constants.SAVE_BUDGET_AMOUNT_KEY;
import static com.tony.odiya.mahanadi.common.Constants.SAVE_BUDGET_CODE;
import static com.tony.odiya.mahanadi.common.Constants.SAVE_BUDGET_ROW_KEY;

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
        /*TextView budgetMessageView = (TextView)view.findViewById(R.id.dialog_budget_setup_message);

        budgetMessageView.setText(getString(R.string.dialog_budget_setup_msg));*/
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
                            Toast.makeText(view.getContext(),"Your budget is empty.", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .setNegativeButton(R.string.budget_later, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog
                        BudgetSetupDialogFragment.this.getDialog().cancel();
                    }
                });
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
}
