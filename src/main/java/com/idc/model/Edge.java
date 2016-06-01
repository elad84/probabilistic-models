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

	public Edge(Node first, Node another) {
		this.firstNode = first;
		this.secondNode = another;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int a = ((firstNode == null) ? 0 : firstNode.hashCode())
				+ ((secondNode == null) ? 0 : secondNode.hashCode());
		return prime * a;
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

		if (firstNode.equals(other.firstNode)
				&& secondNode.equals(other.secondNode))
			return true;
		else if (secondNode.equals(other.firstNode)
				&& firstNode.equals(other.secondNode))
			return true;
		return false;
	}

	@Override
	public String toString() {
		return "Edge [firstNode=" + firstNode + ", secondNode=" + secondNode
				+ "]";
	}

	public Node getFirstNode() {
		return firstNode;
	}

	public Node getSecondNode() {
		return secondNode;
	}

}
