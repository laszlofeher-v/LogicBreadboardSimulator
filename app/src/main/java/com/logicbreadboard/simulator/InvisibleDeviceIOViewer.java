package com.logicbreadboard.simulator;

/**
 * Created on 4/10/16.
 */
public class InvisibleDeviceIOViewer extends DeviceIOViewer {

    InvisibleDeviceIOViewer(short x0, short y0, short x1, short y1, boolean inv) {
        super(x0, y0, x1, y1, inv);
    }

    @Override
    public void select(SelectMsg msg) {
        // Do nothing. It is invisible and not selectable.
    }
}
