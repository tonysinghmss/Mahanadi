package com.tony.odiya.moneyshankar.adapter;

import android.graphics.Color;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SwitchCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;

import com.tony.odiya.moneyshankar.R;
import com.tony.odiya.moneyshankar.adapter.vholder.ExpandedExpenseViewHolder;
import com.tony.odiya.moneyshankar.adapter.vholder.ExpenseViewHolder;
import com.tony.odiya.moneyshankar.fragment.ExpenseFragment.OnListFragmentInteractionListener;
import com.tony.odiya.moneyshankar.model.ExpenseData;

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
    private OnRecyclerItemChangeListener mItemChangeListener;
   /* private final List<String> mExpenseDataIdList = new ArrayList<>(0);
    private Double totalExpenseAmount = 0.0;*/

    /**
     * This interface is used to communicate item clicks with the fragment.
     */
    public interface OnRecyclerItemClickedListener {
        void onItemClicked(int position);

        boolean onItemLongClicked(int position);
        //int updateCurrentMonthBudgetRowForDeletion(List<String> expenseIdList);
    }

    /**
     * This method is used to communicate edit changes in items of recycler view with the fragment.
     */
    public interface OnRecyclerItemChangeListener{
        boolean onItemEdit(int position, String previousAmount, ExpenseData expenseData);
    }


    public MyExpenseRecyclerViewAdapter(OnListFragmentInteractionListener listener, OnRecyclerItemClickedListener itemClickedListener, OnRecyclerItemChangeListener itemChangeListener) {
        //mValues = items;
        mContextListener = listener;
        mItemClickedListener = itemClickedListener;
        mItemChangeListener = itemChangeListener;
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
                holder = new ExpandedExpenseViewHolder(view, mItemClickedListener, mItemChangeListener);
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
                final ExpenseData data = dataset.getExpenseData(position);
                final ExpandedExpenseViewHolder expandedExpenseViewHolder = (ExpandedExpenseViewHolder)holder;
                expandedExpenseViewHolder.bindTo(data, position);
                v = (CardView) expandedExpenseViewHolder.mView.findViewById(R.id.expense_card_view);
                SwitchCompat sc = (SwitchCompat) expandedExpenseViewHolder.mView.findViewById(R.id.edit_expense_toggle);
                sc.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        expandedExpenseViewHolder.toggleEditText(isChecked, position);
                        if(isChecked){
                            data.setEditFlag(true);

                        } else{
                            data.setEditFlag(false);
                        }
                    }
                });
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
        // if the expense item is being edited, don't change the view.
        if(!e.getEditFlag()) {
            e.setExpansionFlag(!e.getExpansionFlag());
            notifyItemChanged(position);
        }
    }

    /*public void removeItem(int position) {
        dataset.removeItem(position);
    }*/

    /*public void removeItems(List<Integer> positions) {

    }*/

   /* private void removeRange(int positionStart, int itemCount) {
        dataset.removeRange(positionStart, itemCount);
    }*/

    /*public int updateCurrentMonthBudgetRowForDeletion(){
        return mItemClickedListener.updateCurrentMonthBudgetRowForDeletion(mExpenseDataIdList);
    }*/
   /* public List<String> getExpenseDataIdList() {
        return dataset.getExpenseDataIdList();
    }

    public Double getTotalExpenseAmount() {
        return dataset.getTotalExpenseAmount();
    }*/

}
