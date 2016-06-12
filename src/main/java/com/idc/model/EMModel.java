package com.idc.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by annishaa on 6/12/16.
 */
public class EMModel {

    private Map<Edge,List<Double[][]>> edge2Ptable = new HashMap<>();

    private double likelihood;
    private double logProb;

    public double getLikelihood() {
        return likelihood;
    }

    public void setLikelihood(double likelihood) {
        this.likelihood = likelihood;
    }

    public Map<Edge, List<Double[][]>> getEdge2Ptable() {
        return edge2Ptable;
    }

    public void setEdge2Ptable(Map<Edge, List<Double[][]>> edge2Ptable) {
        this.edge2Ptable = edge2Ptable;
    }

    public double getLogProb() {
        return logProb;
    }

    public void setLogProb(double logProb) {
        this.logProb = logProb;
    }
}
