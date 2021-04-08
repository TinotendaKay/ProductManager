package com.learn.data;

/**
 * @author TKanyandura
 */
public enum Rating {
    // '\u2605' rated, '\u2606' not rated

    NOT_RATED("\u2606\u2606\u2606\u2606\u2606"),
    ONE_STAR("\u2605\u2606\u2606\u2606\u2606"),
    TWO_STAR("\u2605\u2605\u2606\u2606\u2606"),
    THREE_STAR("\u2605\u2605\u2605\u2606\u2606"),
    FOUR_STAR("\u2605\u2605\u2605\u2605\u2606"),
    FIVE_STAR("\u2605\u2605\u2605\u2605\u2605");

    private String stars;

    private Rating(String rating) {
        this.stars = rating;
    }

    public String getStars() {
        return this.stars;
    }
}
