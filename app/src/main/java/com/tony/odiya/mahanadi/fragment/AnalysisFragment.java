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
import static com.tony.odiya.mahanadi.common.Constants.END_TIME;
import static com.tony.odiya.mahanadi.common.Constants.START_TIME;

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
                return Color.parseColor("#ff33b5e5");
                //return Color.rgb(((int)d.getY()/16)%16,((int)d.getY()/16)%16, 255);
            }
        });
        graph.addSeries(series);
        graph.getGridLabelRenderer().setHorizontalLabelsAngle(135);
        Bundle args =  Utility.getDateRange(mTrend);
        getLoaderManager().restartLoader(ANALYSIS_CATEGORY_LOADER_ID,args,this);
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
    /**
     *
     */
    @Override
    public Loader<Cursor> onCreateLoader(int loaderId, Bundle args) {
        Log.d(LOG_TAG, "Inside onCreateLoader");
        String filterClause = MahanadiContract.Expense.COL_CREATED_ON + " BETWEEN ? AND ?";
        String [] filterArgs = {args.getString(START_TIME), args.getString(END_TIME)};
        switch (loaderId) {
            case ANALYSIS_CATEGORY_LOADER_ID:
                graph.setTitle("By Category");
                return new CursorLoader(getActivity(),
                        Uri.withAppendedPath(MahanadiContract.Expense.CONTENT_URI,"g"),
                        categoryProjection,               // List of columns to fetch
                        filterClause,                       // Filter clauses
                        filterArgs,                         // Filter args
                        null); // Sort order

            case ANALYSIS_ITEM_LOADER_ID:
                graph.setTitle("By Item");
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
        while (dataCursor.moveToNext()) {
            // Create data list for the graph.
            double amount = dataCursor.getDouble(dataCursor.getColumnIndex("Y"));
            if(amount > max){
                max = amount;
            }
            String xPoint = dataCursor.getString(dataCursor.getColumnIndex("X"));
            //Long createdOnMilliSeconds = dataCursor.getLong(dataCursor.getColumnIndex("D"));
            GraphDataPoint dataPoint = new GraphDataPoint(xPoint, amount);
            graphDataPoints.add(dataPoint);
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
