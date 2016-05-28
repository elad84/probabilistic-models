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
    public void calculateMLETest(){
        TransmissionTree tree = TransmissionTreeFactory.buildTreeNullWeight();
        Node node = tree.getNode(1);
        node.setRoot(true);

        ObservationDataInputFileProvider observationDataInputFileProvider = new ObservationDataInputFileProvider();
        ObservationsData observationsData = observationDataInputFileProvider.readObservationsDataFile("input/sample-data-1.txt");

        ParameterInferenceCalculator parameterInferenceCalculator = new ParameterInferenceCalculator(observationsData);
        parameterInferenceCalculator.computeMLE(node);

        System.out.println(parameterInferenceCalculator);


    }
}
