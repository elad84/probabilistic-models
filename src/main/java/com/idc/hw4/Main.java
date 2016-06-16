package com.idc.hw4;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.idc.message.MaxMarginalDisributionCalculator;
import com.idc.message.MarginalDisributionCalculator;
import com.idc.message.TransmissionTreeFactory;
import com.idc.model.BinaryMessage;
import com.idc.model.Edge;
import com.idc.model.MarginalDisribution;
import com.idc.model.Node;
import com.idc.model.Psi;
import com.idc.model.TransmissionTree;

/**
 * Inference data from complete or partial data
 * 
 * @author eladcohen
 *
 */
public class Main {

	public static ArrayList<Edge> edgesOrdered;
	public static TransmissionTree tree;
	static {
		tree = TransmissionTreeFactory.buildHW3Tree();
		edgesOrdered = new ArrayList<Edge>();
		edgesOrdered.add(new Edge(tree.getNode(1), tree.getNode(2)));
		edgesOrdered.add(new Edge(tree.getNode(2), tree.getNode(3)));
		edgesOrdered.add(new Edge(tree.getNode(2), tree.getNode(4)));
		edgesOrdered.add(new Edge(tree.getNode(1), tree.getNode(5)));
		edgesOrdered.add(new Edge(tree.getNode(5), tree.getNode(6)));
		edgesOrdered.add(new Edge(tree.getNode(5), tree.getNode(7)));
		edgesOrdered.add(new Edge(tree.getNode(1), tree.getNode(8)));
		edgesOrdered.add(new Edge(tree.getNode(8), tree.getNode(9)));
		edgesOrdered.add(new Edge(tree.getNode(8), tree.getNode(10)));
	}

	/**
	 * Inferring tree edges based on given method. Supports three methods:
	 * <ul>
	 * <li>
	 * C - indicates inference is done on complete data. Expected arguments:
	 * <ul>
	 * <li>Path to a file with data for all RVs</li>
	 * </ul>
	 * </li>
	 * <li>
	 * M - indicates inference is done based on partial data with Maximum
	 * probability method. Expected arguments:
	 * <ul>
	 * <li>Path to a file with partial data for some of the RVs</li>
	 * <li>initial probability of flip for edge 1-2</li>
	 * <li>initial probability of flip for edge 2-3</li>
	 * <li>initial probability of flip for edge 1-5</li>
	 * <li>initial probability of flip for edge 5-6</li>
	 * <li>initial probability of flip for edge 5-7</li>
	 * <li>initial probability of flip for edge 1-8</li>
	 * <li>initial probability of flip for edge 8-9</li>
	 * <li>initial probability of flip for edge 8-10</li>
	 * </ul>
	 * </li>
	 * <li>
	 * E - indicates inference is done based on partial data with Expected
	 * Maximization method. Expected arguments:
	 * <ul>
	 * <li>Path to a file with partial data for some of the RVs</li>
	 * <li>initial probability of flip for edge 1-2</li>
	 * <li>initial probability of flip for edge 2-3</li>
	 * <li>initial probability of flip for edge 1-5</li>
	 * <li>initial probability of flip for edge 5-6</li>
	 * <li>initial probability of flip for edge 5-7</li>
	 * <li>initial probability of flip for edge 1-8</li>
	 * <li>initial probability of flip for edge 8-9</li>
	 * <li>initial probability of flip for edge 8-10</li>
	 * </ul>
	 * </li>
	 * </ul>
	 * 
	 * @param args
	 * @throws IllegalAccessException
	 */
	public static void main(String[] args) throws IllegalAccessException {

		args[0] = "E";
		args[1] = "tree-data-2c.txt";
		
		String[] probsStr = "0.0001	0.529	0.483	0.0001	0.0001	0.505	0.472	0.019	0.0001".split(" |\t");
		args[2] = probsStr[0];
		args[3] = probsStr[1];
		args[4] = probsStr[2];
		args[5] = probsStr[3];
		args[6] = probsStr[4];
		args[7] = probsStr[5];
		args[8] = probsStr[6];
		args[9] = probsStr[7];
		args[10] = probsStr[8];

		switch (args[0]) {
		case "C":
			inferenceFromCompleteData(args[1]);
			break;
		case "M":
		case "E":
			inferenceFromPartialData(args);
			break;
		default:
			break;
		}

	}

	/**
	 * Inferring the edges values based on sample data with partial RVs
	 * assignment and an initial edges values assignment
	 * 
	 * @param args
	 * @throws IllegalAccessException
	 */
	public static void inferenceFromPartialData(String[] args)
			throws IllegalAccessException {
		if (args.length < 11) {
			throw new IllegalArgumentException(
					"must supply with initial settings for model parameters");
		}

		String dataFilePath = args[1];

		// get initial edges probabilities
		double[] initialParams = new double[9];
		initialParams[0] = Double.valueOf(args[2]);
		initialParams[1] = Double.valueOf(args[3]);
		initialParams[2] = Double.valueOf(args[4]);
		initialParams[3] = Double.valueOf(args[5]);
		initialParams[4] = Double.valueOf(args[6]);
		initialParams[5] = Double.valueOf(args[7]);
		initialParams[6] = Double.valueOf(args[8]);
		initialParams[7] = Double.valueOf(args[9]);
		initialParams[8] = Double.valueOf(args[10]);

		String option = args[0];

		inferenceFromPartialData(option, dataFilePath, initialParams);
	}

	public static double inferenceFromPartialData(String option,
			String dataFilePath, double[] initialParams) {
		tree.setEdgeWeight(initialParams[0], edgesOrdered.get(0));
		tree.setEdgeWeight(initialParams[1], edgesOrdered.get(1));
		tree.setEdgeWeight(initialParams[2], edgesOrdered.get(2));
		tree.setEdgeWeight(initialParams[3], edgesOrdered.get(3));
		tree.setEdgeWeight(initialParams[4], edgesOrdered.get(4));
		tree.setEdgeWeight(initialParams[5], edgesOrdered.get(5));
		tree.setEdgeWeight(initialParams[6], edgesOrdered.get(6));
		tree.setEdgeWeight(initialParams[7], edgesOrdered.get(7));
		tree.setEdgeWeight(initialParams[8], edgesOrdered.get(8));

		// read all observations
		List<HashMap<Node, Double>> observations;
		try {
			observations = ObservationsReader.readObservations(tree,
					dataFilePath);
		} catch (IOException e) {
			System.out.println("Can't read file");
			return 1;
		}

		switch (option) {
		case "M":
			return maxProbabilityInferance(observations);
		case "E":
			return expectationMaximizationInferance(observations);
		default:
			break;
		}
		return 1;
	}

	/**
	 * Inferring tree edges by the max probability method for partial data
	 * 
	 * @param observations
	 *            list of partial observations
	 * @return
	 */
	public static double maxProbabilityInferance(
			List<HashMap<Node, Double>> observations) {

		double prevProbability = Double.NEGATIVE_INFINITY;

		// print header
		for (Edge edge : edgesOrdered) {
			System.out.print("p" + edge.getFirstNode().getKey() + "-"
					+ edge.getSecondNode().getKey() + "\t");
		}
		System.out.print("\tlog-prob");
		System.out.println("\t\tlog-ld");

		List<HashMap<Node, Double>> inferedObservations = ObservationsReader
				.copyObservations(observations);

		// Iterate over data until find local maximum. In each iteration do:
		// 1. Inference the hidden RVs
		// 2. calculate log probability of the observed RVs
		// 3. calculate log likelihood of the complete data
		// 4. update edges values based on complete data
		double dataProbability = 0;
		do {
			dataProbability = 0;
			// iterate over all observations
			for (int i = 0; i < observations.size(); i++) {
				HashMap<Node, Double> observation = observations.get(i);
				// infer the hidden values
				double probability = inferenceHiddenValues(observation,
						inferedObservations.get(i));

				// add current observation likelihood to log-prob
//				System.out.println(probability);
				dataProbability += Math.log(probability);
			}

			double dataLikelihood = 0;

			// calculate the log likelihood of the complete data
			for (HashMap<Node, Double> observation : observations) {
				tree.setValues(observation);
				MarginalDisributionCalculator marginalDisributionCalculator = new MarginalDisributionCalculator(
						tree);
				double likelihood = marginalDisributionCalculator
						.computeMarginals(tree.getRoot());

//				System.out.println(Math.log(likelihood));
				dataLikelihood += Math.log(likelihood);

			}

			// print iteration results
			for (Edge edge : edgesOrdered) {
				System.out.printf("%.3f\t", tree.getEdgeWeight(edge));
			}
			System.out.printf("\t%.4f", dataProbability);
			System.out.printf("\t\t%.4f\n", dataLikelihood);

			if (!Double.isFinite(dataProbability)) {
				break;
			}
			// if data probability did not improve more than epsilon found local
			// maximum
			if (dataProbability - prevProbability < 0.001)
				break;

			// save probability
			prevProbability = dataProbability;

			// update the parameters with the inferred hidden RVs and observed
			// RVs
			inferFromCompleteData(tree, inferedObservations);

		} while (true);

		return dataProbability;
	}

	/**
	 * Inference hidden RVs for given observation
	 * 
	 * @param observation
	 * @param inferedObservations
	 * @param i
	 * @return
	 */
	private static double inferenceHiddenValues(
			HashMap<Node, Double> observation,
			HashMap<Node, Double> inferedObservation) {

		// initialize the tree with the current observation
		tree.setValues(observation);

		// compute assignment that maximizes joint probability
		MaxMarginalDisributionCalculator marginalDisributionCalculator = new MaxMarginalDisributionCalculator(
				tree);
		double probability = marginalDisributionCalculator
				.computeMarginals(tree.getRoot());

		Map<Node, Boolean> marginalDisributionsMap = marginalDisributionCalculator
				.getStarValues();

		// inference hidden RVs
		for (Entry<Node, Boolean> marginalDist : marginalDisributionsMap
				.entrySet()) {
			if (marginalDist.getValue())
				inferedObservation.put(marginalDist.getKey(), 1.0);
			else
				inferedObservation.put(marginalDist.getKey(), 0.0);
		}
		return probability;
	}

	public static double expectationMaximizationInferance(
			List<HashMap<Node, Double>> observations) {

		double prevLikelihood = Double.NEGATIVE_INFINITY;

		// print header
		for (Edge edge : edgesOrdered) {
			System.out.print("p" + edge.getFirstNode().getKey() + "-"
					+ edge.getSecondNode().getKey() + "\t");
		}
		System.out.print("\tlog-prob");
		System.out.println("\t\tlog-ld");

		double dataLikelihood;
		do {

			Map<Edge, Double> expectedFlipProbs = new HashMap<Edge, Double>();
			for (Edge edge : edgesOrdered) {
				expectedFlipProbs.put(edge, 0.0);
			}
			dataLikelihood = 0;
			// iterate over all observations
			for (int i = 0; i < observations.size(); i++) {

				HashMap<Node, Double> observation = observations.get(i);

				// initialize the tree with the current observation
				tree.setValues(observation);

				// compute marginal probabilities for all the nodes
				MarginalDisributionCalculator marginalDisributionCalculator = new MarginalDisributionCalculator(
						tree);
				double likelihood = marginalDisributionCalculator
						.computeMarginals(tree.getRoot());

				Map<Integer, MarginalDisribution> marginalDisributionsMap = tree
						.getNodesMarginalDisribution();
				Map<Edge, BinaryMessage> messages = marginalDisributionCalculator
						.getMessages();
				for (Edge edge : edgesOrdered) {
					BinaryMessage message = messages.get(edge);

					double[] marginalDisribution = marginalDisributionsMap.get(
							edge.getFirstNode().getKey())
							.getNormalizedMarginalDisribution();
					Psi psi = edge.getSecondNode().getPsi();
					Double edgeFlipProb = tree.getEdgeWeight(edge);

					double p1 = marginalDisribution[1] * edgeFlipProb
							* psi.getValue(false) / message.getValue(true);

					double p2 = marginalDisribution[0] * edgeFlipProb
							* psi.getValue(true) / message.getValue(false);

					// System.out.println(firstNode.getKey() + ","
					// + secondNode.getKey() + ":" + zeroOneP + " "
					// + oneZeroP);

					expectedFlipProbs.put(edge, expectedFlipProbs.get(edge)
							+ p1 + p2);

				}

				dataLikelihood += Math.log(likelihood);
			}

			// calculate the log probability of the complete data
			double dataProbability = 0;

			// calculate the log likelihood of the complete data
			for (HashMap<Node, Double> observation : observations) {
				tree.setValues(observation);
				MaxMarginalDisributionCalculator marginalDisributionCalculator = new MaxMarginalDisributionCalculator(
						tree);
				marginalDisributionCalculator.collect(tree.getRoot(), null);
				double probability = marginalDisributionCalculator.getmMax();
				// .computeMarginals(tree.getRoot());

				dataProbability += Math.log(probability);
			}

			// print iteration results
			for (Edge edge : edgesOrdered) {
				System.out.printf("%.3f\t", tree.getEdgeWeight(edge));
			}
			System.out.printf("\t%.4f", dataProbability);
			System.out.printf("\t\t%.4f\n", dataLikelihood);

			if (dataLikelihood - prevLikelihood < 0.001)
				break;

			// save likelihood
			prevLikelihood = dataLikelihood;

			// update the parameters
			for (Entry<Edge, Double> e : expectedFlipProbs.entrySet()) {
				tree.setEdgeWeight(e.getValue() / observations.size(),
						e.getKey());
				// System.out.println(e.getKey() + ":" + e.getValue());
			}

		} while (true);

		return dataLikelihood;
	}

	/**
	 * Print the inferred data from a file with observations
	 * 
	 * @param dataFilePath
	 */
	public static void inferenceFromCompleteData(String dataFilePath) {
		// read all observations
		List<HashMap<Node, Double>> observations;
		try {
			observations = ObservationsReader.readObservations(tree,
					dataFilePath);
		} catch (IOException e) {
			System.out.println("Can't read file");
			return;
		}

		inferFromCompleteData(tree, observations);

		// print results
		for (Edge edge : edgesOrdered) {
			System.out.print("p" + edge.getFirstNode().getKey() + "-"
					+ edge.getSecondNode().getKey() + "\t");
		}
		System.out.println("\tlog-ld");
		for (Edge edge : edgesOrdered) {
			System.out.printf("%.3f\t", tree.getEdgeWeight(edge));
		}
		System.out.printf("\t%.4f\n",
				calcLogProbability(tree, observations, "C"));
	}

	public static void inferFromCompleteData(TransmissionTree tree,
			List<HashMap<Node, Double>> observations) {
		Set<Edge> edges = tree.getEdges().keySet();
		// iterate over all edges and calculate probability
		for (Edge edge : edges) {
			double countFlips = 0;
			// for every edge run on all observations and count data flips
			for (HashMap<Node, Double> observation : observations) {
				Double firstValue = observation.get(edge.getFirstNode());
				Double socondValue = observation.get(edge.getSecondNode());

				countFlips += Math.round(firstValue) == Math.round(socondValue) ? 0
						: 1;
			}

			tree.setEdgeWeight(countFlips / observations.size(), edge);
		}
	}

	/**
	 * Calculates the log probability for given observations with tree
	 * 
	 * @param tree
	 * @param observations
	 * @param option
	 * @return
	 */
	private static double calcLogProbability(TransmissionTree tree,
			List<HashMap<Node, Double>> observations, String option) {
		double logProbability = 0;
		for (HashMap<Node, Double> observation : observations) {
			tree.setValues(observation);
			// for every observation calculate probability
			logProbability += tree.logProbability();
		}
		return logProbability;
	}

	// private static double calcLogLikelihood(TransmissionTree tree,
	// List<HashMap<Node, Double>> observations, String option) {
	// double logLikelihood = 0;
	// for (HashMap<Node, Double> observation : observations) {
	// tree.setValues(observation);
	// // for every observation calculate Likelihood
	// logLikelihood += tree.logLikelihood();
	// }
	// return logLikelihood;
	// }

}
