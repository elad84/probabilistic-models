package com.idc.model;

import java.util.HashMap;
import java.util.Map;

/**
 * Model of transmission tree. Note that this is not
 * an implementation of tree and so cycles are not validated.  
 *  
 * @author eladcohen
 *
 */
public class TransmissionTree {
	
	private Map<Integer, Node> treeNodes;
	private Map<Edge, Double> edges;
	
	public TransmissionTree(){
		this.treeNodes = new HashMap<Integer, Node>();
		this.edges = new HashMap<Edge, Double>();
	}
	
	/**
	 * Add new edge to the tree
	 * 
	 * @param weight
	 * @param first
	 * @param second
	 */
	public void addEdge(double weight, Node first, Node second){
		//add edge in both directions
		Edge edge = new Edge(first, second);
		edges.put(new Edge(second, first),weight);
		edges.put(edge, weight);
		
		//add nodes to the tree
		Node node = treeNodes.get(first.getValue());
		if(node == null){
			//first initialization
			node = first;
			treeNodes.put(first.getValue(), first);
		}
		
		//add edge to first node
		
		node.getNeighbors().add(second);
		node = treeNodes.get(second.getValue());
		if(node == null){
			//first initialization
			node = second;
			treeNodes.put(second.getValue(), second);
		}
		//add edge to second node
		node.getNeighbors().add(first);
	}
	
	public double getEdgeWeight(Edge edge){
		return edges.get(edge);
	}
	
	public Node getNode(int value){
		return treeNodes.get(value);
	}

}
