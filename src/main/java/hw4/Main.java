package hw4;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import com.idc.message.TransmissionTreeFactory;
import com.idc.model.Edge;
import com.idc.model.Node;
import com.idc.model.TransmissionTree;

public class Main {

	public static void main(String[] args) {

		switch (args[0]) {
		case "C":
			inferenceFromCompleteData(args[1]);
			break;
		case "M":

			break;
		case "E":

			break;

		default:
			break;
		}

	}

	public static void inferenceFromCompleteData(String dataFilePath) {
		TransmissionTree tree = TransmissionTreeFactory.buildHW3Tree();

		List<HashMap<Node, Integer>> observations = readObservations(tree,
				dataFilePath);

		Set<Edge> edges = tree.getEdges().keySet();
		for (Edge edge : edges) {
			int countFlips = 0;
			for (HashMap<Node, Integer> observation : observations) {
				Integer firstValue = observation.get(edge.getFirstNode());
				Integer socondValue = observation.get(edge.getSecondNode());
				if (firstValue != socondValue)
					countFlips++;
			}

			tree.setEdgeWeight((double) countFlips / observations.size(), edge);
		}

		// print results
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

	private static double calcLogLikelihood(TransmissionTree tree,
			List<HashMap<Node, Integer>> observations) {
		double logLikelihood = 0;
		for (HashMap<Node, Integer> observation : observations) {
			tree.setValues(observation);
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
