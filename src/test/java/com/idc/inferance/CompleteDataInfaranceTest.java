package com.idc.inferance;

import org.junit.Test;

import com.idc.hw4.Main;

public class CompleteDataInfaranceTest {

	@Test
	public void test1a() {
		testDataFile("tree-data-1a.txt");
		System.out.println();
	}

	@Test
	public void test1b() {
		testDataFile("tree-data-1b.txt");
		System.out.println();
	}

	@Test
	public void test1c() {
		testDataFile("tree-data-1c.txt");
		System.out.println();
	}

	private void testDataFile(String dataFileName) {
		Main.inferenceFromCompleteData(dataFileName);
	}
}
