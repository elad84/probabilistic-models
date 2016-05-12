package com.idc.model;

import java.util.HashSet;
import java.util.Set;

/**
 * Represent a node in a tree.
 * 
 * This class is not thread safe.
 * 
 * @author eladcohen
 *
 */
public class Node {
	
	/**
	 * Indication whether this node is root
	 */
	private boolean root;
	
	/**
	 * Serial number for this node
	 */
	private int value;
	
	private Node parent;
	
	/**
	 * Neighbors of the current node 
	 */
	private Set<Node> neighbors;

	/**
	 * binary value indicator
	 */
	private boolean binaryValue;
	
	private Psi psi;
	
	private MarginalDisribution marginalDisribution;
	
	/**
	 * Constructor with only the serial value of the node 
	 * 
	 * @param value
	 */
	public Node(int value){
		this.value = value;
		this.neighbors = new HashSet<Node>();
	}
	
	public Set<Node> getNeighbors(){
		return neighbors;
	}
	
	public boolean isLeaf(){
		return neighbors.size() == 1 && !isRoot();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + value;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Node other = (Node) obj;
		if (value != other.value)
			return false;
		return true;
	}

	public int getValue() {
		return value;
	}

	public void setValue(int value) {
		this.value = value;
	}

	public Node getParent() {
		return parent;
	}

	public void setParent(Node parent) {
		this.parent = parent;
	}

	public boolean isRoot() {
		return root;
	}

	public void setRoot(boolean root) {
		this.root = root;
	}

	public boolean isTrue() {
		return binaryValue;
	}

	public void setBinaryValue(boolean binaryValue) {
		this.binaryValue = binaryValue;
	}

	@Override
	public String toString() {
		return "Node [root=" + root + ", value=" + value + ", binaryValue="
				+ binaryValue +  ", marginalDisribution="
				+ marginalDisribution +  "]";
	}

	public Psi getPsi() {
		return psi;
	}

	public void setPsi(Psi psi) {
		this.psi = psi;
	}

	public MarginalDisribution getMarginalDisribution() {
		return marginalDisribution;
	}

	public void setMarginalDisribution(MarginalDisribution marginalDisribution) {
		this.marginalDisribution = marginalDisribution;
	}

}
