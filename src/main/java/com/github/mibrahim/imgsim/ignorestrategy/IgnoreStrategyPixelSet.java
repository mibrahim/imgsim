package com.github.mibrahim.imgsim.ignorestrategy;

import java.util.HashSet;
import java.util.Set;

public class IgnoreStrategyPixelSet implements IgnoreStrategy {
    private final Set<Integer> ignoreSet = new HashSet<>();
    private int width = 0;

    public IgnoreStrategyPixelSet(int width) {
        this.width = width;
    }

    public void addPixel(int x, int y) {
        ignoreSet.add(y * width + x);
    }

    @Override
    public boolean isIgnore(int x, int y) {
        int index = y * width + x;
        return ignoreSet.contains(index);
    }
}
