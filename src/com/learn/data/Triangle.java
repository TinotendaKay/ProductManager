package com.learn.data;

public class Triangle implements Shape, Comparable<Shape> {

    private float base;

    private float height;

    private static final int SIDES = 3;

    public Triangle(int base, int height) {
        this.base = base;
        this.height = height;
    }

    @Override
    public double getArea() {
        return (base * height) / 2;
    }

    @Override
    public int getSides() {
        return SIDES;
    }

    public float getBase() {
        return base;
    }

    public float getHeight() {
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
        return "Triangle{" +
                "base=" + base +
                ", height=" + height +
                '}';
    }
}
