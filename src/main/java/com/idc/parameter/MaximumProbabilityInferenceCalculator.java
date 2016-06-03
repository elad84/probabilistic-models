package com.idc.parameter;

import com.idc.message.MaxMarginalDisributionCalculator;
import com.idc.message.TransmissionTreeFactory;
import com.idc.model.Node;
import com.idc.model.ObservationsData;
import com.idc.model.SufficientStatistics;
import com.idc.model.TransmissionTree;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by annishaa on 6/2/16.
 *     //X3	X4	X6	X7	X9	X10
 */
public class MaximumProbabilityInferenceCalculator {

    private ObservationsData observationsData;
    private TransmissionTree transmissionTree;
    private static double THRESHOLD = 0.001;



    public MaximumProbabilityInferenceCalculator(TransmissionTree transmissionTree, ObservationsData observationsData) {
        this.observationsData = observationsData;
        this.transmissionTree = transmissionTree;
    }

    public void compute(Node root){

        int i=0;
        Double beforeLogliklihood = 0.0, logliklihood = 0.0;
        TransmissionTree transmissionTreeRound = this.transmissionTree;

       do {
           beforeLogliklihood = logliklihood;
           //Step 2: for every 𝑖, use observed RVs (𝑋𝐴(𝑖)) and current model parameters
           //(𝜃(𝑟)) to infer most probable assignment to hidden RVs (𝑋𝐵(𝑖))
           //[ use max-probability message passing algorithm ]

           ObservationsData observationsDataComplete = step2(transmissionTreeRound, this.observationsData, root);

           //Step 3: Use 𝑋𝐴(𝑖),𝑋𝐵(𝑖)
           //to compute sufficient statistics 𝑛𝑘|𝑙𝑣 and normalize to
           //obtain parameter values for next iteration (𝜃(𝑟+1))
           //[ assume complete observed data ]
           transmissionTreeRound = TransmissionTreeFactory.buildTreeNullWeight();
           logliklihood = step3(root, transmissionTreeRound, observationsDataComplete);

           System.out.println (i++ +"-----------------------------------------");
       }while (Math.abs(beforeLogliklihood - logliklihood) > THRESHOLD );

    }

    private Double step3(Node root, TransmissionTree transmissionTreeRound, ObservationsData observationsDataComplete) {
        Node rootRound = transmissionTreeRound.getNode(root.getKey());
        rootRound.setRoot(true);

        ParameterInferenceCalculator parameterInferenceCalculator = new ParameterInferenceCalculator(observationsDataComplete);
        SufficientStatistics sufficientStatisticsRound = parameterInferenceCalculator.computeMLE(rootRound);
        System.out.println(sufficientStatisticsRound);

        Map<SufficientStatistics.KeyPair, Double> nodeKey2FlipProbability = sufficientStatisticsRound.getNodeKey2FlipProbability();
        nodeKey2FlipProbability.entrySet().forEach(e -> {
            SufficientStatistics.KeyPair keyPair = e.getKey();
            Double flipProb = e.getValue();
            transmissionTreeRound.addEdge(flipProb, transmissionTreeRound.getNode(keyPair.key), transmissionTreeRound.getNode(keyPair.parentKey));
        });

        return sufficientStatisticsRound.getLoglikelihood();
    }

    private ObservationsData step2(TransmissionTree transmissionTreeRound, ObservationsData observationsDataRound , Node root) {

        ObservationsData observationsDataComplete = new ObservationsData();
        observationsDataComplete.setData(new ArrayList<>());

        List<List<Integer>> data = observationsDataRound.getData();
        int nData = data.size();
        for (int i=0;i<nData;++i) {
            List<Integer> data_i = data.get(i);
            TransmissionTree transmissionTree_i = TransmissionTreeFactory.cloneTree(transmissionTreeRound);
            Node root_i = transmissionTree_i.getNode(root.getKey());
            root_i.setRoot(true);

            //X3 X4 X6 X7 X9 X10
            Node node = transmissionTree_i.getNode(3);
            node.setValue(data_i.get(0));
            node = transmissionTree_i.getNode(4);
            node.setValue(data_i.get(1));
            node = transmissionTree_i.getNode(6);
            node.setValue(data_i.get(2));
            node = transmissionTree_i.getNode(7);
            node.setValue(data_i.get(3));
            node = transmissionTree_i.getNode(9);
            node.setValue(data_i.get(4));
            node = transmissionTree_i.getNode(10);
            node.setValue(data_i.get(5));

            MaxMarginalDisributionCalculator maxMarginalDisributionCalculator = new MaxMarginalDisributionCalculator(transmissionTree_i);
            maxMarginalDisributionCalculator.computeMarginals(root_i);

            List<Integer> res_data_i = maxMarginalDisributionCalculator.getStarValues().entrySet().stream().map(b -> b.getValue() ? 1 : 0).collect(Collectors.toList());
            observationsDataComplete.getData().add(res_data_i);
        }

        observationsDataComplete.calcVariable2Data();
        return observationsDataComplete;
    }
}
