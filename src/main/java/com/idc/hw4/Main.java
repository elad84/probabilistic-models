package com.idc.hw4;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.idc.message.MarginalDisributionCalculator;
import com.idc.message.MaxMarginalDisributionCalculator;
import com.idc.message.TransmissionTreeFactory;
import com.idc.model.Edge;
import com.idc.model.MarginalDisribution;
import com.idc.model.Node;
import com.idc.model.TransmissionTree;

public class Main {

	private static ArrayList<Edge> edgesOrdered;
	private static TransmissionTree tree;

	public static void main(String[] args) throws IllegalAccessException {

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

		String[] probsStr = "0.034	0.503	0.087	0.030	0.116	0.162	0.043	0.253	0.071"
				.split(" |\t");
		// args[2] = probsStr[0];
		// args[3] = probsStr[1];
		// args[4] = probsStr[2];
		// args[5] = probsStr[3];
		// args[6] = probsStr[4];
		// args[7] = probsStr[5];
		// args[8] = probsStr[6];
		// args[9] = probsStr[7];
		// args[10] = probsStr[8];

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

	public static void inferenceFromPartialData(String[] args)
			throws IllegalAccessException {
		if (args.length < 11) {
			throw new IllegalArgumentException(
					"must supply with initial settings for model parameters");
		}

		String dataFilePath = args[1];

		// get initial edges probabilities
		double p12 = Double.valueOf(args[2]);
		double p23 = Double.valueOf(args[3]);
		double p24 = Double.valueOf(args[4]);
		double p15 = Double.valueOf(args[5]);
		double p56 = Double.valueOf(args[6]);
		double p57 = Double.valueOf(args[7]);
		double p18 = Double.valueOf(args[8]);
		double p89 = Double.valueOf(args[9]);
		double p810 = Double.valueOf(args[10]);

		// read all observations
		List<HashMap<Node, Double>> observations = readObservations(tree,
				dataFilePath);

		tree.setEdgeWeight(p12, edgesOrdered.get(0));
		tree.setEdgeWeight(p23, edgesOrdered.get(1));
		tree.setEdgeWeight(p24, edgesOrdered.get(2));
		tree.setEdgeWeight(p15, edgesOrdered.get(3));
		tree.setEdgeWeight(p56, edgesOrdered.get(4));
		tree.setEdgeWeight(p57, edgesOrdered.get(5));
		tree.setEdgeWeight(p18, edgesOrdered.get(6));
		tree.setEdgeWeight(p89, edgesOrdered.get(7));
		tree.setEdgeWeight(p810, edgesOrdered.get(8));

		double prevLikelihood = -999999999;
		double prevProbability = -999999999;

		// print header
		for (Edge edge : edgesOrdered) {
			System.out.print("p" + edge.getFirstNode().getKey() + "-"
					+ edge.getSecondNode().getKey() + "\t");
		}
		System.out.print("\tlog-prob");
		System.out.println("\t\tlog-ld");

		List<HashMap<Node, Double>> inferedObservations = copyObservations(observations);

		do {

			double dataLikelihood = 0;
			// iterate over all observations
			for (int i = 0; i < observations.size(); i++) {

				HashMap<Node, Double> observation = observations.get(i);

				// initialize the tree with the current observation
				tree.setValues(observation);

				// compute marginal probabilities for all the nodes
				MarginalDisributionCalculator marginalDisributionCalculator = new MarginalDisributionCalculator(
						tree);
				marginalDisributionCalculator.computeMarginals(tree.getRoot());

				Map<Integer, MarginalDisribution> marginalDisributionsMap = tree
						.getNodesMarginalDisribution();
				double likelihood = 0;

				// inference hidden RVs
				HashMap<Node, Double> inferedObservation = inferedObservations
						.get(i);
				for (Integer key : marginalDisributionsMap.keySet()) {
					MarginalDisribution marginalDisribution = marginalDisributionsMap
							.get(key);
					if (args[0].equals("M")) {
						double prob0 = marginalDisribution.getValue(false);
						double prob1 = marginalDisribution.getValue(true);
						if (prob0 == prob1) {
							System.out.println();
						}
						// if (key == 1) {
						inferedObservation.put(tree.getNode(key),
								prob1 >= prob0 ? 1.0 : 0.0);
						// } else

						// inferedObservation.put(tree.getNode(key),
						// prob1 > prob0 ? 1.0 : 0.0);
					} else { // args[0].equals("E")
						// the expectation of an indicator is the probability it
						// equals 1
						double[] dist = marginalDisribution
								.getNormalizedMarginalDisribution();
						inferedObservation.put(tree.getNode(key), dist[1]);
					}
				}

				// check that the inferred observations agrees with the actual
				// observations
				// for (Entry<Node, Double> e : observation.entrySet()) {
				// assert (e.getValue().equals(inferedObservation.get(e
				// .getKey())));
				// }

				MarginalDisribution marginalDisribution = marginalDisributionsMap
						.get(1);
				double prob0 = marginalDisribution.getValue(false);
				double prob1 = marginalDisribution.getValue(true);
				dataLikelihood += Math.log(prob0 + prob1);
			}

			// calculate the log probability of the complete data
			double dataProbability = calcLogLikelihood(tree,
					inferedObservations, args[1]);

			// print iteration results
			for (Edge edge : edgesOrdered) {
				System.out.printf("%.3f\t", tree.getEdgeWeight(edge));
			}
			System.out.printf("\t%.4f", dataProbability);
			System.out.printf("\t\t%.4f\n", dataLikelihood);

			if (args[0].equals("M")) {
				if (dataProbability - prevProbability < 0.001)
					break;
			} else if (dataLikelihood - prevLikelihood < 0.001)
				break;

			// save likelihood
			prevLikelihood = dataLikelihood;
			prevProbability = dataProbability;

			// update the parameters with the inferred hidden RVs and observed
			// RVs
			inferFromCompleteData(tree, inferedObservations, args[0]);

		} while (true);
	}

	public static List<HashMap<Node, Double>> copyObservations(
			List<HashMap<Node, Double>> observations) {
		List<HashMap<Node, Double>> copyObservations = new ArrayList<HashMap<Node, Double>>();
		for (HashMap<Node, Double> observation : observations) {
			HashMap<Node, Double> copyObservation = new HashMap<Node, Double>();
			for (Entry<Node, Double> e : observation.entrySet()) {
				copyObservation.put(e.getKey(), new Double(e.getValue()));
			}
			copyObservations.add(copyObservation);
		}
		return copyObservations;
	}

	/**
	 * Print the inferred data from a file with observations
	 * 
	 * @param dataFilePath
	 */
	public static void inferenceFromCompleteData(String dataFilePath) {
		// read all observations
		List<HashMap<Node, Double>> observations = readObservations(tree,
				dataFilePath);

		inferFromCompleteData(tree, observations, "C");

		// print results
		for (Edge edge : edgesOrdered) {
			System.out.print("p" + edge.getFirstNode().getKey() + "-"
					+ edge.getSecondNode().getKey() + "\t");
		}
		System.out.println("\tlog-ld");
		for (Edge edge : edgesOrdered) {
			System.out.printf("%.3f\t", tree.getEdgeWeight(edge));
		}
		System.out.println("\t" + calcLogLikelihood(tree, observations, "C"));

	}

	public static void inferFromCompleteData(TransmissionTree tree,
			List<HashMap<Node, Double>> observations, String option) {
		Set<Edge> edges = tree.getEdges().keySet();
		// iterate over all edges and calculate probability
		for (Edge edge : edges) {
			double countFlips = 0;
			// for every edge run on all observations and count data flips
			for (HashMap<Node, Double> observation : observations) {
				Double firstValue = observation.get(edge.getFirstNode());
				Double socondValue = observation.get(edge.getSecondNode());

				switch (option) {
				case "C":
				case "M":
					if (Math.abs(firstValue - socondValue) > 0.0001)
						countFlips++;
					break;

				case "E":
					double flipProb = firstValue * (1 - socondValue)
							+ (1 - firstValue) * socondValue;
//					double noFlipProb = firstValue * socondValue + (1 - firstValue)
//							* (1 - socondValue);

					countFlips += flipProb;

//					System.out.println("flip " + flipProb);
//					System.out.println("no flip " + noFlipProb);
					break;
				default:
					break;
				}
			}

			tree.setEdgeWeight((double) countFlips / observations.size(), edge);
		}
	}

	/**
	 * Calculates the log likelihood for given observations with tree
	 * 
	 * @param tree
	 * @param observations
	 * @param option
	 * @return
	 */
	private static double calcLogLikelihood(TransmissionTree tree,
			List<HashMap<Node, Double>> observations, String option) {
		double logLikelihood = 0;
		for (HashMap<Node, Double> observation : observations) {
			tree.setValues(observation);
			// for every observation calculate likelihood
			logLikelihood += tree.logLikelihood();
		}
		return logLikelihood;
	}

	private static List<HashMap<Node, Double>> readObservations(
			TransmissionTree tree, String dataFilePath) {
		List<String> lines = null;
		try {
			lines = Files.readAllLines(Paths.get(dataFilePath));
		} catch (IOException e) {
			e.printStackTrace();
		}

		String[] headersStr = lines.get(0).split("\t");

		int[] keys = new int[headersStr.length];
		Node[] observedNodes = new Node[headersStr.length];

		for (int i = 0; i < headersStr.length; i++) {
			String keyStr = headersStr[i].substring(1);
			keys[i] = Integer.parseInt(keyStr);

			observedNodes[i] = tree.getNode(keys[i]);
		}

		List<HashMap<Node, Double>> observations = new ArrayList<HashMap<Node, Double>>();
		for (int i = 1; i < lines.size(); i++) {
			String line = lines.get(i);
			HashMap<Node, Double> observation = new HashMap<Node, Double>();
			String[] observationStr = line.split("\t");
			for (int j = 0; j < observationStr.length; j++) {
				observation.put(observedNodes[j],
						Double.parseDouble(observationStr[j]));
			}
			observations.add(observation);
		}

		return observations;
	}
}
