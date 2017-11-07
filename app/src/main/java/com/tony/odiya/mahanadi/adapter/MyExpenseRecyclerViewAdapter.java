package com.tony.odiya.mahanadi.adapter;

import android.graphics.Color;
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

    private ExpenseDataset dataset;

    public void dataset(ExpenseDataset dataset){
        this.dataset = dataset;
    }

    //private final List<ExpenseData> mValues;
    private final OnListFragmentInteractionListener mContextListener;
    private OnRecyclerItemClickedListener mItemLongClickedListener;
   /* private final List<String> mExpenseDataIdList = new ArrayList<>(0);
    private Double totalExpenseAmount = 0.0;*/

    public interface OnRecyclerItemClickedListener {
        void onItemClicked(int position);

        boolean onItemLongClicked(int position);
        //int updateCurrentMonthBudgetRow(List<String> expenseIdList);
    }

    public MyExpenseRecyclerViewAdapter(OnListFragmentInteractionListener listener, OnRecyclerItemClickedListener itemLongClickedListener) {
        //mValues = items;
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
        holder.bindTo(dataset.getExpenseData(position));
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
        return dataset.size();
    }

    /*public void removeItem(int position) {
        dataset.removeItem(position);
    }*/

    /*public void removeItems(List<Integer> positions) {

    }*/

   /* private void removeRange(int positionStart, int itemCount) {
        dataset.removeRange(positionStart, itemCount);
    }*/

    /*public int updateCurrentMonthBudgetRow(){
        return mItemLongClickedListener.updateCurrentMonthBudgetRow(mExpenseDataIdList);
    }*/
   /* public List<String> getExpenseDataIdList() {
        return dataset.getExpenseDataIdList();
    }

    public Double getTotalExpenseAmount() {
        return dataset.getTotalExpenseAmount();
    }*/

}
