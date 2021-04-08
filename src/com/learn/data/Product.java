package com.learn.data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.Objects;

/**
 * {@code Product} class represents properties and behavior of
 * product object in the Product Management System.
 * <br>
 * Each product has an id, name and price
 * <br>
 * Each product can have a different discount, calculated based on a
 * {@link com.learn.data.Product DISCOUNT_RATE discount rate}
 *
 * @author TKanyandura
 * @version 1.0
 */
public abstract class Product implements Serializable, Ratable<Product> {

    /**
     * A constant that defines a
     * {@link java.math.BigDecimal BigDecimal} value of discount rate
     * <br/>
     * Discount rate is 10%
     */
    public static final BigDecimal DISCOUNT_RATE = BigDecimal.valueOf(0.1);

    private int id;
    private String name;
    private BigDecimal price;
    private Rating rating;

    Product(int id, String name, BigDecimal price, Rating rating) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.rating = rating;
    }

    public int getId() {
        return id;
    }


    public String getName() {
        return name;
    }


    public BigDecimal getPrice() {
        return price;
    }


    /**
     * Calculate discount based on a product price and
     * {@link com.learn.data.Product DISCOUNT_RATE discount rate}
     *
     * @return a {@link java.math.BigDecimal BigDecimal}
     * value of discount
     */
    public BigDecimal getDiscount() {
        return this.price.multiply(DISCOUNT_RATE).setScale(2, RoundingMode.HALF_DOWN);
    }

    @Override
    public Rating getRating() {
        return rating;
    }

    /**
     * Get the value if the best before date of the {@link Food}
     *
     * @return the {@link LocalDate}  value of bestBefore
     */
    public LocalDate getBestBefore() {
        return LocalDate.now();
    }

    @Override
    public String toString() {
        return id + ", " + name + ", " + price + "' " + getDiscount() + ", " + price + "' " + rating.getStars() + ", " + getBestBefore();
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 23 * hash + this.id;
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj instanceof Product) {
            final Product other = (Product) obj;
            return this.id == other.id;
        }

        return false;
    }
}
