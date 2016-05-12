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
		return "MarginalDisribution [toString()=" + super.toString() + "]";
	}

}
