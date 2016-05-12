package com.idc.message;

import org.junit.Assert;
import org.junit.Test;

import com.idc.model.BinaryMessage;
import com.idc.model.Node;
import com.idc.model.TransmissionTree;

public class MessagePasserTest {
	
	@Test
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
		
		BinaryMessage x1Root = messagePasser.collect(node, null);
		System.out.println("--------------------------------------");
		
		node = tree.getNode(1);
		node.setRoot(false);
		
		node = tree.getNode(2);
		node.setRoot(true);
		
		BinaryMessage x2Root = messagePasser.collect(node, null);
		System.out.println("--------------------------------------");
		
		node = tree.getNode(2);
		node.setRoot(false);
		
		node = tree.getNode(6);
		node.setRoot(true);
		
		BinaryMessage x6Root = messagePasser.collect(node, null);
		
		Assert.assertTrue("not all collect values are the same, x1: " + x1Root + ", x2: " + x2Root + ", x6: " + x6Root, 
				x1Root.getValue(false) == x2Root.getValue(false) && 
				x1Root.getValue(false) == x6Root.getValue(false) &&
				x2Root.getValue(false) == x6Root.getValue(false) &&
				x1Root.getValue(true) == x2Root.getValue(true) &&
				x1Root.getValue(false) == x6Root.getValue(true) &&
				x2Root.getValue(true) == x6Root.getValue(true));
	}

}
