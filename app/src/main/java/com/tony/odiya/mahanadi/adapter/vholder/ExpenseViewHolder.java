package com.tony.odiya.mahanadi.adapter.vholder;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.tony.odiya.mahanadi.R;
import com.tony.odiya.mahanadi.adapter.MyExpenseRecyclerViewAdapter;
import com.tony.odiya.mahanadi.model.ExpenseData;

/**
 * Created by tony on 5/11/17.
 */

public class ExpenseViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener{
    private static final String LOG_TAG = ExpenseViewHolder.class.getSimpleName();
    public final View mView;
    public final TextView mCategoryView;
    public final TextView mItemView;
    public final TextView mAmountView;
    public final TextView mRemarkView;
    public ExpenseData mItem;
    private MyExpenseRecyclerViewAdapter.OnRecyclerItemClickedListener recyclerItemClickedListener;

    public ExpenseViewHolder(View view, MyExpenseRecyclerViewAdapter.OnRecyclerItemClickedListener recyclerItemClickedListener) {
        super(view);
        mView = view;
        mCategoryView = (TextView) view.findViewById(R.id.category);
        mItemView = (TextView) view.findViewById(R.id.item);
        mAmountView = (TextView) view.findViewById(R.id.amount);
        mRemarkView = (TextView) view.findViewById(R.id.remark);
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

    public void bindTo(ExpenseData expenseData){
        this.mItem = expenseData;
        this.mCategoryView.setText(expenseData.category);
        this.mItemView.setText(expenseData.item);
        this.mAmountView.setText(expenseData.amount);
        CharSequence shortendRemark = expenseData.remark.length()>10?expenseData.remark.subSequence(0,10)+"...":expenseData.remark;
        this.mRemarkView.setText(shortendRemark);
    }
    
    public void setViewTextColor(int color){
        this.mCategoryView.setTextColor(color);
        this.mItemView.setTextColor(color);
        this.mAmountView.setTextColor(color);
        this.mRemarkView.setTextColor(color);
    }
    
}
