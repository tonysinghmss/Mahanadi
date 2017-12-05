package com.tony.odiya.mahanadi.utils;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import org.threeten.bp.DayOfWeek;
import org.threeten.bp.Instant;
import org.threeten.bp.LocalDate;
import org.threeten.bp.LocalDateTime;
import org.threeten.bp.OffsetDateTime;
import org.threeten.bp.ZoneId;
import org.threeten.bp.ZoneOffset;
import org.threeten.bp.ZonedDateTime;
import org.threeten.bp.format.DateTimeFormatter;
import org.threeten.bp.temporal.TemporalAdjusters;
import org.threeten.bp.temporal.WeekFields;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

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
    public static String convertMillisecondsToDateString( Long timeToFormat){
        DateTimeFormatter iso8601Format = DateTimeFormatter.ofPattern ("yyyy-MM-dd HH:mm:ss.SSS");
        Instant instant = Instant.ofEpochMilli(timeToFormat);
        //Get datetime based on zone
        ZonedDateTime zdt = ZonedDateTime.ofInstant(instant, ZoneId.systemDefault());
        String finalDateTime = iso8601Format.format(zdt);
        return finalDateTime;
    }

    public static Long convertDateStringToMilliseconds( String dateString){
        DateTimeFormatter iso8601Format = DateTimeFormatter.ofPattern ("yyyy-MM-dd HH:mm:ss.SSS");
        LocalDateTime  localDateTime = LocalDateTime.parse(dateString,iso8601Format);
        ZonedDateTime zdt = localDateTime.atZone(ZoneId.systemDefault());
        return zdt.toInstant().toEpochMilli();
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
        Long endTime = 0l; //cal.getTimeInMillis();
        Long startTime = 0l;
        // JSR-310
        Instant instant = Instant.now();
        ZonedDateTime zdtStart = ZonedDateTime.ofInstant(instant, ZoneId.of("UTC"));
        ZoneOffset offset = OffsetDateTime.now(ZoneId.of("UTC")).getOffset();
        LocalDate today = zdtStart.toLocalDate();
        Locale defaultLocale = Locale.getDefault();
        switch (trend){
            case DAILY:
                startTime = today.atStartOfDay().toInstant(offset).toEpochMilli();
                endTime = zdtStart.toLocalDateTime().with(LocalDateTime.MAX).toInstant(offset).toEpochMilli();
                break;
            case WEEKLY:
                DayOfWeek firstDayOfWeek = WeekFields.of(defaultLocale).getFirstDayOfWeek();
                DayOfWeek lastDayOfWeek = firstDayOfWeek.plus(6);
                startTime = today.with(TemporalAdjusters.previousOrSame(firstDayOfWeek))
                        .atStartOfDay().toInstant(offset).toEpochMilli();
                endTime = today.with(TemporalAdjusters.nextOrSame(lastDayOfWeek)).atTime(23,59,59).toInstant(offset).toEpochMilli();
                break;
            case MONTHLY:
                startTime = today.withDayOfMonth(1).atStartOfDay().toInstant(offset).toEpochMilli();
                endTime = today.withDayOfMonth(today.lengthOfMonth()).atTime(23,59,59).toInstant(offset).toEpochMilli();
                break;
            case YEARLY:
                startTime = today.withDayOfYear(1).atStartOfDay().toInstant(offset).toEpochMilli();
                endTime = today.withDayOfYear(today.lengthOfYear()).atTime(23,59,59).toInstant(offset).toEpochMilli();
                break;
        }
        args.putString(START_TIME, startTime.toString());
        args.putString(END_TIME,endTime.toString());
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

    @NonNull
    public static Long getMonthEndInMilliSeconds(){
        Instant instant = Instant.now();
        ZonedDateTime zdtStart = ZonedDateTime.ofInstant(instant, ZoneId.of("UTC"));
        ZoneOffset offset = OffsetDateTime.now(ZoneId.of("UTC")).getOffset();
        LocalDate today = zdtStart.toLocalDate();
        return today.withDayOfMonth(today.lengthOfMonth()).atTime(23,59,59).toInstant(offset).toEpochMilli();
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
