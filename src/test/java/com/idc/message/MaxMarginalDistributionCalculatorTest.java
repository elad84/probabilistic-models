package com.idc.message;

import java.util.Map;
import java.util.function.BooleanSupplier;

import org.junit.Assert;
import org.junit.Test;

import com.idc.model.MarginalDisribution;
import com.idc.model.Node;
import com.idc.model.TransmissionTree;

public class MaxMarginalDistributionCalculatorTest {

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

		double mMax1 = messagePasser.computeMarginals(node);
		Map<Node, Boolean> starValues1 = messagePasser.getStarValues();
		System.out.println("--------------------------------------");

		tree = getTransmissionTree(i);
		messagePasser = new MaxMarginalDisributionCalculator(tree);
		node = tree.getNode(2);
		node.setRoot(true);

		double mMax2 = messagePasser.computeMarginals(node);
		Map<Node, Boolean> starValues2 = messagePasser.getStarValues();
		System.out.println("--------------------------------------");

		tree = getTransmissionTree(i);
		messagePasser = new MaxMarginalDisributionCalculator(tree);
		node = tree.getNode(6);
		node.setRoot(true);

		double mMax3 = messagePasser.computeMarginals(node);
		Map<Node, Boolean> starValues3 = messagePasser.getStarValues();

		Assert.assertEquals("1 vs 2", mMax1,mMax2,.0001);
		Assert.assertEquals("1 vs 3", mMax1,mMax3,.0001);

		assertMaps(starValues1,starValues2);
		assertMaps(starValues1,starValues3);
	}

	private void assertMaps(Map<Node, Boolean> starValues1, Map<Node, Boolean> starValues2) {
		for (Map.Entry<Node, Boolean> entry : starValues1.entrySet()) {
			Node key = entry.getKey();
			Boolean value0 = entry.getValue();
			Boolean value1 = starValues2.get(key);
			Assert.assertNotNull(value0);
			Assert.assertNotNull(value1);

			Assert.assertEquals("key: "+ key.getKey(), value0,value1);
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
