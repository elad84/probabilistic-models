package com.idc.message;

import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

import com.idc.model.MarginalDisribution;
import com.idc.model.Node;
import com.idc.model.TransmissionTree;

public class MaxMessagePasserTest {

	@Test
	public void testComputeMarginals1(){
		testComputeMarginals(1);
	}

	@Test
	public void testComputeMarginals2(){
		testComputeMarginals(2);
	}

	@Test
	public void testComputeMarginals3(){
		testComputeMarginals(3);
	}

	public void testComputeMarginals(int i){
		TransmissionTree tree = getTransmissionTree(i);
		MaxMarginalDisributionCalculator messagePasser = new MaxMarginalDisributionCalculator(tree);
		Node node = tree.getNode(1);
		node.setRoot(true);

		messagePasser.computeMarginals(node);
		Map<Integer, MarginalDisribution> nodesMarginalDisribution0 = tree.getNodesMarginalDisribution();
		System.out.println(tree);
		System.out.println("--------------------------------------");

		tree = getTransmissionTree(i);
		messagePasser = new MaxMarginalDisributionCalculator(tree);
		node = tree.getNode(2);
		node.setRoot(true);

		messagePasser.computeMarginals(node);
		Map<Integer, MarginalDisribution> nodesMarginalDisribution1 = tree.getNodesMarginalDisribution();
		System.out.println(tree);
		System.out.println("--------------------------------------");

		/*tree = getTransmissionTree(i);
		messagePasser = new MaxMarginalDisributionCalculator(tree);
		node = tree.getNode(6);
		node.setRoot(true);

		messagePasser.computeMarginals(node);
		Map<Integer, MarginalDisribution> nodesMarginalDisribution2 = tree.getNodesMarginalDisribution();
		System.out.println(tree);

		assertMaps(nodesMarginalDisribution0,nodesMarginalDisribution1);
		assertMaps(nodesMarginalDisribution1,nodesMarginalDisribution2);*/
	}

	private void assertMaps(Map<Integer, MarginalDisribution> nodesMarginalDisribution0, Map<Integer, MarginalDisribution> nodesMarginalDisribution1) {
		for (Map.Entry<Integer, MarginalDisribution> entry : nodesMarginalDisribution0.entrySet()) {
			Integer key = entry.getKey();
			MarginalDisribution value0 = entry.getValue();
			MarginalDisribution value1 = nodesMarginalDisribution1.get(key);
			Assert.assertNotNull(value0);
			Assert.assertNotNull(value1);

			Assert.assertEquals(value0.toString(),value1.toString());
		}
	}

	private TransmissionTree getTransmissionTree(int i) {
		if (i==1){
			return getTransmissionTree1();
		}
		if (i==2){
			return getTransmissionTree2();
		}
		if (i==3){
			return getTransmissionTree3();
		}
		return null;
	}

	private TransmissionTree getTransmissionTree1() {
		TransmissionTree tree = TransmissionTreeFactory.buildTree();

		Node node = tree.getNode(3);
		node.setValue(0);
		node = tree.getNode(4);
		node.setValue(1);
		node = tree.getNode(6);
		node.setValue(1);
		node = tree.getNode(7);
		node.setValue(0);
		node = tree.getNode(9);
		node.setValue(0);
		node = tree.getNode(10);
		node.setValue(1);
		return tree;
	}

	private TransmissionTree getTransmissionTree2() {
		TransmissionTree tree = TransmissionTreeFactory.buildTree();

		Node node = tree.getNode(3);
		node.setValue(0);
		node = tree.getNode(4);
		node.setValue(0);
		node = tree.getNode(6);
		node.setValue(1);
		node = tree.getNode(7);
		node.setValue(0);
		node = tree.getNode(9);
		node.setValue(0);
		node = tree.getNode(10);
		node.setValue(1);
		return tree;
	}

	private TransmissionTree getTransmissionTree3() {
		TransmissionTree tree = TransmissionTreeFactory.buildTree();

		Node node = tree.getNode(3);
		node.setValue(1);
		node = tree.getNode(4);
		node.setValue(1);
		node = tree.getNode(6);
		node.setValue(1);
		node = tree.getNode(7);
		node.setValue(1);
		node = tree.getNode(9);
		node.setValue(1);
		node = tree.getNode(10);
		node.setValue(1);
		return tree;
	}
}
