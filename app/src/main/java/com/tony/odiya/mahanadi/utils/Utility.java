package com.tony.odiya.mahanadi.utils;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import static com.tony.odiya.mahanadi.common.Constants.CLOTHES;
import static com.tony.odiya.mahanadi.common.Constants.CUSTOM;
import static com.tony.odiya.mahanadi.common.Constants.DAILY;
import static com.tony.odiya.mahanadi.common.Constants.ELECTRONICS;
import static com.tony.odiya.mahanadi.common.Constants.END_TIME;
import static com.tony.odiya.mahanadi.common.Constants.FOOD;
import static com.tony.odiya.mahanadi.common.Constants.GROCERY;
import static com.tony.odiya.mahanadi.common.Constants.HOME;
import static com.tony.odiya.mahanadi.common.Constants.MONTHLY;
import static com.tony.odiya.mahanadi.common.Constants.START_TIME;
import static com.tony.odiya.mahanadi.common.Constants.WEEKLY;
import static com.tony.odiya.mahanadi.common.Constants.YEARLY;

/**
 * Created by tony on 20/10/17.
 */

public class Utility {
    private static final String LOG_TAG = Utility.class.getSimpleName();
    public static String convertMillisecondsToDateString(Context context, Long timeToFormat){
        String finalDateTime = "";
        SimpleDateFormat iso8601Format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        //Date date = null;
        if (timeToFormat != null) {
            Calendar cal = Calendar.getInstance();
            cal.setTimeInMillis(timeToFormat);
           /* date = cal.getTime();
            if (date != null) {
                long when = date.getTime();
                int flags = 0;
                flags |= android.text.format.DateUtils.FORMAT_SHOW_TIME;
                flags |= android.text.format.DateUtils.FORMAT_SHOW_DATE;
                flags |= android.text.format.DateUtils.FORMAT_ABBREV_MONTH;
                flags |= android.text.format.DateUtils.FORMAT_SHOW_YEAR;
                finalDateTime = android.text.format.DateUtils.formatDateTime(context,
                        when + TimeZone.getDefault().getOffset(when), flags);
            }*/
            finalDateTime = iso8601Format.format(cal.getTime());
        }
        return finalDateTime;
    }

    public static Long convertDateStringToMilliseconds( String dateString){
        SimpleDateFormat iso8601Format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        Long timeInMilliseconds = 0l;
        try {
            Date mDate = iso8601Format.parse(dateString);
            timeInMilliseconds = mDate.getTime();
        } catch (ParseException e) {
            Log.d(LOG_TAG, "Unable to parse time format.");
        }
        return timeInMilliseconds;
    }

    public static void setTrendValuesInSpinner(Spinner spinner, Context context){
        List<String> valueList = new ArrayList<>();
        valueList.add(DAILY);
        valueList.add(WEEKLY);
        valueList.add(MONTHLY);
        valueList.add(YEARLY);
        ArrayAdapter<String> trendAdapter = new ArrayAdapter<>(context,
                android.R.layout.simple_spinner_item,
                valueList);
        trendAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(trendAdapter);
    }

    public static void selectDefaultTrendInSpinner(Spinner spinner, String trend){
        ArrayAdapter<String> trendAdapter = (ArrayAdapter<String>) spinner.getAdapter();
        if(trend!=null && !trend.isEmpty()){
            int pos = trendAdapter.getPosition(trend);
            spinner.setSelection(pos);
        }
    }

    public static Bundle getDateRange(String trend){
        Bundle args = new Bundle();
        Calendar cal = Calendar.getInstance();
        Long endTime = cal.getTimeInMillis();
        Long startTime = 0l;
        args.putString(END_TIME,endTime.toString());
        switch (trend){
            case DAILY:
                cal.set(Calendar.HOUR_OF_DAY,0);
                cal.set(Calendar.MINUTE,0);
                cal.set(Calendar.SECOND,0);
                cal.set(Calendar.MILLISECOND,0);
                startTime = cal.getTimeInMillis();
                args.putString(START_TIME, startTime.toString());
                break;
            case WEEKLY:
                cal.set(Calendar.HOUR_OF_DAY, 0);
                cal.set(Calendar.MINUTE,0);
                cal.set(Calendar.SECOND,0);
                cal.set(Calendar.MILLISECOND,0);
                cal.set(Calendar.DAY_OF_WEEK, cal.getFirstDayOfWeek());
                startTime = cal.getTimeInMillis();
                args.putString(START_TIME, startTime.toString());
                break;
            case MONTHLY:
                cal.set(Calendar.HOUR_OF_DAY, 0);
                cal.set(Calendar.MINUTE,0);
                cal.set(Calendar.SECOND,0);
                cal.set(Calendar.MILLISECOND,0);
                cal.set(Calendar.DAY_OF_MONTH, 1);
                startTime = cal.getTimeInMillis();
                args.putString(START_TIME, startTime.toString());
                break;
            case YEARLY:
                cal.set(Calendar.HOUR_OF_DAY, 0);
                cal.set(Calendar.MINUTE,0);
                cal.set(Calendar.SECOND,0);
                cal.set(Calendar.MILLISECOND,0);
                cal.set(Calendar.DAY_OF_YEAR,1);
                startTime = cal.getTimeInMillis();
                args.putString(START_TIME, startTime.toString());
                break;
        }
        return args;
    }

    public static void colorMenuItem(MenuItem menuItem, String color) {
        Drawable drawable = menuItem.getIcon();
        if(drawable != null){
            colorDrawable(drawable,Color.parseColor(color));

        }
    }

    public static void colorMenuItem(MenuItem menuItem, int color) {
        Drawable drawable = menuItem.getIcon();
        if(drawable != null){
            colorDrawable(drawable,color);
        }
    }

    /*
     *   @param color This is value of color.
     */
    public static Drawable colorDrawable(Drawable drawable, int color){
        // If we don't mutate the drawable, then all drawable's with this id will have a color
        // filter applied to it.
        drawable.mutate();
        drawable.setColorFilter(color, PorterDuff.Mode.SRC_ATOP);
        //drawable.setAlpha(255);// aplha value
        return drawable;
    }

    public static Long getMonthEndInMilliSeconds(){
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE,59);
        cal.set(Calendar.SECOND,59);
        cal.set(Calendar.MILLISECOND,1000);
        cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
        return cal.getTimeInMillis();
    }

    public static void setCategoryValuesInSpinner(Spinner spinner, Context context){
        List<String> valueList = new ArrayList<>();
        valueList.add(GROCERY);
        valueList.add(ELECTRONICS);
        valueList.add(FOOD);
        valueList.add(HOME);
        valueList.add(CLOTHES);
        valueList.add(CUSTOM);
        ArrayAdapter<String> trendAdapter = new ArrayAdapter<>(context,
                android.R.layout.simple_spinner_item,
                valueList);
        trendAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(trendAdapter);
    }

    public static void selectDefaultCategoryInSpinner(Spinner spinner, String category){
        ArrayAdapter<String> trendAdapter = (ArrayAdapter<String>) spinner.getAdapter();
        if(category!=null && !category.isEmpty()){
            int pos = trendAdapter.getPosition(category);
            spinner.setSelection(pos);
        }
    }
}
