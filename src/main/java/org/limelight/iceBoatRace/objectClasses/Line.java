package org.limelight.iceBoatRace.objectClasses;

import org.bukkit.World;
import org.joml.Vector2f;

public class Line {
    Vector2f point;
    float gradient;

    // CONSTRUCTORS
    public Line(Vector2f point1, Vector2f point2) {
        this.point = point1;
        this.gradient = (point1.y - point2.y) / (point1.x - point2.x);
    }

    public Line(Vector2f point, float gradient) {
        this.point = point;
        this.gradient = gradient;
    }


    // GETTER METHODS
    public float getGradient() {
        return gradient;
    }


    // FUNCTIONS
    public boolean isBehindLine(Vector2f targetPoint) {
        // Check if targetPoint is behind the line that sits on point with the included gradient
        return targetPoint.y < gradient * (targetPoint.x - point.x) + point.y;
    }

    public Vector2f getNormal(World world) {
        // Get perpendicular gradient
        float normalGradient = -1/gradient;

        // Convert gradient into rise and run
        float rise = normalGradient;
        float run = 1;

        // Get magnitude of the vector created from rise and run
        double magnitude = getMagnitude(rise, run);

        // Divide the rise and run by the magnitude to make it a unit vector
        rise /= magnitude;
        run /= magnitude;

        return new Vector2f(run, rise);
    }

    // STA
    // xTIC METHODS
    public static Vector2f getVector(Vector2f point1, Vector2f point2) {
        return new Vector2f(point1.x - point2.x, point1.y - point2.y);
    }

    public static float getMagnitude(float rise, float run) {
        return (float) Math.sqrt(Math.pow(rise, 2) + Math.pow(run, 2));
    }

    public static float getAngle(float rise, float run) {
        return (float) Math.toDegrees(Math.atan2((double) rise, (double) run)) + 90;
    }

    public static Vector2f interpolate(Vector2f point1, Vector2f point2, float percentage) {
        // Get the rise and run of the vector
        float rise = (point1.y - point2.y) * percentage;
        float run = (point1.x - point2.x) * percentage;

        return new Vector2f(point2.x + run, point2.y + rise);
    }
}
