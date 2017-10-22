package com.tony.odiya.mahanadi.fragment;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.tony.odiya.mahanadi.R;
import com.tony.odiya.mahanadi.fragment.ExpenseFragment.OnListFragmentInteractionListener;
import com.tony.odiya.mahanadi.model.ExpenseData;

import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link ExpenseData} and makes a call to the
 * specified {@link OnListFragmentInteractionListener}.
 */
public class MyExpenseRecyclerViewAdapter extends RecyclerView.Adapter<MyExpenseRecyclerViewAdapter.ViewHolder> {

    private final List<ExpenseData> mValues;
    private final OnListFragmentInteractionListener mListener;

    public MyExpenseRecyclerViewAdapter(List<ExpenseData> items, OnListFragmentInteractionListener listener) {
        mValues = items;
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.expense_card, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
        holder.mCategoryView.setText(mValues.get(position).category);
        holder.mItemView.setText(mValues.get(position).item);
        holder.mAmountView.setText(mValues.get(position).amount);
        holder.mRemarkView.setText(mValues.get(position).remark);

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onListFragmentInteraction(holder.mItem);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mCategoryView;
        public final TextView mItemView;
        public final TextView mAmountView;
        public final TextView mRemarkView;
        public ExpenseData mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mCategoryView = (TextView) view.findViewById(R.id.category);
            mItemView = (TextView) view.findViewById(R.id.item);
            mAmountView = (TextView) view.findViewById(R.id.amount);
            mRemarkView = (TextView) view.findViewById(R.id.remark);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mCategoryView.getText()+", "+mItemView.getText()+", " +mAmountView.getText()+ "'";
        }
    }
}
