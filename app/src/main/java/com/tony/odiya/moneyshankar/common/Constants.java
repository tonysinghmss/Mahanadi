package com.tony.odiya.moneyshankar.common;

import com.tony.odiya.moneyshankar.contract.MahanadiContract;

/**
 * Created by tony on 20/10/17.
 */

public final class Constants {
    private Constants(){}

    public static final String YEARLY = "YEARLY";
    public static final String MONTHLY = "MONTHLY";
    public static final String WEEKLY = "WEEKLY";
    public static final String DAILY = "DAILY";

    public static final String ITEM = "ITEM";
    public static final String CATEGORY = "CATEGORY";
    public static final String TIME_TREND = "TIME_TREND";

    public static final String START_TIME = "starttime";
    public static final String END_TIME = "endtime";
    public static final String CATEGORY_NAME = "category";

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
    public static final String TREND_EXPENSE_CODE = "5";
    public static final String TOTAL_EXPENSE_CODE = "6";
    public static final String QUERY_EXPENSE_CODE = "7";
    public static final String UPDATE_EXPENSE_CODE = "8";

    public static final String SAVE_BUDGET_ROW_KEY = "SAVE_BUDGET_ROW_ID";
    public static final String SAVE_BUDGET_AMOUNT_KEY = "SAVE_BUDGET_AMOUNT_KEY";
    public static final String UPDATE_BUDGET_AMOUNT_COUNT = "UPDATE_BUDGET_AMOUNT_COUNT";
    public static final String DELETE_BUDGET_COUNT = "DELETE_BUDGET_COUNT";
    public static final String DELETE_EXPENSE_COUNT = "DELETE_EXPENSE_COUNT";

    public static final int REQUEST_EXPENSE_ADD_CODE = 1;
    public static final int REQUEST_BUDGET_SETUP_CODE = 2;
    public static final int REQUEST_DELETE_EXPENSE_CODE = 3;
    public static final int REQUEST_BUDGET_EDIT_CODE = 4;
    public static final int REQUEST_BUDGET_RESET_CODE = 5;
    public static final int REQUEST_EXPENSE_EDIT_CODE = 6;
    public static final int RESPONSE_OK = -1;

    public static final String EXPENSE_ID = "EXPENSE_ID";

    public static final String BUDGET_LEFT = "BUDGET_LEFT";

    public static final String BUDGET_TYPE_MONTHLY ="MONTHLY_BUDGET";

    public static final String[] CURRENT_BUDGET_PROJECTION = {
        MahanadiContract.Budget.COL_AMOUNT +" AS BUDGET "
    };

    public static final int REQUEST_AGREEMENT_CODE = 7;

}
