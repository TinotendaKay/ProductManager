package com.learn.data;

public class Circle implements Shape {
    private static final int SIDES = 0;

    private double radius;

    public Circle(double radius) {
        this.radius = radius;
    }

    @Override
    public double getArea() {
        return Math.PI * (Math.pow(radius, 2));
    }

    @Override
    public int getSides() {
        return SIDES;
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
        return "Circle{" +
                "radius=" + radius +
                '}';
    }
}
