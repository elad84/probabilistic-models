package com.idc.parameter;

import com.idc.message.TransmissionTreeFactory;
import com.idc.model.Node;
import com.idc.model.ObservationsData;
import com.idc.model.TransmissionTree;

/**
 * Created by annishaa on 6/15/16.
 */
public class Main {

    public static void main(String[] args) throws IllegalAccessException {
        if (args.length < 1) {
            throw new IllegalArgumentException(
                    "must supply with algorithm type (C / M / E)");
        }

        switch (args[0]) {
            case "C":
                parameterInferenceCompleteData(args[1]);
                break;
            case "M":
                maximumProbabilityInference(args);
                break;
            case "E":
                EM(args);
                break;
            default:
                break;
        }
    }

    private static void maximumProbabilityInference(String[] args) throws IllegalAccessException {
        if (args.length < 11) {
            throw new IllegalArgumentException(
                    "must supply with initial settings for model parameters");
        }

        String inputFile = args[1];
        Double[] weights = getWeights(args);

        TransmissionTree tree = TransmissionTreeFactory.buildTree(weights);
        Node node = tree.getNode(1);
        node.setRoot(true);

        ObservationDataInputFileProvider observationDataInputFileProvider = new ObservationDataInputFileProvider();
        ObservationsData observationsData = observationDataInputFileProvider.readObservationsDataFile(inputFile);

        MaximumProbabilityInferenceCalculator parameterInferenceCalculator = new MaximumProbabilityInferenceCalculator(tree,observationsData);
        parameterInferenceCalculator.compute(node);
    }

    private static void EM(String[] args) throws IllegalAccessException {
        if (args.length < 11) {
            throw new IllegalArgumentException(
                    "must supply with initial settings for model parameters");
        }

        String inputFile = args[1];
        Double[] weights = getWeights(args);

        TransmissionTree tree = TransmissionTreeFactory.buildTree(weights);
        Node node = tree.getNode(1);
        node.setRoot(true);

        ObservationDataInputFileProvider observationDataInputFileProvider = new ObservationDataInputFileProvider();
        ObservationsData observationsData = observationDataInputFileProvider.readObservationsDataFile(inputFile);

        EMCalculator emCalculator = new EMCalculator(tree,observationsData);
        emCalculator.compute(node);
    }

    private static Double[] getWeights(String[] args) {
        double p12 = Double.valueOf(args[2]);
        double p23 = Double.valueOf(args[3]);
        double p24 = Double.valueOf(args[4]);
        double p15 = Double.valueOf(args[5]);
        double p56 = Double.valueOf(args[6]);
        double p57 = Double.valueOf(args[7]);
        double p18 = Double.valueOf(args[8]);
        double p89 = Double.valueOf(args[9]);
        double p810 = Double.valueOf(args[10]);
        return new Double[]{p12,p23,p24,p15,p56,p57,p18,p89,p810};
    }


    private static void parameterInferenceCompleteData(String inputFile) {
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
