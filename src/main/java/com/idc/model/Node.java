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
	 * Serial number for this node
	 */
	private int key;

	private Node parent;

	/**
	 * Neighbors of the current node
	 */
	private Set<Node> neighbors;

	/**
	 * binary value indicator
	 */
	private int value;

	private Psi psi;

	private MarginalDisribution marginalDisribution;

	TransmissionTree tree;

	/**
	 * Constructor with only the serial value of the node
	 * 
	 * @param key
	 */
	public Node(TransmissionTree tree, int key) {
		this.key = key;
		this.neighbors = new HashSet<Node>();
		this.value = -1;
		this.tree = tree;
	}

	public Set<Node> getNeighbors() {
		return neighbors;
	}

	public boolean isLeaf() {
		return neighbors.size() == 1 && !isRoot();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + key;
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
		if (key != other.key)
			return false;
		return true;
	}

	public int getKey() {
		return key;
	}

	public Node getParent() {
		return parent;
	}

	void setParent(Node parent) {
		this.parent = parent;
		for (Node neighbor : this.getNeighbors()) {
			if (neighbor != this.parent) {
				neighbor.setParent(this);
			}
		}
	}

	public boolean isRoot() {
		return parent == null;
	}

	@Override
	public String toString() {
		return "Node [" + key + ", value=" + value + ", md="
				+ marginalDisribution + "]";
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

	public int getValue() {
		return value;
	}

	public void setValue(int value) {
		this.value = value;
	}

	/**
	 * Calculates likelihood for {@link Node} is recursive
	 * 
	 * @return
	 */
	public double probability() {
		double probability = 1;
		// iterate over neighbors and get probability
		for (Node neighbor : this.getNeighbors()) {
			if (neighbor != this.parent) {
				double p = tree.getEdgeWeight(new Edge(this, neighbor));
				double flipProb = value * (1 - neighbor.getValue())
						+ (1 - value) * neighbor.getValue();
				probability *= flipProb * p + (1 - flipProb) * (1 - p);
				probability *= neighbor.probability();

			}
		}
		return probability;
	}

	// public double likelihood() {
	// if (isLeaf()) {
	// return 1;
	// }
	//
	// double likelihood = 1;
	// // iterate over neighbors and get likelihood
	// for (Node neighbor : this.getNeighbors()) {
	// if (neighbor != this.parent) {
	// double p = tree.getEdgeWeight(new Edge(this, neighbor));
	//
	// double neighborValue = neighbor.getValue();
	//
	// likelihood *= (neighborValue==0) ? p : (1-p);
	// likelihood *= neighbor.likelihood();
	//
	// }
	// }
	// return likelihood;
	// }

}
