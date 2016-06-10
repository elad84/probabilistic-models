package com.idc.model;

import com.idc.message.TransmissionTreeFactory;
import com.idc.parameter.LikelihoodCalculator;

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

    public Map<KeyPair, Double> getNodeKey2FlipProbability() {
        return nodeKey2FlipProbability;
    }

    public void setNodeKey2Count(int key, long nObservationsZero, long nObservationsOne) {
        KeyPair keyPair = new KeyPair(key,null);
        nodeKey2Count.put(keyPair, new Long[][]{{nObservationsZero,nObservationsOne}});
    }

    public void setNodeKey2Count(int key, int parentKey, long nObservationsZeroParentZero, long nObservationsOneParentZero, long nObservationsZeroParentOne, long nObservationsOneParentOne) {
        KeyPair keyPair0 = new KeyPair(key,parentKey);
        nodeKey2Count.put(keyPair0, new Long[][]{{ nObservationsZeroParentZero, nObservationsOneParentZero}, { nObservationsZeroParentOne, nObservationsOneParentOne}});
    }

    public void computeMLE(){
        nodeKey2CountMLE = nodeKey2Count.entrySet().stream()
                .collect(Collectors.toMap(
                        e -> e.getKey(),
                        e -> {
                            Long[][] nCounts = e.getValue();
                            long rowParentZero = nCounts[0][0] + nCounts[0][1];
                            double pParentZero = rowParentZero == 0 ? 0 : nCounts[0][0] / (double) rowParentZero;
                            double pParentZero1 = rowParentZero == 0 ? 0 :nCounts[0][1] / (double) (rowParentZero);
                            if (e.getKey().parentKey == null){
                                return new Double[][]{{pParentZero, pParentZero1}};
                            }

                            long rowParentOne = nCounts[1][0] + nCounts[1][1];
                            double pParentOne = rowParentOne == 0 ? 0 : nCounts[1][0] / (double) rowParentOne;
                            double pParentOne1 = rowParentOne == 0 ? 0 : nCounts[1][1] / (double) (rowParentOne);
                            return new Double[][]{{pParentZero, pParentZero1},
                                    {pParentOne, pParentOne1}};
                        }));
    }

    public void computeFlipProbabilities(){
        Stream<Map.Entry<KeyPair, Double[][]>> entryStream = nodeKey2CountMLE.entrySet().stream().filter(e -> e.getKey().parentKey!=null);
        this.nodeKey2FlipProbability = entryStream.collect(Collectors.toMap(
                e -> e.getKey(),
                e ->  0.5 * ( e.getValue()[0][1] + e.getValue()[1][0])
        ));
    }

    public Double getFlipProbability(KeyPair keyPair) {
        Double flipProb = nodeKey2FlipProbability.get(keyPair);
        if (flipProb == null)
            flipProb = nodeKey2FlipProbability.get(new KeyPair(keyPair.parentKey,keyPair.key));
        return flipProb;
    }

    public static class KeyPair{
        public Integer key;
        public Integer parentKey;

        public KeyPair(Integer key, Integer parentKey) {
            this.key = key;
            this.parentKey = parentKey;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            KeyPair keyPair = (KeyPair) o;

            if (!key.equals(keyPair.key)) return false;
            return parentKey != null ? parentKey.equals(keyPair.parentKey) : keyPair.parentKey == null;

        }

        @Override
        public int hashCode() {
            int result = key.hashCode();
            result = 31 * result + (parentKey != null ? parentKey.hashCode() : 0);
            return result;
        }

        @Override
        public String toString() {
            return String.format("%d | %d", key, parentKey);
        }
    }
}
