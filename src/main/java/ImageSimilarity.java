import java.awt.image.BufferedImage;
import java.util.stream.IntStream;

public class ImageSimilarity {
    private BufferedImage referenceImage;

    public ImageSimilarity(BufferedImage referenceImage){
        this.referenceImage=referenceImage;
    }

    public double compare(BufferedImage targetImage, final SimilarityMetric similarityMetric,
                          IgnoreStrategy ignoreStrategy){
        // Check dimensions
        if (targetImage.getWidth() != referenceImage.getWidth())
            throw new RuntimeException("Target and reference are different widths");

        if (targetImage.getHeight() != referenceImage.getHeight())
            throw new RuntimeException("Target and reference are different height");

        IntStream.range(0, targetImage.getHeight()).parallel().forEach(
                y -> {
                    SimilarityMetric localMetric = similarityMetric.createInstance();

                    for(int x=0;x!=targetImage.getWidth();x++){
                        if (ignoreStrategy.isIgnore(x,y)) continue;

                        int rRGB = referenceImage.getRGB(x,y);
                        int tRGB = targetImage.getRGB(x,y);

                        localMetric.add(
                                (rRGB >> 16) & 255,
                                (rRGB >> 8) & 255,
                                (rRGB >> 0) & 255,
                                (tRGB >> 16) & 255,
                                (tRGB >> 8) & 255,
                                (tRGB >> 0) & 255
                        );
                    }

                    synchronized (similarityMetric){
                        similarityMetric.add(localMetric);
                    }
                }
        );

        return similarityMetric.computeMetric();
    }
}
