package com.idc.parameter;

import com.idc.message.MarginalDisributionCalculator;
import com.idc.message.MaxMarginalDisributionCalculator;
import com.idc.message.TransmissionTreeFactory;
import com.idc.model.*;

import java.util.*;

/**
 * Created by annishaa on 6/2/16.
 *     //X3	X4	X6	X7	X9	X10
 */
public class EMCalculator {

    private ObservationsData observationsData;
    private TransmissionTree transmissionTree;
    private static double THRESHOLD = 0.001;



    public EMCalculator(TransmissionTree transmissionTree, ObservationsData observationsData) {
        this.observationsData = observationsData;
        this.transmissionTree = transmissionTree;
    }

    public void compute(Node root) throws IllegalAccessException {
        Double beforeLogliklihood = 0.0, dataProbability = 0.0;
        TransmissionTree transmissionTreeRound = this.transmissionTree;

        List<Edge> edgesOrdered = getOrderedEdges();

        // print header
        for (Edge edge : edgesOrdered) {
            System.out.print("p" + edge.getFirstNode().getKey() + "-"
                    + edge.getSecondNode().getKey() + "\t");
        }
        System.out.print("\tlog-prob");
        System.out.println("\t\tlog-ld");

        do {
            beforeLogliklihood = dataProbability;

            EMModel emModel = EStep(transmissionTreeRound, this.observationsData, root);
            Node treeRoot = transmissionTreeRound.getNode(root.getKey());
            treeRoot.setRoot(true);
            treeRoot.setParent(null);

            dataProbability = emModel.getLogProb();// LikelihoodCalculator.calculateLogLikelihood(transmissionTreeRound,  emModel);
            // print results
            for (Edge edge : edgesOrdered) {
                System.out.printf("%.3f\t", transmissionTreeRound.getEdgeWeight(edge));
            }
            System.out.printf("\t%.4f", dataProbability);
            System.out.printf("\t\t%.4f\n", emModel.getLikelihood());

            transmissionTreeRound = TransmissionTreeFactory.buildTreeNullWeight();
            MStep(transmissionTreeRound, emModel);
        }while (Math.abs(beforeLogliklihood - dataProbability) > THRESHOLD );
    }

    private ArrayList<Edge> getOrderedEdges() {
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
        return edgesOrdered;
    }

    private EMModel EStep(TransmissionTree transmissionTreeRound, ObservationsData observationsDataRound , Node root) throws IllegalAccessException {

        EMModel emModel = new EMModel();
        Double dataLikelihood = 0.0;
        Double logProb = 0.0;

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

            // compute marginal probabilities for all the nodes
            MarginalDisributionCalculator marginalDisributionCalculator = new MarginalDisributionCalculator(
                    transmissionTree_i);
            BinaryMessage rootProb = marginalDisributionCalculator.computeMarginals(root_i);
            double likelihood = rootProb.getValue(false) + rootProb.getValue(true);
            dataLikelihood += Math.log(likelihood);

            BinaryMessage argMaxBinaryMessage = new MaxMarginalDisributionCalculator(transmissionTree_i).collect(root_i, null).getBinaryMessage();
            logProb += Math.log(0.5 * Math.max(argMaxBinaryMessage.getValue(false),argMaxBinaryMessage.getValue(true)));


            Map<Integer, MarginalDisribution> marginalDisributionsMap = transmissionTree_i.getNodesMarginalDisribution();
            Map<Edge, BinaryMessage> messages = marginalDisributionCalculator.getMessages();
            for (Map.Entry<Edge, BinaryMessage> edgeBinaryMessageEntry : messages.entrySet()) {
                Edge edge = edgeBinaryMessageEntry.getKey();
                BinaryMessage binaryMessage = edgeBinaryMessageEntry.getValue();
                Node firstNode = edge.getFirstNode();
                Node secondNode = edge.getSecondNode();
                MarginalDisribution marginalDisribution = marginalDisributionsMap.get(secondNode.getKey());
                Psi firstNodePsi = firstNode.getPsi();
                Double edgeWeight = transmissionTreeRound.getEdgeWeight(firstNode, secondNode);

                double zeroMessage = binaryMessage.getValue(false);
                double oneMessage = binaryMessage.getValue(true);

                double zeroF = marginalDisribution.getNormalizedMarginalDisribution()[0];
                double oneF = marginalDisribution.getNormalizedMarginalDisribution()[1];

                double zeroPsiValue = firstNodePsi.getValue(false);
                double onePsiValue = firstNodePsi.getValue(true);

                double zeroZeroP = zeroF * (1 - edgeWeight) * zeroPsiValue / zeroMessage;
                double zeroOneP = oneF * (edgeWeight) * zeroPsiValue / oneMessage;
                double oneOneP = oneF * (1- edgeWeight) * onePsiValue / oneMessage;
                double oneZeroP = zeroF * (edgeWeight) * onePsiValue / zeroMessage;

                double sum = zeroZeroP + zeroOneP +  oneOneP + oneZeroP;
                emModel.getEdge2Ptable().putIfAbsent(edge,new ArrayList<>());
                emModel.getEdge2Ptable().get(edge).add(new Double[][]{{zeroZeroP/sum, zeroOneP/sum}, {oneZeroP/sum, oneOneP/sum}});
            }
        }

        emModel.setLikelihood(dataLikelihood);
        emModel.setLogProb(logProb);
        return emModel;
    }

    private void MStep(TransmissionTree tree, EMModel emModel) {
        for (Map.Entry<Edge, List<Double[][]>> entry : emModel.getEdge2Ptable().entrySet()) {
            Edge edge = entry.getKey();
            Node firstNode = edge.getFirstNode();
            Node secondNode = edge.getSecondNode();
            List<Double[][]> pTable = entry.getValue();
            int size = pTable.size();
            double sum = pTable.stream().mapToDouble(e -> e[0][1] + e[1][0]).sum();
            tree.addEdge(sum / (size), firstNode, secondNode);
        }
    }
}
