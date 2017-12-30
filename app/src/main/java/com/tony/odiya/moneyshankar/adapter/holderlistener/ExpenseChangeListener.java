package com.tony.odiya.moneyshankar.adapter.holderlistener;

import android.text.Editable;
import android.text.TextWatcher;

/**
 * Created by TONY on 11/27/2017.
 */

public class ExpenseChangeListener implements TextWatcher {
    private Boolean itemChanged = Boolean.FALSE;
    private static int mPosition =0;
    public ExpenseChangeListener() {

    }

    public void updatePosition( int position){
        mPosition = position;
    }
    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        itemChanged = Boolean.TRUE;
    }

    @Override
    public void afterTextChanged(Editable editable) {}

    public Boolean getItemChanged() {
        return itemChanged;
    }

    public void setItemChanged(Boolean itemChanged) {
        this.itemChanged = itemChanged;
    }
}
