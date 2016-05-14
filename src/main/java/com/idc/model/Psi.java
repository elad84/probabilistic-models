package com.idc.model;


/**
 * Psi model
 * 
 * @author eladcohen
 *
 */
public class Psi {
	
	private double[] values;
	
	public Psi(){
		values = new double[2];
	}
	
	public Psi(double zeroValue, double oneValue){
		values = new double[2];
		values[0] = zeroValue;
		values[1] = oneValue;
	}
	
	public void setValue(boolean key,double value){
		int index = key ? 1 : 0;
		values[index] = value;
	}
	
	public double getValue(boolean key){
		return key ? values[1] : values[0];
	}


	@Override
	public String toString() {
		return String.format("[%.5f,%.5f]", values[0], values[1]);
	}
}
