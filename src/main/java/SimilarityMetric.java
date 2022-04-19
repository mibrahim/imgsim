public interface SimilarityMetric {
    void add(int rR, int rG, int rB, int tR, int tG, int tB);
    void add(SimilarityMetric rhs);
    double computeMetric();
    SimilarityMetric createInstance();
}
