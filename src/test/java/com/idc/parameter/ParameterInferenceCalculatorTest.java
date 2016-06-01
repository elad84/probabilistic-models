package com.idc.parameter;

import com.idc.message.TransmissionTreeFactory;
import com.idc.model.Node;
import com.idc.model.ObservationsData;
import com.idc.model.TransmissionTree;
import org.junit.Test;

/**
 * Created by annishaa on 5/28/16.
 */
public class ParameterInferenceCalculatorTest {

    @Test
    public void calculateMLETest0(){
        computeMLE("input/sample-data-1.txt");
    }

    @Test
    public void calculateMLETest1(){
        computeMLE("input/tree-data-1a.txt");
    }

    @Test
    public void calculateMLETest2(){
        computeMLE("input/tree-data-1b.txt");
    }

    @Test
    public void calculateMLETest3(){
        computeMLE("input/tree-data-1c.txt");
    }

    private void computeMLE(String inputFile) {
        TransmissionTree tree = TransmissionTreeFactory.buildTreeNullWeight();
        Node node = tree.getNode(1);
        node.setRoot(true);

        ObservationDataInputFileProvider observationDataInputFileProvider = new ObservationDataInputFileProvider();
        ObservationsData observationsData = observationDataInputFileProvider.readObservationsDataFile(inputFile);

        ParameterInferenceCalculator parameterInferenceCalculator = new ParameterInferenceCalculator(observationsData);
        parameterInferenceCalculator.computeMLE(node);

        System.out.println(parameterInferenceCalculator);
    }
}
