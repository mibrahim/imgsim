package com.github.mibrahim.imgsim.similaritymetric;

public abstract class SimilarityMetric {
    abstract public void add(int rR, int rG, int rB, int tR, int tG, int tB);

    abstract public void add(SimilarityMetric rhs);

    abstract public double computeMetric();

    abstract public SimilarityMetric createInstance();

    public boolean different(int rR, int rG, int rB, int tR, int tG, int tB) {
        throw new RuntimeException("Unimplemented");
    }
}
