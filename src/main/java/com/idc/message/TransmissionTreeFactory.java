package com.idc.message;

import com.idc.model.Node;
import com.idc.model.TransmissionTree;

/**
 * Generates a {@link TransmissionTree} as described in the exercise 
 * 
 * @author eladcohen
 *
 */
public class TransmissionTreeFactory {
	
	public static TransmissionTree buildTree(){
		TransmissionTree transmissionTree = new TransmissionTree();
		Node x1 = new Node(1);
		Node x2 = new Node(2);
		Node x3 = new Node(3);
		Node x4 = new Node(4);
		Node x5 = new Node(5);
		Node x6 = new Node(6);
		Node x7 = new Node(7);
		Node x8 = new Node(8);
		Node x9 = new Node(9);
		Node x10 = new Node(10);
		
		transmissionTree.addEdge(0.1, x1, x2);
		transmissionTree.addEdge(0.1, x2, x3);
		transmissionTree.addEdge(0.2, x2, x4);
		transmissionTree.addEdge(0.1, x1, x5);
		transmissionTree.addEdge(0.1, x5, x6);
		transmissionTree.addEdge(0.4, x5, x7);
		transmissionTree.addEdge(0.1, x1, x8);
		transmissionTree.addEdge(0.5, x8, x9);
		transmissionTree.addEdge(0.3, x8, x10);
		
		
		return transmissionTree;
	}

}
