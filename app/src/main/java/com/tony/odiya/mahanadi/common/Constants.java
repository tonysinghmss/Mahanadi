package com.tony.odiya.mahanadi.common;

import com.tony.odiya.mahanadi.contract.MahanadiContract;

/**
 * Created by tony on 20/10/17.
 */

public final class Constants {
    private Constants(){}

    public static final String YEARLY = "YEARLY";
    public static final String MONTHLY = "MONTHLY";
    public static final String WEEKLY = "WEEKLY";
    public static final String DAILY = "DAILY";

    public static final String START_TIME = "starttime";
    public static final String END_TIME = "endtime";

    public static final String GROCERY = "GROCERY";
    public static final String ELECTRONICS = "ELECTRONICS";
    public static final String FOOD = "FOOD";
    public static final String HOME = "HOME";
    public static final String CLOTHES = "CLOTHES";
    public static final String CUSTOM = "CUSTOM";

    public static final String SAVE_EXPENSE_CODE = "1";
    public static final String SAVE_BUDGET_CODE = "2";
    public static final String QUERY_BUDGET_CODE = "3";
    public static final String UPDATE_BUDGET_CODE = "4";
    public static final String SAVE_BUDGET_ROW_KEY = "SavedBudgetRowId";
    public static final String SAVE_BUDGET_AMOUNT_KEY = "MonthlyBudget";
    public static final int REQUEST_EXPENSE_CODE =1;
    public static final int REQUEST_BUDGET_SETUP_CODE =2;

    public static final String BUDGET_TYPE_MONTHLY ="MONTHLY_BUDGET";

    public static final String[] CURRENT_BUDGET_PROJECTION = {
        MahanadiContract.Budget.COL_AMOUNT +" AS BUDGET "
    };

}