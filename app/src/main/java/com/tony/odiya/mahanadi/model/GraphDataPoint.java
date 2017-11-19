package com.tony.odiya.mahanadi.model;

import com.jjoe64.graphview.series.DataPointInterface;

import java.util.Comparator;

/**
 * Created by tony on 19/10/17.
 */

public class GraphDataPoint implements DataPointInterface {

    private int count;
    private String xpointName;
    private Double pointValue;

    public static final Comparator<GraphDataPoint> ALPHABETIC_COMPARATOR = new PointNameComparator();

    public GraphDataPoint( String xpointName, Double pointValue) {
        if(xpointName.length()>0){
            this.xpointName = xpointName.length()>3?xpointName.substring(0,3):xpointName;
        }
        else{
            this.xpointName = "N.A.";
        }
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

    public String getXpointName() {
        return xpointName;
    }

    public void setXpointName(String xpointName) {
        if(xpointName.length()>0){
            this.xpointName = xpointName.length()>3?xpointName.substring(0,3):xpointName;
        }
        else{
            this.xpointName = "N.A.";
        }
    }

    public Double getPointValue() {
        return pointValue;
    }

    public void setPointValue(Double pointValue) {
        this.pointValue = pointValue;
    }

    private static class PointNameComparator implements Comparator<GraphDataPoint>{
        @Override
        public int compare(GraphDataPoint gdp, GraphDataPoint t1) {
            return gdp.getXpointName().compareTo(t1.getXpointName());
        }
    }
}
