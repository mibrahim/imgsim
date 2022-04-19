public class SimilarityMetricEuclidean implements SimilarityMetric {
    private double sumOfSquaredError = 0;
    private int count = 0;

    @Override
    public void add(int rR, int rG, int rB, int tR, int tG, int tB) {
        sumOfSquaredError +=
                (rR - tR) * (rR - tR) +
                        (rG - tG) * (rG - tG) +
                        (rB - tB) * (rB - tB);

        count++;
    }

    @Override
    public void add(SimilarityMetric rhs) {
        if (!rhs.getClass().getName().equals(rhs.getClass().getName()))
            throw new RuntimeException("Different similarity metrics");

        SimilarityMetricEuclidean rhsEuclid = (SimilarityMetricEuclidean) rhs;

        this.sumOfSquaredError += rhsEuclid.sumOfSquaredError;
        this.count += rhsEuclid.count;
    }

    @Override
    public double computeMetric() {
        return Math.sqrt(sumOfSquaredError / count);
    }

    @Override
    public SimilarityMetric createInstance() {
        return new SimilarityMetricEuclideanLAB();
    }

}