package com.idc.hw4;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import com.idc.model.Node;
import com.idc.model.TransmissionTree;

public class ObservationsReader {

	static List<HashMap<Node, Double>> readObservations(TransmissionTree tree,
			String dataFilePath) throws IOException {
		List<String> lines = null;
		lines = Files.readAllLines(Paths.get(dataFilePath));

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

	static List<HashMap<Node, Double>> copyObservations(
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

}
