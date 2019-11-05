package teamcode.common;

/**
 * Represents an immutable 2-dimensional vector.
 */
public final class Vector2 {

    public static final Vector2 FORWARD = new Vector2(0.0, 1.0);
    public static final Vector2 BACKWARD = new Vector2(0.0, -1.0);
    public static final Vector2 LEFT = new Vector2(-1.0, 0.0);
    public static final Vector2 RIGHT = new Vector2(1.0, 0.0);
    public static final Vector2 ZERO = new Vector2(0.0, 0.0);

    private final double x;
    private final double y;

    public Vector2(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public Vector2 multiply(double scalar) {
        return new Vector2(x * scalar, y * scalar);
    }

    public double magnitude() {
        return Math.sqrt(x * x + y * y);
    }

    public Vector2 normalized() {
        double magnitude = magnitude();
        return new Vector2(x / magnitude, y / magnitude);
    }

    /**
     *
     * @param other, a 2 dimensional vector added to the vector which is the
     * @return a new vector that is the sum of the 2 passed in vectors
     */
    public Vector2 add(Vector2 other){
        double xSum = other.x * Math.cos(other.getDirection()) + this.x * Math.cos(this.getDirection());
        double ySum = other.y * Math.sin(other.getDirection()) + this.y * Math.sin(this.getDirection());
        return new Vector2(xSum, ySum);
    }

    public double dotProduct(Vector2 other) {
        return this.x * other.x + this.y * other.y;
    }

    /**
     * @return the angle in radians
     */
    public double angleBetween(Vector2 other) {
        return Math.acos(this.dotProduct(other) / (this.magnitude() * other.magnitude()));
    }

    public boolean isZero() {
        return x == 0.0 && y == 0.0;
    }

    /**
     * @return the angle in radians from -pi to pi.
     */
    public double getDirection() {
        return Math.atan2(y, x);
    }

    @Override
    public String toString() {
        return String.format("x = %.1f, y = %.1f", x, y);
    }



}