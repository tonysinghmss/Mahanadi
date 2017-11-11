package com.tony.odiya.mahanadi.dialoglistener;

import android.os.Bundle;

/**
 * Created by tony on 28/10/17.
 */

public interface DialogListener {
    void onBudgetReset(Bundle args);
    void onBudgetSetup(Bundle args);
    void onBudgetEdit(Bundle args);

}
