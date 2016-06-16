package com.idc.inferance;

import org.junit.Test;

import com.idc.hw4.Main;
import com.idc.model.Edge;

public class EMInferenceTest {

	@Test
	public void test2a() {
//		testDataFile("tree-data-2a.txt");
//		testDataFile("tree-data-2b.txt");
		testDataFile("tree-data-2c.txt");
		System.out.println();
	}

	private void testDataFile(String fileFath) {
		double maxLogLd = Double.NEGATIVE_INFINITY;
		double[] bestParams = null;
		for (int i = 0; i < 9; i++) {
			double[] initialParams = new double[9];
			for (int j = 0; j < initialParams.length; j++) {
				initialParams[j] = Math.random();
			}
			double logLd = Main.inferenceFromPartialData("E", fileFath,
					initialParams);

			if (logLd > maxLogLd) {
				maxLogLd = logLd;
				bestParams = Main.tree.getWeights(Main.edgesOrdered);
			}
		}

		for (Edge edge : Main.edgesOrdered) {
			System.out.print("p" + edge.getFirstNode().getKey() + "-"
					+ edge.getSecondNode().getKey() + "\t");
		}
		System.out.println("\tlog-ld");

		for (double d : bestParams) {
			System.out.printf("%f\t", d);
		}
		System.out.printf("\t%f\n", maxLogLd);
		for (double d : bestParams) {
			System.out.printf("%.3f\t", d);
		}
		System.out.printf("\t%f\n", maxLogLd);
	}
}
