package com.tony.odiya.mahanadi.contract;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by tony on 19/10/17.
 */

public class MahanadiContract {
    private MahanadiContract(){}

    public static final String DATABASE_NAME = "MahanadiDatabase    ";
    public static final int DATABASE_VERSION = 1;

    public static final String SCHEME = "content";
    // The provider's authority
    public static final String AUTHORITY = "com.tony.odiya.mahanadi.provider";

    public static class Category implements BaseColumns{
        public static final String TABLE_NAME = "Category";
        public static final String COL_NAME = "name";
        public static final String COL_CREATED_ON = "createdOn";
            public static final Uri CONTENT_URI = Uri.parse(SCHEME + "://" + AUTHORITY+"/"+TABLE_NAME);
        public static final String MIME_TYPE_ROWS =
                "vnd.android.cursor.dir/vnd."+AUTHORITY+"."+TABLE_NAME;
        public static final String MIME_TYPE_SINGLE_ROW =
                "vnd.android.cursor.item/vnd."+AUTHORITY+"."+TABLE_NAME;
    }

    public static class Expense implements BaseColumns{
        public static final String TABLE_NAME = "ExpenseData";
        public static final String COL_CATEGORY = "category";
        public static final String COL_ITEM = "item";
        public static final String COL_AMOUNT = "amount";
        public static final String COL_REMARK = "remark";
        public static final String COL_CREATED_ON = "createdOn";
        public static final Uri CONTENT_URI = Uri.parse(SCHEME + "://" + AUTHORITY+"/"+TABLE_NAME);
        public static final String MIME_TYPE_ROWS =
                "vnd.android.cursor.dir/vnd."+AUTHORITY+"."+TABLE_NAME;
        public static final String MIME_TYPE_SINGLE_ROW =
                "vnd.android.cursor.item/vnd."+AUTHORITY+"."+TABLE_NAME;
    }

    public static class Budget implements BaseColumns{
        public static final String TABLE_NAME = "Budget";
        public static final String COL_TYPE = "type";
        public static final String COL_AMOUNT = "amount";
        public static final String COL_CREATED_ON = "createdOn";
        public static final String COL_END_DATE = "endDate";
        public static final Uri CONTENT_URI = Uri.parse(SCHEME + "://" + AUTHORITY+"/"+TABLE_NAME);
        public static final String MIME_TYPE_ROWS =
                "vnd.android.cursor.dir/vnd."+AUTHORITY+"."+TABLE_NAME;
        public static final String MIME_TYPE_SINGLE_ROW =
                "vnd.android.cursor.item/vnd."+AUTHORITY+"."+TABLE_NAME;
    }
}
