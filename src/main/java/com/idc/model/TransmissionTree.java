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

	public Double getEdgeWeight(Node first, Node second){
		Edge edge = new Edge(first, second);
		return this.edges.get(edge);
	}
	
	/**
	 * Add new edge to the tree
	 * 
	 * @param weight
	 * @param first
	 * @param second
	 */
	public void addEdge(Double weight, Node first, Node second){
		//add edge in both directions
		Edge edge = new Edge(first, second);
		edges.put(new Edge(second, first),weight);
		edges.put(edge, weight);
		
		//add nodes to the tree
		Node node = treeNodes.get(first.getKey());
		if(node == null){
			//first initialization
			node = first;
			treeNodes.put(first.getKey(), first);
		}
		
		//add edge to first node
		
		node.getNeighbors().add(second);
		node = treeNodes.get(second.getKey());
		if(node == null){
			//first initialization
			node = second;
			treeNodes.put(second.getKey(), second);
		}
		//add edge to second node
		node.getNeighbors().add(first);
	}

	@Override
	public String toString(){
		StringBuilder sb = new StringBuilder();

		sb.append(getTreeRepresentationString());

		for (Node node : treeNodes.values()) {
			sb.append("\n");
			sb.append(node);
		}

		return sb.toString();
	}

	private String getTreeRepresentationString(){
		Node root = null;
		for (Node node : treeNodes.values()) {
			if (node.isRoot()){
				if (root!=null){
					throw new IllegalArgumentException("two roots in tree: " + root + node);
				}
				root = node;
			}
		}

		if (root == null){
			throw new IllegalArgumentException("root is not given");
		}

		StringBuilder sb = new StringBuilder();
		doGetTreeRepresentationString(root,null, sb, 0);

		return sb.toString();
	}

	private void doGetTreeRepresentationString(Node root, Node caller, StringBuilder sb, int d){

		for (int i=0;i<d;++i) {
			sb.append("\t");
		}
		sb.append(root.getKey());
		sb.append("\n");


		for (Node child : root.getNeighbors()) {
			if(!child.equals(caller)) {
				doGetTreeRepresentationString(child, root, sb, d + 1);
			}
		}
	}
	
	public double getEdgeWeight(Edge edge){
		Double val = edges.get(edge);
		if(val == null){
			System.out.println(edge);
		}
		return edges.get(edge);
	}
	
	public Node getNode(int value){
		return treeNodes.get(value);
	}

	/**
	 * Gets a map of node key to the marginal distribution of the current snapshot of the tree.
	 * If a node does not have all marginal distribution calculated yet the value
	 * will be null for that node
	 * 
	 * @return a map contains all nodes marginal distribution. 
	 */
	public Map<Integer,MarginalDisribution> getNodesMarginalDisribution(){
		Map<Integer,MarginalDisribution> marginalDisribution = new HashMap<Integer,MarginalDisribution>();
		for (Node node : treeNodes.values()) {
			marginalDisribution.put(node.getKey(),node.getMarginalDisribution());
		}
		return marginalDisribution;
	}

}
