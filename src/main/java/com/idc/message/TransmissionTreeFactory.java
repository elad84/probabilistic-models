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

	public static TransmissionTree buildTree(Double[] weights){
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

		transmissionTree.addEdge(weights[0], x1, x2);
		transmissionTree.addEdge(weights[1], x2, x3);
		transmissionTree.addEdge(weights[2], x2, x4);
		transmissionTree.addEdge(weights[3], x1, x5);
		transmissionTree.addEdge(weights[4], x5, x6);
		transmissionTree.addEdge(weights[5], x5, x7);
		transmissionTree.addEdge(weights[6], x1, x8);
		transmissionTree.addEdge(weights[7], x8, x9);
		transmissionTree.addEdge(weights[8], x8, x10);

		return transmissionTree;
	}

	public static TransmissionTree cloneTree(TransmissionTree transmissionTreeOrig){
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

		transmissionTree.addEdge(transmissionTreeOrig.getEdgeWeight(x1,x2), x1, x2);
		transmissionTree.addEdge(transmissionTreeOrig.getEdgeWeight(x2,x3), x2, x3);
		transmissionTree.addEdge(transmissionTreeOrig.getEdgeWeight(x2,x4), x2, x4);
		transmissionTree.addEdge(transmissionTreeOrig.getEdgeWeight(x1,x5), x1, x5);
		transmissionTree.addEdge(transmissionTreeOrig.getEdgeWeight(x5,x6), x5, x6);
		transmissionTree.addEdge(transmissionTreeOrig.getEdgeWeight(x5,x7), x5, x7);
		transmissionTree.addEdge(transmissionTreeOrig.getEdgeWeight(x1,x8), x1, x8);
		transmissionTree.addEdge(transmissionTreeOrig.getEdgeWeight(x8,x9), x8, x9);
		transmissionTree.addEdge(transmissionTreeOrig.getEdgeWeight(x8,x10), x8, x10);

		return transmissionTree;
	}

	public static TransmissionTree buildTreeNullWeight(){
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

		transmissionTree.addEdge(null, x1, x2);
		transmissionTree.addEdge(null, x2, x3);
		transmissionTree.addEdge(null, x2, x4);
		transmissionTree.addEdge(null, x1, x5);
		transmissionTree.addEdge(null, x5, x6);
		transmissionTree.addEdge(null, x5, x7);
		transmissionTree.addEdge(null, x1, x8);
		transmissionTree.addEdge(null, x8, x9);
		transmissionTree.addEdge(null, x8, x10);

		return transmissionTree;
	}
}
