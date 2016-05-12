package com.idc.message;

import com.idc.model.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Compute marginal distribution for all nodes in a {@link TransmissionTree}
 * 
 * @author eladcohen
 *
 */
public class MaxMarginalDisributionCalculator {

	private TransmissionTree tree;
	private Map<Edge, BinaryMessage> messages;
	private Map<Edge, ArgMax> maxValues;

	private Map<Node, Boolean> starValues;

	public MaxMarginalDisributionCalculator(TransmissionTree tree){
		this.tree = tree;
		this.messages = new HashMap<>();
		this.maxValues = new HashMap<>();
		this.starValues = new HashMap<>();
	}

	public void computeMarginals(Node root){
		collect(root, null);
		distribute(root);
	}
	
	/**
	 * Collects messages from children to parent and calculate Psi 
	 * for each of the nodes
	 *  
	 * @param node
	 * @param caller
	 * @return
	 */
	public ArgMaxBinaryMessage collect(Node node, Node caller){
//		System.out.println("passing message from node: " + node);
		Psi psi;
		if(node.isLeaf()){
			double zero = node.isTrue() ? 0 : 1;
			double one = node.isTrue() ? 1 : 0;
			psi = new Psi(zero, one);
		}else{
			//calculate message from children
			List<BinaryMessage> messages = new ArrayList<>();
			List<ArgMax> maxValues = new ArrayList<>();
			//iterate over all neighbors
			for(Node child : node.getNeighbors()){
				//do not call the same node twice to avoid cycle calls
				if(!child.equals(caller)){
					child.setParent(node);
					ArgMaxBinaryMessage argMaxBinaryMessage = collect(child, node);

					//add for distribute map
					Edge edge = new Edge(child, node);
					this.messages.put(edge, argMaxBinaryMessage.getBinaryMessage());
					messages.add(argMaxBinaryMessage.getBinaryMessage());

					this.maxValues.put(edge, argMaxBinaryMessage.getArgMax());
					maxValues.add(argMaxBinaryMessage.getArgMax());
				}
			}
			double zeroPsi = 1;
			double onePsi = 1;
			//calculate Psi for current node
			for(BinaryMessage message : messages){
				if(node.isRoot()){
					System.out.println("adding message " + message + " to calculation");
				}
				
				zeroPsi *= message.getValue(false);
				onePsi *= message.getValue(true);
			}
			psi = new Psi(zeroPsi, onePsi);
		}
		
		node.setPsi(psi);
		
		//calculate message from child to parent, when root just return
		if(node.isRoot()){
			BinaryMessage message =  new BinaryMessage(psi.getValue(false), psi.getValue(true));

			double mMax = Math.max(psi.getValue(false),psi.getValue(true));
			boolean  argMaxStar = (psi.getValue(false) == mMax) ? false : true;

			System.out.println(node.getValue() + " m: " + mMax + " x*: "+ argMaxStar);
			starValues.put(node,argMaxStar);

			return new ArgMaxBinaryMessage(message,null);
		}else{
			Node parent = node.getParent();
//			System.out.println("calculation message between: " + node + " and parent: " + parent);
			double edgeFlipProbability = tree.getEdgeWeight(new Edge(parent, node));
			double zeroChildMessage = Math.max((1 - edgeFlipProbability) * psi.getValue(false) , edgeFlipProbability * psi.getValue(true));
			double oneChildMessage = Math.max(edgeFlipProbability * psi.getValue(false) , (1- edgeFlipProbability) * psi.getValue(true));

			boolean  argmaxZeroChildMessage = (zeroChildMessage == ((1 - edgeFlipProbability) * psi.getValue(false))) ? false : true;
			boolean  argmaxOneChildMessage = (zeroChildMessage == (edgeFlipProbability * psi.getValue(false))) ? false : true;

			BinaryMessage message = new BinaryMessage(zeroChildMessage, oneChildMessage);
			ArgMax argMax = new ArgMax(argmaxZeroChildMessage,argmaxOneChildMessage);
			return new ArgMaxBinaryMessage(message,argMax);
		}	
	}
	
	/**
	 * Distributes messages from parent to child nodes
	 *
	 */
	public void distribute(Node node){
		if(!node.isLeaf()){

			Boolean starValue = starValues.get(node);
			//iterate over children
			for(Node child : node.getNeighbors()){
				if(node.getParent() == null || !node.getParent().equals(child)){
					Edge edge = new Edge(child, node);
					ArgMax argMax = maxValues.get(edge);
					boolean childStarValue = starValue ? argMax.isOneValue() : argMax.isZeroValue();
					starValues.put(child, childStarValue);

					System.out.println(child.getValue() + " x*: "+ childStarValue);
					distribute(child);
				}
			}
		}
	}

}
