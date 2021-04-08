package com.learn.data;

import java.util.Comparator;

public class ShapeCompareBySides implements Comparator<Shape> {
    @Override
    public int compare(Shape shape1, Shape shape2) {
        return shape1.getSides() - shape2.getSides();
    }
}
