package com.github.mikephil.charting.data.filter;

import android.annotation.TargetApi;
import android.os.Build;

import java.util.Arrays;

/**
 * Implemented according to Wiki-Pseudocode {@link}
 * http://en.wikipedia.org/wiki/Ramer�Douglas�Peucker_algorithm
 *
 * @author Philipp Baldauf & Phliipp Jahoda
 */
@SuppressWarnings({"unused", "WeakerAccess"})
public class Approximator {

    @SuppressWarnings("UnnecessaryLocalVariable")
    @TargetApi(Build.VERSION_CODES.GINGERBREAD)
    public float[] reduceWithDouglasPeucker(float[] points, float tolerance) {
        int greatestIndex = 0;
        float greatestDistance = 0f;

        final Line line = new Line(points[0], points[1], points[points.length - 2], points[points.length - 1]);
        for (int i = 2; i < points.length - 2; i += 2) {
            final float distance = line.distance(points[i], points[i + 1]);
            if (distance > greatestDistance) {
                greatestDistance = distance;
                greatestIndex = i;
            }
        }

        if (greatestDistance > tolerance) {
            final float[] reduced1 = reduceWithDouglasPeucker(Arrays.copyOfRange(points, 0, greatestIndex + 2), tolerance);
            final float[] reduced2 = reduceWithDouglasPeucker(Arrays.copyOfRange(points, greatestIndex, points.length), tolerance);
            final float[] result1 = reduced1;
            final float[] result2 = Arrays.copyOfRange(reduced2, 2, reduced2.length);
            return concat(result1, result2);
        } else {
            return line.getPoints();
        }
    }

    /**
     * Combine arrays.
     */
    float[] concat(float[]... arrays) {
        int length = 0;
        for (float[] array : arrays) {
            length += array.length;
        }
        final float[] result = new float[length];
        int pos = 0;
        for (float[] array : arrays) {
            for (float element : array) {
                result[pos] = element;
                pos++;
            }
        }
        return result;
    }

    private class Line {
        private float[] points;
        private float sxey;
        private float exsy;
        private float dx;
        private float dy;
        private float length;

        public Line(float x1, float y1, float x2, float y2) {
            dx = x1 - x2;
            dy = y1 - y2;
            sxey = x1 * y2;
            exsy = x2 * y1;
            length = (float) Math.sqrt(dx * dx + dy * dy);

            points = new float[]{x1, y1, x2, y2};
        }

        public float distance(float x, float y) {
            return Math.abs(dy * x - dx * y + sxey - exsy) / length;
        }

        public float[] getPoints() {
            return points;
        }
    }
}
