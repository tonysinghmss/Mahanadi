package com.tony.odiya.mahanadi.model;



import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.tony.odiya.mahanadi.utils.Utility;

import java.util.Comparator;

/**
 * Created by tony on 19/10/17.
 */

public class ExpenseData {
    public static final String LOG_TAG = ExpenseData.class.getSimpleName();
    public String expenseId;
    public String category, item, amount, remark, createdOn;

    public static final Parcelable.Creator<ExpenseData> CREATOR
            = new Parcelable.Creator<ExpenseData>() {
        public ExpenseData createFromParcel(Parcel in) {
            ExpenseData ed = new ExpenseData();
            ed.setExpenseId(in.readString());
            ed.setCategory(in.readString());
            ed.setItem(in.readString());
            ed.setAmount(in.readString());
            ed.setRemark(in.readString());
            ed.setCreatedOn(in.readString());
            return ed;
        }

        public ExpenseData[] newArray(int size) {
            return new ExpenseData[size];
        }
    };

    public String getExpenseId() {
        return expenseId;
    }

    public void setExpenseId(String expenseId) {
        this.expenseId = expenseId;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getItem() {
        return item;
    }

    public void setItem(String item) {
        this.item = item;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(String createdOn) {
        this.createdOn = createdOn;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ExpenseData that = (ExpenseData) o;
        //if (Long.valueOf(expenseId) != Long.valueOf(that.expenseId)) return false;
        if (!category.equals(that.category)) return false;
        if (!item.equals(that.item)) return false;
        if (!amount.equals(that.amount)) return false;
        if (remark != null ? !remark.equals(that.remark) : that.remark != null) return false;
        return createdOn.equals(that.createdOn);

    }

    @Override
    public int hashCode() {
        int result = Long.valueOf(expenseId).hashCode();
        result = 31 * result + category.hashCode();
        //int result = category.hashCode();
        result = 31 * result + item.hashCode();
        result = 31 * result + amount.hashCode();
        result = 31 * result + (remark != null ? remark.hashCode() : 0);
        result = 31 * result + createdOn.hashCode();
        return result;
    }

    public static class ExpenseCategoryComparator implements Comparator<ExpenseData>{
        @Override
        public int compare(ExpenseData expenseData, ExpenseData t1) {
            return expenseData.getCategory().compareToIgnoreCase(t1.getCategory());
        }
    }

    public static class ExpenseItemComparator implements Comparator<ExpenseData>{
        @Override
        public int compare(ExpenseData expenseData, ExpenseData t1) {
            return expenseData.getItem().compareToIgnoreCase(t1.getItem());
        }
    }

    public static class ExpenseAmountComparator implements Comparator<ExpenseData>{
        @Override
        public int compare(ExpenseData expenseData, ExpenseData t1) {
            Double d1 = Double.valueOf(expenseData.getAmount());
            Double d2 = Double.valueOf(t1.getAmount());
            return Double.compare(d1,d2);
        }
    }
    public static class ExpenseRemarkComparator implements Comparator<ExpenseData>{
        @Override
        public int compare(ExpenseData expenseData, ExpenseData t1) {
            return expenseData.getRemark().compareTo(t1.getRemark());
        }
    }

    public static class ExpenseCreatedOnComparator implements Comparator<ExpenseData>{
        @Override
        public int compare(ExpenseData expenseData, ExpenseData t1) {
            Long createdOn1 = Utility.convertDateStringToMilliseconds(expenseData.getCreatedOn());
            Long createdOn2 =  Utility.convertDateStringToMilliseconds(t1.getCreatedOn());
            Log.d(LOG_TAG, "CreatedOnComparator");
            Log.d(LOG_TAG, expenseData+ createdOn1.toString());
            Log.d(LOG_TAG, t1+ createdOn2.toString());
            Log.d(LOG_TAG, "Compare : "+Long.compare(createdOn1,createdOn2));
            return Long.compare(createdOn1,createdOn2);
        }
    }

    public static final ExpenseCategoryComparator CATEGORY_COMPARATOR = new ExpenseCategoryComparator();
    public static final ExpenseItemComparator ITEM_COMPARATOR = new ExpenseItemComparator();
    public static final ExpenseAmountComparator AMOUNT_COMPARATOR = new ExpenseAmountComparator();
    public static final ExpenseRemarkComparator REMARK_COMPARATOR = new ExpenseRemarkComparator();
    public static final ExpenseCreatedOnComparator CREATION_TIME_COMPARATOR = new ExpenseCreatedOnComparator();

    public int compare(ExpenseData expenseData) {
        Log.d(LOG_TAG, "Compare :"+CREATION_TIME_COMPARATOR.compare(this, expenseData));
        return CREATION_TIME_COMPARATOR.compare(this, expenseData);
    }

    public boolean areContentsTheSame(ExpenseData that){
        Log.d(LOG_TAG, "areContentsTheSame : "+Boolean.toString(this.equals(that)));
        // Called by the SortedList when it wants to check whether two items have the same data or not
        return  this.equals(that);
    }

    public boolean areItemsTheSame(ExpenseData that){
        Log.d(LOG_TAG, "areItemsTheSame : "+Boolean.toString(this.hashCode() == that.hashCode()));
        // Called by the SortedList to decide whether two object represent the same Item or not.
        return this.hashCode() == that.hashCode();
    }

    @Override
    public String toString() {
        return "ExpenseData{" +
                "expenseId='" + expenseId + '\'' +
                ", category='" + category + '\'' +
                ", item='" + item + '\'' +
                ", amount='" + amount + '\'' +
                ", remark='" + remark + '\'' +
                ", createdOn='" + createdOn + '\'' +
                '}';
    }
    /*@Override
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeString(expenseId);
        parcel.writeString(category);
        parcel.writeString(item);
        parcel.writeString(amount);
        parcel.writeString(remark);
        parcel.writeString(createdOn);
    }

    @Override
    public int describeContents() {
        return 0;
    }*/
}
