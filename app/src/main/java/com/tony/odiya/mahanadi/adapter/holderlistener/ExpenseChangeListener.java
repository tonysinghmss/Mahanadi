package com.tony.odiya.mahanadi.adapter.holderlistener;

import android.text.Editable;
import android.text.TextWatcher;

/**
 * Created by TONY on 11/27/2017.
 */

public class ExpenseChangeListener implements TextWatcher {
    private Boolean mItemChanged = Boolean.FALSE;
    private static int mPosition =0;
    public ExpenseChangeListener(Boolean changeFlag) {
        mItemChanged = changeFlag;
    }

    public void updatePosition( int position){
        mPosition = position;
    }
    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        mItemChanged = Boolean.TRUE;
    }

    @Override
    public void afterTextChanged(Editable editable) {}
}
