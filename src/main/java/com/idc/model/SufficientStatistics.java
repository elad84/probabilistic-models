package com.idc.model;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by annishaa on 5/28/16.
 */
public class SufficientStatistics {

    private  Map<KeyPair,Long[]> nodeKey2Count = new HashMap<>();

    private  Map<KeyPair,Double[]> nodeKey2CountMLE = new HashMap<>();

    private Double loglikelihood;

    public void setNodeKey2Count(int key, long nObservationsZero, long nObservationsOne) {
        KeyPair keyPair = new KeyPair();
        keyPair.key = key;

        nodeKey2Count.put(keyPair, new Long[]{nObservationsZero,nObservationsOne});
    }

    public void setNodeKey2Count(int key, int parentKey, long nObservationsZeroParentZero, long nObservationsOneParentZero, long nObservationsZeroParentOne, long nObservationsOneParentOne) {
        KeyPair keyPair0 = new KeyPair();
        keyPair0.key = key;
        keyPair0.parentKey = parentKey;
        keyPair0.parentValue = 0;
        nodeKey2Count.put(keyPair0, new Long[]{ nObservationsZeroParentZero, nObservationsOneParentZero});


        KeyPair keyPair1 = new KeyPair();
        keyPair1.key = key;
        keyPair1.parentKey = parentKey;
        keyPair1.parentValue = 1;
        nodeKey2Count.put(keyPair1, new Long[]{ nObservationsZeroParentOne, nObservationsOneParentOne});
    }

    public void computeMLE(){
        nodeKey2CountMLE = nodeKey2Count.entrySet().stream()
                .collect(Collectors.toMap(
                        e -> e.getKey(),
                        e -> new Double[]{(e.getValue()[0] / (double) (e.getValue()[0] + e.getValue()[1])), (e.getValue()[1] / (double) (e.getValue()[0] + e.getValue()[1]))}
                ));
    }

    public void computeLoglikelihood(){
        Stream<Map.Entry<KeyPair, Double[]>> entryZeroStream = nodeKey2CountMLE.entrySet().stream().filter(e -> e.getKey().parentValue == null || e.getKey().parentValue == 0);
        Double loglikelihood = entryZeroStream.mapToDouble(e -> Math.log(e.getValue()[1]) * nodeKey2Count.get(e.getKey())[1]).sum();

        this.loglikelihood = loglikelihood;
    }

    @Override
    public String toString() {
        StringBuilder sbTitle = new StringBuilder();
        StringBuilder sb = new StringBuilder();
        Stream<Map.Entry<KeyPair, Double[]>> entryZeroStream = nodeKey2CountMLE.entrySet().stream().filter(e -> e.getKey().parentValue != null && e.getKey().parentValue == 0);
        entryZeroStream.forEach( e -> {
            sb.append(String.format("%.3f\t ",e.getValue()[1]));
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
        public Integer parentValue;

        @Override
        public String toString() {
            return String.format("%d | %d = %d", key, parentKey, parentValue);
        }
    }
}
