package com.github.mibrahim.imgsim.ignorestrategy;

import java.awt.image.BufferedImage;

public class IgnoreStrategyAlpha implements IgnoreStrategy {
    private BufferedImage referenceImage;

    public IgnoreStrategyAlpha(BufferedImage referenceImage) {
        this.referenceImage = referenceImage;
    }

    @Override
    public boolean isIgnore(int x, int y) {
        int alpha = (referenceImage.getRGB(x, y) << 24) & 255;

        if (alpha < 128) return true;

        return false;
    }
}
