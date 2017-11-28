package com.tony.odiya.mahanadi.adapter.vholder;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SwitchCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.tony.odiya.mahanadi.R;
import com.tony.odiya.mahanadi.adapter.MyExpenseRecyclerViewAdapter;
import com.tony.odiya.mahanadi.adapter.holderlistener.ExpenseChangeListener;
import com.tony.odiya.mahanadi.contract.MahanadiContract;
import com.tony.odiya.mahanadi.model.ExpenseData;
import com.tony.odiya.mahanadi.utils.Utility;

import static com.tony.odiya.mahanadi.common.Constants.CURRENT_BUDGET_PROJECTION;
import static com.tony.odiya.mahanadi.common.Constants.END_TIME;
import static com.tony.odiya.mahanadi.common.Constants.MONTHLY;
import static com.tony.odiya.mahanadi.common.Constants.QUERY_BUDGET_CODE;
import static com.tony.odiya.mahanadi.common.Constants.QUERY_EXPENSE_CODE;
import static com.tony.odiya.mahanadi.common.Constants.START_TIME;
import static com.tony.odiya.mahanadi.common.Constants.UPDATE_BUDGET_CODE;
import static com.tony.odiya.mahanadi.common.Constants.UPDATE_EXPENSE_CODE;

/**
 * Created by tony on 25/11/17.
 */


public class ExpandedExpenseViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
    private static final String LOG_TAG = ExpenseViewHolder.class.getSimpleName();
    public final View mView;
    public final TextView mCategoryLabelView;
    public final TextView mItemLabelView;
    public final TextView mAmountLabelView;
    public final TextView mRemarkLabelView;
    public final TextView mCategoryView;
    public final TextView mItemView;
    public final TextView mAmountView;
    public final TextView mRemarkView;

    public final EditText mCategoryEditText;
    public final EditText mItemEditText;
    public final EditText mRemarkEditText;
    public final EditText mAmountEditText;

    public Boolean mItemChanged = Boolean.FALSE;
    private String mExpenseId;
    private String mItemBought;
    private String mCategory;
    private String mRemark;
    private String mAmount;
    //public final SwitchCompat mSwitchCompat;

    public ExpenseData mItem;
    private MyExpenseRecyclerViewAdapter.OnRecyclerItemClickedListener recyclerItemClickedListener;
    private MyExpenseRecyclerViewAdapter.OnRecyclerItemChangeListener recyclerItemChangeListener;

    private TextWatcher textWatcher = new ExpenseChangeListener(mItemChanged);

    public ExpandedExpenseViewHolder(View view, MyExpenseRecyclerViewAdapter.OnRecyclerItemClickedListener recyclerItemClickedListener,
                                     MyExpenseRecyclerViewAdapter.OnRecyclerItemChangeListener recyclerItemChangeListener) {
        super(view);
        mView = view;

        mCategoryLabelView = (TextView) view.findViewById(R.id.category_label);
        mItemLabelView = (TextView) view.findViewById(R.id.item_label);
        mAmountLabelView = (TextView) view.findViewById(R.id.amount_label);
        mRemarkLabelView = (TextView) view.findViewById(R.id.remark_label);

        mCategoryView = (TextView) view.findViewById(R.id.expanded_category);
        mItemView = (TextView) view.findViewById(R.id.expanded_item);
        mAmountView = (TextView) view.findViewById(R.id.expanded_amount);
        mRemarkView = (TextView) view.findViewById(R.id.expanded_remark);

        mCategoryEditText = (EditText) view.findViewById(R.id.expanded_edit_category);
        mItemEditText = (EditText) view.findViewById(R.id.expanded_edit_item);
        mAmountEditText = (EditText) view.findViewById(R.id.expanded_edit_amount);
        mRemarkEditText = (EditText) view.findViewById(R.id.expanded_edit_remark);

        mCategoryEditText.addTextChangedListener(textWatcher);
        mItemEditText.addTextChangedListener(textWatcher);
        mAmountEditText.addTextChangedListener(textWatcher);
        mRemarkEditText.addTextChangedListener(textWatcher);

        /*mSwitchCompat = (SwitchCompat) view.findViewById(R.id.edit_expense_toggle);
        mSwitchCompat.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                toggleEditView(isChecked);
            }
        });*/
        this.recyclerItemClickedListener = recyclerItemClickedListener;
        this.recyclerItemChangeListener = recyclerItemChangeListener;
        view.setOnClickListener(this);
        view.setOnLongClickListener(this);
    }

    @Override
    public String toString() {
        return super.toString() + " '" + mCategoryView.getText() + ", " + mItemView.getText() + ", " + mAmountView.getText() + "'";
    }

    @Override
    public void onClick(View v) {
        Log.d(LOG_TAG, "You have clicked on " + mItemView.getText());
        int position = getAdapterPosition();
        if (recyclerItemClickedListener != null) {
            recyclerItemClickedListener.onItemClicked(position);
        }
    }

    @Override
    public boolean onLongClick(View v) {
        Log.d(LOG_TAG, "You have long clicked on " + mItemView.getText());
        int position = getAdapterPosition();
        if (recyclerItemClickedListener != null) {
            return recyclerItemClickedListener.onItemLongClicked(position);
        }
        return false;
    }

    public void bindTo(ExpenseData expenseData, int position) {

        this.mItem = expenseData;
        this.mCategoryView.setText(expenseData.category.length() > 0 ? expenseData.category : "N.A.");
        this.mItemView.setText(expenseData.item);
        this.mAmountView.setText(expenseData.amount);
        CharSequence shortendRemark = null;
        if (expenseData.remark.length() > 0) {
            shortendRemark = expenseData.remark.length() > 10 ? expenseData.remark.subSequence(0, 10) + "..." : expenseData.remark;
        } else {
            shortendRemark = "N.A.";
        }
        this.mRemarkView.setText(shortendRemark);

        this.mCategoryEditText.setText(expenseData.category);
        this.mItemEditText.setText(expenseData.item);
        this.mAmountEditText.setText(expenseData.amount);
        this.mRemarkEditText.setText(expenseData.remark);
    }

    public void setViewTextColor(int color) {
        this.mCategoryLabelView.setTextColor(color);
        this.mItemLabelView.setTextColor(color);
        this.mAmountLabelView.setTextColor(color);
        this.mRemarkLabelView.setTextColor(color);

        this.mCategoryView.setTextColor(color);
        this.mItemView.setTextColor(color);
        this.mAmountView.setTextColor(color);
        this.mRemarkView.setTextColor(color);

        this.mCategoryEditText.setTextColor(color);
        this.mItemEditText.setTextColor(color);
        this.mAmountEditText.setTextColor(color);
        this.mRemarkEditText.setTextColor(color);
    }

    public void toggleEditView(boolean isChecked) {
        if (isChecked) {
            // The toggle is enabled
            mCategoryView.setVisibility(View.GONE);
            mItemView.setVisibility(View.GONE);
            mAmountView.setVisibility(View.GONE);
            mRemarkView.setVisibility(View.GONE);

            mCategoryEditText.setVisibility(View.VISIBLE);
            mItemEditText.setVisibility(View.VISIBLE);
            mAmountEditText.setVisibility(View.VISIBLE);
            mRemarkEditText.setVisibility(View.VISIBLE);

        } else {
            // The toggle is disabled
            mCategoryView.setVisibility(View.VISIBLE);
            mItemView.setVisibility(View.VISIBLE);
            mAmountView.setVisibility(View.VISIBLE);
            mRemarkView.setVisibility(View.VISIBLE);

            mCategoryEditText.setVisibility(View.GONE);
            mItemEditText.setVisibility(View.GONE);
            mAmountEditText.setVisibility(View.GONE);
            mRemarkEditText.setVisibility(View.GONE);

            if(mItemChanged){
                // Update changes into database
                String sCategory = mCategoryEditText.getText().toString();
                String sAmount = mAmountEditText.getText().toString();
                String sRemark = mRemarkEditText.getText().toString();
                String sItem = mItemEditText.getText().toString();
                //TODO: Save into database
                if(sItem.equals("")||sAmount.equals("")){
                    /*Toast.makeText(this,"Item and Amount are mandatory.", Toast.LENGTH_SHORT).show();*/
                }
                else {
                    /*Double dAmount = Double.valueOf(sAmount);
                    ContentValues expenseDetails = new ContentValues();
                    expenseDetails.put(MahanadiContract.Expense.COL_CATEGORY, sCategory);
                    expenseDetails.put(MahanadiContract.Expense.COL_ITEM, sItem);
                    expenseDetails.put(MahanadiContract.Expense.COL_AMOUNT, dAmount);
                    expenseDetails.put(MahanadiContract.Expense.COL_REMARK, sRemark);
                    String where = MahanadiContract.Expense._ID + " = ? ";
                    String[] whereArgs = {this.mItem.getExpenseId()};
                    // Update expense
                    int updateExpenseCount = getContentResolver().update(Uri.withAppendedPath(MahanadiContract.Expense.CONTENT_URI, UPDATE_EXPENSE_CODE), expenseDetails,
                            where, whereArgs);
                    // Update budget
                    if(updateExpenseCount>0){
                        Double budgetDiff  = dAmount - Double.valueOf(mAmount);
                        int updateBudgetCount = updateCurrentMonthBudgetRow(budgetDiff);
                        // restore the flag to false
                        mItemChanged = Boolean.FALSE;
                        mCategoryView.setText(mCategoryEditText.getText());
                        mItemView.setText(mItemEditText.getText());
                        mAmountView.setText(mAmountEditText.getText());
                        mRemarkView.setText(mRemarkEditText.getText());
                    }*/
                }
            }
        }

    }

}

