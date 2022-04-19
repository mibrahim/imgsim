package com.github.mibrahim.imgsim.similaritymetric;

public class SimilarityMetricJaccard extends SimilarityMetric {
    double union = 0;
    private double intersection = 0;

    @Override
    public void add(int rR, int rG, int rB, int tR, int tG, int tB) {
        union += Math.max(rR, tR);
        union += Math.max(rG, tG);
        union += Math.max(rB, tB);

        intersection += Math.min(rR, tR);
        intersection += Math.min(rG, tG);
        intersection += Math.min(rB, tB);
    }

    @Override
    public boolean different(int rR, int rG, int rB, int tR, int tG, int tB) {
        double localUnion = Math.max(rR, tR);
        localUnion += Math.max(rG, tG);
        localUnion += Math.max(rB, tB);

        double localIntersection = Math.min(rR, tR);
        localIntersection += Math.min(rG, tG);
        localIntersection += Math.min(rB, tB);

        double index = localIntersection / localUnion;

        return index < 0.8;
    }

    @Override
    public void add(SimilarityMetric rhs) {
        if (!rhs.getClass().getName().equals(rhs.getClass().getName()))
            throw new RuntimeException("Different similarity metrics");

        SimilarityMetricJaccard rhsJaccard = (SimilarityMetricJaccard) rhs;

        this.union += rhsJaccard.union;
        this.intersection += rhsJaccard.intersection;
    }

    @Override
    public double computeMetric() {
        return intersection / union;
    }

    @Override
    public SimilarityMetric createInstance() {
        return new SimilarityMetricJaccard();
    }
}
