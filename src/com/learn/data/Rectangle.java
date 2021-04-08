package com.learn.data;

public class Rectangle implements Shape, Comparable<Shape> {
    public static final int SIDES = 4;
    private double width;
    private double height;

    @Override
    public double getArea() {
        return height * width;
    }

    @Override
    public int getSides() {
        return SIDES;
    }

    public Rectangle(double width, double height) {
        this.width = width;
        this.height = height;
    }

    public double getWidth() {
        return width;
    }

    public double getHeight() {
        return height;
    }

    @Override
    public int compareTo(Shape o) {
        // if this object's area is greater than o, result  1 else result -1
        var isGreater = this.getArea() - o.getArea();

        if (isGreater == 0) {
            return 0;
        } else if (isGreater < 0) {
            return -1;
        } else {
            return 1;
        }
    }

    @Override
    public String toString() {
        return "Rectangle{" +
                "width=" + width +
                ", height=" + height +
                '}';
    }
}
