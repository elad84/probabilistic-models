package com.idc.hw4;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.idc.message.MarginalDisributionCalculator;
import com.idc.message.TransmissionTreeFactory;
import com.idc.model.Edge;
import com.idc.model.MarginalDisribution;
import com.idc.model.Node;
import com.idc.model.TransmissionTree;

public class Main {

	public static void main(String[] args) throws IllegalAccessException {

		switch (args[0]) {
		case "C":
			inferenceFromCompleteData(args[1]);
			break;
		case "M":
			if(args.length < 11){
				throw new IllegalArgumentException("must supply with initial settings for model parameters");
			}
			
			String dataFilePath = args[1];
			
			double p12 = Double.valueOf(args[2]);
			double p23 = Double.valueOf(args[3]);
			double p24 = Double.valueOf(args[4]);
			double p15 = Double.valueOf(args[5]);
			double p56 = Double.valueOf(args[6]);
			double p57 = Double.valueOf(args[7]);
			double p18 = Double.valueOf(args[8]);
			double p89 = Double.valueOf(args[9]);
			double p810 = Double.valueOf(args[10]);
			
			TransmissionTree tree = TransmissionTreeFactory.buildHW3Tree();

			//read all observations
			List<HashMap<Node, Integer>> observations = readObservations(tree,
					dataFilePath);

			Set<Edge> edges = tree.getEdges().keySet();
			int edgeInt;
			//set probability for all edges
			for (Edge edge : edges) {
				edgeInt = 10 * edge.getFirstNode().getKey() + edge.getSecondNode().getKey();
				switch(edgeInt){
					case 12:
					case 21:
						tree.setEdgeWeight(p12, edge);
						break;
					case 15:
					case 51:
						tree.setEdgeWeight(p15, edge);
						break;
					case 23:
					case 32:
						tree.setEdgeWeight(p23, edge);
						break;
					case 24:
					case 42:
						tree.setEdgeWeight(p24, edge);
						break;
					case 56:
					case 65:
						tree.setEdgeWeight(p56, edge);
						break;
					case 57:
					case 75:
						tree.setEdgeWeight(p57, edge);
						break;
					case 18:
					case 81:
						tree.setEdgeWeight(p18, edge);
						break;
					case 89:
					case 98:
						tree.setEdgeWeight(p89, edge);
						break;
					case 108:
					case 90:
						tree.setEdgeWeight(p810, edge);
						break;
					default:
						throw new IllegalArgumentException("no such edge found");
				}
			}
			
			double dataLikelihood = calcLogLikelihood(tree, observations);
			
			for(HashMap<Node, Integer> observation  : observations){
				tree.setValues(observation);
				MarginalDisributionCalculator marginalDisributionCalculator = new MarginalDisributionCalculator(tree);
				marginalDisributionCalculator.computeMarginals(tree.getRoot());
				Map<Integer, MarginalDisribution> map = tree.getNodesMarginalDisribution();
				for(Integer key : map.keySet()){
					observation.put(tree.getNode(key), map.get(key).getValue(true) > map.get(key).getValue(false) ? 1 : 
						map.get(key).getValue(true) == map.get(key).getValue(false) ? 1 : 0);
				}
			}
			

			// print results
			for (Edge edge : edges) {
				System.out.print("p" + edge.getFirstNode().getKey() + "-"
						+ edge.getSecondNode().getKey() + "\t");
			}
			System.out.print("\tlog-prob");
			System.out.println("\t\tlog-ld");
			for (Edge edge : edges) {
				System.out.print(tree.getEdgeWeight(edge) + "\t");
			}
			System.out.print("\t" + dataLikelihood);
			System.out.println("\t" + calcLogLikelihood(tree, observations));
			break;
		case "E":

			break;

		default:
			break;
		}

	}

	/**
	 * Print the inferred data from a file with observations
	 * 
	 * @param dataFilePath
	 */
	public static void inferenceFromCompleteData(String dataFilePath) {
		TransmissionTree tree = TransmissionTreeFactory.buildHW3Tree();

		//read all observations
		List<HashMap<Node, Integer>> observations = readObservations(tree,
				dataFilePath);

		inferFromCompleteData(tree, observations);

		// print results
		Set<Edge> edges = tree.getEdges().keySet();
		for (Edge edge : edges) {
			System.out.print("p" + edge.getFirstNode().getKey() + "-"
					+ edge.getSecondNode().getKey() + "\t");
		}
		System.out.println("\tlog-ld");
		for (Edge edge : edges) {
			System.out.print(tree.getEdgeWeight(edge) + "\t");
		}
		System.out.println("\t" + calcLogLikelihood(tree, observations));

	}

	public static void inferFromCompleteData(TransmissionTree tree,
			List<HashMap<Node, Integer>> observations) {
		Set<Edge> edges = tree.getEdges().keySet();
		//iterate over all edges and calculate probability
		for (Edge edge : edges) {
			int countFlips = 0;
			//for every edge run on all observations and count data flips 
			for (HashMap<Node, Integer> observation : observations) {
				Integer firstValue = observation.get(edge.getFirstNode());
				Integer socondValue = observation.get(edge.getSecondNode());
				if (firstValue != socondValue)
					countFlips++;
			}

			tree.setEdgeWeight((double) countFlips / observations.size(), edge);
		}
	}

	/**
	 * Calculates the log likelihood for given observations
	 * with tree
	 * 
	 * @param tree
	 * @param observations
	 * @return
	 */
	private static double calcLogLikelihood(TransmissionTree tree,
			List<HashMap<Node, Integer>> observations) {
		double logLikelihood = 0;
		for (HashMap<Node, Integer> observation : observations) {
			tree.setValues(observation);
			//for every observation calculate likelihood
			logLikelihood += tree.logLikelihood();
		}
		return logLikelihood;
	}

	private static List<HashMap<Node, Integer>> readObservations(
			TransmissionTree tree, String dataFilePath) {
		List<String> lines = null;
		try {
			lines = Files.readAllLines(Paths.get(dataFilePath));
		} catch (IOException e) {
			// TODO Auto-generated catch block
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

		List<HashMap<Node, Integer>> observations = new ArrayList<HashMap<Node, Integer>>();
		for (int i = 1; i < lines.size(); i++) {
			String line = lines.get(i);
			HashMap<Node, Integer> observation = new HashMap<Node, Integer>();
			String[] observationStr = line.split("\t");
			for (int j = 0; j < observationStr.length; j++) {
				observation.put(observedNodes[j],
						Integer.parseInt(observationStr[j]));
			}
			observations.add(observation);
		}

		return observations;
	}
}
