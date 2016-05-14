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
		double[] normalizedMarginalDisribution = getNormalizedMarginalDisribution();
		return String.format("[%.5f,%.5f]", normalizedMarginalDisribution[0], normalizedMarginalDisribution[1]);
	}

	public double[] getNormalizedMarginalDisribution(){
		return new double[]{values[0]/(values[0]+values[1]), values[1]/(values[0]+values[1])};
	}

}
