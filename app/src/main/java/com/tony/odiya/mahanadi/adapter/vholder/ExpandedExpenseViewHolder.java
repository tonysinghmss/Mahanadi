package com.tony.odiya.mahanadi.adapter.vholder;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.tony.odiya.mahanadi.R;
import com.tony.odiya.mahanadi.adapter.MyExpenseRecyclerViewAdapter;
import com.tony.odiya.mahanadi.model.ExpenseData;

/**
 * Created by tony on 25/11/17.
 */


public class ExpandedExpenseViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener{
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

    public final ToggleButton mToggleButton;

    public ExpenseData mItem;
    private MyExpenseRecyclerViewAdapter.OnRecyclerItemClickedListener recyclerItemClickedListener;

    public ExpandedExpenseViewHolder(View view, MyExpenseRecyclerViewAdapter.OnRecyclerItemClickedListener recyclerItemClickedListener) {
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

        mCategoryEditText = (EditText)view.findViewById(R.id.expanded_edit_category);
        mItemEditText = (EditText) view.findViewById(R.id.expanded_edit_item);
        mAmountEditText = (EditText) view.findViewById(R.id.expanded_edit_amount);
        mRemarkEditText = (EditText) view.findViewById(R.id.expanded_edit_remark);

        mToggleButton = (ToggleButton) view.findViewById(R.id.edit_expense_toggle);
        this.recyclerItemClickedListener = recyclerItemClickedListener;
        view.setOnClickListener(this);
        view.setOnLongClickListener(this);
    }

    @Override
    public String toString() {
        return super.toString() + " '" + mCategoryView.getText()+", "+mItemView.getText()+", " +mAmountView.getText()+ "'";
    }

    @Override
    public void onClick(View v){
        Log.d(LOG_TAG, "You have clicked on "+mItemView.getText());
        int position = getAdapterPosition();
        if(recyclerItemClickedListener !=null){
            recyclerItemClickedListener.onItemClicked(position);
        }
    }

    @Override
    public boolean onLongClick(View v){
        Log.d(LOG_TAG, "You have long clicked on "+mItemView.getText());
        int position = getAdapterPosition();
        if(recyclerItemClickedListener !=null){
            return recyclerItemClickedListener.onItemLongClicked(position);
        }
        return false;
    }

    public void bindTo(ExpenseData expenseData, int position){

        this.mItem = expenseData;
        this.mCategoryView.setText(expenseData.category.length()>0?expenseData.category:"N.A.");
        this.mItemView.setText(expenseData.item);
        this.mAmountView.setText(expenseData.amount);
        CharSequence shortendRemark = null;
        if(expenseData.remark.length()>0) {
            shortendRemark = expenseData.remark.length() > 10 ? expenseData.remark.subSequence(0, 10) + "..." : expenseData.remark;
        }
        else {
            shortendRemark = "N.A.";
        }
        this.mRemarkView.setText(shortendRemark);
    }

    public void setViewTextColor(int color){
        this.mCategoryLabelView.setTextColor(color);
        this.mItemLabelView.setTextColor(color);
        this.mAmountLabelView.setTextColor(color);
        this.mRemarkLabelView.setTextColor(color);

        this.mCategoryView.setTextColor(color);
        this.mItemView.setTextColor(color);
        this.mAmountView.setTextColor(color);
        this.mRemarkView.setTextColor(color);
    }

}
