package com.swaprakashheader.model;

import com.progressrecyclerview.model.IData;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class HeaderData implements IData {
    private String currency;
    private BigDecimal amount;
    private String title;

    public HeaderData(String currency, BigDecimal amount, String title){
        this.currency = currency;
        this.amount = amount;
        this.title = title;
    }

    @Override
    public void setCurrency(String currency) {
        this.currency = currency;
    }

    @Override
    public String getCurrency() {
        return currency;
    }

    @Override
    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    @Override
    public BigDecimal getAmount() {
        return amount;
    }

    @Override
    public String getFormattedAmount() {
        return new DecimalFormat("##,##,##0").format(amount);
    }

    @Override
    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public String getTitle() {
        return title;
    }

    public static List<IData> getData(){
        List<IData> list = new ArrayList<>();
        list.add(new HeaderData("₹", new BigDecimal("6243"),"Expense"));
        list.add(new HeaderData("₹", new BigDecimal("11624"),"Income"));
        list.add(new HeaderData("₹", new BigDecimal("5381"),"Available Balance"));
        list.add(new HeaderData("₹", new BigDecimal("692"),"Today's Expense"));
        return list;
    }
}
