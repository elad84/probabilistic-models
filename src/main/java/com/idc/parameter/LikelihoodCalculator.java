package com.idc.parameter;

import com.idc.model.Node;
import com.idc.model.ObservationsData;
import com.idc.model.TransmissionTree;

import java.util.HashMap;
import java.util.List;

/**
 * Created by annishaa on 6/4/16.
 */
public class LikelihoodCalculator {

    public static double calcLogLikelihood(TransmissionTree tree, Node root,
                                            ObservationsData observationsData) {
        double logLikelihood = 0;
        for (List<Integer> observation : observationsData.getData()) {

            //set observation values to tree
            for (int i=0;i<observation.size();++i) {
                Node node = tree.getNode(i+1);
                node.setValue(observation.get(i));
            }

            logLikelihood += getLogLikelihood(tree, root);
        }
        return logLikelihood;
    }

    private static double getLogLikelihood(TransmissionTree tree, Node root) {
        return Math.log(0.5 * root.likelihood(tree));
    }

}
