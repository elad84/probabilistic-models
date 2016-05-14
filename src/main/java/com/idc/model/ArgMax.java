package com.idc.model;

/**
 * Created by eladcohen on 5/12/16.
 */
public class ArgMax
{
    public boolean isZeroValue() {
        return zeroValue;
    }

    public boolean isOneValue() {
        return oneValue;
    }

    private boolean zeroValue;
    private boolean oneValue;

    public ArgMax(boolean zeroValue, boolean oneValue) {
        this.zeroValue = zeroValue;
        this.oneValue = oneValue;
    }

    @Override
    public String toString() {
        return "ArgMax[" +
                    zeroValue +
                ", " + oneValue +
                ']';
    }
}
