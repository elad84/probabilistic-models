package com.idc.model;

import java.util.Arrays;

/**
 * Marginal distribution model 
 * 
 * @author eladcohen
 *
 */
public class MarginalDisribution extends BinaryMessage{

	public MarginalDisribution(double zeroValue, double oneValue) {
		super(zeroValue, oneValue);
	}

	@Override
	public String toString() {
		return String.format("[%.5f,%.5f]", values[0], values[1]);
	}

}
