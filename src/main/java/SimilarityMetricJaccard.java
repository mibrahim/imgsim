public class SimilarityMetricJaccard implements SimilarityMetric {
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
