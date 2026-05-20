package com.campus.leave.model;

import java.io.Serializable;

public class StatItem implements Serializable {
    private String name;
    private int count;
    private double days;

    public StatItem() {
    }

    public StatItem(String name, int count, double days) {
        this.name = name;
        this.count = count;
        this.days = days;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public double getDays() {
        return days;
    }

    public void setDays(double days) {
        this.days = days;
    }
}
