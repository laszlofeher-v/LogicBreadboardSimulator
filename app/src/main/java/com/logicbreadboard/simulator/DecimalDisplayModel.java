package com.logicbreadboard.simulator;

/**
 * Created on 8/26/15.
 */
public class DecimalDisplayModel extends DeviceModel {
    final static int BAD_VALUE = -1;
    DecimalDisplayViewer viewer;
    int value;
    boolean enabled;
    public DecimalDisplayModel(DecimalDisplayViewer decimalDisplayViewer) {
        viewer = decimalDisplayViewer;
    }

    @Override
    void reset() {
        super.reset();
        enabled = false;
        value = BAD_VALUE;
    }

    @Override
    void createNextEvent() {
        value = 0;
        DeviceIOModel io = firstInputModel;
        if (io.level == State.Level.undefined) {
            value = BAD_VALUE;
        } else if (io.level == State.Level.low) {
            enabled = false;
        } else {
            io = io.next;
            enabled = true;
            while (io != null) {
                if (io.level == State.Level.undefined) {
                    value = BAD_VALUE;
                    break;
                } else if (io.level == State.Level.low) {
                    value = value << 1;
                } else {
                    value = value << 1;
                    value |= 1;
                }
                io = io.next;
            }
        }
        Event.displayEvent = true;
    }
}

