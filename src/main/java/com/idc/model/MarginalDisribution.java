package com.idc.model;


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
		return String.format("[%.4f,%.4f]", values[0], values[1]);
	}

}
