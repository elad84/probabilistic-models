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

	/**
	 * Tree to do all operations on
	 */
	private TransmissionTree tree;

	/**
	 * Map of messages passed in the tree.
	 */
	private Map<Edge, BinaryMessage> messages;

	/**
	 * Map of max values per edge
	 */
	private Map<Edge, ArgMax> maxValues;

	/**
	 *
	 */
	private Map<Node, Boolean> starValues;

	private double mMax;

	/**
	 * Initialize the calculator with the tree to calculate the data for
	 *
	 * @param tree
	 */
	public MaxMarginalDisributionCalculator(TransmissionTree tree) {
		this.tree = tree;
		this.messages = new HashMap<Edge, BinaryMessage>();
		this.maxValues = new HashMap<Edge, ArgMax>();
		this.starValues = new HashMap<Node, Boolean>();
	}

	public Map<Node, Boolean> getStarValues() {
		return starValues;
	}

	/**
	 * Computes marginal for all nodes in {@link #tree}
	 *
	 * @param root
	 * @return
	 */
	public double computeMarginals(Node root) {
		collect(root, null);
		distribute(root);
		return mMax;
	}

	/**
	 * Collects messages from children to parent and calculate Psi for each of
	 * the nodes
	 *
	 * @param node
	 * @param caller
	 * @return
	 */
	public ArgMaxBinaryMessage collect(Node node, Node caller) {
		// System.out.println("passing message from node: " + node);
		Psi psi;
		if (node.isLeaf()) {
			double zero = 1 - node.getValue();
			double one = node.getValue();
			psi = new Psi(zero, one);
		} else {
			// calculate message from children
			List<BinaryMessage> messages = new ArrayList<BinaryMessage>();
			// iterate over all neighbors
			for (Node child : node.getNeighbors()) {
				// do not call the same node twice to avoid cycle calls
				if (!child.equals(caller)) {
					ArgMaxBinaryMessage argMaxBinaryMessage = collect(child,
							node);

					// add for distribute map
					Edge edge = new Edge(child, node);
					this.messages.put(edge,
							argMaxBinaryMessage.getBinaryMessage());
					messages.add(argMaxBinaryMessage.getBinaryMessage());

					this.maxValues.put(edge, argMaxBinaryMessage.getArgMax());
				}
			}
			double zeroPsi = 1;
			double onePsi = 1;
			// calculate Psi for current node
			for (BinaryMessage message : messages) {
				zeroPsi *= message.getValue(false);
				onePsi *= message.getValue(true);
			}
			if (node.isRoot())
				psi = new Psi(zeroPsi * 0.5, onePsi * 0.5);
			else
				psi = new Psi(zeroPsi, onePsi);
		}

		node.setPsi(psi);

		// calculate message from child to parent, when root just return
		if (node.isRoot()) {
			BinaryMessage message = new BinaryMessage(psi.getValue(false),
					psi.getValue(true));

			if (node.getValue() > -1 && node.getValue() < 2) {
				mMax = node.getValue() == 1 ? psi.getValue(true) : psi
						.getValue(false);
			} else {
				mMax = Math.max(psi.getValue(false), psi.getValue(true));
			}
			boolean argMaxStar = (psi.getValue(false) == mMax) ? false : true;

			System.out.println(node.getKey() + " m: " + mMax + " x*: "+ argMaxStar + " " + message);
			starValues.put(node,argMaxStar);

			return new ArgMaxBinaryMessage(message, null);
		} else {
			Node parent = node.getParent();

			double edgeFlipProbability = tree.getEdgeWeight(new Edge(parent,
					node));
			double zeroFalse = (1 - edgeFlipProbability) * psi.getValue(false);
			double zeroTrue = edgeFlipProbability * psi.getValue(true);
			double oneFalse = edgeFlipProbability * psi.getValue(false);
			double oneTrue = (1 - edgeFlipProbability) * psi.getValue(true);

			double oneChildMessage = Math.max(oneFalse, oneTrue);
			double zeroChildMessage = Math.max(zeroFalse, zeroTrue);

			boolean argmaxZeroChildMessage = (zeroChildMessage == zeroFalse) ? false
					: true;
			boolean argmaxOneChildMessage = (oneChildMessage == oneFalse) ? false
					: true;

			BinaryMessage message = new BinaryMessage(zeroChildMessage,
					oneChildMessage);
			ArgMax argMax = new ArgMax(argmaxZeroChildMessage,
					argmaxOneChildMessage);
			return new ArgMaxBinaryMessage(message, argMax);
		}
	}

	/**
	 * Distributes messages from parent to child nodes
	 *
	 */
	public void distribute(Node node) {
		if (!node.isLeaf()) {

			Boolean starValue = starValues.get(node);
			// iterate over children
			for (Node child : node.getNeighbors()) {
				if (node.getParent() == null || !node.getParent().equals(child)) {
					Edge edge = new Edge(child, node);
					ArgMax argMax = maxValues.get(edge);
					boolean childStarValue = starValue ? argMax.isOneValue()
							: argMax.isZeroValue();
					starValues.put(child, childStarValue);

					System.out.println(child.getKey() + " x*: "+ childStarValue + " " + argMax + " " + messages.get(edge));
					distribute(child);
				}
			}
		}
	}
}
