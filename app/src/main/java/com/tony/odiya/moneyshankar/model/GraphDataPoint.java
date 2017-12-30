package com.tony.odiya.moneyshankar.model;

import com.jjoe64.graphview.series.DataPointInterface;

import java.util.Comparator;

/**
 * Created by tony on 19/10/17.
 */

public class GraphDataPoint implements DataPointInterface {

    private int count;
    private String xpointDisplayName;
    private Long xtime;
    private Double pointValue;
    private String xpoint;

    public static final Comparator<GraphDataPoint> ALPHABETIC_COMPARATOR = new PointNameComparator();
    public static final Comparator<GraphDataPoint> CREATION_TIME_COMPARATOR = new PointCreatedOnComparator();

    public GraphDataPoint(String xpointDisplayName, Double pointValue) {
        //if(pointValue>0) {
            /*if (xpointDisplayName.length() > 0) {
                this.xpointDisplayName = xpointDisplayName.length() > 3 ? xpointDisplayName.substring(0, 3) : xpointDisplayName;
            } else if(xpointDisplayName.length() == 0){
                this.xpointDisplayName = "";
            }
            else {
                this.xpointDisplayName = "N.A.";
            }*/
        /*}
        else this.xpointDisplayName ="";*/
        this.xpointDisplayName = xpointDisplayName;
        this.pointValue = pointValue;
    }

    @Override
    public double getX() {
        return getCount();
    }

    @Override
    public double getY() {
        return getPointValue();
    }


    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public String getXpointDisplayName() {
        return xpointDisplayName;
    }

    public void setXpointDisplayName(String xpointDisplayName) {
        if(xpointDisplayName.length()>0){
            this.xpointDisplayName = xpointDisplayName.length()>3? xpointDisplayName.substring(0,3): xpointDisplayName;
        }
        else{
            this.xpointDisplayName = "N.A.";
        }
    }

    public Long getXtime() {
        return xtime;
    }

    public void setXtime(Long xtime) {
        this.xtime = xtime;
    }

    public Double getPointValue() {
        return pointValue;
    }

    public void setPointValue(Double pointValue) {
        this.pointValue = pointValue;
    }

    public String getXpoint() {
        return xpoint;
    }

    public void setXpoint(String xpoint) {
        this.xpoint = xpoint;
    }

    private static class PointNameComparator implements Comparator<GraphDataPoint>{
        @Override
        public int compare(GraphDataPoint gdp, GraphDataPoint t1) {
            return gdp.getXpointDisplayName().compareTo(t1.getXpointDisplayName());
        }
    }

    private static class PointCreatedOnComparator implements Comparator<GraphDataPoint>{
        @Override
        public int compare(GraphDataPoint gdp, GraphDataPoint t1) {
            return gdp.getXtime().compareTo(t1.getXtime());
        }
    }
}
