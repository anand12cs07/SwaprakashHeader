package com.progressrecyclerview.model;

import java.math.BigDecimal;

public interface IData {

    void setCurrency(String currency);

    String getCurrency();

    void setAmount(BigDecimal amount);

    BigDecimal getAmount();

    String getFormattedAmount();

    void setTitle(String title);

    String getTitle();

}
