package com.tony.odiya.mahanadi.adapter;

import android.graphics.Color;
import android.support.v7.util.SortedList;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.tony.odiya.mahanadi.R;
import com.tony.odiya.mahanadi.adapter.vholder.ExpenseViewHolder;
import com.tony.odiya.mahanadi.fragment.ExpenseFragment.OnListFragmentInteractionListener;
import com.tony.odiya.mahanadi.model.ExpenseData;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link ExpenseData} and makes a call to the
 * specified {@link OnListFragmentInteractionListener}.
 */
public class MyExpenseRecyclerViewAdapter extends SelectableAdapter<ExpenseViewHolder> {
    private static final String LOG_TAG = MyExpenseRecyclerViewAdapter.class.getSimpleName();
    private final List<ExpenseData> mValues;
    private final OnListFragmentInteractionListener mContextListener;
    private OnRecyclerItemClickedListener mItemLongClickedListener;
    private final List<String> mExpenseDataIdList = new ArrayList<>(0);
    private Double totalExpenseAmount = 0.0;

    public interface OnRecyclerItemClickedListener {
        void onItemClicked(int position);

        boolean onItemLongClicked(int position);
        //int updateCurrentMonthBudgetRow(List<String> expenseIdList);
    }

    public MyExpenseRecyclerViewAdapter(List<ExpenseData> items, OnListFragmentInteractionListener listener, OnRecyclerItemClickedListener itemLongClickedListener) {
        mValues = items;
        mContextListener = listener;
        mItemLongClickedListener = itemLongClickedListener;
    }

    @Override
    public ExpenseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //Take viewtype and change the color of views in future
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.expense_card, parent, false);
        return new ExpenseViewHolder(view, mItemLongClickedListener);
    }

    @Override
    public void onBindViewHolder(final ExpenseViewHolder holder, final int position) {
        holder.bindTo(mValues.get(position));
        CardView v = (CardView) holder.mView.findViewById(R.id.expense_card_view);
        if (isSelected(position)) {
            v.setCardBackgroundColor(Color.parseColor("#ff0099cc"));//Dark blue
            holder.setViewTextColor(Color.WHITE);
        } else {
            v.setCardBackgroundColor(Color.WHITE);
            holder.setViewTextColor(Color.BLACK);
        }
    }

    @Override
    public int getItemCount() {
        if (mValues != null) {
            return mValues.size();
        } else
            return 0;
    }

    public void removeItem(int position) {
        // Add the id of removed item into list. This id list will be used to update database in fragment.
        ExpenseData expense = mValues.get(position);
        mExpenseDataIdList.add(expense.getExpenseId());
        totalExpenseAmount += Double.valueOf(expense.getAmount());
        mValues.remove(position);
        notifyItemRemoved(position);
    }

    public void removeItems(List<Integer> positions) {
        // Reverse-sort the list
        Collections.sort(positions, new Comparator<Integer>() {
            @Override
            public int compare(Integer lhs, Integer rhs) {
                return rhs - lhs;
            }
        });

        // Split the list in ranges
        while (!positions.isEmpty()) {
            if (positions.size() == 1) {
                removeItem(positions.get(0));
                positions.remove(0);
            } else {
                int count = 1;
                while (positions.size() > count && positions.get(count).equals(positions.get(count - 1) - 1)) {
                    ++count;
                }

                if (count == 1) {
                    removeItem(positions.get(0));
                } else {
                    removeRange(positions.get(count - 1), count);
                }

                for (int i = 0; i < count; ++i) {
                    positions.remove(0);
                }
            }
        }
    }

    private void removeRange(int positionStart, int itemCount) {
        for (int i = 0; i < itemCount; ++i) {
            ExpenseData expense = mValues.get(positionStart);
            mExpenseDataIdList.add(expense.getExpenseId());
            totalExpenseAmount += Double.valueOf(expense.getAmount());
            mValues.remove(positionStart);
        }
        notifyItemRangeRemoved(positionStart, itemCount);
    }

    /*public int updateCurrentMonthBudgetRow(){
        return mItemLongClickedListener.updateCurrentMonthBudgetRow(mExpenseDataIdList);
    }*/
    public List<String> getExpenseDataIdList() {
        return mExpenseDataIdList;
    }

    public Double getTotalExpenseAmount() {
        return totalExpenseAmount;
    }


}
