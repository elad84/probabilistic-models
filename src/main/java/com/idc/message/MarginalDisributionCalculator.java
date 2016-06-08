package com.idc.message;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

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

	private static Logger logger = Logger
			.getLogger(MarginalDisributionCalculator.class);

	private TransmissionTree tree;
	private Map<Edge, BinaryMessage> messages;

	/**
	 * Constructor getting tree to calculate distribution for
	 * 
	 * @param tree
	 */
	public MarginalDisributionCalculator(TransmissionTree tree) {
		this.tree = tree;
		this.messages = new HashMap<Edge, BinaryMessage>();
	}

	/**
	 * Computes the marginal distribution per node
	 * 
	 * @param root
	 * @throws IllegalAccessException
	 */
	public void computeMarginals(Node root) throws IllegalAccessException {
		collect(root, null);
		distribute(root, null);

//		System.out
//				.println("P(XA): "
//						+ (root.getPsi().getValue(false) + root.getPsi()
//								.getValue(true)));
	}

	/**
	 * Collects messages from children to parent and calculate Psi for each of
	 * the nodes
	 * 
	 * @param node
	 * @param caller
	 * @return
	 * @throws IllegalAccessException
	 */
	public BinaryMessage collect(Node node, Node caller)
			throws IllegalAccessException {
//		System.out.println("passing message from node: " + node);
		if (logger.isDebugEnabled()) {
			logger.debug("running collect for node: " + node);
		}
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
					BinaryMessage message = collect(child, node);
					// add for distribute map
					this.messages.put(new Edge(child, node), message);
					messages.add(message);
				}

			}
			BigDecimal zeroPsi = BigDecimal.valueOf(1.0);
			BigDecimal onePsi = BigDecimal.valueOf(1.0);
			// calculate Psi for current node
			for (BinaryMessage message : messages) {
				zeroPsi = zeroPsi.multiply(BigDecimal.valueOf(message
						.getValue(false)));
				onePsi = onePsi.multiply(BigDecimal.valueOf(message
						.getValue(true)));
			}
			if (node.isRoot())
				psi = new Psi(zeroPsi.doubleValue() * 0.5,
						onePsi.doubleValue() * 0.5);
			else
				psi = new Psi(zeroPsi.doubleValue(), onePsi.doubleValue());
		}

		node.setPsi(psi);

		// calculate message from child to parent, when root just return
		if (node.isRoot()) {
			return new BinaryMessage(psi.getValue(false), psi.getValue(true));
		} else {
			Node parent = node.getParent();
			double edgeFlipProbability = tree.getEdgeWeight(new Edge(parent,
					node));
			double zeroChildMessage = (1 - edgeFlipProbability)
					* psi.getValue(false) + edgeFlipProbability
					* psi.getValue(true);
			double oneChildMessage = edgeFlipProbability * psi.getValue(false)
					+ (1 - edgeFlipProbability) * psi.getValue(true);
			logger.debug("message " + node.getKey() + "->" + parent.getKey()
					+ ", values: " + zeroChildMessage + ", " + oneChildMessage);
			return new BinaryMessage(zeroChildMessage, oneChildMessage);
		}
	}

	/**
	 * Distributes messages from parent to child nodes
	 * 
	 * @param node
	 * @param message
	 */
	public void distribute(Node node, BinaryMessage message) {
		double zero;
		double one;
		Psi psi = node.getPsi();
		// calculate the final marginal distribution for given node
		if (message != null) {
			zero = psi.getValue(false) * message.getValue(false);
			one = psi.getValue(true) * message.getValue(true);
		} else {
			if (node.getValue() > -1 && node.getValue() < 2) {
				zero = (1 - node.getValue()) * psi.getValue(false);
				one = node.getValue() * psi.getValue(true);
			} else {
				zero = psi.getValue(false);
				one = psi.getValue(true);
			}
		}

		node.setPsi(new Psi(zero, one));
		node.setMarginalDisribution(new MarginalDisribution(zero, one));

		if (!node.isLeaf()) {
			// iterate over children, for each one calculate the message from 
			//parent and distribute the message to child
			for (Node child : node.getNeighbors()) {
				if (node.getParent() == null || !node.getParent().equals(child)) {
					Edge edge = new Edge(child, node);
					BinaryMessage childMessage = messages.get(edge);
					double edgeFlipProbability = tree.getEdgeWeight(edge);
					double zeroParentMessage = (1 - edgeFlipProbability) * zero
							/ childMessage.getValue(false)
							+ edgeFlipProbability * one
							/ childMessage.getValue(true);
					double oneParentMessage = edgeFlipProbability * zero
							/ childMessage.getValue(false)
							+ (1 - edgeFlipProbability) * one
							/ childMessage.getValue(true);
					distribute(child, new BinaryMessage(zeroParentMessage,
							oneParentMessage));
				}
			}
		}
	}

}