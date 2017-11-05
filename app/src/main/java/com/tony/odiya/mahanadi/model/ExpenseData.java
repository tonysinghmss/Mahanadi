package com.tony.odiya.mahanadi.model;

/**
 * Created by tony on 19/10/17.
 */

public class ExpenseData {
    public String expenseId;
    public String category, item, amount, remark, createdOn;

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
        if (!expenseId.equals(that.expenseId)) return false;
        if (!category.equals(that.category)) return false;
        if (!item.equals(that.item)) return false;
        if (!amount.equals(that.amount)) return false;
        if (remark != null ? !remark.equals(that.remark) : that.remark != null) return false;
        return createdOn.equals(that.createdOn);

    }

    @Override
    public int hashCode() {
        int result = expenseId.hashCode();
        result =  31 * result + category.hashCode();
        result = 31 * result + item.hashCode();
        result = 31 * result + amount.hashCode();
        result = 31 * result + (remark != null ? remark.hashCode() : 0);
        result = 31 * result + createdOn.hashCode();
        return result;
    }
}
