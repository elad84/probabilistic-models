package com.idc.parameter;

import com.idc.message.TransmissionTreeFactory;
import com.idc.model.Node;
import com.idc.model.ObservationsData;
import com.idc.model.TransmissionTree;
import org.junit.Ignore;
import org.junit.Test;

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

    @Test @Ignore
    public void calculate1() throws IllegalAccessException {
        for (int i=1 ; i< 10 ;++i) {
            double c = i/10.0;
            compute("input/tree-data-2a.txt", new Double[]{c,c,c,c,c,c,c,c,c});
        }
    }

    @Test @Ignore
    public void calculate2() throws IllegalAccessException {
        for (int i=1 ; i< 10 ;++i) {
            double c = i/10.0;
            compute("input/tree-data-2b.txt", new Double[]{c,c,c,c,c,c,c,c,c});
        }
    }

    @Test
    public void calculate3() throws IllegalAccessException {
        for (int i=1 ; i< 10 ;++i) {
            double c = i/10.0;
            compute("input/tree-data-2c.txt", new Double[]{c,c,c,c,c,c,c,c,c});
        }
    }


    private void compute(String inputFile, Double[] weights) throws IllegalAccessException {
        System.out.println();

        TransmissionTree tree = TransmissionTreeFactory.buildTree(weights);
        Node node = tree.getNode(1);
        node.setRoot(true);

        ObservationDataInputFileProvider observationDataInputFileProvider = new ObservationDataInputFileProvider();
        ObservationsData observationsData = observationDataInputFileProvider.readObservationsDataFile(inputFile);

        MaximumProbabilityInferenceCalculator parameterInferenceCalculator = new MaximumProbabilityInferenceCalculator(tree,observationsData);
        parameterInferenceCalculator.compute(node);

        System.out.println();
    }
}
