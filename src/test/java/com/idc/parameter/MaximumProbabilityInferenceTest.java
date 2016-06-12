package com.idc.parameter;

import com.idc.message.TransmissionTreeFactory;
import com.idc.model.Node;
import com.idc.model.ObservationsData;
import com.idc.model.TransmissionTree;
import org.junit.Ignore;
import org.junit.Test;

import java.util.*;

/**
 * Created by annishaa on 6/1/16.
 */
public class MaximumProbabilityInferenceTest {

    @Test @Ignore
    public void calculate0_0() throws IllegalAccessException {
        compute("input/sample-data-2.txt", new Double[]{0.5,0.5,0.5,0.5,0.5,0.5,0.5,0.5,0.5});
        compute("input/sample-data-2.txt", new Double[]{0.2,0.2,0.2,0.2,0.2,0.2,0.2,0.2,0.2});
        compute("input/sample-data-2.txt", new Double[]{0.6,0.6,0.6,0.6,0.6,0.6,0.6,0.6,0.6});
    }

    @Test
    public void calculate1() throws IllegalAccessException {
        randomCompute("input/tree-data-2a.txt");
    }

    @Test
    public void calculate2() throws IllegalAccessException {
        randomCompute("input/tree-data-2b.txt");
    }

    @Test @Ignore
    public void calculate3() throws IllegalAccessException {
        randomCompute("input/tree-data-2c.txt");
    }

    public void randomCompute(String path) throws IllegalAccessException {
        List<String> result2count = new ArrayList<>();

        for (int i=1 ; i< 100 ;++i) {
            Double[] weights = new Double[10];
            for (int j = 0; j < 10; j++) {
                weights[j] = Math.random();
            }

            String results = compute(path, weights);
            result2count.add(results);
        }

        System.out.println ("*******************");
        for (String result : result2count) {
            System.out.print(result);
        }
    }


    private String compute(String inputFile, Double[] weights) throws IllegalAccessException {
        System.out.println();

        TransmissionTree tree = TransmissionTreeFactory.buildTree(weights);
        Node node = tree.getNode(1);
        node.setRoot(true);

        ObservationDataInputFileProvider observationDataInputFileProvider = new ObservationDataInputFileProvider();
        ObservationsData observationsData = observationDataInputFileProvider.readObservationsDataFile(inputFile);

        MaximumProbabilityInferenceCalculator parameterInferenceCalculator = new MaximumProbabilityInferenceCalculator(tree,observationsData);
        return parameterInferenceCalculator.compute(node);
    }
}
