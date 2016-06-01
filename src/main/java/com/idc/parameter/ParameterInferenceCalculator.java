package com.idc.parameter;

import com.idc.model.Node;
import com.idc.model.ObservationsData;
import com.idc.model.SufficientStatistics;
import com.idc.model.TransmissionTree;

import java.util.List;

/**
 * Created by annishaa on 5/28/16.
 */
public class ParameterInferenceCalculator {
    private ObservationsData observationsData;
    private SufficientStatistics sufficientStatistics;

    public ParameterInferenceCalculator( ObservationsData observationsData) {
        this.observationsData = observationsData;
        this.sufficientStatistics = new SufficientStatistics();
    }

    public void computeMLE(Node root){
        computeSufficientStatistics(root,null);
        sufficientStatistics.computeMLE();
        sufficientStatistics.computeLoglikelihood();
        sufficientStatistics.computeFlipProbabilities();

    }

    private void computeSufficientStatistics(Node root, Node caller){
        int key = root.getKey();
        List<Integer> variableData = observationsData.getVariable2Data(key);

        if (root.isRoot()) {
            long nObservationsZero = variableData.stream().filter(observation -> observation == 0).count();
            long nObservationsOne = variableData.stream().filter(observation -> observation == 1).count();

            sufficientStatistics.setNodeKey2Count(key, nObservationsZero, nObservationsOne);
        }
        else{
            Node parent = root.getParent();
            int parentKey = parent.getKey();

            List<Integer[]> variableAndParentData = observationsData.getVariable2Data(key, parentKey);
            long nObservationsZeroParentZero = variableAndParentData.stream().filter(variableAndParent -> variableAndParent[1] == 0 && variableAndParent[0] == 0).count();
            long nObservationsOneParentZero = variableAndParentData.stream().filter(variableAndParent -> variableAndParent[1] == 0 && variableAndParent[0] == 1).count();
            long nObservationsZeroParentOne = variableAndParentData.stream().filter(variableAndParent -> variableAndParent[1] == 1 && variableAndParent[0] == 0).count();
            long nObservationsOneParentOne = variableAndParentData.stream().filter(variableAndParent -> variableAndParent[1] == 1 && variableAndParent[0] == 1).count();
            sufficientStatistics.setNodeKey2Count(key, parentKey, nObservationsZeroParentZero, nObservationsOneParentZero, nObservationsZeroParentOne, nObservationsOneParentOne);
        }

        for (Node child : root.getNeighbors()) {
            if(!child.equals(caller)) {
                child.setParent(root);
                computeSufficientStatistics(child, root);
            }
        }
    }

    @Override
    public String toString() {
        return sufficientStatistics.toString();
    }
}
