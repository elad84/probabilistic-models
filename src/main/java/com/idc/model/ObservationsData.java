package com.idc.model;

import javafx.util.Pair;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by annishaa on 5/28/16.
 */
public class ObservationsData {

    private List<String> variables;
    private List<List<Integer>> data;
    private Map<Integer, List<Integer>> variable2Data;

    private double likelihood;

    public double getLikelihood() {
        return likelihood;
    }

    public void setLikelihood(double likelihood) {
        this.likelihood = likelihood;
    }

    public List<String> getVariables() {
        return variables;
    }

    public void setVariables(List<String> variables) {
        this.variables = variables;
    }

    public List<List<Integer>> getData() {
        return data;
    }

    public void setData(List<List<Integer>> data) {
        this.data = data;
    }

    private Map<Integer, List<Integer>> getVariable2Data() {
        return variable2Data;
    }

    public void setVariable2Data(Map<Integer, List<Integer>> variable2Data) {
        this.variable2Data = variable2Data;
    }

    public  List<Integer> getVariable2Data (Integer key){
        return getVariable2Data().get(key - 1);
    }

    public  List<Integer[]> getVariable2Data (Integer key, Integer parentKey){
        List<Integer> variableData = getVariable2Data().get(key - 1);
        List<Integer> variableParentData = getVariable2Data().get(parentKey - 1);

        Stream<Integer[]> variableAndParentData = zip(variableData.stream(), variableParentData.stream());
        return variableAndParentData.collect(Collectors.toList());
    }

    static Stream<Integer[]> zip(Stream<Integer> as, Stream<Integer> bs)
    {
        Iterator<Integer> i=as.iterator();
        return bs.filter(x->i.hasNext()).map(b->new Integer[]{i.next(), b});
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
