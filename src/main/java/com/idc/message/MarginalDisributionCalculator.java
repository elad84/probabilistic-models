package com.idc.message;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.idc.model.BinaryMessage;
import com.idc.model.Edge;
import com.idc.model.MarginalDisribution;
import com.idc.model.Node;
import com.idc.model.Psi;
import com.idc.model.TransmissionTree;

/**
 * Compute marginal distribution for all nodes in a {@link TransmissionTree}
 * 
 * @author eladcohen
 *
 */
public class MarginalDisributionCalculator {
	
	private TransmissionTree tree;
	private Map<Edge, BinaryMessage> messages;
	
	public MarginalDisributionCalculator(TransmissionTree tree){
		this.tree = tree;
		this.messages = new HashMap<Edge, BinaryMessage>();
	}
	
	/**
	 * Collects messages from children to parent and calculate Psi 
	 * for each of the nodes
	 *  
	 * @param node
	 * @param caller
	 * @return
	 */
	public BinaryMessage collect(Node node, Node caller){
//		System.out.println("passing message from node: " + node);
		Psi psi;
		if(node.isLeaf()){
			double zero = node.isTrue() ? 0 : 1;
			double one = node.isTrue() ? 1 : 0;
			psi = new Psi(zero, one);
		}else{
			//calculate message from children
			List<BinaryMessage> messages = new ArrayList<BinaryMessage>();
			//iterate over all neighbors
			for(Node child : node.getNeighbors()){
				//do not call the same node twice to avoid cycle calls
				if(!child.equals(caller)){
					child.setParent(node);
					BinaryMessage message = collect(child, node);
					//add for distribute map
					this.messages.put(new Edge(child, node), message);
					messages.add(message);
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
			return new BinaryMessage(psi.getValue(false), psi.getValue(true));
		}else{
			Node parent = node.getParent();
//			System.out.println("calculation message between: " + node + " and parent: " + parent);
			double edgeFlipProbability = tree.getEdgeWeight(new Edge(parent, node));
			double zeroChildMessage = (1 - edgeFlipProbability) * psi.getValue(false) + edgeFlipProbability * psi.getValue(true);
			double oneChildMessage = edgeFlipProbability * psi.getValue(false) + (1- edgeFlipProbability) * psi.getValue(true);
			return new BinaryMessage(zeroChildMessage, oneChildMessage);
		}	
	}
	
	/**
	 * Distributes messages from parent to child nodes
	 * 
	 * @param node
	 * @param message
	 */
	public void distribute(Node node, BinaryMessage message){
		double zero;
		double one;
		Psi psi = node.getPsi();
		//calculate 
		if(message != null){
			zero = psi.getValue(false) * message.getValue(false);
			one = psi.getValue(true) * message.getValue(true);
		}else{
			zero = psi.getValue(false);
			one = psi.getValue(true);
		}
		
		node.setMarginalDisribution(new MarginalDisribution(zero, one));
		
		if(!node.isLeaf()){
			//iterate over children
			for(Node child : node.getNeighbors()){
				if(node.getParent() == null || !node.getParent().equals(child)){
					Edge edge = new Edge(child, node);
					BinaryMessage childMessage = messages.get(edge);
					double edgeFlipProbability = tree.getEdgeWeight(edge);
					double zeroParentMessage =  (1- edgeFlipProbability) * zero / childMessage.getValue(false) + 
							edgeFlipProbability * one / childMessage.getValue(true);
					double oneParentMessage = edgeFlipProbability * zero / childMessage.getValue(false) +
							(1 - edgeFlipProbability) * one / childMessage.getValue(true);
					distribute(child, new BinaryMessage(zeroParentMessage, oneParentMessage));
				}
			}
		}
	}

}
