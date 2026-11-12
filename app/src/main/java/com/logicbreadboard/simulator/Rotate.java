package com.logicbreadboard.simulator;

/**
 * Created on 1/4/15.
 */
public class Rotate {
    static final public int R_0 = 0;
    static final public int R_45 = 1;
    static final public int R_90 = 2;
    static final public int R_135 = 3;
    static final public int R_180 = 4;
    static final public int R_225 = 5;
    static final public int R_270 = 6;
    static final public int R_315 = 7;
    static protected final int NUM_OF_ROT = 8;
    static float angles[] = new float[NUM_OF_ROT];
    static final double sin[] = new double[NUM_OF_ROT];
    static final double cos[] = new double[NUM_OF_ROT];

    static {
        for (int angleIndex = 0; angleIndex < NUM_OF_ROT; angleIndex++) {
            float rad = (float) (angleIndex * 45 * Math.PI / 360);
            cos[angleIndex] = Math.cos(rad);
            sin[angleIndex] = Math.sin(rad);
            angles[angleIndex] = angleIndex * 45;
        }
    }

    static public float rotateXLeft(float x, float y, int angleIndex) {
        return (float) (cos[angleIndex] * x + sin[angleIndex] * y);
    }

    static public float rotateYLeft(float x, float y, int angleIndex) {
        return (float) (-sin[angleIndex] * x + cos[angleIndex] * y);
    }

    static public float rotateXRight(float x, float y, int angleIndex) {
        angleIndex = (NUM_OF_ROT - angleIndex) % NUM_OF_ROT;
        return (float) (cos[angleIndex] * x + sin[angleIndex] * y);
    }

    static public float rotateYRight(float x, float y, int angleIndex) {
        angleIndex = (NUM_OF_ROT - angleIndex) % NUM_OF_ROT;
        return (float) (-sin[angleIndex] * x + cos[angleIndex] * y);
    }

}
