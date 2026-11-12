package com.logicbreadboard.simulator;

/**
 * Created on 4/10/16.
 */
public class InvisibleDeviceViewer extends DeviceViewer {
    @Override
    public void select(SelectMsg msg) {
        // Do nothing. It is invisible and not selectable.
    }
}
