package com.tony.odiya.mahanadi.fragment;

import android.app.LoaderManager;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GridLabelRenderer;
import com.jjoe64.graphview.ValueDependentColor;
import com.jjoe64.graphview.helper.StaticLabelsFormatter;
import com.jjoe64.graphview.series.BarGraphSeries;
import com.tony.odiya.mahanadi.R;
import com.tony.odiya.mahanadi.contract.MahanadiContract;
import com.tony.odiya.mahanadi.model.GraphDataPoint;
import com.tony.odiya.mahanadi.utils.Utility;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.tony.odiya.mahanadi.common.Constants.CATEGORY_NAME;
import static com.tony.odiya.mahanadi.common.Constants.DAILY;
import static com.tony.odiya.mahanadi.common.Constants.END_TIME;
import static com.tony.odiya.mahanadi.common.Constants.MONTHLY;
import static com.tony.odiya.mahanadi.common.Constants.START_TIME;
import static com.tony.odiya.mahanadi.common.Constants.WEEKLY;
import static com.tony.odiya.mahanadi.common.Constants.YEARLY;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnAnalysisFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link AnalysisFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AnalysisFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>{
    private static final String LOG_TAG = AnalysisFragment.class.getSimpleName();
    private static final String ARG_TREND = "trend";

    private static final int ANALYSIS_CATEGORY_LOADER_ID = 31;
    private static final int ANALYSIS_ITEM_LOADER_ID = 32;
    private static final int ANALYSIS_TIME_LOADER_ID = 33;

    private String mTrend;

    private GraphView graph;
    private BarGraphSeries<GraphDataPoint> series;
    private static final int MAX_DATA_POINT = 100;
    private Toolbar analysisToolbar;
    private OnAnalysisFragmentInteractionListener mListener;

    public AnalysisFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param trend Trend selected by user.
     * @return A new instance of fragment AnalysisFragment.
     */
    public static AnalysisFragment newInstance(String trend) {
        AnalysisFragment fragment = new AnalysisFragment();
        Bundle args = new Bundle();
        args.putString(ARG_TREND, trend);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mTrend = getArguments().getString(ARG_TREND);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_analysis, container, false);
        analysisToolbar = (Toolbar)view.findViewById(R.id.analysis_toolbar);
        ((AppCompatActivity)getActivity()).setSupportActionBar(analysisToolbar);
        //TODO: Set the width and height of graph programmatically to match phone.
        graph = (GraphView) view.findViewById(R.id.category_graph);
        series = new BarGraphSeries<>();
        series.setDrawValuesOnTop(true);
        series.setValuesOnTopColor(Color.RED);
        series.setSpacing(50);
        series.setValueDependentColor(new ValueDependentColor<GraphDataPoint>() {
            @Override
            public int get(GraphDataPoint d) {
                return Color.parseColor("#ff0099cc");
                //return Color.rgb(((int)d.getY()/16)%16,((int)d.getY()/16)%16, 255);
            }
        });
        graph.addSeries(series);
        graph.getGridLabelRenderer().setHorizontalLabelsAngle(115);
        graph.getGridLabelRenderer().setGridStyle(GridLabelRenderer.GridStyle.HORIZONTAL);
        Bundle args =  Utility.getDateRange(mTrend);
        getLoaderManager().restartLoader(ANALYSIS_TIME_LOADER_ID,args,this);
        //TODO: Drill down to item level for future release.
        return view;
    }

    /*
    public void onButtonPressed(String trend) {
        if (mListener != null) {
            mListener.onAnalysisTrendInteraction(trend);
        }
    }*/

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnAnalysisFragmentInteractionListener) {
            mListener = (OnAnalysisFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnAnalysisFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnAnalysisFragmentInteractionListener {
        void onAnalysisTrendInteraction(String trend);
    }

    //  CALLBACKS FOR LOADER CURSOR
    private String[] categoryProjection = {
            "SUM("+MahanadiContract.Expense.COL_AMOUNT+") AS Y",
            MahanadiContract.Expense.COL_CATEGORY +" AS X"
    };
    private  String[] itemProjection = {
            "SUM("+MahanadiContract.Expense.COL_AMOUNT+") AS Y",
            MahanadiContract.Expense.COL_ITEM+" AS X"
    };
    private String[] trendProjectionByHourOfDay = {
            "SUM("+MahanadiContract.Expense.COL_AMOUNT+") AS Y",
            " case cast ( strftime('%H',"+MahanadiContract.Expense.COL_CREATED_ON +") as integer) "+
                    "when 0 then '12AM Morning' "+
                    "when 12 then '12PM Noon' "+
                    "when 24 then '12AM Night' "+
                    "else '' end "+
                    " AS X"
    };
    private String[] trendProjectionByDayOfWeek = {
            "SUM("+MahanadiContract.Expense.COL_AMOUNT+") AS Y",
            " case cast ( strftime('%w',"+MahanadiContract.Expense.COL_CREATED_ON +") as integer) "+
                    "when 0 then 'S ' || (strftime('%d', "+MahanadiContract.Expense.COL_CREATED_ON +") + 1 ) " +
                    "when 1 then 'M ' || (strftime('%d', "+MahanadiContract.Expense.COL_CREATED_ON +") + 1 ) " +
                    "when 2 then 'T ' || (strftime('%d', "+MahanadiContract.Expense.COL_CREATED_ON +") + 1 ) " +
                    "when 3 then 'W ' || (strftime('%d', "+MahanadiContract.Expense.COL_CREATED_ON +") + 1 ) " +
                    "when 4 then 'T ' || (strftime('%d', "+MahanadiContract.Expense.COL_CREATED_ON +") + 1 ) " +
                    "when 5 then 'F ' || (strftime('%d', "+MahanadiContract.Expense.COL_CREATED_ON +") + 1 ) " +
                    "else 'S ' || (strftime('%d', "+MahanadiContract.Expense.COL_CREATED_ON +") + 1 ) end " +
                    "AS X"
    };

    private String[] trendProjectionByWeek = {
            "SUM("+MahanadiContract.Expense.COL_AMOUNT+") AS Y",
            // week of month = (week of year - week of first day of month + 1)
            /*"strftime('%W', "+MahanadiContract.Expense.COL_CREATED_ON+
                    ") - strftime('%W', date("+MahanadiContract.Expense.COL_CREATED_ON+", 'start of month')) +1 AS MW",
            "strftime('%d',"+MahanadiContract.Expense.COL_CREATED_ON+
                    ", 'weekday 0', '-7 days') || '-'||strftime('%d',"
                    +MahanadiContract.Expense.COL_CREATED_ON+", 'weekday 0','-1 days') ",*/
            "(strftime('%d',"+MahanadiContract.Expense.COL_CREATED_ON+") - strftime('%w', "
                    +MahanadiContract.Expense.COL_CREATED_ON+")) ||'-'|| (strftime('%d',"+
                    MahanadiContract.Expense.COL_CREATED_ON+") - strftime('%w', "+
                    MahanadiContract.Expense.COL_CREATED_ON+")+6 ) AS X"
    };

    private String[] trendProjectionByMonth = {
            "SUM("+MahanadiContract.Expense.COL_AMOUNT+") AS Y",
            "case cast ( strftime('%m',"+MahanadiContract.Expense.COL_CREATED_ON +") as integer) "+
                    "when 1 then 'JAN'"+
                    "when 2 then 'FEB'"+
                    "when 3 then 'MAR'"+
                    "when 4 then 'APR'"+
                    "when 5 then 'MAY'"+
                    "when 6 then 'JUN'"+
                    "when 7 then 'JUL'"+
                    "when 8 then 'AUG'"+
                    "when 9 then 'SEP'"+
                    "when 10 then 'OCT'"+
                    "when 11 then 'NOV'"+
                    "else 'DEC' end "+
                    "AS X"

    };
    /**
     *
     */
    @Override
    public Loader<Cursor> onCreateLoader(int loaderId, Bundle args) {
        Log.d(LOG_TAG, "Inside onCreateLoader");
        String[] projection =null;
        String filterClause = MahanadiContract.Expense.COL_CREATED_ON + " BETWEEN ? AND ?";
        String [] filterArgs = {args.getString(START_TIME), args.getString(END_TIME)};
        switch (loaderId) {
            case ANALYSIS_CATEGORY_LOADER_ID:
                graph.setTitle(getString(R.string.by_category));
                return new CursorLoader(getActivity(),
                        Uri.withAppendedPath(MahanadiContract.Expense.CONTENT_URI,"g"),
                        categoryProjection,               // List of columns to fetch
                        filterClause,                       // Filter clauses
                        filterArgs,                         // Filter args
                        null); // Sort order

            case ANALYSIS_ITEM_LOADER_ID:
                graph.setTitle(getString(R.string.by_item));
                String categoryFilter = MahanadiContract.Expense.COL_CATEGORY + " = ? ";
                String itemFilterClause = DatabaseUtils.concatenateWhere(filterClause,categoryFilter);
                String[] categoryArgs = {args.getString(CATEGORY_NAME)};
                String[] itemFilterArgs = DatabaseUtils.appendSelectionArgs(filterArgs, categoryArgs);
                return new CursorLoader(getActivity(),
                        MahanadiContract.Expense.CONTENT_URI,
                        itemProjection,
                        itemFilterClause,
                        itemFilterArgs,
                        null);
            case ANALYSIS_TIME_LOADER_ID:

                Uri trendUri = null;
                /*String trendFilterClause = null;
                String[] extraArgs = null;
                String[] extraFilterArgs = null;*/
                switch (mTrend){
                    case DAILY:
                        graph.setTitle(getString(R.string.by_trend_daily));
                        projection =trendProjectionByHourOfDay;
                        trendUri = Uri.withAppendedPath(MahanadiContract.Expense.CONTENT_URI, "hourOfDay");
                        break;
                    case WEEKLY:
                        graph.setTitle(getString(R.string.by_trend_weekly));
                        projection =trendProjectionByDayOfWeek;
                        trendUri = Uri.withAppendedPath(MahanadiContract.Expense.CONTENT_URI,"dayOfWeek");
                        break;
                    case MONTHLY:
                        graph.setTitle(getString(R.string.by_trend_monthly));
                        projection = trendProjectionByWeek;
                        trendUri = Uri.withAppendedPath(MahanadiContract.Expense.CONTENT_URI,"weekOfMonth");
                        break;
                    case YEARLY:
                        graph.setTitle(getString(R.string.by_trend_yearly));
                        projection =trendProjectionByMonth;
                        trendUri = Uri.withAppendedPath(MahanadiContract.Expense.CONTENT_URI,"monthOfYear");
                        break;
                }
                return new CursorLoader(getActivity(),
                        trendUri,
                        projection,               // List of columns to fetch
                        filterClause,                       // Filter clauses
                        filterArgs,                         // Filter args
                        null); // Sort order
            default:
                return null;
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor dataCursor) {
        Log.d(LOG_TAG, "Inside onLoadFinished");
        List<GraphDataPoint> graphDataPoints = new ArrayList<>(0);
        int count = 0;
        Log.d(LOG_TAG, "Cursor count : "+dataCursor.getCount());
        double max =0;
        switch (loader.getId()){
            case ANALYSIS_CATEGORY_LOADER_ID:
                while (dataCursor.moveToNext()) {
                    // Create data list for the graph.
                    double amount = dataCursor.getDouble(dataCursor.getColumnIndex("Y"));
                    if(amount > max){
                        max = amount;
                    }
                    String xPoint = dataCursor.getString(dataCursor.getColumnIndex("X"));
                    if (xPoint.length() > 0) {
                        xPoint = xPoint.length() > 3 ? xPoint.substring(0, 3) : xPoint;
                    } else if(xPoint.length() == 0){
                        xPoint = "";
                    }
                    else {
                        xPoint = "N.A.";
                    }
                    Log.d(LOG_TAG,xPoint);

                    //Long createdOnMilliSeconds = dataCursor.getLong(dataCursor.getColumnIndex("D"));
                    GraphDataPoint dataPoint = new GraphDataPoint(xPoint, amount);
                    graphDataPoints.add(dataPoint);
                }
                break;
            case ANALYSIS_ITEM_LOADER_ID:
                while (dataCursor.moveToNext()) {
                    // Create data list for the graph.
                    double amount = dataCursor.getDouble(dataCursor.getColumnIndex("Y"));
                    if(amount > max){
                        max = amount;
                    }
                    String xPoint = dataCursor.getString(dataCursor.getColumnIndex("X"));
                    if (xPoint.length() > 0) {
                        xPoint = xPoint.length() > 3 ? xPoint.substring(0, 3) : xPoint;
                    } else if(xPoint.length() == 0){
                        xPoint = "";
                    }
                    else {
                        xPoint = "N.A.";
                    }
                    Log.d(LOG_TAG,xPoint);

                    //Long createdOnMilliSeconds = dataCursor.getLong(dataCursor.getColumnIndex("D"));
                    GraphDataPoint dataPoint = new GraphDataPoint(xPoint, amount);
                    graphDataPoints.add(dataPoint);
                }
                break;
            case ANALYSIS_TIME_LOADER_ID:
                while (dataCursor.moveToNext()) {
                    // Create data list for the graph.
                    double amount = dataCursor.getDouble(dataCursor.getColumnIndex("Y"));
                    if(amount > max){
                        max = amount;
                    }
                    String xPoint = dataCursor.getString(dataCursor.getColumnIndex("X"));
                    Log.d(LOG_TAG," X coordinate : "+xPoint);

                    //Long createdOnMilliSeconds = dataCursor.getLong(dataCursor.getColumnIndex("D"));
                    GraphDataPoint dataPoint = new GraphDataPoint(xPoint, amount);
                    graphDataPoints.add(dataPoint);
                }
                break;
        }


        graph.getViewport().setYAxisBoundsManual(true);
        graph.getViewport().setMinY(0);
        graph.getViewport().setMaxY(max+10);
        if(graphDataPoints.size()>10){
            graph.getViewport().setScrollableY(true);
        }
        // populate the graph with data obtained above.
        if(!graphDataPoints.isEmpty()) {
            if(graphDataPoints.size()==1){
                /*
                 * Blank datapoint added to avoid labels format exception incase of single datapoint.
                 */
                GraphDataPoint blank = new GraphDataPoint("", 0.0);
                graphDataPoints.add(blank);
            }
            Collections.sort(graphDataPoints, GraphDataPoint.ALPHABETIC_COMPARATOR);
            // Set the x axis labels.
            List<String> horizontalLabels = new ArrayList<>(0);
            for (GraphDataPoint p : graphDataPoints) {
                // set the value of getX after sorting so that static labels appear in order of sorting.
                p.setCount(count);
                count++;
                horizontalLabels.add(p.getXpointName());
            }
            series.resetData(graphDataPoints.toArray(new GraphDataPoint[graphDataPoints.size()]));
            StaticLabelsFormatter staticLabelsFormatter = new StaticLabelsFormatter(graph);
            staticLabelsFormatter.setHorizontalLabels(horizontalLabels.toArray(new String[horizontalLabels.size()]));
            graph.getGridLabelRenderer().setLabelFormatter(staticLabelsFormatter);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {}
}
