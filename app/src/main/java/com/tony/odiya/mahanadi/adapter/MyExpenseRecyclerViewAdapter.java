package com.tony.odiya.mahanadi.adapter;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.tony.odiya.mahanadi.R;
import com.tony.odiya.mahanadi.fragment.ExpenseFragment.OnListFragmentInteractionListener;
import com.tony.odiya.mahanadi.model.ExpenseData;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link ExpenseData} and makes a call to the
 * specified {@link OnListFragmentInteractionListener}.
 */
public class MyExpenseRecyclerViewAdapter extends SelectableAdapter<MyExpenseRecyclerViewAdapter.ViewHolder> {
    private static final String LOG_TAG = MyExpenseRecyclerViewAdapter.class.getSimpleName();
    private final List<ExpenseData> mValues;
    private final OnListFragmentInteractionListener mContextListener;

    private OnRecyclerItemClickedListener mItemLongClickedListener;

    public interface OnRecyclerItemClickedListener {
        void onItemClicked(int position);
        boolean onItemLongClicked(int position);
    }

    public MyExpenseRecyclerViewAdapter(List<ExpenseData> items, OnListFragmentInteractionListener listener, OnRecyclerItemClickedListener itemLongClickedListener) {
        mValues = items;
        mContextListener = listener;
        mItemLongClickedListener = itemLongClickedListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //Take viewtype and change the color of views in future
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.expense_card, parent, false);
        return new ViewHolder(view, mItemLongClickedListener);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        holder.mItem = mValues.get(position);
        holder.mCategoryView.setText(mValues.get(position).category);
        holder.mItemView.setText(mValues.get(position).item);
        holder.mAmountView.setText(mValues.get(position).amount);
        holder.mRemarkView.setText(mValues.get(position).remark);
        holder.mSelectedOverlay.setVisibility(isSelected(position)?View.VISIBLE:View.INVISIBLE);

       /* holder.mView.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                 if (null != mContextListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mContextListener.onListFragmentInteraction(holder.mItem);
                }
            }
        });
        holder.mView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                mItemLongClickedListener.onItemLongClicked(position);
                return true;
            }
        });*/

    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener{
        public final View mView;
        public final TextView mCategoryView;
        public final TextView mItemView;
        public final TextView mAmountView;
        public final TextView mRemarkView;
        public final View mSelectedOverlay;
        public ExpenseData mItem;
        private OnRecyclerItemClickedListener recyclerItemClickedListener;
        public ViewHolder(View view, OnRecyclerItemClickedListener recyclerItemClickedListener) {
            super(view);
            mView = view;
            mCategoryView = (TextView) view.findViewById(R.id.category);
            mItemView = (TextView) view.findViewById(R.id.item);
            mAmountView = (TextView) view.findViewById(R.id.amount);
            mRemarkView = (TextView) view.findViewById(R.id.remark);
            mSelectedOverlay = (View) view.findViewById(R.id.selected_overlay);
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
    }

    public void removeItem(int position) {
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
            mValues.remove(positionStart);
        }
        notifyItemRangeRemoved(positionStart, itemCount);
    }
}
