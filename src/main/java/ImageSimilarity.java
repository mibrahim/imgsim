import com.github.mibrahim.imgsim.ignorestrategy.IgnoreStrategy;
import com.github.mibrahim.imgsim.similaritymetric.SimilarityMetric;

import java.awt.image.BufferedImage;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.IntStream;

public class ImageSimilarity {
    private BufferedImage referenceImage;

    public ImageSimilarity(BufferedImage referenceImage) {
        this.referenceImage = referenceImage;
    }

    public double compare(BufferedImage targetImage, final SimilarityMetric similarityMetric,
                          IgnoreStrategy ignoreStrategy) {
        // Check dimensions
        if (targetImage.getWidth() != referenceImage.getWidth())
            throw new RuntimeException("Target and reference are different widths");

        if (targetImage.getHeight() != referenceImage.getHeight())
            throw new RuntimeException("Target and reference are different height");

        IntStream.range(0, targetImage.getHeight()).parallel().forEach(
                y -> {
                    SimilarityMetric localMetric = similarityMetric.createInstance();

                    for (int x = 0; x != targetImage.getWidth(); x++) {
                        if (ignoreStrategy.isIgnore(x, y)) continue;

                        int rRGB = referenceImage.getRGB(x, y);
                        int tRGB = targetImage.getRGB(x, y);

                        localMetric.add(
                                (rRGB >> 16) & 255,
                                (rRGB >> 8) & 255,
                                (rRGB >> 0) & 255,
                                (tRGB >> 16) & 255,
                                (tRGB >> 8) & 255,
                                (tRGB >> 0) & 255
                        );
                    }

                    synchronized (similarityMetric) {
                        similarityMetric.add(localMetric);
                    }
                }
        );

        return similarityMetric.computeMetric();
    }

    public Set<Integer> differences(BufferedImage targetImage, final SimilarityMetric similarityMetric,
                                    IgnoreStrategy ignoreStrategy) {
        // Check dimensions
        if (targetImage.getWidth() != referenceImage.getWidth())
            throw new RuntimeException("Target and reference are different widths");

        if (targetImage.getHeight() != referenceImage.getHeight())
            throw new RuntimeException("Target and reference are different height");

        Set<Integer> differentPixels = ConcurrentHashMap.newKeySet();

        int width = targetImage.getWidth();

        IntStream.range(0, targetImage.getHeight()).parallel().forEach(
                y -> {
                    for (int x = 0; x != targetImage.getWidth(); x++) {
                        if (ignoreStrategy.isIgnore(x, y)) continue;

                        int rRGB = referenceImage.getRGB(x, y);
                        int tRGB = targetImage.getRGB(x, y);

                        if (similarityMetric.different(
                                (rRGB >> 16) & 255,
                                (rRGB >> 8) & 255,
                                (rRGB >> 0) & 255,
                                (tRGB >> 16) & 255,
                                (tRGB >> 8) & 255,
                                (tRGB >> 0) & 255
                        )) {
                            int index = y * width + x;
                            differentPixels.add(index);
                        }
                    }
                }
        );

        return differentPixels;
    }

    public Set<Set<Integer>> differentRegions(BufferedImage targetImage, final SimilarityMetric similarityMetric,
                                              IgnoreStrategy ignoreStrategy, int ignoreSize) {
        Set<Integer> differentPixels = differences(targetImage, similarityMetric, ignoreStrategy);

        Set<Set<Integer>> regions = new HashSet<>();

        // Segment assuming a neighbor in the same cluster is within 7x7
        int width = targetImage.getWidth();
        int height = targetImage.getHeight();

        while (!differentPixels.isEmpty()) {
            int pixel = differentPixels.iterator().next();
            differentPixels.remove(pixel);
            Set<Integer> newRegion = ConcurrentHashMap.newKeySet();
            newRegion.add(pixel);

            Set<Integer> toInspect = ConcurrentHashMap.newKeySet();
            toInspect.add(pixel);

            Set<Integer> newToInspect = ConcurrentHashMap.newKeySet();
            while (!toInspect.isEmpty()) {
                // Get a pixel out
                int pixelToInspect = differentPixels.iterator().next();
                int x = pixelToInspect % width;
                int y = pixelToInspect / width;

                // Test its 7x7 neighbors
                IntStream.range(-3, 4).parallel().forEach(
                        xd -> {
                            int newX = x + xd;
                            if (newX < 0 || newX >= width) return;

                            IntStream.range(-3, 4).parallel().forEach(
                                    yd -> {
                                        int newY = y + yd;
                                        if (newY < 0 || newY >= height) return;

                                        int newIndex = newY * width + x;

                                        if (differentPixels.contains(newIndex)) {
                                            newToInspect.add(newIndex);
                                            differentPixels.remove(newIndex);
                                            newRegion.add(newIndex);
                                        }
                                    }
                            );
                        }
                );

                toInspect = newToInspect;
            }

            if (newRegion.size() > ignoreSize) regions.add(newRegion);
        }

        return regions;
    }

    public int[] getBoundingBox(Set<Integer> region) {
        int[] boundingBox = new int[]{Integer.MAX_VALUE, Integer.MAX_VALUE, -1, -1};
        int width = referenceImage.getWidth();

        for (int index : region) {
            int x = index % width;
            int y = index / width;

            if (x < boundingBox[0]) boundingBox[0] = x;
            if (y < boundingBox[1]) boundingBox[1] = y;
            if (x > boundingBox[2]) boundingBox[2] = x;
            if (y > boundingBox[3]) boundingBox[3] = y;
        }

        return boundingBox;
    }

    public BufferedImage highlightRegions(int R, int G, int B, Set<Set<Integer>> regions, BufferedImage targetImage) {
        BufferedImage highlightedImage = new BufferedImage(
                targetImage.getWidth(),
                targetImage.getHeight(),
                targetImage.getType()
        );

        highlightedImage.getGraphics().drawImage(
                targetImage, 0, 0, null
        );

        for (Set<Integer> region : regions) {
            int[] boundingBox = getBoundingBox(region);
            for (int x = boundingBox[0]; x <= boundingBox[2]; x++) {
                for (int y = boundingBox[1]; y <= boundingBox[3]; y++) {
                    int argb = targetImage.getRGB(x, y);
                    int tr = (argb >> 16) & 255;
                    int tg = (argb >> 8) & 255;
                    int tb = argb  & 255;

                    int nr = (R + tr) / 2;
                    int ng = (G + tg) / 2;
                    int nb = (B + tb) / 2;

                    argb = (argb & 0xFF000000) | (nr << 16) | (ng << 8) | nb;

                    highlightedImage.setRGB(x, y, argb);
                }
            }
        }

        return highlightedImage;
    }
}
