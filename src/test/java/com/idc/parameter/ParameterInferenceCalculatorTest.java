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
    public void compute0(){
        compute("input/sample-data-1.txt");
    }

    @Test
    public void compute1(){
        compute("input/tree-data-1a.txt");
    }

    @Test
    public void compute2(){
        compute("input/tree-data-1b.txt");
    }

    @Test
    public void compute3(){
        compute("input/tree-data-1c.txt");
    }

    private void compute(String inputFile) {
        TransmissionTree tree = TransmissionTreeFactory.buildTreeNullWeight();
        Node node = tree.getNode(1);
        node.setRoot(true);

        ObservationDataInputFileProvider observationDataInputFileProvider = new ObservationDataInputFileProvider();
        ObservationsData observationsData = observationDataInputFileProvider.readObservationsDataFile(inputFile);

        ParameterInferenceCalculator parameterInferenceCalculator = new ParameterInferenceCalculator(observationsData);
        parameterInferenceCalculator.compute(node);

        System.out.println(parameterInferenceCalculator);
    }
}
