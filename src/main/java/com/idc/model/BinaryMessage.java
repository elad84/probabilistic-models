package com.idc.model;

import java.util.Arrays;

/**
 * Binary message model between node 
 * 
 * @author eladcohen
 *
 */
public class BinaryMessage {
	
	private double[] values;
	
	public BinaryMessage(double zeroValue, double oneValue){
		values = new double[2];
		values[0] = zeroValue;
		values[1] = oneValue;
	}
	
	public double getValue(boolean key){
		return key ? values[1] : values[0];
	}

	@Override
	public String toString() {
		return "BinaryMessage [values=" + Arrays.toString(values) + "]";
	}
}
