package com.idc.message;

import com.idc.model.MarginalDisribution;
import org.junit.Assert;
import org.junit.Test;

import com.idc.model.BinaryMessage;
import com.idc.model.Node;
import com.idc.model.TransmissionTree;

import java.util.Map;

public class MessagePasserTest {
	
	//@Test
	public void testFirstSetting(){
		TransmissionTree tree = TransmissionTreeFactory.buildTree();
		
		MarginalDisributionCalculator messagePasser = new MarginalDisributionCalculator(tree);
		
		Node node = tree.getNode(3);
		node.setBinaryValue(false);
		node = tree.getNode(4);
		node.setBinaryValue(true);
		node = tree.getNode(6);
		node.setBinaryValue(true);
		node = tree.getNode(7);
		node.setBinaryValue(false);
		node = tree.getNode(9);
		node.setBinaryValue(false);
		node = tree.getNode(10);
		node.setBinaryValue(false);
		
		node = tree.getNode(1);
		node.setRoot(true);

		System.out.println(tree);

		BinaryMessage x1Root = messagePasser.collect(node, null);
		System.out.println("--------------------------------------");
		
		node = tree.getNode(1);
		node.setRoot(false);
		
		node = tree.getNode(2);
		node.setRoot(true);

		System.out.println(tree);
		
		BinaryMessage x2Root = messagePasser.collect(node, null);
		System.out.println("--------------------------------------");
		
		node = tree.getNode(2);
		node.setRoot(false);
		
		node = tree.getNode(6);
		node.setRoot(true);

		System.out.println(tree);
		
		BinaryMessage x6Root = messagePasser.collect(node, null);
		
		Assert.assertTrue("not all collect values are the same, x1: " + x1Root + ", x2: " + x2Root + ", x6: " + x6Root, 
				x1Root.getValue(false) == x2Root.getValue(false) && 
				x1Root.getValue(false) == x6Root.getValue(false) &&
				x2Root.getValue(false) == x6Root.getValue(false) &&
				x1Root.getValue(true) == x2Root.getValue(true) &&
				x1Root.getValue(false) == x6Root.getValue(true) &&
				x2Root.getValue(true) == x6Root.getValue(true));
	}

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
		MarginalDisributionCalculator messagePasser = new MarginalDisributionCalculator(tree);
		Node node = tree.getNode(1);
		node.setRoot(true);

		messagePasser.computeMarginals(node);
		Map<Integer, MarginalDisribution> nodesMarginalDisribution0 = tree.getNodesMarginalDisribution();
		System.out.println(tree);
		System.out.println("--------------------------------------");

		tree = getTransmissionTree(i);
		messagePasser = new MarginalDisributionCalculator(tree);
		node = tree.getNode(2);
		node.setRoot(true);

		messagePasser.computeMarginals(node);
		Map<Integer, MarginalDisribution> nodesMarginalDisribution1 = tree.getNodesMarginalDisribution();
		System.out.println(tree);
		System.out.println("--------------------------------------");

		tree = getTransmissionTree(i);
		messagePasser = new MarginalDisributionCalculator(tree);
		node = tree.getNode(6);
		node.setRoot(true);

		messagePasser.computeMarginals(node);
		Map<Integer, MarginalDisribution> nodesMarginalDisribution2 = tree.getNodesMarginalDisribution();
		System.out.println(tree);

		assertMaps(nodesMarginalDisribution0,nodesMarginalDisribution1);
		assertMaps(nodesMarginalDisribution1,nodesMarginalDisribution2);
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
		node.setBinaryValue(false);
		node = tree.getNode(4);
		node.setBinaryValue(true);
		node = tree.getNode(6);
		node.setBinaryValue(true);
		node = tree.getNode(7);
		node.setBinaryValue(false);
		node = tree.getNode(9);
		node.setBinaryValue(false);
		node = tree.getNode(10);
		node.setBinaryValue(true);
		return tree;
	}

	private TransmissionTree getTransmissionTree2() {
		TransmissionTree tree = TransmissionTreeFactory.buildTree();

		Node node = tree.getNode(3);
		node.setBinaryValue(false);
		node = tree.getNode(4);
		node.setBinaryValue(false);
		node = tree.getNode(6);
		node.setBinaryValue(true);
		node = tree.getNode(7);
		node.setBinaryValue(false);
		node = tree.getNode(9);
		node.setBinaryValue(false);
		node = tree.getNode(10);
		node.setBinaryValue(true);
		return tree;
	}

	private TransmissionTree getTransmissionTree3() {
		TransmissionTree tree = TransmissionTreeFactory.buildTree();

		Node node = tree.getNode(3);
		node.setBinaryValue(true);
		node = tree.getNode(4);
		node.setBinaryValue(true);
		node = tree.getNode(6);
		node.setBinaryValue(true);
		node = tree.getNode(7);
		node.setBinaryValue(true);
		node = tree.getNode(9);
		node.setBinaryValue(true);
		node = tree.getNode(10);
		node.setBinaryValue(true);
		return tree;
	}
}
