package com.tony.odiya.mahanadi.adapter;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.View;
import android.widget.TextView;

import com.tony.odiya.mahanadi.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by TONY on 11/2/2017.
 */

public abstract class SelectableAdapter<VH extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<VH> {
    private SparseBooleanArray selectedItems;

    public SelectableAdapter(){
        selectedItems = new SparseBooleanArray();
    }

    public boolean isSelected(int position){
        return getSelectedItems().contains(position);
    }

    public List<Integer> getSelectedItems(){
        List<Integer> items = new ArrayList<>(selectedItems.size());
        for(int i =0; i < selectedItems.size(); i++){
            items.add(selectedItems.keyAt(i));
        }
        return items;
    }

    public void toggleSelection(int position){
        /*View card = v.findViewById(R.id.expense_card_view);
        TextView categoryView = (TextView) v.findViewById(R.id.category);
        TextView itemView = (TextView) v.findViewById(R.id.item);
        TextView amountView = (TextView) v.findViewById(R.id.amount);
        TextView remarkView = (TextView) v.findViewById(R.id.remark);*/
        if(selectedItems.get(position,false)){
            selectedItems.delete(position);
            //v.setSelected(false);
        } else {
            selectedItems.put(position,true);
            //v.setSelected(true);
        }
        notifyItemChanged(position);
    }

    public void clearSelection(){
        List<Integer> selection = getSelectedItems();
        selectedItems.clear();
        for(Integer i : selection){
            notifyItemChanged(i);
        }
    }
    public  int getSelectedItemCount(){
        return selectedItems.size();
    }
}
