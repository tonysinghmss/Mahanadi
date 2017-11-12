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
    public ExpenseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //Take viewtype and change the color of views in future
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.expense_card, parent, false);
        return new ExpenseViewHolder(view, mItemClickedListener);
    }

    @Override
    public void onBindViewHolder(final ExpenseViewHolder holder, final int position) {
        holder.bindTo(dataset.getExpenseData(position), position);
        CardView v = (CardView) holder.mView.findViewById(R.id.expense_card_view);
        holder.setViewTextColor(Color.WHITE);
        if (isSelected(position)) {
            v.setCardBackgroundColor(Color.parseColor("#ff4081"));//Dark blue
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
