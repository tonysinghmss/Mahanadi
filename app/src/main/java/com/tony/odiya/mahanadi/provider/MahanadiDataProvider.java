package com.tony.odiya.mahanadi.provider;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.util.SparseArray;

import com.tony.odiya.mahanadi.contract.MahanadiContract;

import java.util.Arrays;

/**
 * Created by tony on 19/10/17.
 */

public class MahanadiDataProvider extends ContentProvider{
    public static final String LOG_TAG = MahanadiDataProvider.class.getSimpleName();
    // Indicates that type of incoming query
    public static final int EXPENSE_LIST = 1;
    public static final int EXPENSE_ROW = 2;


    public static final int CATEGORY_LIST = 3;
    public static final int CATEGORY_ROW = 4;

    public static final int BUDGET_LIST = 5;
    public static final int BUDGET_ROW = 6;

    public static final int EXPENSE_GROUPBY_CATEGORY = 7;
    public static final int EXPENSE_GROUPBY_HOUROFDAY = 8;
    public static final int EXPENSE_GROUPBY_DAYOFWEEK = 9;
    public static final int EXPENSE_GROUPBY_WEEKOFMONTH = 10;
    public static final int EXPENSE_GROUPBY_MONTHOFYEAR = 11;

    public static final int INVALID_URI = -1;

    private static final String COMMA_SEP = ",";
    private static final String CREATE_TABLE = "CREATE TABLE ";
    private static final String DROP_TABLE = "DROP TABLE IF EXISTS ";
    private static final String REAL_TYPE = "REAL";
    private static final String TEXT_TYPE = "TEXT";
    private static final String INTEGER_TYPE = "INTEGER";
    private static final String PRIMARY_KEY_TYPE = "INTEGER PRIMARY KEY";
    public static final String SQL_CREATE_CATEGORY =
            CREATE_TABLE + MahanadiContract.Category.TABLE_NAME + "(" +
                    MahanadiContract.Category._ID + " "+PRIMARY_KEY_TYPE+ COMMA_SEP+
                    MahanadiContract.Category.COL_NAME +" "+TEXT_TYPE+" "+"UNIQUE NOT NULL"+ COMMA_SEP+
                    MahanadiContract.Category.COL_CREATED_ON +" "+INTEGER_TYPE+")";
    public static final String SQL_DROP_CATEGORY =
            DROP_TABLE+ MahanadiContract.Category.TABLE_NAME;
    //Expense table
    public static final String SQL_CREATE_EXPENSE =
            CREATE_TABLE + MahanadiContract.Expense.TABLE_NAME + "(" +
                    MahanadiContract.Expense._ID + " "+PRIMARY_KEY_TYPE+ COMMA_SEP+
                    MahanadiContract.Expense.COL_CATEGORY +" "+TEXT_TYPE+ COMMA_SEP+
                    MahanadiContract.Expense.COL_ITEM +" "+TEXT_TYPE+ COMMA_SEP+
                    MahanadiContract.Expense.COL_AMOUNT+" "+REAL_TYPE+COMMA_SEP+
                    MahanadiContract.Expense.COL_REMARK +" "+TEXT_TYPE+ COMMA_SEP+
                    MahanadiContract.Expense.COL_CREATED_ON +" "+INTEGER_TYPE+")";
    public static final String SQL_DROP_EXPENSE =
            DROP_TABLE + MahanadiContract.Expense.TABLE_NAME;
    //Budget table
    public static final String SQL_CREATE_BUDGET =
            CREATE_TABLE + MahanadiContract.Budget.TABLE_NAME + "(" +
                    MahanadiContract.Budget._ID + " "+PRIMARY_KEY_TYPE+ COMMA_SEP+
                    MahanadiContract.Budget.COL_TYPE +" "+TEXT_TYPE+ COMMA_SEP+
                    MahanadiContract.Budget.COL_AMOUNT +" "+TEXT_TYPE+ COMMA_SEP+
                    MahanadiContract.Budget.COL_CREATED_ON +" "+INTEGER_TYPE+ COMMA_SEP+
                    MahanadiContract.Budget.COL_END_DATE +" "+INTEGER_TYPE+")";
    public static final String SQL_DROP_BUDGET =
            DROP_TABLE + MahanadiContract.Budget.TABLE_NAME;

    // Defines a helper object that matches content URIs to table-specific parameters
    private static final UriMatcher sUriMatcher;
    // Defines an helper object for the backing database
    private SQLiteOpenHelper mHelper;
    // Stores the MIME types served by this provider
    private static final SparseArray<String> sMimeTypes;
    /*
     * Initializes meta-data used by the content provider:
     * - UriMatcher that maps content URIs to codes
     * - MimeType array that returns the custom MIME type of a table
     */
    static {

        // Creates an object that associates content URIs with numeric codes
        sUriMatcher = new UriMatcher(0);

        /*
         * Sets up an array that maps content URIs to MIME types, via a mapping between the
         * URIs and an integer code. These are custom MIME types that apply to tables and rows
         * in this particular provider.
         */
        sMimeTypes = new SparseArray<>();
        // Sets up CATEGORY_LIST as code to represent URI for multiple rows of Category table.
        sUriMatcher.addURI(
                MahanadiContract.AUTHORITY,
                MahanadiContract.Category.TABLE_NAME,
                CATEGORY_LIST);
        sUriMatcher.addURI(
                MahanadiContract.AUTHORITY,
                MahanadiContract.Category.TABLE_NAME+"/#",
                CATEGORY_ROW);
        // Sets up EXPENSE_LIST as code to represent URI for multiple rows of ExpenseData table.
        sUriMatcher.addURI(
                MahanadiContract.AUTHORITY,
                MahanadiContract.Expense.TABLE_NAME,
                EXPENSE_LIST);
        sUriMatcher.addURI(
                MahanadiContract.AUTHORITY,
                MahanadiContract.Expense.TABLE_NAME+"/#",
                EXPENSE_ROW);
        sUriMatcher.addURI(
                MahanadiContract.AUTHORITY,
                MahanadiContract.Expense.TABLE_NAME+"/g",
                EXPENSE_GROUPBY_CATEGORY);
        sUriMatcher.addURI(
                MahanadiContract.AUTHORITY,
                MahanadiContract.Expense.TABLE_NAME+"/hourOfDay",
                EXPENSE_GROUPBY_HOUROFDAY);
        sUriMatcher.addURI(
                MahanadiContract.AUTHORITY,
                MahanadiContract.Expense.TABLE_NAME+"/dayOfWeek",
                EXPENSE_GROUPBY_DAYOFWEEK);
        sUriMatcher.addURI(
                MahanadiContract.AUTHORITY,
                MahanadiContract.Expense.TABLE_NAME+"/weekOfMonth",
                EXPENSE_GROUPBY_WEEKOFMONTH);
        sUriMatcher.addURI(
                MahanadiContract.AUTHORITY,
                MahanadiContract.Expense.TABLE_NAME+"/monthOfYear",
                EXPENSE_GROUPBY_MONTHOFYEAR);
        // Sets up BUDGET_LIST as code to represent URI for multiple rows of ExpenseData table.
        sUriMatcher.addURI(
                MahanadiContract.AUTHORITY,
                MahanadiContract.Budget.TABLE_NAME,
                BUDGET_LIST);
        sUriMatcher.addURI(
                MahanadiContract.AUTHORITY,
                MahanadiContract.Budget.TABLE_NAME+"/#",
                BUDGET_ROW);
        // Specifies a custom MIME type for a multiple rows of Category table.
        sMimeTypes.put(
                CATEGORY_LIST,
                MahanadiContract.Category.MIME_TYPE_ROWS);
        sMimeTypes.put(
                CATEGORY_ROW,
                MahanadiContract.Category.MIME_TYPE_SINGLE_ROW);
        // Specifies a custom MIME type for a multiple rows of ExpenseData table.
        sMimeTypes.put(
                EXPENSE_LIST,
                MahanadiContract.Expense.MIME_TYPE_ROWS);
        sMimeTypes.put(
                EXPENSE_ROW,
                MahanadiContract.Expense.MIME_TYPE_SINGLE_ROW);
        sMimeTypes.put(EXPENSE_GROUPBY_CATEGORY,
                MahanadiContract.Expense.MIME_TYPE_GROUPBY_CATEGORY);
        sMimeTypes.put(EXPENSE_GROUPBY_HOUROFDAY,
                MahanadiContract.Expense.MIME_TYPE_GROUPBY_HOUROFDAY);
        sMimeTypes.put(EXPENSE_GROUPBY_DAYOFWEEK,
                MahanadiContract.Expense.MIME_TYPE_GROUPBY_DAYOFWEEK);
        sMimeTypes.put(EXPENSE_GROUPBY_WEEKOFMONTH,
                MahanadiContract.Expense.MIME_TYPE_GROUPBY_WEEKOFMONTH);
        sMimeTypes.put(EXPENSE_GROUPBY_MONTHOFYEAR,
                MahanadiContract.Expense.MIME_TYPE_GROUPBY_MONTHOFYEAR);
        // Specifies a custom MIME type for a multiple rows of ExpenseData table.
        sMimeTypes.put(
                BUDGET_LIST,
                MahanadiContract.Budget.MIME_TYPE_ROWS);
        sMimeTypes.put(
                BUDGET_ROW,
                MahanadiContract.Budget.MIME_TYPE_SINGLE_ROW);
    }
    // Closes the SQLite database helper class, to avoid memory leaks
    public void close() {
        mHelper.close();
    }

    private class DataProviderHelper extends SQLiteOpenHelper {
        DataProviderHelper(Context context) {
            super(context,
                    MahanadiContract.DATABASE_NAME,
                    null,
                    MahanadiContract.DATABASE_VERSION);
        }

        private void dropTables(SQLiteDatabase db) {
            // If the table doesn't exist, don't throw an error
            db.execSQL(SQL_DROP_CATEGORY);
            db.execSQL(SQL_DROP_EXPENSE);
            db.execSQL(SQL_DROP_BUDGET);

        }
        @Override
        public void onCreate(SQLiteDatabase db) {
            // Creates the tables in the backing database for this provider
            db.execSQL(SQL_CREATE_CATEGORY);
            db.execSQL(SQL_CREATE_EXPENSE);
            db.execSQL(SQL_CREATE_BUDGET);
        }
        /**
         * Handles upgrading the database from a previous version. Drops the old tables and creates
         * new ones.
         *
         * @param db The database to upgrade
         * @param version1 The old database version
         * @param version2 The new database version
         */
        @Override
        public void onUpgrade(SQLiteDatabase db, int version1, int version2) {
            Log.w(DataProviderHelper.class.getName(),
                    "Upgrading database from version " + version1 + " to "
                            + version2 + ", which will destroy all the existing data");

            // Drops all the existing tables in the database
            dropTables(db);

            // Invokes the onCreate callback to build new tables
            onCreate(db);
        }
        /**
         * Handles downgrading the database from a new to a previous version. Drops the old tables
         * and creates new ones.
         * @param db The database object to downgrade
         * @param version1 The old database version
         * @param version2 The new database version
         */
        @Override
        public void onDowngrade(SQLiteDatabase db, int version1, int version2) {
            Log.w(DataProviderHelper.class.getName(),
                    "Downgrading database from version " + version1 + " to "
                            + version2 + ", which will destroy all the existing data");

            // Drops all the existing tables in the database
            dropTables(db);

            // Invokes the onCreate callback to build new tables
            onCreate(db);

        }
    }
    @Override
    public boolean onCreate() {
        // Creates a new database helper object
        mHelper = new DataProviderHelper(getContext());
        return true;
    }
    /**
     * Returns the mimeType associated with the Uri (query).
     * @see android.content.ContentProvider#getType(Uri)
     * @param uri the content URI to be checked
     * @return the corresponding MIMEtype
     */
    @Nullable
    @Override
    public String getType(Uri uri) {
        return sMimeTypes.get(sUriMatcher.match(uri));
    }

    /**
     * Returns the result of querying the chosen table.
     * @param uri The content URI of the table
     * @param projection The names of the columns to return in the cursor
     * @param selection The selection clause for the query
     * @param selectionArgs An array of Strings containing search criteria
     * @param sortOrder A clause defining the order in which the retrieved rows should be sorted
     * @return The query results, as a {@link android.database.Cursor} of rows and columns
     */
    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        //Log.d(LOG_TAG, "Inside Mahanadi Data Provider query : "+ uri.toString() );
        SQLiteDatabase db = mHelper.getReadableDatabase();
        Cursor cursor = null;
        //Log.d(LOG_TAG,"Uri Matcher case : "+Integer.toString(sUriMatcher.match(uri) ));
        String groupBy = null;
        String query = null;

        switch (sUriMatcher.match(uri)) {
            case EXPENSE_LIST:
                if (TextUtils.isEmpty(sortOrder)) sortOrder = "_ID ASC";
                query = SQLiteQueryBuilder.buildQueryString(false,MahanadiContract.Expense.TABLE_NAME,
                        projection,selection,null,null,sortOrder,null);
                Log.d(LOG_TAG, "Query : "+query);
                Log.d(LOG_TAG, " Query args : "+ Arrays.toString(selectionArgs));

                cursor = db.query(
                        MahanadiContract.Expense.TABLE_NAME,    //Table pointName
                        projection,                                 //Columns to be shown
                        selection,                                  //Filter clause
                        selectionArgs,                              //Filter arguments
                        null,                                       //group by clause
                        null,                                       //having clause
                        sortOrder                                   //order by clause
                );
                break;
            case EXPENSE_ROW:
                query = SQLiteQueryBuilder.buildQueryString(false,MahanadiContract.Expense.TABLE_NAME,
                        projection,selection,null,null,sortOrder,null);
                Log.d(LOG_TAG, "Query : "+query);
                Log.d(LOG_TAG, " Query args : "+ Arrays.toString(selectionArgs));
                cursor = db.query(
                        MahanadiContract.Expense.TABLE_NAME,    //Table pointName
                        projection,                                 //Columns to be shown
                        selection,                                  //Filter clause
                        selectionArgs,                              //Filter arguments
                        null,                                       //group by clause
                        null,                                       //having clause
                        sortOrder                                   //order by clause
                );
                break;
            case EXPENSE_GROUPBY_CATEGORY:
                if (TextUtils.isEmpty(sortOrder)) sortOrder = MahanadiContract.Expense.COL_CATEGORY+" ASC";
                groupBy = MahanadiContract.Expense.COL_CATEGORY;
                query = SQLiteQueryBuilder.buildQueryString(false,MahanadiContract.Expense.TABLE_NAME,
                        projection,selection,groupBy,null,sortOrder,null);
                Log.d(LOG_TAG, "Query : "+query);
                Log.d(LOG_TAG, " Query args : "+ Arrays.toString(selectionArgs));
                cursor = db.query(
                        MahanadiContract.Expense.TABLE_NAME,    //Table pointName
                        projection,                                 //Columns to be shown
                        selection,                                  //Filter clause
                        selectionArgs,                              //Filter arguments
                        groupBy,                                       //group by clause
                        null,                                       //having clause
                        sortOrder                                   //order by clause
                );
                break;
            case EXPENSE_GROUPBY_HOUROFDAY:
                if (TextUtils.isEmpty(sortOrder)) sortOrder = "strftime('%Y-%m-%dT%H:00:00.000',"+MahanadiContract.Expense.COL_CREATED_ON +") ASC";
                groupBy = "strftime('%Y-%m-%dT%H:00:00.000',"+MahanadiContract.Expense.COL_CREATED_ON +")";
                query = SQLiteQueryBuilder.buildQueryString(false,MahanadiContract.Expense.TABLE_NAME,
                        projection,selection,groupBy,null,sortOrder,null);
                Log.d(LOG_TAG, "Query : "+query);
                Log.d(LOG_TAG, " Query args : "+ Arrays.toString(selectionArgs));
                cursor = db.query(
                        MahanadiContract.Expense.TABLE_NAME,    //Table pointName
                        projection,                                 //Columns to be shown
                        selection,                                  //Filter clause
                        selectionArgs,                              //Filter arguments
                        groupBy,                                       //group by clause
                        null,                                       //having clause
                        sortOrder                                   //order by clause
                );
                break;
            case EXPENSE_GROUPBY_DAYOFWEEK:
                if (TextUtils.isEmpty(sortOrder)) sortOrder =  "strftime('%w',"+MahanadiContract.Expense.COL_CREATED_ON +") ASC";
                groupBy = "strftime('%w',"+MahanadiContract.Expense.COL_CREATED_ON +")";
                query = SQLiteQueryBuilder.buildQueryString(false,MahanadiContract.Expense.TABLE_NAME,
                        projection,selection,groupBy,null,sortOrder,null);
                Log.d(LOG_TAG, "Query : "+query);
                Log.d(LOG_TAG, " Query args : "+ Arrays.toString(selectionArgs));
                cursor = db.query(
                        MahanadiContract.Expense.TABLE_NAME,    //Table pointName
                        projection,                                 //Columns to be shown
                        selection,                                  //Filter clause
                        selectionArgs,                              //Filter arguments
                        groupBy,                                       //group by clause
                        null,                                       //having clause
                        sortOrder                                   //order by clause
                );
                break;
            case EXPENSE_GROUPBY_WEEKOFMONTH:
                if (TextUtils.isEmpty(sortOrder)) sortOrder =  "strftime('%d',"+MahanadiContract.Expense.COL_CREATED_ON+") - strftime('%w', "
                        +MahanadiContract.Expense.COL_CREATED_ON+") ||'-'|| strftime('%d',"+
                        MahanadiContract.Expense.COL_CREATED_ON+") - strftime('%w', "+
                        MahanadiContract.Expense.COL_CREATED_ON+")+6 ASC";
                groupBy = "(strftime('%d',"+MahanadiContract.Expense.COL_CREATED_ON+") - strftime('%w', "
                        +MahanadiContract.Expense.COL_CREATED_ON+")) ||'-'|| (strftime('%d',"+
                        MahanadiContract.Expense.COL_CREATED_ON+") - strftime('%w', "+
                        MahanadiContract.Expense.COL_CREATED_ON+")+6 )";
                query = SQLiteQueryBuilder.buildQueryString(false,MahanadiContract.Expense.TABLE_NAME,
                        projection,selection,groupBy,null,sortOrder,null);
                Log.d(LOG_TAG, "Query : "+query);
                Log.d(LOG_TAG, " Query args : "+ Arrays.toString(selectionArgs));
                cursor = db.query(
                        MahanadiContract.Expense.TABLE_NAME,    //Table pointName
                        projection,                                 //Columns to be shown
                        selection,                                  //Filter clause
                        selectionArgs,                              //Filter arguments
                        groupBy,                                       //group by clause
                        null,                                       //having clause
                        sortOrder                                   //order by clause
                );
                break;
            case EXPENSE_GROUPBY_MONTHOFYEAR:
                if (TextUtils.isEmpty(sortOrder)) sortOrder =   "strftime('%m',"+MahanadiContract.Expense.COL_CREATED_ON +") ASC";
                groupBy = "strftime('%m',"+MahanadiContract.Expense.COL_CREATED_ON +")";
                query = SQLiteQueryBuilder.buildQueryString(false,MahanadiContract.Expense.TABLE_NAME,
                        projection,selection,groupBy,null,sortOrder,null);
                Log.d(LOG_TAG, "Query : "+query);
                Log.d(LOG_TAG, " Query args : "+ Arrays.toString(selectionArgs));
                cursor = db.query(
                        MahanadiContract.Expense.TABLE_NAME,    //Table pointName
                        projection,                                 //Columns to be shown
                        selection,                                  //Filter clause
                        selectionArgs,                              //Filter arguments
                        groupBy,                                       //group by clause
                        null,                                       //having clause
                        sortOrder                                   //order by clause
                );
                break;
            case CATEGORY_LIST:
                if (TextUtils.isEmpty(sortOrder)) sortOrder = "_ID ASC";
                query = SQLiteQueryBuilder.buildQueryString(false,MahanadiContract.Category.TABLE_NAME,
                        projection,selection,null,null,sortOrder,null);
                Log.d(LOG_TAG, "Query : "+query);
                Log.d(LOG_TAG, " Query args : "+ Arrays.toString(selectionArgs));
                cursor = db.query(
                        MahanadiContract.Category.TABLE_NAME,    //Table pointName
                        projection,                                 //Columns to be shown
                        selection,                                  //Filter clause
                        selectionArgs,                              //Filter arguments
                        null,                                       //group by clause
                        null,                                       //having clause
                        sortOrder                                   //order by clause
                );
                break;
            case BUDGET_LIST:
                if (TextUtils.isEmpty(sortOrder)) sortOrder = "_ID ASC";
                query = SQLiteQueryBuilder.buildQueryString(false,MahanadiContract.Budget.TABLE_NAME,
                        projection,selection,null,null,sortOrder,null);
                Log.d(LOG_TAG, "Query : "+query);
                Log.d(LOG_TAG, " Query args : "+ Arrays.toString(selectionArgs));
                cursor = db.query(
                        MahanadiContract.Budget.TABLE_NAME,    //Table pointName
                        projection,                                 //Columns to be shown
                        selection,                                  //Filter clause
                        selectionArgs,                              //Filter arguments
                        null,                                       //group by clause
                        null,                                       //having clause
                        sortOrder                                   //order by clause
                );
                break;
            case BUDGET_ROW:
                query = SQLiteQueryBuilder.buildQueryString(false,MahanadiContract.Budget.TABLE_NAME,
                        projection,selection,null,null,sortOrder,null);
                Log.d(LOG_TAG, "Query : "+query);
                Log.d(LOG_TAG, " Query args : "+ Arrays.toString(selectionArgs));
                cursor = db.query(
                        MahanadiContract.Budget.TABLE_NAME,    //Table pointName
                        projection,                                 //Columns to be shown
                        selection,                                  //Filter clause
                        selectionArgs,                              //Filter arguments
                        null,                                       //group by clause
                        null,                                       //having clause
                        null                                   //order by clause
                );
                break;
            default:
                throw new IllegalArgumentException("Query -- Invalid URI:" + uri);
        }
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        Log.d(LOG_TAG, "Inside Mahanadi Data Provider insert");
        // Creates a writeable database or gets one from cache
        SQLiteDatabase localSQLiteDatabase = mHelper.getWritableDatabase();
        long id = -1;
        Log.d(LOG_TAG, "Insert Uri matched with case : "+sUriMatcher.match(uri));
        switch (sUriMatcher.match(uri)){
            case EXPENSE_ROW:
                // localSQLiteDatabase = mHelper.getWritableDatabase();
                // Inserts a single row into the table and returns the new row's _id value
                 id = localSQLiteDatabase.insertOrThrow(
                        MahanadiContract.Expense.TABLE_NAME,
                        MahanadiContract.Expense.COL_REMARK,//null column hack
                        values
                );
                break;
            case CATEGORY_ROW:
                // localSQLiteDatabase = mHelper.getWritableDatabase();
                // Inserts the row into the table and returns the new row's _id value
                id = localSQLiteDatabase.insertOrThrow(
                        MahanadiContract.Category.TABLE_NAME,
                        null,
                        values
                );
                break;
            case BUDGET_ROW:
                // localSQLiteDatabase = mHelper.getWritableDatabase();
                // Inserts the row into the table and returns the new row's _id value
                id = localSQLiteDatabase.insertOrThrow(
                        MahanadiContract.Budget.TABLE_NAME,
                        null,
                        values
                );
                break;
            default:
                throw new IllegalArgumentException("Insert -- Invalid URI:" + uri);
        }
        if (-1 != id) {
            Log.d(LOG_TAG, "Row inserted with id :"+id);
            getContext().getContentResolver().notifyChange(uri, null);
            return Uri.withAppendedPath(uri, Long.toString(id));
        }else
            return null;
    }
    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        Log.d(LOG_TAG, "Inside MahanadiDataprovider delete");
        int deleteCount = 0;
        // Creates a writeable database or gets one from cache
        SQLiteDatabase localSQLiteDatabase = mHelper.getWritableDatabase();
        switch (sUriMatcher.match(uri)){
            case EXPENSE_ROW:
                deleteCount = localSQLiteDatabase.delete(MahanadiContract.Expense.TABLE_NAME, selection, selectionArgs);
                break;
            case EXPENSE_LIST:
                deleteCount = localSQLiteDatabase.delete(MahanadiContract.Expense.TABLE_NAME, selection, selectionArgs);
                break;
            case BUDGET_ROW:
                deleteCount = localSQLiteDatabase.delete(MahanadiContract.Budget.TABLE_NAME, selection, selectionArgs);
                break;
            case BUDGET_LIST:
                deleteCount = localSQLiteDatabase.delete(MahanadiContract.Budget.TABLE_NAME, selection, selectionArgs);
                break;
            /*case CATEGORY_ROW:
                break;*/
            default:
                throw new UnsupportedOperationException("Delete multiple rows -- unsupported operation " + uri);
        }
        if(deleteCount>0){
            getContext().getContentResolver().notifyChange(uri,null);
        }
        return deleteCount;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        Log.d(LOG_TAG, "Inside MahanadiDataprovider update");
        // Creates a writeable database or gets one from cache
        SQLiteDatabase localSQLiteDatabase = mHelper.getWritableDatabase();
        int updateCount = -1;
        switch (sUriMatcher.match(uri)){
            case BUDGET_ROW:
                updateCount = localSQLiteDatabase.update(MahanadiContract.Budget.TABLE_NAME,
                        values,
                        selection,
                        selectionArgs);
                break;
            case EXPENSE_ROW:
                updateCount = localSQLiteDatabase.update(MahanadiContract.Expense.TABLE_NAME,
                        values,
                        selection,
                        selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Update: Invalid URI: " + uri);
        }
        return updateCount;
    }
}
