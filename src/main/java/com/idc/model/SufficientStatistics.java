package com.idc.model;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by annishaa on 5/28/16.
 */
public class SufficientStatistics {

    private  Map<KeyPair,Long[][]> nodeKey2Count = new HashMap<>();

    private  Map<KeyPair,Double[][]> nodeKey2CountMLE = new HashMap<>();

    private  Map<KeyPair,Double> nodeKey2FlipProbability = new HashMap<>();

    private Double loglikelihood;

    public Map<KeyPair, Double> getNodeKey2FlipProbability() {
        return nodeKey2FlipProbability;
    }

    public Double getLoglikelihood() {
        return loglikelihood;
    }

    public void setNodeKey2Count(int key, long nObservationsZero, long nObservationsOne) {
        KeyPair keyPair = new KeyPair();
        keyPair.key = key;

        nodeKey2Count.put(keyPair, new Long[][]{{nObservationsZero,nObservationsOne}});
    }

    public void setNodeKey2Count(int key, int parentKey, long nObservationsZeroParentZero, long nObservationsOneParentZero, long nObservationsZeroParentOne, long nObservationsOneParentOne) {
        KeyPair keyPair0 = new KeyPair();
        keyPair0.key = key;
        keyPair0.parentKey = parentKey;
        nodeKey2Count.put(keyPair0, new Long[][]{{ nObservationsZeroParentZero, nObservationsOneParentZero}, { nObservationsZeroParentOne, nObservationsOneParentOne}});
    }

    public void computeMLE(){
        nodeKey2CountMLE = nodeKey2Count.entrySet().stream()
                .collect(Collectors.toMap(
                        e -> e.getKey(),
                        e -> {
                            Long[][] nCounts = e.getValue();
                            long rowParentZero = nCounts[0][0] + nCounts[0][1];
                            if (e.getKey().parentKey == null){
                                return new Double[][]{{(nCounts[0][0] / (double) rowParentZero), (nCounts[0][1] / (double) (rowParentZero))}};
                            }

                            long rowParentOne = nCounts[1][0] + nCounts[1][1];
                            return new Double[][]{{(nCounts[0][0] / (double) rowParentZero), (nCounts[0][1] / (double) (rowParentZero))},
                                    {(nCounts[1][0] / (double) rowParentOne), (nCounts[1][1] / (double) (rowParentOne))}};
                        }));
    }

    public void computeLoglikelihood(){
        Stream<Map.Entry<KeyPair, Double[][]>> entryZeroStream = nodeKey2CountMLE.entrySet().stream();
        Double loglikelihood = entryZeroStream.mapToDouble(e -> {
            Double[][] nProb = e.getValue();
            Long[][] nCounts = nodeKey2Count.get(e.getKey());
            double p00 =  nCounts[0][0] == 0 ? 0 : Math.log(nProb[0][0]) * nCounts[0][0];
            double p01 =  nCounts[0][1] == 0 ? 0 :  Math.log(nProb[0][1]) * nCounts[0][1];
            if (e.getKey().parentKey == null){
                return p00 +p01;
            }

            double p10 = nCounts[1][0] == 0 ? 0 : Math.log(nProb[1][0]) * nCounts[1][0];
            double p11 =  nCounts[1][1] == 0 ? 0 : Math.log(nProb[1][1]) * nCounts[1][1];
            return p00 + p10  + p01 + p11;
        }).sum();

        this.loglikelihood = loglikelihood;
    }


    public void computeFlipProbabilities(){
        Stream<Map.Entry<KeyPair, Double[][]>> entryStream = nodeKey2CountMLE.entrySet().stream().filter(e -> e.getKey().parentKey!=null);
        this.nodeKey2FlipProbability = entryStream.collect(Collectors.toMap(
                e -> e.getKey(),
                e ->  0.5 * ( e.getValue()[0][1] + e.getValue()[1][0])
        ));
    }

    @Override
    public String toString() {
        StringBuilder sbTitle = new StringBuilder();
        StringBuilder sb = new StringBuilder();
        nodeKey2FlipProbability.entrySet().stream().forEach(e ->{
            sb.append(String.format("%.3f\t ", e.getValue()));
            sbTitle.append(String.format("p%d-%d\t ",e.getKey().key, e.getKey().parentKey));
        });

        sbTitle.append("log-ld\n");
        sb.append(loglikelihood);
        sbTitle.append(sb);
        return sbTitle.toString();
    }

    public static class KeyPair{
        public Integer key;
        public Integer parentKey;

        @Override
        public String toString() {
            return String.format("%d | %d", key, parentKey);
        }
    }
}
