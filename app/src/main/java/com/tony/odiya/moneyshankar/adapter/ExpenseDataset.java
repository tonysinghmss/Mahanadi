package com.tony.odiya.moneyshankar.adapter;

import android.support.v7.util.SortedList;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.util.SortedListAdapterCallback;
import android.util.Log;

import com.tony.odiya.moneyshankar.model.ExpenseData;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

/**
 * Created by TONY on 11/7/2017.
 */

public class ExpenseDataset {
    private static final String LOG_TAG = ExpenseData.class.getSimpleName();
    private static final String DATASET = "dataset";
    private static final String SORT_TYPE = "sortType";

    public enum SortType {
        CATEGORY,
        ITEM,
        AMOUNT,
        REMARK,
        CREATION_TIME
    }

    private List<String> mExpenseDataIdList = new ArrayList<>(0);
    private Double totalExpenseAmount = 0.0;


    public List<String> getExpenseDataIdList() {
        return mExpenseDataIdList;
    }

    public Double getTotalExpenseAmount() {
        return totalExpenseAmount;
    }

    private SortedList<ExpenseData> mSortedList = null;
    private SortType sortType = SortType.CREATION_TIME;

    public SortedList<ExpenseData> getSortedList(){
        return mSortedList;
    }

    public ExpenseDataset(final RecyclerView recyclerView, final RecyclerView.Adapter adapter){

        this.mSortedList = new SortedList<>(ExpenseData.class,
                new SortedList.BatchedCallback<>(new SortedListAdapterCallback<ExpenseData>(adapter) {
            @Override public int compare(ExpenseData a1, ExpenseData a2) {
                return getComparator().compare(a1, a2);
            }

            @Override public boolean areContentsTheSame(ExpenseData oldItem, ExpenseData newItem) {
                return oldItem.areContentsTheSame(newItem);
            }

            @Override public boolean areItemsTheSame(ExpenseData item1, ExpenseData item2) {
                return item1.areItemsTheSame(item2);
            }

            @Override public void onInserted(int position, int count) {
                super.onInserted(position, count);
                //recyclerView.scrollToPosition(position);
                adapter.notifyItemInserted(position);
                adapter.notifyItemRangeInserted(position,count);
            }

            @Override
            public void onRemoved(int position, int count) {
                super.onRemoved(position, count);
                adapter.notifyItemRemoved(position);
                adapter.notifyItemRangeRemoved(position, count);
            }

            @Override
            public void onMoved(int fromPosition, int toPosition) {
                super.onMoved(fromPosition, toPosition);
                adapter.notifyItemMoved(fromPosition, toPosition);
            }

            @Override
            public void onChanged(int position, int count) {
                super.onChanged(position, count);
                adapter.notifyItemChanged(position);
                adapter.notifyItemRangeChanged(position, count);
            }
        }));

    }

    /*public void restore(Bundle bundle) {
        if (bundle != null) {
            ArrayList<ExpenseData> expenses = bundle.getParcelableArrayList(DATASET);
            this.sortType = (SortType) bundle.getSerializable(SORT_TYPE);
            mSortedList.beginBatchedUpdates();
            mSortedList.addAll(expenses);
            mSortedList.endBatchedUpdates();
        }
    }*/

    /*public Bundle asBundle() {
        ArrayList<ExpenseData> expenseDatas = new ArrayList<>();
        for (int i = 0; i < mSortedList.size(); i++) {
            expenseDatas.add(mSortedList.get(i));
        }
        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList(DATASET, expenseDatas);
        bundle.putSerializable(SORT_TYPE, sortType);
        return bundle;
    }
*/
    public List<ExpenseData> asList(){
        ArrayList<ExpenseData> expenseDatas = new ArrayList<>();
        for (int i = 0; i < mSortedList.size(); i++) {
            expenseDatas.add(mSortedList.get(i));
        }
        return expenseDatas;
    }

    public int size() {
        if(mSortedList != null)
        return mSortedList.size();
        else return 0;
    }

    public void changeSortType(SortType sortType) {
        if (!this.sortType.equals(sortType)) {
            this.sortType = sortType;
            List<ExpenseData> expenses = new ArrayList<>();
            for (int j = 0; j < mSortedList.size(); j++) {
                expenses.add(mSortedList.get(j));
            }
            mSortedList.clear();
            mSortedList.addAll(expenses);
            mSortedList.endBatchedUpdates();
        }
    }

    public ExpenseData getExpenseData(int position){
        return mSortedList.get(position);
    }

    public void remove(ExpenseData ed){
        mSortedList.beginBatchedUpdates();
        mSortedList.remove(ed);
        mSortedList.endBatchedUpdates();
    }

    public void add(ExpenseData ed){
        mSortedList.beginBatchedUpdates();
        mSortedList.add(ed);
        mSortedList.endBatchedUpdates();
    }

    public void add(Collection<ExpenseData> ed){
        mSortedList.beginBatchedUpdates();
        mSortedList.addAll(ed);
        mSortedList.endBatchedUpdates();
    }

    public void removeItem(int position) {
        // Add the id of removed item into list. This id list will be used to update database in fragment.
        if(mSortedList.size() == 0)
            return;
        ExpenseData expense = mSortedList.get(position);
        mExpenseDataIdList.add(expense.getExpenseId());
        totalExpenseAmount += Double.valueOf(expense.getAmount());
        mSortedList.beginBatchedUpdates();
        mSortedList.removeItemAt(position);
        mSortedList.endBatchedUpdates();
    }

    public void removeRange(int positionStart, int itemCount) {
        mSortedList.beginBatchedUpdates();
        for (int i = 0; i < itemCount; ++i) {
            ExpenseData expense = mSortedList.get(positionStart);
            mExpenseDataIdList.add(expense.getExpenseId());
            totalExpenseAmount += Double.valueOf(expense.getAmount());
            mSortedList.removeItemAt(positionStart);
        }
        mSortedList.endBatchedUpdates();
    }

    public void removeItems(List<Integer> positions) {
        Log.d(LOG_TAG, "Remove Items");
        totalExpenseAmount = 0.0;
        /*for(Integer i : positions){
            removeItem(i);
        }*/
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
                // Jarvis says Awesome logic!!!
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

    public void replaceAll(Set<ExpenseData> models) {
        mSortedList.beginBatchedUpdates();
        mSortedList.clear();
        mSortedList.addAll(models);
        mSortedList.endBatchedUpdates();
    }

    private Comparator<ExpenseData> getComparator() {
        switch (sortType) {
            case ITEM:
                return ExpenseData.ITEM_COMPARATOR;
            case CATEGORY:
                return ExpenseData.CATEGORY_COMPARATOR;
            case AMOUNT:
                return ExpenseData.AMOUNT_COMPARATOR;
            case REMARK:
                return ExpenseData.REMARK_COMPARATOR;
            default:
                return ExpenseData.CREATION_TIME_COMPARATOR;
        }
    }

}
