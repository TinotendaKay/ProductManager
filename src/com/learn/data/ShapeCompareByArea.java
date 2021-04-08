package com.learn.data;

import java.util.Comparator;

public class ShapeCompareByArea implements Comparator<Shape> {
    @Override
    public int compare(Shape o1, Shape o2) {
        return o1.compareTo(o2);
    }
}
