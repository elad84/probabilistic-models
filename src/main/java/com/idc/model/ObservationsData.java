package com.idc.model;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by annishaa on 5/28/16.
 */
public class ObservationsData {

    private List<String> variables;
    private List<List<Double>> data;
    private Map<Integer, List<Double>> variable2Data;

    private double logprob;

    public double getLogprob() {
        return logprob;
    }

    public void setLogprob(double logprob) {
        this.logprob = logprob;
    }

    public List<String> getVariables() {
        return variables;
    }

    public void setVariables(List<String> variables) {
        this.variables = variables;
    }

    public List<List<Double>> getData() {
        return data;
    }

    public void setData(List<List<Double>> data) {
        this.data = data;
    }

    private Map<Integer, List<Double>> getVariable2Data() {
        return variable2Data;
    }

    public void setVariable2Data(Map<Integer, List<Double>> variable2Data) {
        this.variable2Data = variable2Data;
    }

    public  List<Double> getVariable2Data (Integer key){
        return getVariable2Data().get(key - 1);
    }

    public  List<Double[]> getVariable2Data (Integer key, Integer parentKey){
        List<Double> variableData = getVariable2Data().get(key - 1);
        List<Double> variableParentData = getVariable2Data().get(parentKey - 1);

        Stream<Double[]> variableAndParentData = zip(variableData.stream(), variableParentData.stream());
        return variableAndParentData.collect(Collectors.toList());
    }

    static Stream<Double[]> zip(Stream<Double> as, Stream<Double> bs)
    {
        Iterator<Double> i=as.iterator();
        return bs.filter(x->i.hasNext()).map(b->new Double[]{i.next(), b});
    }

    public void calcVariable2Data(){
        variable2Data = new HashMap<>();
        data.stream().forEach(observationList -> {
            for (int i=0;i<observationList.size();++i){
                if (!variable2Data.containsKey(i)){
                    variable2Data.put(i,new ArrayList<>());
                }
                variable2Data.get(i).add(observationList.get(i));
            }
        });
    }
}
