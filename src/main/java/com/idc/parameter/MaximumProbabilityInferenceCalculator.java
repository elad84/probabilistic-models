package com.idc.parameter;

import com.idc.message.MarginalDisributionCalculator;
import com.idc.message.TransmissionTreeFactory;
import com.idc.model.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by annishaa on 6/2/16.
 */
public class MaximumProbabilityInferenceCalculator {

    private ObservationsData observationsData;
    private TransmissionTree transmissionTree;
    private static double THRESHOLD = 0.0001;

    public MaximumProbabilityInferenceCalculator(TransmissionTree transmissionTree, ObservationsData observationsData) {
        this.observationsData = observationsData;
        this.transmissionTree = transmissionTree;
    }

    public String compute(Node root) throws IllegalAccessException {
        Double beforeLogliklihood = 0.0, dataProbability = 0.0;
        TransmissionTree transmissionTreeRound = this.transmissionTree;

        ArrayList<Edge> edgesOrdered = new ArrayList<>();
        edgesOrdered.add(new Edge(transmissionTree.getNode(1), transmissionTree.getNode(2)));
        edgesOrdered.add(new Edge(transmissionTree.getNode(2), transmissionTree.getNode(3)));
        edgesOrdered.add(new Edge(transmissionTree.getNode(2), transmissionTree.getNode(4)));
        edgesOrdered.add(new Edge(transmissionTree.getNode(1), transmissionTree.getNode(5)));
        edgesOrdered.add(new Edge(transmissionTree.getNode(5), transmissionTree.getNode(6)));
        edgesOrdered.add(new Edge(transmissionTree.getNode(5), transmissionTree.getNode(7)));
        edgesOrdered.add(new Edge(transmissionTree.getNode(1), transmissionTree.getNode(8)));
        edgesOrdered.add(new Edge(transmissionTree.getNode(8), transmissionTree.getNode(9)));
        edgesOrdered.add(new Edge(transmissionTree.getNode(8), transmissionTree.getNode(10)));

        // print header
        for (Edge edge : edgesOrdered) {
            System.out.print("p" + edge.getFirstNode().getKey() + "-"
                    + edge.getSecondNode().getKey() + "\t");
        }
        System.out.print("\tlog-prob");
        System.out.println("\t\tlog-ld");

        String results;

       do {
           beforeLogliklihood = dataProbability;
           //Step 2: for every 𝑖, use observed RVs (𝑋𝐴(𝑖)) and current model parameters
           //(𝜃(𝑟)) to infer most probable assignment to hidden RVs (𝑋𝐵(𝑖))
           //[ use message passing algorithm ]

           ObservationsData observationsDataComplete = step2(transmissionTreeRound, this.observationsData, root);
           Node treeRoot = transmissionTreeRound.getNode(root.getKey());
           treeRoot.setRoot(true);
           treeRoot.setParent(null);

           dataProbability = LikelihoodCalculator.calculateLogLikelihood(transmissionTreeRound,transmissionTreeRound.getNode(root.getKey()), observationsDataComplete);
           // print results
           results = printResults(dataProbability, transmissionTreeRound, edgesOrdered, observationsDataComplete);
           System.out.print(results);

           //Step 3: Use 𝑋𝐴(𝑖),𝑋𝐵(𝑖)
           //to compute sufficient statistics 𝑛𝑘|𝑙𝑣 and normalize to
           //obtain parameter values for next iteration (𝜃(𝑟+1))
           //[ assume complete observed data ]
           transmissionTreeRound = TransmissionTreeFactory.buildTreeNullWeight();
           step3(root, transmissionTreeRound, observationsDataComplete);

       }while (Math.abs(beforeLogliklihood - dataProbability) > THRESHOLD );

        return results;
    }

    private String printResults(Double dataProbability, TransmissionTree transmissionTreeRound, ArrayList<Edge> edgesOrdered, ObservationsData observationsDataComplete) {
        StringBuffer sb = new StringBuffer();

        for (Edge edge : edgesOrdered) {
           sb.append(String.format("%.3f\t", transmissionTreeRound.getEdgeWeight(edge)));
        }
        sb.append(String.format("\t%.4f", dataProbability)); //log-prob
        sb.append(String.format("\t\t%.4f\n", observationsDataComplete.getLogprob()));
        return sb.toString();
    }

    private void step3(Node root, TransmissionTree transmissionTreeRound, ObservationsData observationsDataComplete) {
        Node rootRound = transmissionTreeRound.getNode(root.getKey());
        rootRound.setRoot(true);

        ParameterInferenceCalculator parameterInferenceCalculator = new ParameterInferenceCalculator(observationsDataComplete);
        SufficientStatistics sufficientStatisticsRound = parameterInferenceCalculator.compute(rootRound);

        Map<SufficientStatistics.KeyPair, Double> nodeKey2FlipProbability = sufficientStatisticsRound.getNodeKey2FlipProbability();
        nodeKey2FlipProbability.entrySet().forEach(e -> {
            SufficientStatistics.KeyPair keyPair = e.getKey();
            Double flipProb = e.getValue();
            transmissionTreeRound.addEdge(flipProb, transmissionTreeRound.getNode(keyPair.key), transmissionTreeRound.getNode(keyPair.parentKey));
        });
    }

    private ObservationsData step2(TransmissionTree transmissionTreeRound, ObservationsData observationsDataRound , Node root) throws IllegalAccessException {

        ObservationsData observationsDataComplete = new ObservationsData();
        observationsDataComplete.setData(new ArrayList<>());

        List<List<Double>> data = observationsDataRound.getData();
        int nData = data.size();
        for (int i=0;i<nData;++i) {
            List<Double> data_i = data.get(i);
            TransmissionTree transmissionTree_i = TransmissionTreeFactory.cloneTree(transmissionTreeRound);
            Node root_i = transmissionTree_i.getNode(root.getKey());
            root_i.setRoot(true);

            //X3 X4 X6 X7 X9 X10
            Node node = transmissionTree_i.getNode(3);
            node.setValue(data_i.get(0).intValue());
            node = transmissionTree_i.getNode(4);
            node.setValue(data_i.get(1).intValue());
            node = transmissionTree_i.getNode(6);
            node.setValue(data_i.get(2).intValue());
            node = transmissionTree_i.getNode(7);
            node.setValue(data_i.get(3).intValue());
            node = transmissionTree_i.getNode(9);
            node.setValue(data_i.get(4).intValue());
            node = transmissionTree_i.getNode(10);
            node.setValue(data_i.get(5).intValue());

            MarginalDisributionCalculator marginalDisributionCalculator = new MarginalDisributionCalculator(transmissionTree_i);
            marginalDisributionCalculator.computeMarginals(root_i);

            Map<Integer, MarginalDisribution> nodesMarginalDisribution = transmissionTree_i.getNodesMarginalDisribution();

            List<Double> res_data_i = new ArrayList<>();
            for (Integer key : nodesMarginalDisribution.keySet()) {
                double prob0 = nodesMarginalDisribution.get(key).getValue(false);
                double prob1 = nodesMarginalDisribution.get(key).getValue(true);
                double obs =  prob1 >= prob0 ? 1 : 0;
                res_data_i.add(obs);
            }

            MarginalDisribution marginalDisribution = nodesMarginalDisribution.get(1);
            double prob0 = marginalDisribution.getValue(false);
            double prob1 = marginalDisribution.getValue(true);
            observationsDataComplete.setLogprob( observationsDataComplete.getLogprob() + Math.log(prob0 + prob1));

            observationsDataComplete.getData().add(res_data_i);
        }

        observationsDataComplete.calcVariable2Data();
        return observationsDataComplete;
    }
}
