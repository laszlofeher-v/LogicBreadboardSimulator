package com.logicbreadboard.simulator;

/**
 * Created by laci on 7/20/15.
 */
public class WirePoint {
    short x;
    short y;
    WirePoint next;
    WirePoint(short x, short y) {
        this.x = x;
        this.y = y;
    }
}
