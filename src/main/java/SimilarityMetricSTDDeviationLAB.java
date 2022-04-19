public class SimilarityMetricSTDDeviationLAB implements SimilarityMetric {
    private double sumOfSquaredError = 0;
    private int count = 0;

    @Override
    public void add(int rR, int rG, int rB, int tR, int tG, int tB) {
        // TODO: Convert RGB to LAB, subtract and add to SumOfSquaredError

        count++;
    }

    @Override
    public void add(SimilarityMetric rhs) {
        if (!rhs.getClass().getName().equals(rhs.getClass().getName()))
            throw new RuntimeException("Different similarity metrics");

        SimilarityMetricSTDDeviationLAB rhsLAB = (SimilarityMetricSTDDeviationLAB) rhs;

        this.sumOfSquaredError += rhsLAB.sumOfSquaredError;
        this.count += rhsLAB.count;
    }

    @Override
    public double computeMetric() {
        return Math.sqrt(sumOfSquaredError / count);
    }

    @Override
    public SimilarityMetric createInstance() {
        return new SimilarityMetricSTDDeviationLAB();
    }
}
