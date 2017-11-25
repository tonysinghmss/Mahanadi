package com.tony.odiya.mahanadi.adapter;

import android.graphics.Color;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.tony.odiya.mahanadi.R;
import com.tony.odiya.mahanadi.adapter.vholder.ExpandedExpenseViewHolder;
import com.tony.odiya.mahanadi.adapter.vholder.ExpenseViewHolder;
import com.tony.odiya.mahanadi.fragment.ExpenseFragment.OnListFragmentInteractionListener;
import com.tony.odiya.mahanadi.model.ExpenseData;

/**
 * {@link RecyclerView.Adapter} that can display a {@link ExpenseData} and makes a call to the
 * specified {@link OnListFragmentInteractionListener}.
 */
public class MyExpenseRecyclerViewAdapter extends SelectableAdapter<RecyclerView.ViewHolder> {
    private static final String LOG_TAG = MyExpenseRecyclerViewAdapter.class.getSimpleName();
    private static final int NORMAL_VIEW = 0;
    private static final int EXPANDED_VIEW = 1;

    private ExpenseDataset dataset;

    public void dataset(ExpenseDataset dataset){
        this.dataset = dataset;
    }

    //private final List<ExpenseData> mValues;
    private final OnListFragmentInteractionListener mContextListener;
    private OnRecyclerItemClickedListener mItemClickedListener;
   /* private final List<String> mExpenseDataIdList = new ArrayList<>(0);
    private Double totalExpenseAmount = 0.0;*/

    public interface OnRecyclerItemClickedListener {
        void onItemClicked(int position);

        boolean onItemLongClicked(int position);
        //int updateCurrentMonthBudgetRow(List<String> expenseIdList);
    }

    public MyExpenseRecyclerViewAdapter(OnListFragmentInteractionListener listener, OnRecyclerItemClickedListener itemClickedListener) {
        //mValues = items;
        mContextListener = listener;
        mItemClickedListener = itemClickedListener;
    }

    @Override
    public int getItemViewType(int position) {
        ExpenseData e = dataset.getExpenseData(position);
        if(e.getExpansionFlag()){
            return EXPANDED_VIEW;
        }
        else {
            return NORMAL_VIEW;
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //Take viewtype and change the color of views in future
        View view = null;
        RecyclerView.ViewHolder holder = null;
        switch (viewType){
            case NORMAL_VIEW:
                view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.expense_card, parent, false);
                holder = new ExpenseViewHolder(view, mItemClickedListener);
                break;
            case EXPANDED_VIEW:
                view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.expanded_expense_card, parent, false);
                holder = new ExpandedExpenseViewHolder(view, mItemClickedListener);
                break;
        }
        return holder;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        CardView v = null;
        switch(getItemViewType(position)){
            case NORMAL_VIEW:
                ExpenseViewHolder expenseViewHolder = (ExpenseViewHolder)holder;
                expenseViewHolder.bindTo(dataset.getExpenseData(position), position);
                v = (CardView) expenseViewHolder.mView.findViewById(R.id.expense_card_view);
                expenseViewHolder.setViewTextColor(Color.WHITE);
                break;
            case EXPANDED_VIEW:
                ExpandedExpenseViewHolder expandedExpenseViewHolder = (ExpandedExpenseViewHolder)holder;
                expandedExpenseViewHolder.bindTo(dataset.getExpenseData(position), position);
                v = (CardView) expandedExpenseViewHolder.mView.findViewById(R.id.expense_card_view);
                expandedExpenseViewHolder.setViewTextColor(Color.WHITE);
                break;
        }

        if (isSelected(position)) {
            v.setCardBackgroundColor(Color.parseColor("#ff4081"));//colorAccent
            //holder.setViewTextColor(Color.WHITE);
        } else {
            v.setCardBackgroundColor(Color.parseColor("#ff33b5e5"));
            //holder.setViewTextColor(Color.WHITE);
        }
    }

    @Override
    public int getItemCount() {
        return dataset.size();
    }

    public void toggleView(int position){
        ExpenseData e = dataset.getExpenseData(position);
        e.setExpansionFlag(!e.getExpansionFlag());
        notifyItemChanged(position);
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
        return mItemClickedListener.updateCurrentMonthBudgetRow(mExpenseDataIdList);
    }*/
   /* public List<String> getExpenseDataIdList() {
        return dataset.getExpenseDataIdList();
    }

    public Double getTotalExpenseAmount() {
        return dataset.getTotalExpenseAmount();
    }*/

}
