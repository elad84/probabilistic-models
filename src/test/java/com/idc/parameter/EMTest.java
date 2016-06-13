package com.idc.parameter;

import com.idc.message.TransmissionTreeFactory;
import com.idc.model.Node;
import com.idc.model.ObservationsData;
import com.idc.model.TransmissionTree;
import org.junit.Test;

/**
 * Created by annishaa on 6/1/16.
 */
public class EMTest {

    @Test
    public void calculate0() throws IllegalAccessException {
        compute("input/sample-data-2.txt");
    }

    private void compute(String inputFile) throws IllegalAccessException {
        TransmissionTree tree = TransmissionTreeFactory.buildTree(new Double[]{0.2,0.2,0.2,0.2,0.2,0.2,0.2,0.2,0.2});
        Node node = tree.getNode(1);
        node.setRoot(true);

        ObservationDataInputFileProvider observationDataInputFileProvider = new ObservationDataInputFileProvider();
        ObservationsData observationsData = observationDataInputFileProvider.readObservationsDataFile(inputFile);

        EMCalculator emCalculator = new EMCalculator(tree,observationsData);
        emCalculator.compute(node);

        //System.out.println(emCalculator);
    }
}
