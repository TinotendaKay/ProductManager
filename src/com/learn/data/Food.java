package com.learn.data;

import java.math.BigDecimal;
import java.time.LocalDate;

public final class Food extends Product {
    private LocalDate bestBefore;

    Food(int id, String name, BigDecimal price, Rating rating, LocalDate bestBefore) {
        super(id, name, price, rating);
        this.bestBefore = bestBefore;
    }

    /**
     * Get the value if the best before date of the {@link Food}
     *
     * @return the {@link LocalDate}  value of bestBefore
     */
    @Override
    public LocalDate getBestBefore() {
        return bestBefore;
    }

    /**
     * Calculate discount based on a product price and
     * {@link Product DISCOUNT_RATE discount rate}
     *
     * @return a {@link BigDecimal BigDecimal}
     * value of discount
     */
    @Override
    public BigDecimal getDiscount() {
        return (bestBefore.isEqual(LocalDate.now())) ? super.getDiscount() : BigDecimal.ZERO;
    }



    @Override
    public Product applyRating(Rating newRating) {
        return new Food(this.getId(), this.getName(), this.getPrice(), newRating, bestBefore);
    }

    @Override
    public String toString() {
        return super.toString();
    }
}
