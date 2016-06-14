package com.idc.parameter;

import com.idc.message.TransmissionTreeFactory;
import com.idc.model.*;

import java.util.List;
import java.util.Map;

/**
 * Created by annishaa on 6/4/16.
 */
public class LikelihoodCalculator {

    public static double calculateLogLikelihood(TransmissionTree tree, Node root,
                                                ObservationsData observationsData) {
        double logLikelihood = 0;
        for (List<Double> observation : observationsData.getData()) {
            //set observation values to tree
            for (int i=0;i<observation.size();++i) {
                Node node = tree.getNode(i+1);
                node.setValue(observation.get(i).intValue());
            }

            logLikelihood += calculateLogLikelihood(tree, root);
        }
        return logLikelihood;
    }


    public static double calculateLogLikelihood(TransmissionTree tree, Node root) {
        return Math.log(0.5 * root.likelihood(tree));
    }

    public static double calculateLogLikelihood(ObservationsData observationsData, Node root, SufficientStatistics sufficientStatistics){
        Double[] weights = new Double[] {sufficientStatistics.getFlipProbability(new SufficientStatistics.KeyPair(1, 2)), sufficientStatistics.getFlipProbability(new SufficientStatistics.KeyPair(2,3)),
                sufficientStatistics.getFlipProbability(new SufficientStatistics.KeyPair(2,4)), sufficientStatistics.getFlipProbability(new SufficientStatistics.KeyPair(1,5)),
                sufficientStatistics.getFlipProbability(new SufficientStatistics.KeyPair(5,6)), sufficientStatistics.getFlipProbability(new SufficientStatistics.KeyPair(5,7)),
                sufficientStatistics.getFlipProbability(new SufficientStatistics.KeyPair(1,8)), sufficientStatistics.getFlipProbability(new SufficientStatistics.KeyPair(8,9)),
                sufficientStatistics.getFlipProbability(new SufficientStatistics.KeyPair(8,10))};

        TransmissionTree transmissionTree = TransmissionTreeFactory.buildTree(weights);
        Node treeRoot = transmissionTree.getNode(root.getKey());
        treeRoot.setRoot(true);
        treeRoot.setParent(null);
        return LikelihoodCalculator.calculateLogLikelihood(transmissionTree, treeRoot, observationsData);
    }
}
