package com.idc.message;

import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

import com.idc.model.MarginalDisribution;
import com.idc.model.Node;
import com.idc.model.TransmissionTree;

/**
 * Tests {@link MarginalDisributionCalculator} logic
 * 
 * @author eladcohen
 *
 */
public class MarginalDistributionCalculatorTest {

	/**
	 * Tests data settings:
	 * <ul>
	 * <li>X3=0</li>
	 * <li>X4=1</li>
	 * <li>X6=1</li>
	 * <li>X7=0</li>
	 * <li>X9=0</li>
	 * <li>X10=1</li>
	 * </ul>
	 * 
	 * The tests runs calculate three times where each time there is
	 * one node as root:
	 * <ul>
	 * <li>X1</li>
	 * <li>X2</li>
	 * <li>X6</li>
	 * </ul>
	 * 
	 * Validate the marginal distribution for every node is the same for all iterations
	 * @throws IllegalAccessException 
	 */
	@Test
	public void testComputeMarginals1() throws IllegalAccessException{
		testComputeMarginals(1);
	}

	/**
	 * Tests data settings:
	 * <ul>
	 * <li>X3=0</li>
	 * <li>X4=0</li>
	 * <li>X6=1</li>
	 * <li>X7=0</li>
	 * <li>X9=0</li>
	 * <li>X10=1</li>
	 * </ul>
	 * 
	 * The tests runs calculate three times where each time there is
	 * one node as root:
	 * <ul>
	 * <li>X1</li>
	 * <li>X2</li>
	 * <li>X6</li>
	 * </ul>
	 * 
	 * Validate the marginal distribution for every node is the same for all iterations
	 * @throws IllegalAccessException 
	 */
	@Test
	public void testComputeMarginals2() throws IllegalAccessException{
		testComputeMarginals(2);
	}

	/**
	 * Tests data settings:
	 * <ul>
	 * <li>X3=1</li>
	 * <li>X4=1</li>
	 * <li>X6=1</li>
	 * <li>X7=1</li>
	 * <li>X9=1</li>
	 * <li>X10=1</li>
	 * </ul>
	 * 
	 * The tests runs calculate three times where each time there is
	 * one node as root:
	 * <ul>
	 * <li>X1</li>
	 * <li>X2</li>
	 * <li>X6</li>
	 * </ul>
	 * 
	 * Validate the marginal distribution for every node is the same for all iterations
	 * @throws IllegalAccessException 
	 */
	@Test
	public void testComputeMarginals3() throws IllegalAccessException{
		testComputeMarginals(3);
	}

	public void testComputeMarginals(int i) throws IllegalAccessException{
		TransmissionTree tree = getTransmissionTree(i);
		MarginalDisributionCalculator messagePasser = new MarginalDisributionCalculator(tree);
		Node node = tree.getNode(1);
		tree.setRoot(node);

		messagePasser.computeMarginals(node);
		Map<Integer, MarginalDisribution> nodesMarginalDisribution1 = tree.getNodesMarginalDisribution();
		System.out.println(tree);
		System.out.println("--------------------------------------");

		tree = getTransmissionTree(i);
		messagePasser = new MarginalDisributionCalculator(tree);
		node = tree.getNode(2);
		tree.setRoot(node);

		messagePasser.computeMarginals(node);
		Map<Integer, MarginalDisribution> nodesMarginalDisribution2 = tree.getNodesMarginalDisribution();
		System.out.println(tree);
		System.out.println("--------------------------------------");

		tree = getTransmissionTree(i);
		messagePasser = new MarginalDisributionCalculator(tree);
		node = tree.getNode(6);
		tree.setRoot(node);

		messagePasser.computeMarginals(node);
		Map<Integer, MarginalDisribution> nodesMarginalDisribution6 = tree.getNodesMarginalDisribution();
		System.out.println(tree);

		assertMaps(nodesMarginalDisribution1,nodesMarginalDisribution2);
		assertMaps(nodesMarginalDisribution2,nodesMarginalDisribution6);
	}

	private void assertMaps(Map<Integer, MarginalDisribution> nodesMarginalDisribution0, Map<Integer, MarginalDisribution> nodesMarginalDisribution1) {
		for (Map.Entry<Integer, MarginalDisribution> entry : nodesMarginalDisribution0.entrySet()) {
			Integer key = entry.getKey();
			MarginalDisribution value0 = entry.getValue();
			MarginalDisribution value1 = nodesMarginalDisribution1.get(key);
			Assert.assertNotNull(value0);
			Assert.assertNotNull(value1);

			Assert.assertEquals("expected data to be the same for both nodes but got:"
					+ " Node " + key + " , with marginal : " + value0 +
					"and second marginal is: "+ value1,value0.toString(),value1.toString());
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
		TransmissionTree tree = TransmissionTreeFactory.buildHW2Tree();

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
		TransmissionTree tree = TransmissionTreeFactory.buildHW2Tree();

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
		TransmissionTree tree = TransmissionTreeFactory.buildHW2Tree();

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
