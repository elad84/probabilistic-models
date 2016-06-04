package com.idc.model;

import java.util.Comparator;
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
public class Node implements Comparable{
	
	/**
	 * Indication whether this node is root
	 */
	private boolean root;
	
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
	
	/**
	 * Constructor with only the serial value of the node 
	 * 
	 * @param value
	 */
	public Node(int value){
		this.key = value;
		this.neighbors = new HashSet<Node>();
		this.value = -1;
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

	public void setParent(Node parent) {
		this.parent = parent;
		for (Node neighbor : this.getNeighbors()) {
			if (neighbor != this.parent) {
				neighbor.setParent(this);
			}
		}
	}

	public boolean isRoot() {
		return root;
	}

	public void setRoot(boolean root) {
		this.root = root;
	}


	@Override
	public String toString() {
		return "Node [root=" + root + ", key=" + key + ", value="
				+ value +  ", marginalDisribution="
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

	public int getValue(){
		return value;
	}

	public void setValue(int value) {
		this.value = value;
	}


	@Override
	public int compareTo(Object o) {
		return this.getKey() - ((Node) o).getKey();
	}

	public double likelihood(TransmissionTree tree) {
		double likelihood = 1;
		for (Node neighbor : this.getNeighbors()) {
			if (neighbor != this.parent) {
				double p = tree.getEdgeWeight(new Edge(this, neighbor));
				likelihood *= (value == neighbor.getValue() ? (1 - p) : p);
				likelihood *= neighbor.likelihood(tree);

			}
		}
		return likelihood;
	}
}
