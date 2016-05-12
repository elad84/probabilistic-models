package com.idc.model;
/**
 * Tree edge between two nodes
 * 
 * @author eladcohen
 *
 */
public class Edge {
	
	private Node firstNode;
	private Node secondNode;
	
	public Edge(Node first, Node another){
		this.firstNode = first;
		this.secondNode = another;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((firstNode == null) ? 0 : firstNode.hashCode());
		result = prime * result
				+ ((secondNode == null) ? 0 : secondNode.hashCode());
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
		Edge other = (Edge) obj;
		if (firstNode == null) {
			if (other.firstNode != null)
				return false;
		} else if (!firstNode.equals(other.firstNode) && !firstNode.equals(other.secondNode))
			return false;
		if (secondNode == null) {
			if (other.secondNode != null)
				return false;
		} else if (!secondNode.equals(other.secondNode) && !secondNode.equals(other.firstNode))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Edge [firstNode=" + firstNode + ", secondNode=" + secondNode
				+ "]";
	}

}
